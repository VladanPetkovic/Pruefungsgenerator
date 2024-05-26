package com.example.backend.app;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;

public class ExportCSV {
    private String directoryName;
    private int lastQuestionId = 0;
    private StudyProgram selectedStudyProgram = null;
    private Course selectedCourse = null;

    public ExportCSV(String directoryName) {
        this.directoryName = directoryName;
    }

    public void initStudyProgram(String studyProgram) {
        this.selectedStudyProgram = SQLiteDatabaseConnection.studyProgramRepository.get(studyProgram);
    }

    public void initCourse(String course) {
        this.selectedCourse = SQLiteDatabaseConnection.courseRepository.get(course);
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
            writer.append("question_string;categoryName_string;difficulty_int;points_float;questionType_string;remark_string;answers_string;keywords_string;courseName_string;studyProgramName_string\n");

            if (exportType == 0) {          // export all questions
                ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll();
                for (StudyProgram studyProgram : studyPrograms) {
                    ArrayList<Course> courses = SQLiteDatabaseConnection.courseRepository.getAll(studyProgram.getId());
                    for (Course course : courses) {
                        writeQuestionsToFile(writer, course, studyProgram);
                    }
                }
            } else if (exportType == 1) {   // export questions for one studyProgram
                ArrayList<Course> courses = SQLiteDatabaseConnection.courseRepository.getAll(this.selectedStudyProgram.getId());
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
    private void writeQuestionsToFile(FileWriter writer, Course course, StudyProgram studyProgram) throws IOException {
        ArrayList<Question> questions = new ArrayList<>();
        lastQuestionId = 0;     // TODO: this would be an edge-case (questions equal for different studyPrograms)
        do {
            questions = SQLiteDatabaseConnection.questionRepository.getAll(course, lastQuestionId);
            Logger.log(getClass().getName(), "Fetched questions from DB: " + questions.size(), LogLevel.INFO);
            // Write question data
            for (Question question : questions) {
                lastQuestionId = question.getId();
                writer.append("\"").append(question.getQuestion()).append("\"").append(";");
                writer.append("\"").append(question.getCategory().getName()).append("\"").append(";");
                writer.append(String.valueOf(question.getDifficulty())).append(";");
                writer.append(String.valueOf(question.getPoints())).append(";");
                writer.append("\"").append(question.getType().getName()).append("\"").append(";");
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

    private int getCountOfQuestions(int exportType) {
        int count = 0;

        if (exportType == 0) {          // count all questions
            count = SQLiteDatabaseConnection.questionRepository.getCountOfAllQuestions();
        } else if (exportType == 1) {   // count questions for one studyProgram
            count = SQLiteDatabaseConnection.questionRepository.getCountOfAllQuestions(this.selectedStudyProgram);
        } else {                        // count questions for one course
            count = SQLiteDatabaseConnection.questionRepository.getCountOfAllQuestions(this.selectedCourse);
        }

        return count;
    }

    private String createFileName() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        return String.format("tourExport_%04d-%02d-%02d_%02d-%02d-%02d.csv",
                currentDateTime.getYear(),
                currentDateTime.getMonthValue(),
                currentDateTime.getDayOfMonth(),
                currentDateTime.getHour(),
                currentDateTime.getMinute(),
                currentDateTime.getSecond());
    }
}
