package com.example.backend.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.backend.db.models.Question;
import com.example.backend.db.models.Topic;
import com.example.backend.db.repositories.KeywordRepository;
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
            preparedStatement.setString(1, question.getTopic().getTopic());

            ResultSet resultSet = preparedStatement.executeQuery();

            // check if a record was found
            if (resultSet.next()) {
                int topicId = resultSet.getInt("TopicID");

                String insertStmt = "INSERT into Questions (FK_Topic_ID, Difficulty, Points, Question, MultipleChoice, Language, Remarks, Answers) VALUES (?, ?, ?, ?, ?, ?, ?);";
                try {
                    preparedStatement = getConnection().prepareStatement(insertStmt);
                    // use the retrieved TopicID
                    preparedStatement.setInt(1, topicId);
                    preparedStatement.setInt(2, question.getDifficulty());
                    preparedStatement.setInt(3, question.getPoints());
                    preparedStatement.setString(4, question.getQuestionString());
                    preparedStatement.setInt(5, question.getMultipleChoice());
                    preparedStatement.setString(6, question.getLanguage());
                    preparedStatement.setString(7, question.getRemarks());
                    preparedStatement.setString(8, question.getAnswers());

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
    public ArrayList<Question> readAll() {
        return null;
    }

    public ArrayList<Question> readAll(String subject) {
        ArrayList<Question> questions = new ArrayList<>();

        if (questionsCache != null) {
            System.out.println("TEST");
            return questionsCache;
        }

        String selectTopicIdStmt = "SELECT TopicID FROM Topics WHERE Topic = ?;"; // Query to get TopicID
        String selectQuestionsStmt = "SELECT * FROM Questions WHERE FK_Topic_ID = ?;"; // Query to get Questions

        try {
            // step 1: get TopicID
            PreparedStatement topicIdStatement = getConnection().prepareStatement(selectTopicIdStmt);
            topicIdStatement.setString(1, subject);

            ResultSet topicIdResultSet = topicIdStatement.executeQuery();

            if (topicIdResultSet.next()) {
                int topicId = topicIdResultSet.getInt("TopicID");

                // step 2: get questions for the TopicID
                PreparedStatement questionsStatement = getConnection().prepareStatement(selectQuestionsStmt);
                questionsStatement.setInt(1, topicId);

                ResultSet questionsResultSet = questionsStatement.executeQuery();

                while (questionsResultSet.next()) {
                    Question question = new Question(
                            new Topic(subject),
                            questionsResultSet.getInt("Difficulty"),
                            questionsResultSet.getInt("Points"),
                            questionsResultSet.getString("Question"),
                            questionsResultSet.getInt("MultipleChoice"),
                            questionsResultSet.getString("Language"),
                            questionsResultSet.getString("Remarks"),
                            questionsResultSet.getString("Answers"),
                            // keywords müssen für die jeweilige frage abgerufen werden und hier eingefügt werden
                            // // new KeywordRepository().getAll(questionsResultSet.getInt("Question_ID")),
                            // arraylist von Images muss für die jeweilige Frage abgerufen und hinzugefügt werden
                            // --> circa so:
                            // // new ImageRepository().getAll(questionsResultSet.getInt("Question_ID"))
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
        String selectStmt = "SELECT * FROM Questions WHERE QuestionID = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Question question = new Question(
                        // topic muss abgerufen und hier eingefügt werden
                        resultSet.getInt("Difficulty"),
                        resultSet.getInt("Points"),
                        resultSet.getString("Question"),
                        resultSet.getInt("MultipleChoice"),
                        resultSet.getString("Language"),
                        resultSet.getString("Remarks"),
                        // keywords müssen für die jeweilige frage abgerufen werden und hier eingefügt werden
                        // images auch --> siehe wie oben
                );
                getConnection().close();
                return question;
            } else {
                System.out.println("Question not found with ID: " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    public void update(Question question) {

    }
    @Override
    public void delete(int id) {

    }
}
