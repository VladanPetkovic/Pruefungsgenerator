package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;

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
        String insertStmt = "INSERT INTO Categories (Category) VALUES (?);";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setString(1, category.getCategory());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
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

        String selectStmt = "SELECT CategoryID, Category FROM Categories;";

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
                "SELECT Categories.CategoryID, Categories.Category " +
                        "FROM Categories " +
                        "JOIN hasCC ON Categories.CategoryID = hasCC.CategoryID " +
                        "JOIN Courses ON hasCC.CourseID = Courses.CourseID " +
                        "WHERE hasCC.CourseID = ?;";

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
                "SELECT CategoryID, Category " +
                "FROM Categories " +
                "WHERE CategoryID = ?;";

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
                "SELECT Categories.CategoryID, Categories.Category " +
                "FROM Questions " +
                "INNER JOIN Categories ON Questions.FK_Category_ID = Categories.CategoryID " +
                "WHERE Questions.QuestionID = ?;";

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
                "SELECT CategoryID, Category " +
                        "FROM Categories " +
                        "WHERE Category = ?;";

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
        String updateStmt = "UPDATE Categories SET Category = ? WHERE CategoryID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {

            preparedStatement.setString(1, category.getCategory());
            preparedStatement.setInt(2, category.getCategory_id());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a category from the database.
     *
     * @param id The ID of the category to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Categories WHERE CategoryID = ?;";
        String deleteHasCCStmt = "DELETE FROM hasCC WHERE CategoryID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPpStmt = connection.prepareStatement(deleteHasCCStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            secondPpStmt.setInt(1, id);
            secondPpStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection between a course and a category.
     *
     * @param course_id   The ID of the course.
     * @param category_id The ID of the category.
     */
    public void addCCConnection(int course_id, int category_id) {
        String insertStmt = "INSERT INTO hasCC (CourseID, CategoryID) VALUES (?, ?);";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setInt(1, course_id);
            preparedStatement.setInt(2, category_id);
            preparedStatement.executeUpdate();

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
                resultSet.getInt("CategoryID"),
                resultSet.getString("Category")
        );
    }
}
