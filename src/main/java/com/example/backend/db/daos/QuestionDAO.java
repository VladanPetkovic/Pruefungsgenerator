package com.example.backend.db.daos;

import java.sql.*;
import java.util.ArrayList;

import com.example.backend.db.models.Image;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class QuestionDAO implements DAO<Question> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Question> questionsCache;

    public QuestionDAO() {
        //setConnection(connection);
    }

    @Override
    public void create(Question question) {
        String insertStmt =
            "INSERT INTO Questions " +
            "(FK_Topic_ID, Difficulty, Points, Question, MultipleChoice, Language, Remarks, Answers) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            // use the retrieved TopicID
            preparedStatement.setInt(1, question.getTopic().getTopic_id());
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
    }

    @Override
    public ArrayList<Question> readAll() {
        ArrayList<Question> questions = new ArrayList<>();

        String selectQuestionsStmt = "SELECT * FROM Questions;";

        try {
            PreparedStatement questionsStatement = getConnection().prepareStatement(selectQuestionsStmt);
            ResultSet questionsResultSet = questionsStatement.executeQuery();

            while (questionsResultSet.next()) {
                int question_id = questionsResultSet.getInt("QuestionID");
                // getting topics
                TopicDAO topicDAO = new TopicDAO();
                Topic newTopic = topicDAO.read(questionsResultSet.getInt("FK_Topic_ID"));

                // getting keywords --> maybe "keywords" are lost and not more accessible when asking for "question"
                KeywordDAO keywordDAO = new KeywordDAO();
                ArrayList<Keyword> keywords = keywordDAO.readAllForOneQuestion(question_id);

                // getting images
                ImageDAO imageDAO = new ImageDAO();
                ArrayList<Image> images = imageDAO.readAllForOneQuestion(question_id);

                Question question = new Question(
                        question_id,
                        new Topic(newTopic),
                        questionsResultSet.getInt("Difficulty"),
                        questionsResultSet.getInt("Points"),
                        questionsResultSet.getString("Question"),
                        questionsResultSet.getInt("MultipleChoice"),
                        questionsResultSet.getString("Language"),
                        questionsResultSet.getString("Remarks"),
                        questionsResultSet.getString("Answers"),
                        keywords,
                        images
                );
                questions.add(question);
            }

            setQuestionsCache(questions);
            getConnection().close();
            return questions;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Question> readAll(String topic) {
        ArrayList<Question> questions = new ArrayList<>();

        String selectQuestionsStmt = "SELECT * FROM Questions WHERE FK_Topic_ID = ?;"; // Query to get Questions

        // get the topic_id
        TopicDAO topicDAO = new TopicDAO();
        Topic question_topic = topicDAO.read(topic);

        if(question_topic != null) {
            try {
                // get questions for the TopicID
                PreparedStatement questionsStatement = getConnection().prepareStatement(selectQuestionsStmt);
                questionsStatement.setInt(1, question_topic.getTopic_id());

                ResultSet questionsResultSet = questionsStatement.executeQuery();

                while (questionsResultSet.next()) {
                    int question_id = questionsResultSet.getInt("QuestionID");
                    // getting keywords --> maybe "keywords" are lost and not more accessible when asking for "question"
                    KeywordDAO keywordDAO = new KeywordDAO();
                    ArrayList<Keyword> keywords = keywordDAO.readAllForOneQuestion(question_id);

                    // getting images
                    ImageDAO imageDAO = new ImageDAO();
                    ArrayList<Image> images = imageDAO.readAllForOneQuestion(question_id);

                    Question question = new Question(
                        question_id,
                        new Topic(question_topic),
                        questionsResultSet.getInt("Difficulty"),
                        questionsResultSet.getInt("Points"),
                        questionsResultSet.getString("Question"),
                        questionsResultSet.getInt("MultipleChoice"),
                        questionsResultSet.getString("Language"),
                        questionsResultSet.getString("Remarks"),
                        questionsResultSet.getString("Answers"),
                        keywords,
                        images
                    );
                    questions.add(question);
                }

                setQuestionsCache(questions);
                getConnection().close();
                return questions;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    @Override
    public Question read(int question_id) {
        Question question = null;

        String selectStmt = "SELECT * FROM Questions WHERE QuestionID = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, question_id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // getting topic
                TopicDAO topicDAO = new TopicDAO();
                Topic question_topic = topicDAO.read(resultSet.getInt("FK_Topic_ID"));

                // getting keywords
                KeywordDAO keywordDAO = new KeywordDAO();
                ArrayList<Keyword> keywords = keywordDAO.readAllForOneQuestion(question_id);

                // getting images
                ImageDAO imageDAO = new ImageDAO();
                ArrayList<Image> images = imageDAO.readAllForOneQuestion(question_id);

                question = new Question(
                        question_id,
                        question_topic,
                        resultSet.getInt("Difficulty"),
                        resultSet.getInt("Points"),
                        resultSet.getString("Question"),
                        resultSet.getInt("MultipleChoice"),
                        resultSet.getString("Language"),
                        resultSet.getString("Remarks"),
                        resultSet.getString("Answers"),
                        keywords,
                        images
                );
                getConnection().close();
                setQuestionsCache(null);
            } else {
                System.out.println("Question not found with ID: " + question_id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return question;
    }
    @Override
    public void update(Question question) {
        String updateStmt =
                "UPDATE Questions " +
                "SET FK_Topic_ID = ?, Difficulty = ?, Points = ?, Question = ?, " +
                "MultipleChoice = ?, Language = ?, Remarks = ?, Answers = ? " +
                "WHERE QuestionID = ?";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setInt(1, question.getTopic().getTopic_id());
            preparedStatement.setInt(2, question.getDifficulty());
            preparedStatement.setInt(3, question.getPoints());
            preparedStatement.setString(4, question.getQuestionString());
            preparedStatement.setInt(5, question.getMultipleChoice());
            preparedStatement.setString(6, question.getLanguage());
            preparedStatement.setString(7, question.getRemarks());
            preparedStatement.setString(8, question.getAnswers());
            preparedStatement.setInt(9, question.getQuestion_id());
            preparedStatement.execute();
            getConnection().close();
            setQuestionsCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Questions WHERE QuestionID = ?;";
        String deleteHasIQStmt = "DELETE FROM hasIQ WHERE QuestionID = ?;";
        String deleteHasKQStmt = "DELETE FROM hasKQ WHERE QuestionID = ?;";
        try {
            // deleting from Courses table
            PreparedStatement preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            // deleting from hasCT table
            PreparedStatement secondPpStmt = getConnection().prepareStatement(deleteHasIQStmt);
            secondPpStmt.setInt(1, id);
            secondPpStmt.execute();
            // deleting from hasSC table
            PreparedStatement thirdPpStmt = getConnection().prepareStatement(deleteHasKQStmt);
            thirdPpStmt.setInt(1, id);
            thirdPpStmt.execute();

            getConnection().close();
            setQuestionsCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
