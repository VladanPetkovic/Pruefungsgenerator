package com.example.backend.app;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;

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
        // Checks if the filePath is null, which means no file was selected
        if (filePath == null) {
            Logger.log(getClass().getName(), "No file selected.", LogLevel.INFO);
            return false;
        }

        Logger.log(getClass().getName(), "Importing data from file: " + filePath, LogLevel.INFO);
        if (!importDataFromFile(filePath)) {
            Logger.log(getClass().getName(), "Failed to import data from file: " + filePath, LogLevel.INFO);
            return false;
        }
        // Returns true if the data import is successful
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
                // Split the line into an array of strings using ";" as the delimiter
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
        // Check if the CSV row contains exactly 10 values
        if (values.length != 10) {
            Logger.log(getClass().getName(), "Invalid CSV format.", LogLevel.INFO);
            return;
        }

        // Parse values from the CSV row, removing any double quotes
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
            // Disable auto-commit mode to start a database transaction
            connection.setAutoCommit(false);
            try {
                StudyProgram studyProgram = StudyProgram.createNewStudyProgramInDatabase(studyProgramName);
                Course course = Course.createNewCourseInDatabase(courseName, studyProgram);
                Category category = Category.createNewCategoryInDatabase(categoryName, course);

                QuestionType questionType = getQuestionType(questionTypeName);

                // Split the answers text by commas to get individual answers
                String[] answersArray = answersText.split(",");
                // Create a list of Answer objects from the split answers
                ArrayList<Answer> answers = createAnswers(answersArray);
                // Split the keywords text by commas to get individual keywords
                String[] keywordsArray = keywordsText.split(",");
                // Create a list of Keyword objects from the split keywords
                ArrayList<Keyword> keywords = createKeywords(keywordsArray);

                int questionId = insertQuestion(category, difficulty, points, questionText, questionType, remark, answers, keywords);

                // Commit the transaction if all operations succeed
                connection.commit();
            } catch (Exception e) {
                // Rollback the transaction in case of an exception
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private QuestionType getQuestionType(String questionTypeName) {
        // Check for existence of a QuestionType with the given name in the repository
        QuestionType newQuestionType = SQLiteDatabaseConnection.QUESTION_TYPE_REPOSITORY.get(questionTypeName);

        // If the QuestionType does not exist, create and add it to the repository
        if (newQuestionType == null) {
            QuestionType addToDatabase = new QuestionType(questionTypeName);
            SQLiteDatabaseConnection.QUESTION_TYPE_REPOSITORY.add(addToDatabase);
            // Retrieve the newly added QuestionType from the repository
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
