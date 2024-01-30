package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Keyword;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for handling Keyword entities in the database.
 */
public class KeywordDAO implements DAO<Keyword> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Keyword> keywordCache;

    /**
     * Creates a new keyword entry in the database.
     *
     * @param keyword The Keyword object to create in the database.
     */
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

    /**
     * Retrieves all keywords from the database.
     *
     * @return An ArrayList containing all keywords retrieved from the database.
     */
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

    /**
     * Retrieves all keywords associated with a specific question from the database.
     *
     * @param questionId The ID of the question to retrieve keywords for.
     * @return An ArrayList containing all keywords associated with the specified question.
     */
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

    /**
     * Retrieves a keyword by its ID from the database.
     *
     * @param id The ID of the keyword to retrieve.
     * @return The Keyword object corresponding to the given ID.
     */
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

    /**
     * Retrieves a keyword by its name from the database.
     *
     * @param keywordName The name of the keyword to retrieve.
     * @return The Keyword object corresponding to the given name.
     */
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

    /**
     * Updates a keyword entry in the database.
     *
     * @param keyword The Keyword object containing updated information.
     */
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

    /**
     * Deletes a keyword entry from the database.
     *
     * @param id The ID of the keyword to delete.
     */
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

    /**
     * Adds a connection between a keyword and a question in the database.
     *
     * @param keywordId  The ID of the keyword.
     * @param questionId The ID of the question.
     */
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

    /**
     * Removes a connection between a keyword and a question from the database.
     *
     * @param keywordId  The ID of the keyword.
     * @param questionId The ID of the question.
     */
    public void removeKQConnection(int keywordId, int questionId) {
        String deleteStmt = "DELETE FROM hasKQ WHERE KeywordID = ? AND QuestionID = ?";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt)) {
            preparedStatement.setInt(1, keywordId);
            preparedStatement.setInt(2, questionId);
            preparedStatement.executeUpdate();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Keyword object from a ResultSet.
     *
     * @param resultSet The ResultSet containing keyword data.
     * @return A Keyword object created from the ResultSet.
     * @throws SQLException If an SQL exception occurs.
     */
    @Override
    public Keyword createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Keyword(
                resultSet.getInt("KeywordID"),
                resultSet.getString("Keyword")
        );
    }
}
