package com.example.backend.db.daos;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Answer;
import com.example.backend.db.models.Message;
import lombok.AccessLevel;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AnswerDAO implements DAO<Answer> {
    @Setter(AccessLevel.PRIVATE)
    ArrayList<Answer> answerCache;

    @Override
    public void create(Answer answer) {
        String insertStmt = "INSERT OR IGNORE INTO answers (answer) VALUES (?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, answer.getAnswer());
            preparedStatement.executeUpdate();
            setAnswerCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperationStatus(String.format("{ \"error\": \"%s\" }", Message.CREATE_ANSWER_ERROR_MESSAGE.getMessage()));
        }
    }

    /**
     * ATTENTION: duplicate records will be ignored
     * @param answers ArrayList of Answers, so one or multiple answers can be inserted at once into the answers table.
     */
    public void create(ArrayList<Answer> answers) {
        int answerCounter = 1;

        StringBuilder insertStmt = new StringBuilder("INSERT OR IGNORE INTO answers (answer) VALUES (?)");
        prepareInsertAnswerQuery(insertStmt, answers);
        Logger.log(getClass().getName(), String.valueOf(insertStmt), LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(String.valueOf(insertStmt))) {

            // insert new answers
            for (Answer answer : answers) {
                preparedStatement.setString(answerCounter, answer.getAnswer());
                answerCounter++;
            }

            preparedStatement.executeUpdate();
            setAnswerCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperationStatus(String.format("{ \"error\": \"%s\" }", Message.CREATE_ANSWERS_ERROR_MESSAGE.getMessage()));
        }
    }

    /**
     * ATTENTION: duplicate records are ignored and no error will show up.
     * @param answers ArrayList of Answers, inserting one or multiple answer_ids in the has_aq table
     * @param question_id The id of the Question
     */
    public void addHasAQConnection(ArrayList<Answer> answers, int question_id) {
        int answerCounter = 1;

        StringBuilder insertHasAQStmt = new StringBuilder("" +
                "INSERT OR IGNORE INTO has_aq (fk_answer_id, fk_question_id) " +
                "SELECT (SELECT id FROM answers WHERE answer = ?), ? ");
        prepareInsertConnectionQuery(insertHasAQStmt, answers);
        Logger.log(getClass().getName(), String.valueOf(insertHasAQStmt), LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatementHasAQ = connection.prepareStatement(String.valueOf(insertHasAQStmt))) {

            // insert new connections
            for (Answer answer : answers) {
                preparedStatementHasAQ.setString(answerCounter, answer.getAnswer());
                answerCounter++;
                preparedStatementHasAQ.setInt(answerCounter, question_id);
                answerCounter++;
            }

            preparedStatementHasAQ.executeUpdate();
            setAnswerCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperationStatus(String.format("{ \"error\": \"%s\" }", Message.CREATE_HASAQ_CONNECTION_ERROR_MESSAGE.getMessage()));
        }
    }

    public void prepareInsertAnswerQuery(StringBuilder insertStmt, ArrayList<Answer> answers) {
        if (answers.size() > 1) {
            // -1, because we already have one insert
            for (int i = 0; i < answers.size() - 1; i++) {
                insertStmt.append(", (?)");
            }
        }

        insertStmt.append(";");
    }

    public void prepareInsertConnectionQuery(StringBuilder insertStmt, ArrayList<Answer> answers) {
        if (answers.size() > 1) {
            // -1, because we already have one insert
            for (int i = 0; i < answers.size() - 1; i++) {
                insertStmt.append("UNION ALL SELECT (SELECT id FROM answers WHERE answer = ?), ? ");
            }
        }

        insertStmt.append(";");
    }

    @Override
    public ArrayList<Answer> readAll() {
        String selectStmt = "SELECT * FROM answers;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ArrayList<Answer> answers = new ArrayList<>();
            while (resultSet.next()) {
                Answer newAnswer = createModelFromResultSet(resultSet);
                answers.add(newAnswer);
            }

            setAnswerCache(answers);
            return answers;

        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperationStatus(String.format("{ \"error\": \"%s\" }", Message.READALL_ANSWERS_ERROR_MESSAGE.getMessage()));
        }

        return null;
    }

    @Override
    public Answer read(int id) {
        Answer answer = null;

        String readStmt = "SELECT * FROM answers WHERE id = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    answer = createModelFromResultSet(result);
                }
                setAnswerCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperationStatus(String.format("{ \"error\": \"%s\" }", Message.READ_ANSWER_BY_ID_ERROR_MESSAGE.getMessage()));
        }

        return answer;
    }

    public Answer read(String answer) {
        Answer returnAnswer = null;

        String readStmt = "SELECT * FROM answers WHERE answer = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setString(1, answer);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    returnAnswer = createModelFromResultSet(result);
                }
                setAnswerCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperationStatus(String.format("{ \"error\": \"%s\" }", Message.READ_ANSWER_BY_NAME_ERROR_MESSAGE.getMessage()));
        }

        return returnAnswer;
    }

    @Override
    public void update(Answer answer) {
        String updateStmt = "UPDATE answers SET answer = ? WHERE id = ?";
        Logger.log(getClass().getName(), updateStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
            preparedStatement.setString(1, answer.getAnswer());
            preparedStatement.setInt(2, answer.getId());
            preparedStatement.executeUpdate();
            setAnswerCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperationStatus(String.format("{ \"error\": \"%s\" }", Message.UPDATE_ANSWER_ERROR_MESSAGE.getMessage()));
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM answers WHERE id = ?;";
        String deleteHasAQStmt = "DELETE FROM has_aq WHERE fk_answer_id = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPpStmt = connection.prepareStatement(deleteHasAQStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            secondPpStmt.setInt(1, id);
            secondPpStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperationStatus(String.format("{ \"error\": \"%s\" }", Message.DELETE_ANSWER_ERROR_MESSAGE.getMessage()));
        }
    }

    @Override
    public Answer createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Answer(
                resultSet.getInt("id"),
                resultSet.getString("answer")
        );
    }
}
