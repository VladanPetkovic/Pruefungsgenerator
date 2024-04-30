package com.example.backend.db.daos;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Message;
import com.example.backend.db.models.QuestionType;
import lombok.AccessLevel;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuestionTypeDAO implements DAO<QuestionType> {
    @Setter(AccessLevel.PRIVATE)
    ArrayList<QuestionType> questionTypeCache;

    @Override
    public void create(QuestionType type) {
        String insertStmt = "INSERT INTO question_types (name) VALUES (?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, type.getName());
            preparedStatement.executeUpdate();
            setQuestionTypeCache(null);

            SharedData.setOperation(Message.CREATE_QUESTION_TYPE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.CREATE_QUESTION_TYPE_ERROR_MESSAGE);
        }
    }

    @Override
    public ArrayList<QuestionType> readAll() {
        String selectStmt = "SELECT * FROM question_types;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ArrayList<QuestionType> types = new ArrayList<>();
            while (resultSet.next()) {
                QuestionType newType = createModelFromResultSet(resultSet);
                types.add(newType);
            }

            setQuestionTypeCache(types);
            return types;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public QuestionType read(int id) {
        QuestionType questionType = null;

        String readStmt = "SELECT * FROM question_types WHERE id = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    questionType = createModelFromResultSet(result);
                }
                setQuestionTypeCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questionType;
    }

    public QuestionType read(String type_name) {
        QuestionType questionType = null;

        String readStmt = "SELECT * FROM question_types WHERE name = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setString(1, type_name);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    questionType = createModelFromResultSet(result);
                }
                setQuestionTypeCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questionType;
    }

    @Override
    public void update(QuestionType type) {
        String updateStmt = "UPDATE question_types SET name = ? WHERE id = ?";
        Logger.log(getClass().getName(), updateStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
            preparedStatement.setString(1, type.getName());
            preparedStatement.setInt(2, type.getId());
            preparedStatement.executeUpdate();
            setQuestionTypeCache(null);

            SharedData.setOperation(Message.UPDATE_QUESTION_TYPE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.UPDATE_QUESTION_TYPE_ERROR_MESSAGE);
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM question_types WHERE id = ?;";
        // TODO: what happens when the question_type gets deleted for the corresponding questions --> should not get deleted
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt)) {
            // deleting from question_types table
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            setQuestionTypeCache(null);

            SharedData.setOperation(Message.DELETE_QUESTION_TYPE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.DELETE_QUESTION_TYPE_ERROR_MESSAGE);
        }
    }

    @Override
    public QuestionType createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new QuestionType(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
