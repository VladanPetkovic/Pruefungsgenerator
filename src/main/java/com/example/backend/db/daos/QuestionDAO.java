package com.example.backend.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.backend.app.Question;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class QuestionDAO implements DAO<Question> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Question> questionsCache;

    public QuestionDAO(Connection connection) {
        setConnection(connection);
    }

    @Override
    public void create(Question question) {

        // Suche nach FK_Topic_ID muss implementiert werden
        // die gefundene Topic_ID muss im INSERT Statement eingefügt werden

        String searchStmt = "SELECT TopicID FROM Topics WHERE Topic = ?; ";
        try{
            PreparedStatement preparedStatement = getConnection().prepareStatement(searchStmt);
            preparedStatement.setString(1, question.getTopic());

            ResultSet resultSet = preparedStatement.executeQuery();

            // check if a record was found
            if (resultSet.next()) {
                int topicId = resultSet.getInt("TopicID");

                String insertStmt = "INSERT into Questions (FK_Topic_ID, Difficulty, Points, Question, MultipleChoice, Language, Remarks) VALUES (?, ?, ?, ?, ?, ?, ?);";
                try {
                    preparedStatement = getConnection().prepareStatement(insertStmt);
                    // use the retrieved TopicID
                    preparedStatement.setInt(1, topicId);
                    preparedStatement.setInt(2, question.getDifficulty());
                    preparedStatement.setInt(3, question.getPoints());
                    preparedStatement.setString(4, question.getQuestionString());
                    preparedStatement.setInt(5, question.getMultipleChoice());
                    preparedStatement.setBoolean(6, question.getLanguage());
                    preparedStatement.setString(7, question.getRemarks());

                    preparedStatement.execute();
                    getConnection().close();
                    setQuestionsCache(null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // handle the case when the topic is not found in the db
                // in this case we should create a new entry in the Topics table
                System.out.println("Topic not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Question> readAll(String subject) {
        ArrayList<Question> questions = new ArrayList<>();

        if (questionsCache != null) {
            System.out.println("TEST");
            return questionsCache;
        }

        String selectTopicIdStmt = "SELECT TopicID FROM Topics WHERE Topic = ?;"; // Query to get TopicID
        String selectQuestionsStmt = "SELECT * FROM Questions WHERE FK_Topic_ID = ?;"; // Query to get Questions

        try {
            // Step 1: Get TopicID
            PreparedStatement topicIdStatement = getConnection().prepareStatement(selectTopicIdStmt);
            topicIdStatement.setString(1, subject);

            ResultSet topicIdResultSet = topicIdStatement.executeQuery();

            if (topicIdResultSet.next()) {
                int topicId = topicIdResultSet.getInt("TopicID");

                // Step 2: Get Questions for the TopicID
                PreparedStatement questionsStatement = getConnection().prepareStatement(selectQuestionsStmt);
                questionsStatement.setInt(1, topicId);

                ResultSet questionsResultSet = questionsStatement.executeQuery();

                while (questionsResultSet.next()) {
                    Question question = new Question(
                            questionsResultSet.getInt("Difficulty"),
                            questionsResultSet.getInt("Points"),
                            questionsResultSet.getString("Question"),
                            questionsResultSet.getBoolean("MultipleChoice"),
                            subject,
                            // keywords müssen für die jeweilige frage abgerufen werden und hier eingefügt werden
                            questionsResultSet.getString("Language"),
                            questionsResultSet.getString("Remarks"),
                    );
                    questions.add(question);
                }

                setQuestionsCache(questions);
                getConnection().close();
                return questions;
            } else {
                System.out.println("Topic not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public Question read(int id) {
        return null;
    }

    @Override
    public void update(Question user) {

    }

    @Override
    public void delete(int id) {

    }
}

