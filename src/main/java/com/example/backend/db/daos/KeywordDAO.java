package com.example.backend.db.daos;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Answer;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Message;
import lombok.AccessLevel;
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
        String insertStmt = "INSERT OR IGNORE INTO keywords (keyword) VALUES (?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, keyword.getKeyword());
            preparedStatement.executeUpdate();
            setKeywordCache(null);

            SharedData.setOperation(Message.CREATE_KEYWORD_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.CREATE_KEYWORD_ERROR_MESSAGE);
        }
    }

    /**
     * ATTENTION: duplicate records will be ignored
     * @param keywords ArrayList of Keywords, so one or multiple keywords can be inserted at once into the keywords table.
     */
    public void create(ArrayList<Keyword> keywords) {
        int keywordCounter = 1;

        StringBuilder insertStmt = new StringBuilder("INSERT OR IGNORE INTO keywords (keyword) VALUES (?)");
        prepareInsertKeywordQuery(insertStmt, keywords);
        Logger.log(getClass().getName(), String.valueOf(insertStmt), LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(String.valueOf(insertStmt))) {

            // insert new answers
            for (Keyword keyword : keywords) {
                preparedStatement.setString(keywordCounter, keyword.getKeyword());
                keywordCounter++;
            }

            preparedStatement.executeUpdate();
            setKeywordCache(null);


        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.CREATE_KEYWORD_ERROR_MESSAGE);
        }
    }

    public void prepareInsertKeywordQuery(StringBuilder insertStmt, ArrayList<Keyword> keywords) {
        if (keywords.size() > 1) {
            // -1, because we already have one insert
            for (int i = 0; i < keywords.size() - 1; i++) {
                insertStmt.append(", (?)");
            }
        }

        insertStmt.append(";");
    }

    public void prepareInsertConnectionQuery(StringBuilder insertStmt, ArrayList<Keyword> keywords) {
        if (keywords.size() > 1) {
            // -1, because we already have one insert
            for (int i = 0; i < keywords.size() - 1; i++) {
                insertStmt.append("UNION ALL SELECT (SELECT id FROM keywords WHERE keyword = ?), ? ");
            }
        }

        insertStmt.append(";");
    }

    /**
     * ATTENTION: duplicate records are ignored and no error will show up.
     * @param keywords ArrayList of Keywords, inserting one or multiple keyword_ids in the has_kq table
     * @param question_id The id of the Question
     */
    public void addKQConnection(ArrayList<Keyword> keywords, int question_id) {
        int keywordCounter = 1;

        StringBuilder insertHasKQStmt = new StringBuilder("" +
                "INSERT OR IGNORE INTO has_kq (fk_keyword_id, fk_question_id) " +
                "SELECT (SELECT id FROM keywords WHERE keyword = ?), ? ");
        prepareInsertConnectionQuery(insertHasKQStmt, keywords);
        Logger.log(getClass().getName(), String.valueOf(insertHasKQStmt), LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatementHasKQ = connection.prepareStatement(String.valueOf(insertHasKQStmt))) {

            // insert new connections
            for (Keyword keyword : keywords) {
                preparedStatementHasKQ.setString(keywordCounter, keyword.getKeyword());
                keywordCounter++;
                preparedStatementHasKQ.setInt(keywordCounter, question_id);
                keywordCounter++;
            }

            preparedStatementHasKQ.executeUpdate();
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
        String selectStmt = "SELECT * FROM keywords;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

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
                "SELECT DISTINCT keywords.* " +
                "FROM keywords " +
                "JOIN has_kq ON keywords.id = has_kq.fk_keyword_id " +
                "JOIN questions ON has_kq.fk_question_id = questions.id " +
                "WHERE has_kq.fk_question_id = ?;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

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

    public ArrayList<Keyword> readAllForOneCourse(int course_id) {
        String selectStmt =
                "SELECT DISTINCT keywords.* " +
                "FROM keywords " +
                "JOIN has_kq ON keywords.id = has_kq.fk_keyword_id " +
                "JOIN (SELECT questions.id " +
                "FROM questions " +
                "JOIN has_kq ON questions.id = has_kq.fk_question_id " +
                "WHERE questions.fk_category_id IN ( " +
                "SELECT categories.id " +
                "FROM categories " +
                "JOIN has_cc ON has_cc.fk_category_id = categories.id " +
                "WHERE has_cc.fk_course_id = ?)" +
                ") AS filtered_questions ON has_kq.fk_question_id = filtered_questions.id;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {
            preparedStatement.setInt(1, course_id);
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
                "SELECT * " +
                "FROM keywords " +
                "WHERE id = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

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
                "SELECT * " +
                "FROM keywords " +
                "WHERE keyword = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

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
        String updateStmt = "UPDATE keywords SET keyword = ? WHERE id = ?";
        Logger.log(getClass().getName(), updateStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
            preparedStatement.setString(1, keyword.getKeyword());
            preparedStatement.setInt(2, keyword.getId());
            preparedStatement.executeUpdate();
            setKeywordCache(null);

            SharedData.setOperation(Message.UPDATE_KEYWORD_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.UPDATE_KEYWORD_ERROR_MESSAGE);
        }
    }

    /**
     * Deletes a keyword entry from the database.
     *
     * @param id The ID of the keyword to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM keywords WHERE id = ?;";
        String deleteHasKQStmt = "DELETE FROM has_kq WHERE fk_keyword_id = ?;";
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);
        Logger.log(getClass().getName(), deleteHasKQStmt, LogLevel.DEBUG);

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

            SharedData.setOperation(Message.DELETE_KEYWORD_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.DELETE_KEYWORD_ERROR_MESSAGE);
        }
    }

    /**
     * Adds a connection between a keyword and a question in the database.
     *
     * @param keywordId  The ID of the keyword.
     * @param questionId The ID of the question.
     */
    public void addKQConnection(int keywordId, int questionId) {
        String insertStmt = "INSERT INTO has_kq (fk_keyword_id, fk_question_id) VALUES (?, ?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

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
        String deleteStmt = "DELETE FROM has_kq WHERE fk_keyword_id = ? AND fk_question_id = ?";
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);

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
                resultSet.getInt("id"),
                resultSet.getString("keyword"));
    }
}
