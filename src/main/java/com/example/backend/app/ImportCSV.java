package com.example.backend.app;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import com.example.backend.db.models.Image;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

public class ImportCSV {
    private String filePath;

    public ImportCSV(String filePath) {
        this.filePath = filePath;
    }

    public boolean importData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header line
            if ((line = reader.readLine()) == null) {
                Logger.log(getClass().getName(), "CSV file is empty.", LogLevel.INFO);
                return false;
            }
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                importRow(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void importRow(String[] values) {
        if (values.length != 10) {
            Logger.log(getClass().getName(), "Invalid CSV format.", LogLevel.INFO);
            return;
        }

        // Parse values
        String questionText = values[0].replace("\"", "");
        String categoryName = values[1].replace("\"", "");
        int difficulty = Integer.parseInt(values[2]);
        float points = Float.parseFloat(values[3]);
        String questionTypeName = values[4].replace("\"", "");
        String remark = values[5].replace("\"", "");
        String answersText = values[6].replace("\"", "");
        String keywordsText = values[7].replace("\"", "");
        String courseName = values[8].replace("\"", "");
        String studyProgramName = values[9].replace("\"", "");

        // Handle database operations
        try (Connection connection = SQLiteDatabaseConnection.connect()) {
            connection.setAutoCommit(false);  // Start transaction
            try {
                StudyProgram studyProgram = getStudyProgram(studyProgramName);
                Course course = Course.createNewCourseInDatabase(courseName, studyProgram);
                Category category = Category.createNewCategoryInDatabase(categoryName, course);

                QuestionType questionType = getQuestionType(questionTypeName);

                String[] answers = answersText.split(",");
                ArrayList<Answer> newAnswers = createAnswers(answers);
                String[] keywords = keywordsText.split(",");
                ArrayList<Keyword> newKeywords = createKeywords(keywords);

                int questionId = insertQuestion(category, difficulty, points, questionText, questionType, remark, newAnswers, newKeywords);

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private QuestionType getQuestionType(String questionTypeName) {
        // check for existence
        QuestionType newQuestionType = SQLiteDatabaseConnection.QUESTION_TYPE_REPOSITORY.get(questionTypeName);

        if (newQuestionType == null) {
            QuestionType addToDatabase = new QuestionType(questionTypeName);
            SQLiteDatabaseConnection.QUESTION_TYPE_REPOSITORY.add(addToDatabase);
            newQuestionType = SQLiteDatabaseConnection.QUESTION_TYPE_REPOSITORY.get(questionTypeName);
        }
        return newQuestionType;
    }

    private int insertQuestion(Category category, int difficulty, float points, String questionText, QuestionType questionType, String remark, ArrayList<Answer> answers,
                               ArrayList<Keyword> keywords /*, ArrayList<Image> images*/) {
        Question question = new Question();
        question.setCategory(category);
        question.setDifficulty(difficulty);
        question.setPoints(points);
        question.setQuestion(questionText);
        question.setType(questionType);
        question.setRemark(remark);
        question.setCreated_at(Timestamp.from(Instant.now()));
        question.setUpdated_at(Timestamp.from(Instant.now()));
        question.setAnswers(answers);
        question.setKeywords(keywords);
        //question.setImages(images);

        Question.createNewQuestionInDatabase(question);
        return question.getId();
    }

    private Course getCourse(String courseName) {
        // check for existence
        Course newCourse = SQLiteDatabaseConnection.courseRepository.get(courseName);

        if (newCourse == null) {
            Course addToDataBase = new Course();
            addToDataBase.setName(courseName);
            SQLiteDatabaseConnection.courseRepository.add(addToDataBase);
            newCourse = SQLiteDatabaseConnection.courseRepository.get(courseName);
        }
        return newCourse;
    }

    private StudyProgram getStudyProgram(String studyProgramName) {
        // check for existence
        StudyProgram newStudyProgram = SQLiteDatabaseConnection.studyProgramRepository.get(studyProgramName);

        if (newStudyProgram == null) {
            StudyProgram addToDataBase = new StudyProgram();
            addToDataBase.setName(studyProgramName);
            SQLiteDatabaseConnection.studyProgramRepository.add(addToDataBase);
            newStudyProgram = SQLiteDatabaseConnection.studyProgramRepository.get(studyProgramName);
        }
        return newStudyProgram;
    }

    private ArrayList<Answer> createAnswers(String[] answers) {
        // create empty ArrayList
        ArrayList<Answer> newAnswers = new ArrayList<Answer>();

        for (String answerText : answers) {
            Answer answer = new Answer(answerText.trim());
            newAnswers.add(answer);
        }

        return newAnswers;
    }

    private ArrayList<Keyword> createKeywords(String[] keywords) {
        // create empty ArrayList
        ArrayList<Keyword> newKeywords = new ArrayList<Keyword>();

        for (String keywordText : keywords) {
            Keyword keyword = Keyword.createNewKeywordInDatabase(keywordText.trim());
            newKeywords.add(keyword);
        }

        return newKeywords;
    }


}
