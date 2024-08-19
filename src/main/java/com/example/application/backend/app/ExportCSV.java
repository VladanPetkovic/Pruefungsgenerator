package com.example.application.backend.app;

import com.example.application.backend.db.models.*;
import com.example.application.backend.db.services.CourseService;
import com.example.application.backend.db.services.QuestionService;
import com.example.application.backend.db.services.StudyProgramService;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExportCSV {
    private final StudyProgramService studyProgramService;
    private final CourseService courseService;
    private final QuestionService questionService;
    private String directoryName;
    private Long lastQuestionId;
    private StudyProgram selectedStudyProgram = null;
    private Course selectedCourse = null;

    public ExportCSV(String directoryName, StudyProgramService studyProgramService, CourseService courseService, QuestionService questionService) {
        this.directoryName = directoryName;
        this.studyProgramService = studyProgramService;
        this.courseService = courseService;
        this.questionService = questionService;
    }

    public void initStudyProgram(String studyProgram) {
        this.selectedStudyProgram = studyProgramService.getByName(studyProgram);
    }

    public void initCourse(String course) {
        this.selectedCourse = courseService.getByNameAndStudyProgramId(course, null);   // TODO: add StudyProgram
    }

    /**
     * This function exports questions to CSV. We are calling the DB multiple times to avoid a stack-overflow for large data.
     * @param exportType - Type of questions: ALL; All for one StudyProgram; All for one Course
     * @return True, when the export was successful, false otherwise.
     */
    public boolean export(int exportType) {
        Logger.log(getClass().getName(), "Question-count: " + getCountOfQuestions(exportType), LogLevel.INFO);

        // open and write a csv file
        try (FileWriter writer = new FileWriter(this.directoryName + "\\" + createFileName())) {
            // Write CSV header
            //TODO: question ID should be exported as well, because we will need it for later import
            // also the question is: do we need the course name and the study program name?
            writer.append("question_id;question_string;categoryName_string;difficulty_int;points_float;questionType_string;remark_string;answers_string;keywords_string;courseName_string;studyProgramName_string\n");

            if (exportType == 0) {          // export all questions
                List<StudyProgram> studyPrograms = studyProgramService.getAll();
                for (StudyProgram studyProgram : studyPrograms) {
                    List<Course> courses = courseService.getAllByStudyProgram(studyProgram.getId());
                    for (Course course : courses) {
                        writeQuestionsToFile(writer, course, studyProgram);
                    }
                }
            } else if (exportType == 1) {   // export questions for one studyProgram
                List<Course> courses = courseService.getAllByStudyProgram(this.selectedStudyProgram.getId());
                for (Course course : courses) {
                    writeQuestionsToFile(writer, course, this.selectedStudyProgram);
                }
            } else {                        // export questions for one course
                writeQuestionsToFile(writer, this.selectedCourse, SharedData.getSelectedStudyProgram());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // TODO: add later the possibility to add the question_id or not (for updating questions)
    // correction: the question_id is needed in every case, so it should be exported as well
    private void writeQuestionsToFile(FileWriter writer, Course course, StudyProgram studyProgram) throws IOException {
        List<Question> questions = new ArrayList<>();
        lastQuestionId = 0L;     // TODO: this would be an edge-case (questions equal for different studyPrograms)
        do {
            // this function returns all question, that have a larger id than the specified minQuestionID
            questions = questionService.getAllByCourseAndIdGreaterThan(course.getId(), lastQuestionId);
            Logger.log(getClass().getName(), "Fetched questions from DB: " + questions.size(), LogLevel.INFO);
            // Write question data
            for (Question question : questions) {
                lastQuestionId = question.getId();
                writer.append(String.valueOf(question.getId())).append(";");
                writer.append("\"").append(question.getQuestion()).append("\"").append(";");
                writer.append("\"").append(question.getCategory().getName()).append("\"").append(";");
                writer.append(String.valueOf(question.getDifficulty())).append(";");
                writer.append(String.valueOf(question.getPoints())).append(";");
                writer.append("\"").append(question.getType()).append("\"").append(";");
                writer.append("\"").append(question.getRemark()).append("\"").append(";");
                writeAnswers(question, writer);
                writeKeywords(question, writer);
                writer.append("\"").append(course.getName()).append("\"").append(";");
                writer.append("\"").append(studyProgram.getName()).append("\"").append("\n");
            }
        } while (!questions.isEmpty());
    }

    private void writeAnswers(Question question, FileWriter writer) throws IOException {
        int answerSize = question.getAnswers().size();
        writer.append("\"");
        for (Answer answer : question.getAnswers()) {
            answerSize--;
            writer.append(answer.getAnswer());
            if (answerSize > 0) {
                writer.append(",");
            }
        }
        if (answerSize == 0) {
            writer.append("\"").append(";");
        }
    }

    private void writeKeywords(Question question, FileWriter writer) throws IOException {
        int keywordsSize = question.getKeywords().size();
        writer.append("\"");
        for (Keyword keyword : question.getKeywords()) {
            keywordsSize--;
            writer.append(keyword.getKeyword());
            if (keywordsSize > 0) {
                writer.append(",");
            }
        }
        if (keywordsSize == 0) {
            writer.append("\"").append(";");
        }
    }

    private long getCountOfQuestions(int exportType) {
        return switch (exportType) {
            case 0 -> questionService.getCount();
            case 1 -> questionService.getCountByStudyProgram(this.selectedStudyProgram.getId());
            default -> questionService.getCountByCourse(this.selectedCourse.getId());
        };
    }

    private String createFileName() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        return String.format("questionDBExport_%04d-%02d-%02d_%02d-%02d-%02d.csv",
                currentDateTime.getYear(),
                currentDateTime.getMonthValue(),
                currentDateTime.getDayOfMonth(),
                currentDateTime.getHour(),
                currentDateTime.getMinute(),
                currentDateTime.getSecond());
    }
}
