package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Keyword;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;

public class KeywordDAO implements DAO<Keyword> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Keyword> keywordCache;

    @Override
    public void create(Keyword keyword) {
        String insertStmt = "INSERT INTO Keywords (Keyword) VALUES (?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, keyword.getKeyword_text());
            preparedStatement.executeUpdate();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Keyword> readAll() {
        String selectStmt = "SELECT KeywordID, Keyword FROM Keywords;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ArrayList<Keyword> keywords = new ArrayList<>();
            while (resultSet.next()) {
                Keyword newKeyword = createModelFromResultSet(resultSet);
                keywords.add(newKeyword);
            }

            setKeywordCache(keywords);
            return keywords;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Keyword> readAllForOneQuestion(int questionId) {
        String selectStmt =
                "SELECT Keywords.KeywordID, Keyword " +
                        "FROM Keywords " +
                        "JOIN hasKQ ON Keywords.KeywordID = hasKQ.KeywordID " +
                        "JOIN Questions ON hasKQ.QuestionID = Questions.QuestionID " +
                        "WHERE hasKQ.QuestionID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {
            preparedStatement.setInt(1, questionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ArrayList<Keyword> keywords = new ArrayList<>();
                while (resultSet.next()) {
                    Keyword newKeyword = createModelFromResultSet(resultSet);
                    keywords.add(newKeyword);
                }
                setKeywordCache(keywords);
                return keywords;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Keyword read(int id) {
        Keyword keyword = null;

        String readStmt =
                "SELECT KeywordID, Keyword " +
                        "FROM Keywords " +
                        "WHERE KeywordID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    keyword = createModelFromResultSet(result);
                }
                setKeywordCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return keyword;
    }

    public Keyword read(String keywordName) {
        Keyword keyword = null;

        String readStmt =
                "SELECT KeywordID, Keyword " +
                        "FROM Keywords " +
                        "WHERE Keyword = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setString(1, keywordName);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    keyword = createModelFromResultSet(result);
                }
                setKeywordCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return keyword;
    }

    @Override
    public void update(Keyword keyword) {
        String updateStmt = "UPDATE Keywords SET Keyword = ? WHERE KeywordID = ?";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
            preparedStatement.setString(1, keyword.getKeyword_text());
            preparedStatement.setInt(2, keyword.getKeyword_id());
            preparedStatement.executeUpdate();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Keywords WHERE KeywordID = ?;";
        String deleteHasKQStmt = "DELETE FROM hasKQ WHERE KeywordID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasKQStmt)) {
            // deleting from Keywords table
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            // deleting from hasKQ table
            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.executeUpdate();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addKQConnection(int keywordId, int questionId) {
        String insertStmt = "INSERT INTO hasKQ (KeywordID, QuestionID) VALUES (?, ?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setInt(1, keywordId);
            preparedStatement.setInt(2, questionId);
            preparedStatement.executeUpdate();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Keyword createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Keyword(
                resultSet.getInt("KeywordID"),
                resultSet.getString("Keyword")
        );
    }
}
