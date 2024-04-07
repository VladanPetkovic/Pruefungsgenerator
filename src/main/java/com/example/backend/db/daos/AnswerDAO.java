package com.example.backend.db.daos;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Answer;
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
        String insertStmt = "INSERT INTO answers (answer) VALUES (?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, answer.getAnswer());
            preparedStatement.executeUpdate();
            setAnswerCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM answers WHERE id = ?;";
        String deleteHasAQStmt = "DELETE FROM has_aq WHERE fk_answer_id = ?;";
        // TODO: what happens with the question, that has fk_answer_id deleted?

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPpStmt = connection.prepareStatement(deleteHasAQStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            secondPpStmt.setInt(1, id);
            secondPpStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Answer createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Answer(
                resultSet.getInt("id"),
                resultSet.getString("answer"));
    }
}
