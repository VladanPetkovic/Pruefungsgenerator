package com.example.backend.app;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

public class ImportCSV {
    private String filePath;

    public ImportCSV() {
        this.filePath = selectFile();
    }

    private String selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile != null && selectedFile.exists()) {
                return selectedFile.getAbsolutePath();
            } else {
                System.out.println("Selected file does not exist.");
            }
        } else {
            System.out.println("File selection cancelled.");
        }
        return null;
    }

    public boolean importData() {
        if (filePath == null) {
            Logger.log(getClass().getName(), "No file selected.", LogLevel.INFO);
            return false;
        }

        Logger.log(getClass().getName(), "Importing data from file: " + filePath, LogLevel.INFO);
        if (!importDataFromFile(filePath)) {
            Logger.log(getClass().getName(), "Failed to import data from file: " + filePath, LogLevel.INFO);
            return false;
        }
        return true;
    }

    private boolean importDataFromFile(String filePath) {
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
                StudyProgram studyProgram = StudyProgram.createNewStudyProgramInDatabase(studyProgramName);
                Course course = Course.createNewCourseInDatabase(courseName, studyProgram);
                Category category = Category.createNewCategoryInDatabase(categoryName, course);

                QuestionType questionType = getQuestionType(questionTypeName);

                String[] answersArray = answersText.split(",");
                ArrayList<Answer> answers = createAnswers(answersArray);
                String[] keywordsArray = keywordsText.split(",");
                ArrayList<Keyword> keywords = createKeywords(keywordsArray);

                int questionId = insertQuestion(category, difficulty, points, questionText, questionType, remark, answers, keywords);

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
