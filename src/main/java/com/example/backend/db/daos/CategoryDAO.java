package com.example.backend.db.daos;

import com.example.backend.app.SharedData;
import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) for managing Category objects in the database.
 */
public class CategoryDAO implements DAO<Category> {

    /**
     * Inserts a new Category into the database.
     *
     * @param category The Category object to insert.
     */
    @Override
    public void create(Category category) {
        String insertStmt = "INSERT OR IGNORE INTO categories (name) VALUES (?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.executeUpdate();

            SharedData.setOperation(Message.CREATE_CATEGORY_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.CREATE_CATEGORY_ERROR_MESSAGE);
        }
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return An ArrayList containing all categories.
     */
    @Override
    public ArrayList<Category> readAll() {
        ArrayList<Category> categories = new ArrayList<>();

        String selectStmt = "SELECT * FROM categories;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Category newCategory = createModelFromResultSet(resultSet);
                categories.add(newCategory);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Retrieves all categories associated with a specific course.
     *
     * @param course_id The ID of the course.
     * @return An ArrayList containing categories associated with the specified course.
     */
    public ArrayList<Category> readAllForOneCourse(int course_id) {
        ArrayList<Category> categories = new ArrayList<>();

        String selectStmt =
                "SELECT categories.* " +
                "FROM categories " +
                "JOIN has_cc ON categories.id = has_cc.fk_category_id " +
                "JOIN courses ON has_cc.fk_course_id = courses.id " +
                "WHERE has_cc.fk_course_id = ?;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {

            preparedStatement.setInt(1, course_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category newCategory = createModelFromResultSet(resultSet);
                categories.add(newCategory);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Retrieves a category by its ID from the database.
     *
     * @param id The ID of the category to retrieve.
     * @return The Category object corresponding to the given ID.
     */
    @Override
    public Category read(int id) {
        Category category = null;

        String readStmt =
                "SELECT * " +
                "FROM categories " +
                "WHERE id = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {

            preparedStatement.setInt(1, id);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    category = createModelFromResultSet(result);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }

    /**
     * Retrieves the category associated with a specific question.
     *
     * @param question_id The ID of the question.
     * @return The Category object associated with the specified question.
     */
    public Category readForQuestion(int question_id) {
        Category category = null;

        String readStmt =
                "SELECT categories.* " +
                "FROM questions " +
                "INNER JOIN categories ON questions.fk_category_id = categories.id " +
                "WHERE questions.id = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {

            preparedStatement.setInt(1, question_id);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    category = createModelFromResultSet(result);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }

    /**
     * Retrieves a category by its name from the database.
     *
     * @param category_text The name of the category to retrieve.
     * @return The Category object corresponding to the given name.
     */
    public Category read(String category_text) {
        Category category = null;

        String readStmt =
                "SELECT * " +
                "FROM categories " +
                "WHERE name = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {

            preparedStatement.setString(1, category_text);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    category = createModelFromResultSet(result);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }

    /**
     * Updates a category in the database.
     *
     * @param category The Category object with updated information.
     */
    @Override
    public void update(Category category) {
        String updateStmt = "UPDATE categories SET name = ? WHERE id = ?;";
        Logger.log(getClass().getName(), updateStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setInt(2, category.getId());
            preparedStatement.executeUpdate();

            SharedData.setOperation(Message.UPDATE_CATEGORY_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.UPDATE_CATEGORY_ERROR_MESSAGE);
        }
    }

    /**
     * Deletes a category from the database.
     *
     * @param id The ID of the category to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM categories WHERE id = ?;";
        String deleteHasCCStmt = "DELETE FROM has_cc WHERE fk_category_id = ?;";
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);
        Logger.log(getClass().getName(), deleteHasCCStmt, LogLevel.DEBUG);
        // TODO: what happens with the question, that has fk_category_id deleted?

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPpStmt = connection.prepareStatement(deleteHasCCStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            secondPpStmt.setInt(1, id);
            secondPpStmt.executeUpdate();

            SharedData.setOperation(Message.DELETE_CATEGORY_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.DELETE_CATEGORY_ERROR_MESSAGE);
        }
    }

    /**
     * This function deletes unused categories.
     */
    public void delete() {
        String deleteStmt = "DELETE FROM categories " +
                "WHERE id NOT IN (SELECT questions.fk_category_id FROM questions);";
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt)) {
            // deleting from Categories table
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection between a course and a category, ensuring no duplicate connections are added.
     *
     * @param course_id   The ID of the course.
     * @param category_id The ID of the category.
     */
    public void addCCConnection(int course_id, int category_id) {
        String checkStmt = "SELECT COUNT(*) FROM has_cc WHERE fk_course_id = ? AND fk_category_id = ?";
        String insertStmt = "INSERT INTO has_cc (fk_course_id, fk_category_id) VALUES (?, ?);";
        Logger.log(getClass().getName(), checkStmt, LogLevel.DEBUG);
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement checkPreparedStatement = connection.prepareStatement(checkStmt)) {

            checkPreparedStatement.setInt(1, course_id);
            checkPreparedStatement.setInt(2, category_id);

            try (ResultSet resultSet = checkPreparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    try (PreparedStatement insertPreparedStatement = connection.prepareStatement(insertStmt)) {
                        insertPreparedStatement.setInt(1, course_id);
                        insertPreparedStatement.setInt(2, category_id);
                        insertPreparedStatement.executeUpdate();
                        Logger.log(getClass().getName(), "Connection added: course_id=" + course_id + ", category_id=" + category_id, LogLevel.INFO);
                    }
                } else {
                    Logger.log(getClass().getName(), "Connection already exists: course_id=" + course_id + ", category_id=" + category_id, LogLevel.INFO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a connection between a course and a category from the database, ensuring the connection exists before removal.
     *
     * @param course_id   The ID of the course.
     * @param category_id The ID of the category.
     */
    public void removeCCConnection(int course_id, int category_id) {
        String checkStmt = "SELECT COUNT(*) FROM has_cc WHERE fk_course_id = ? AND fk_category_id = ?";
        String deleteStmt = "DELETE FROM has_cc WHERE fk_course_id = ? AND fk_category_id = ?;";
        Logger.log(getClass().getName(), checkStmt, LogLevel.DEBUG);
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement checkPreparedStatement = connection.prepareStatement(checkStmt)) {

            checkPreparedStatement.setInt(1, course_id);
            checkPreparedStatement.setInt(2, category_id);

            try (ResultSet resultSet = checkPreparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    try (PreparedStatement deletePreparedStatement = connection.prepareStatement(deleteStmt)) {
                        deletePreparedStatement.setInt(1, course_id);
                        deletePreparedStatement.setInt(2, category_id);
                        deletePreparedStatement.executeUpdate();
                        Logger.log(getClass().getName(), "Connection removed: course_id=" + course_id + ", category_id=" + category_id, LogLevel.INFO);
                    }
                } else {
                    Logger.log(getClass().getName(), "Connection does not exist: course_id=" + course_id + ", category_id=" + category_id, LogLevel.INFO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Category object from the ResultSet.
     *
     * @param resultSet The ResultSet containing category data.
     * @return The created Category object.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    @Override
    public Category createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Category(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }
}
