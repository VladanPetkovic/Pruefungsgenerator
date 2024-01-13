package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryDAO implements DAO<Category> {
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

    public ArrayList<Category> readAllForOneCourse(int course_id) {
        ArrayList<Category> categories = new ArrayList<>();

        String selectStmt =
                "SELECT CategoryID, Category " +
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

    @Override
    public Category createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Category(
                resultSet.getInt("CategoryID"),
                resultSet.getString("Category")
        );
    }
}
