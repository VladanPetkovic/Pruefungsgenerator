package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class QuestionDAO implements DAO<Question> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Question> questionsCache;

    public QuestionDAO() {
        // setConnection(connection);
    }

    @Override
    public void create(Question question) {
        String insertStmt =
                "INSERT INTO Questions " +
                        "(FK_Topic_ID, Difficulty, Points, Question, MultipleChoice, Language, Remarks, Answers) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setInt(1, question.getTopic().getTopic_id());
            preparedStatement.setInt(2, question.getDifficulty());
            preparedStatement.setInt(3, question.getPoints());
            preparedStatement.setString(4, question.getQuestionString());
            preparedStatement.setInt(5, question.getMultipleChoice());
            preparedStatement.setString(6, question.getLanguage());
            preparedStatement.setString(7, question.getRemarks());
            preparedStatement.setString(8, question.getAnswers());

            preparedStatement.executeUpdate();
            setQuestionsCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Question> readAll() {
        ArrayList<Question> questions = new ArrayList<>();

        String selectQuestionsStmt = "SELECT * FROM Questions;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt);
             ResultSet questionsResultSet = questionsStatement.executeQuery()) {

            while (questionsResultSet.next()) {
                int question_id = questionsResultSet.getInt("QuestionID");

                TopicDAO topicDAO = new TopicDAO();
                Topic newTopic = topicDAO.read(questionsResultSet.getInt("FK_Topic_ID"));

                KeywordDAO keywordDAO = new KeywordDAO();
                ArrayList<Keyword> keywords = keywordDAO.readAllForOneQuestion(question_id);

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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    public ArrayList<Question> readAll(String topic) {
        ArrayList<Question> questions = new ArrayList<>();

        String selectQuestionsStmt = "SELECT * FROM Questions WHERE FK_Topic_ID = ?;";

        TopicDAO topicDAO = new TopicDAO();
        Topic questionTopic = topicDAO.read(topic);

        if (questionTopic != null) {
            try (Connection connection = SQLiteDatabaseConnection.connect();
                 PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt)) {

                questionsStatement.setInt(1, questionTopic.getTopic_id());
                try (ResultSet questionsResultSet = questionsStatement.executeQuery()) {
                    while (questionsResultSet.next()) {
                        int question_id = questionsResultSet.getInt("QuestionID");

                        KeywordDAO keywordDAO = new KeywordDAO();
                        ArrayList<Keyword> keywords = keywordDAO.readAllForOneQuestion(question_id);

                        ImageDAO imageDAO = new ImageDAO();
                        ArrayList<Image> images = imageDAO.readAllForOneQuestion(question_id);

                        Question question = new Question(
                                question_id,
                                new Topic(questionTopic),
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
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return questions;
    }

    public ArrayList<Question> readAll(ArrayList<SearchObject<?>> searchOptions) {
        ArrayList<Question> questions = new ArrayList<>();
        // making a list of values for the preparedStmt
        ArrayList<Object> listForPreparedStmt = new ArrayList<>();
        // if topic is present, then set topic
        TopicDAO topicDAO = new TopicDAO();
        Topic questionTopic = null;

        StringBuilder selectQuestionsStmt = new StringBuilder("SELECT * FROM Questions WHERE");

        for(SearchObject<?> searchObject : searchOptions) {
            // append only objects with set flag and a columnName (otherwise we would insert into non-existing columns)
            if(searchObject.isSet() && !Objects.equals(searchObject.getColumn_name(), "")) {
                selectQuestionsStmt.append(" ").append(searchObject.getColumn_name()).append(" = ?,");
                listForPreparedStmt.add(searchObject.getValueOfObject());
            }

            // set topic, if it is set
            if(Objects.equals(searchObject.getColumn_name(), "FK_TOPIC_ID") && searchObject.isSet()) {
                questionTopic = (Topic) searchObject.getValueOfObject();
            }
        }

        // replace last char ',' to ';'
        selectQuestionsStmt.deleteCharAt(selectQuestionsStmt.length() - 1);
        selectQuestionsStmt.append(';');

        // TODO: change this to get only questions for the course
        if(listForPreparedStmt.isEmpty()) {
            return readAll();
        }

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement questionsStatement = connection.prepareStatement(String.valueOf(selectQuestionsStmt))) {

            // insert into prepared stmt
            int count = 1;
            for(Object prepObjects : listForPreparedStmt) {
                if(prepObjects instanceof String) {
                    questionsStatement.setString(count, (String) prepObjects);
                } else if(prepObjects instanceof Integer) {
                    questionsStatement.setInt(count, (int) prepObjects);
                }

                count++;
            }

            try (ResultSet questionsResultSet = questionsStatement.executeQuery()) {
                while (questionsResultSet.next()) {
                    int question_id = questionsResultSet.getInt("QuestionID");

                    questionTopic = topicDAO.readForQuestion(question_id);

                    KeywordDAO keywordDAO = new KeywordDAO();
                    ArrayList<Keyword> keywords = keywordDAO.readAllForOneQuestion(question_id);

                    ImageDAO imageDAO = new ImageDAO();
                    ArrayList<Image> images = imageDAO.readAllForOneQuestion(question_id);

                    Question question = new Question(
                            question_id,
                            new Topic(questionTopic),
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    @Override
    public Question read(int questionId) {
        Question question = null;

        String selectStmt = "SELECT * FROM Questions WHERE QuestionID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {

            preparedStatement.setInt(1, questionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    TopicDAO topicDAO = new TopicDAO();
                    Topic questionTopic = topicDAO.read(resultSet.getInt("FK_Topic_ID"));

                    KeywordDAO keywordDAO = new KeywordDAO();
                    ArrayList<Keyword> keywords = keywordDAO.readAllForOneQuestion(questionId);

                    ImageDAO imageDAO = new ImageDAO();
                    ArrayList<Image> images = imageDAO.readAllForOneQuestion(questionId);

                    question = new Question(
                            questionId,
                            questionTopic,
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
                    setQuestionsCache(null);
                } else {
                    System.out.println("Question not found with ID: " + questionId);
                }
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
                        "WHERE QuestionID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {

            preparedStatement.setInt(1, question.getTopic().getTopic_id());
            preparedStatement.setInt(2, question.getDifficulty());
            preparedStatement.setInt(3, question.getPoints());
            preparedStatement.setString(4, question.getQuestionString());
            preparedStatement.setInt(5, question.getMultipleChoice());
            preparedStatement.setString(6, question.getLanguage());
            preparedStatement.setString(7, question.getRemarks());
            preparedStatement.setString(8, question.getAnswers());
            preparedStatement.setInt(9, question.getQuestion_id());

            preparedStatement.executeUpdate();
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
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasIQStmt);
             PreparedStatement thirdPreparedStatement = connection.prepareStatement(deleteHasKQStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.executeUpdate();

            thirdPreparedStatement.setInt(1, id);
            thirdPreparedStatement.executeUpdate();

            setQuestionsCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Question createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return null;
    }
}
