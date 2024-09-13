package com.example.application.backend.app;

import com.example.application.backend.db.models.*;
import com.example.application.MainApp;
import com.example.application.backend.db.services.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ImportCSV {
    private final StudyProgramService studyProgramService;
    private final CourseService courseService;
    private final QuestionService questionService;
    private final CategoryService categoryService;
    private final AnswerService answerService;
    private final KeywordService keywordService;
    private String filePath;
    private String modeOfImport;
    private String importTargetStudyProgram;
    private String importTargetCourse;

    public ImportCSV(String filePath,
                     StudyProgramService studyProgramService,
                     CourseService courseService,
                     QuestionService questionService,
                     CategoryService categoryService,
                     AnswerService answerService,
                     KeywordService keywordService) {
        this.filePath = filePath;
        this.studyProgramService = studyProgramService;
        this.courseService = courseService;
        this.questionService = questionService;
        this.categoryService = categoryService;
        this.answerService = answerService;
        this.keywordService = keywordService;
        this.modeOfImport = SharedData.getModeOfImport();
        this.importTargetStudyProgram = SharedData.getImportTargetStudyProgram();
        this.importTargetCourse = SharedData.getImportTargetCourse();
    }

    /**
     * This function imports questions.
     * @return True, when the import was successful and False, when the check or import went wrong.
     */
    public boolean importData() {
        // Checks if the filePath is null, which means no file was selected
        if (filePath == null) {
            Logger.log(getClass().getName(), "No file selected.", LogLevel.INFO);
            return false;
        }

        Logger.log(getClass().getName(), "Checking data from file: " + filePath, LogLevel.INFO);
        if (!checkingDataFromFile(filePath)) {
            Logger.log(getClass().getName(), "File-check failed!", LogLevel.WARN);
            return false;
        }

        Logger.log(getClass().getName(), "Importing data from file: " + filePath, LogLevel.INFO);
        if (!importDataFromFile(filePath)) {
            Logger.log(getClass().getName(), "Failed to import data from file: " + filePath, LogLevel.ERROR);
            return false;
        }

        return true;
    }

    private boolean checkingDataFromFile(String filePath) {
        // TODO: FIRST: CHECK THE FILE - SO WE CAN PROCEED WITHOUT ESTABLISHING A DB-CONNECTION
        // OR: we create a new method in one service and use the @Transactional keyword
        return true;
    }

    private boolean importDataFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Read the first line, which is expected to be the header, and skip it
            if ((line = reader.readLine()) == null) {
                Logger.log(getClass().getName(), "CSV file is empty.", LogLevel.INFO);
                // Return false because there is no data to import
                return false;
            }
            // Loop through each subsequent line in the CSV file
            while ((line = reader.readLine()) != null) {
                // Split the line into an array of strings using ";" as the delimiter   // TODO: check, if ';' is between '" "'
                String[] values = line.split(";");
                // process each row
                importRow(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // Return true to indicate that the data import was successful
        return true;
    }

    private void importRow(String[] values) {
        // question_id; question_text; category_name; difficulty; points; question_type; remarks; answers; keywords; course_name; course_number; studyprogram_name
        if (values.length != 12) {
            Logger.log(getClass().getName(), "Invalid CSV format.", LogLevel.INFO);
            return;
        }

        ParsedCSVRow parsedRow = parseCSVRow(values);

        if (modeOfImport.equals(MainApp.resourceBundle.getString("insert_new_questions"))) {
            parsedRow.setCourseName(importTargetCourse);
            parsedRow.setStudyProgramName(importTargetStudyProgram);
            handleQuestions(parsedRow, true);
        } else {
            handleQuestions(parsedRow, false);
        }
    }

    private ParsedCSVRow parseCSVRow(String[] values) {
        Long question_id = Long.parseLong(values[0]);
        String questionText = values[1];
        String categoryName = values[2];
        int difficulty = Integer.parseInt(values[3]);
        float points = Float.parseFloat(values[4].replace(",", "."));
        String questionTypeName = values[5];
        String remark = values[6];
        String answersText = values[7];
        String keywordsText = values[8];
        String courseName = values[9];
        Integer courseNumber = Integer.parseInt(values[10]);
        String studyProgramName = values[11];

        return new ParsedCSVRow(question_id, questionText, categoryName, difficulty, points, questionTypeName, remark, answersText, keywordsText, courseName, courseNumber, studyProgramName);
    }


    private void handleQuestions(ParsedCSVRow parsedRow, boolean isNew) {
        // mode of import: insert the questions from the file into a different studyprogram and course
        StudyProgram studyProgram = studyProgramService.add(parsedRow.getStudyProgramName());
        Course course = courseService.add(new Course(parsedRow.getCourseName(), parsedRow.getCourseNumber(), "Lector"), studyProgram);
        Category category = categoryService.add(new Category(parsedRow.getCategoryName()), course);

        Set<Answer> answers = Answer.getAnswersAsSet(parsedRow.getAnswersText().split("\\|"));
        Set<Keyword> keywords = Keyword.getKeywordsAsSet(parsedRow.getKeywordsText().split("\\|"));
        Set<Keyword> savedKeywords = new HashSet<>();
        // TODO: refactor to avoid n+1 select
        for (Keyword k : keywords) {
            savedKeywords.add(keywordService.add(k, course));
        }

        if (isNew) {
            Question newQuestion = insertQuestion(category, parsedRow.getDifficulty(), parsedRow.getPoints(), parsedRow.getQuestionText(), parsedRow.getType(), parsedRow.getRemark(), savedKeywords);
            if (newQuestion != null) {
                answerService.addAnswers(newQuestion.getId(), answers);
            }
        } else {
            updateQuestion(parsedRow.getQuestionId(), category, parsedRow.getDifficulty(), parsedRow.getPoints(), parsedRow.getQuestionText(), parsedRow.getType(), parsedRow.getRemark(), answers, savedKeywords);
        }
    }

    private Question insertQuestion(Category category, Integer difficulty, Float points, String questionText, String type, String remark, Set<Keyword> keywords) {
        Question question = new Question();
        question.setCategory(category);
        question.setDifficulty(difficulty);
        question.setPoints(points);
        question.setQuestion(questionText);
        question.setType(type);
        question.setRemark(remark);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        question.setKeywords(keywords);

        return questionService.add(question);
    }

    private void updateQuestion(Long questionId, Category category, Integer difficulty, Float points, String questionText, String type, String remark, Set<Answer> answers, Set<Keyword> keywords) {
        Question question = new Question();
        question.setId(questionId);
        question.setCategory(category);
        question.setDifficulty(difficulty);
        question.setPoints(points);
        question.setQuestion(questionText);
        question.setType(type);
        question.setRemark(remark);
        question.setUpdatedAt(LocalDateTime.now());
        question.setAnswers(answers);
        question.setKeywords(keywords);

        questionService.update(question);
    }
}
