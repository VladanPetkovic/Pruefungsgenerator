package com.example.application.backend.app;

import com.example.application.backend.db.models.*;
import com.example.application.backend.db.services.*;
import lombok.Getter;

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
    private final String filePath;
    private final boolean isCreateMode;
    private final String importTargetStudyProgram;
    private final String importTargetCourse;
    @Getter
    private String errorMessage;

    public ImportCSV(String filePath,
                     StudyProgramService studyProgramService,
                     CourseService courseService,
                     QuestionService questionService,
                     CategoryService categoryService,
                     AnswerService answerService,
                     KeywordService keywordService,
                     boolean isCreateMode,
                     String importTargetStudyProgram,
                     String importTargetCourse
                     ) {
        this.filePath = filePath;
        this.studyProgramService = studyProgramService;
        this.courseService = courseService;
        this.questionService = questionService;
        this.categoryService = categoryService;
        this.answerService = answerService;
        this.keywordService = keywordService;
        this.isCreateMode = isCreateMode;
        this.importTargetStudyProgram = importTargetStudyProgram;
        this.importTargetCourse = importTargetCourse;
    }

    public void setOptions() {

    }

    /**
     * This function imports questions.
     *
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
            // TODO: write the errorMessage for every use-case
            return false;
        }

        Logger.log(getClass().getName(), "Importing data from file: " + filePath, LogLevel.INFO);
        if (!importDataFromFile(filePath)) {
            Logger.log(getClass().getName(), "Failed to import data from file: " + filePath, LogLevel.ERROR);
            return false;
        }

        return true;
    }


    // TODO: FIRST: CHECK THE FILE - SO WE CAN PROCEED WITHOUT ESTABLISHING A DB-CONNECTION
    // OR: we create a new method in one service and use the @Transactional keyword
    private boolean checkingDataFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine();
            if (header == null || !header.contains("question_id")) {
                Logger.log(getClass().getName(), "Invalid or missing header in CSV.", LogLevel.WARN);
                errorMessage = "Invalid or missing header in CSV."; // TODO: continue with assignments like this
                return false;
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(";");

                // question_id; question_text; category_name; difficulty; points; question_type; remarks; answers; keywords; course_name; course_number; studyprogram_name
                if (fields.length != 12) {
                    Logger.log(getClass().getName(), "Invalid format - expected 12 fields per line at line " + lineNumber, LogLevel.WARN);
                    return false;
                }

                try {
                    // Check data types and values for each field
                    Long.parseLong(fields[0]); // question_id
                    if (fields[1].trim().isEmpty()) throw new IllegalArgumentException("Question text cannot be empty");
                    if (fields[2].trim().isEmpty()) throw new IllegalArgumentException("Category name cannot be empty");

                    // Ensure difficulty is non-null and within range
                    int difficulty = Integer.parseInt(fields[3].trim());
                    if (difficulty < 1 || difficulty > 10)
                        throw new IllegalArgumentException("Difficulty must be between 1 and 10");

                    // Ensure points is non-null and parseable
                    float points = Float.parseFloat(fields[4].trim().replace(",", "."));
                    if (points < 0) throw new IllegalArgumentException("Points cannot be negative");

                    if (fields[5].trim().isEmpty()) throw new IllegalArgumentException("Question type cannot be empty");
                    
                    /*
                    if (!fields[7].contains("|")) throw new IllegalArgumentException("Answers should be separated by '|'");
                    if (!fields[8].contains("|")) throw new IllegalArgumentException("Keywords should be separated by '|'");
                     */

                    if (fields[9].trim().isEmpty()) throw new IllegalArgumentException("Course name cannot be empty");
                    Integer.parseInt(fields[10]); // course_number

                    if (fields[11].trim().isEmpty())
                        throw new IllegalArgumentException("Study program name cannot be empty");

                } catch (IllegalArgumentException e) {
                    Logger.log(getClass().getName(), "Data validation error at line " + lineNumber + ": " + e.getMessage(), LogLevel.WARN);
                    return false;
                }
            }

            return true; // All checks passed
        } catch (IOException e) {
            Logger.log(getClass().getName(), "Error reading file for data check: " + e.getMessage(), LogLevel.ERROR);
            return false;
        }
    }


    private boolean importDataFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read the first line, which is expected to be the header, and skip it
            reader.readLine();
            String line;
            // Loop through each subsequent line in the CSV file
            while ((line = reader.readLine()) != null) {
                // TODO: check, if ';' is between '" "'
                if (line.contains(";")) { // Basic delimiter check
                    // Split the line into an array of strings using ";" as the delimiter
                    String[] values = line.split(";");
                    // question_id; question_text; category_name; difficulty; points; question_type; remarks; answers; keywords; course_name; course_number; studyprogram_name
                    if (values.length == 12) {
                        importRow(values);
                    } else {
                        Logger.log(getClass().getName(), "Row has incorrect number of fields, skipping line.", LogLevel.WARN);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // Return true to indicate that the data import was successful
        return true;
    }

    private void importRow(String[] values) {

        ParsedCSVRow parsedRow = parseCSVRow(values);

        if (isCreateMode) {
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
        Set<Keyword> savedKeywords = saveKeywords(keywords, course);

        if (isNew) {
            Question newQuestion = insertQuestion(category, parsedRow.getDifficulty(), parsedRow.getPoints(), parsedRow.getQuestionText(), parsedRow.getType(), parsedRow.getRemark(), savedKeywords);
            if (newQuestion != null) {
                answerService.addAnswers(newQuestion.getId(), answers);
            }
        } else {
            updateQuestion(parsedRow.getQuestionId(), category, parsedRow.getDifficulty(), parsedRow.getPoints(), parsedRow.getQuestionText(), parsedRow.getType(), parsedRow.getRemark(), answers, savedKeywords);
        }
    }

    // TODO: refactor to avoid n+1 select
    private Set<Keyword> saveKeywords(Set<Keyword> keywords, Course course) {
        Set<Keyword> savedKeywords = new HashSet<>();
        for (Keyword keyword : keywords) {
            savedKeywords.add(keywordService.add(keyword, course));
        }
        return savedKeywords;
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
