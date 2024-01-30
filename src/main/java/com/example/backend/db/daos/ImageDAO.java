package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) for managing Image objects in the database.
 */
public class ImageDAO implements DAO<Image> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Image> imageCache;

    /**
     * Creates a new image record in the database.
     *
     * @param image The Image object to create.
     */
    @Override
    public void create(Image image) {
        String insertStmt =
                "INSERT INTO Images (Link, Imagename, Position) " +
                        "VALUES (?, ?, ?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, image.getLink());
            preparedStatement.setString(2, image.getImageName());
            preparedStatement.setInt(3, image.getPosition());
            preparedStatement.executeUpdate();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all images from the database.
     *
     * @return An ArrayList containing all images.
     */
    @Override
    public ArrayList<Image> readAll() {
        String selectStmt = "SELECT ImageID, Link, Imagename, Position FROM Images;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ArrayList<Image> images = new ArrayList<>();
            while (resultSet.next()) {
                Image newImage = createModelFromResultSet(resultSet);
                images.add(newImage);
            }

            setImageCache(images);
            return images;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all images associated with a specific question.
     *
     * @param questionId The ID of the question.
     * @return An ArrayList containing images associated with the specified question.
     */
    public ArrayList<Image> readAllForOneQuestion(int questionId) {
        String selectStmt =
                "SELECT Images.ImageID, Link, Imagename, Position " +
                        "FROM Images " +
                        "JOIN hasIQ ON Images.ImageID = hasIQ.ImageID " +
                        "JOIN Questions ON hasIQ.QuestionID = Questions.QuestionID " +
                        "WHERE hasIQ.QuestionID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {
            preparedStatement.setInt(1, questionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ArrayList<Image> images = new ArrayList<>();
                while (resultSet.next()) {
                    Image newImage = createModelFromResultSet(resultSet);
                    images.add(newImage);
                }
                setImageCache(images);
                return images;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves an image by its ID from the database.
     *
     * @param id The ID of the image to retrieve.
     * @return The Image object corresponding to the given ID.
     */
    @Override
    public Image read(int id) {
        Image image = null;

        String readStmt =
                "SELECT ImageID, Link, Imagename, Position " +
                        "FROM Images " +
                        "WHERE ImageID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    image = createModelFromResultSet(result);
                }
                setImageCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Retrieves an image by its link from the database.
     *
     * @param link The link of the image to retrieve.
     * @return The Image object corresponding to the given link.
     */
    public Image readByLink(String link) {
        Image image = null;

        // SQL statement for selecting an image by link
        String readStmt =
                "SELECT ImageID, Link, Imagename, Position " +
                        "FROM Images " +
                        "WHERE Link = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setString(1, link);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    image = createModelFromResultSet(result);
                }
                setImageCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Retrieves an image by its name from the database.
     *
     * @param imageName The name of the image to retrieve.
     * @return The Image object corresponding to the given name.
     */
    public Image readByName(String imageName) {
        Image image = null;

        // SQL statement for selecting an image by name
        String readStmt =
                "SELECT ImageID, Link, Imagename, Position " +
                        "FROM Images " +
                        "WHERE Imagename = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setString(1, imageName);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    image = createModelFromResultSet(result);
                }
                setImageCache(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Updates an existing image record in the database.
     *
     * @param image The Image object with updated information.
     */
    @Override
    public void update(Image image) {
        String updateStmt =
                "UPDATE Images " +
                        "SET Link = ?, Imagename = ?, Position = ? " +
                        "WHERE ImageID = ?";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
            preparedStatement.setString(1, image.getLink());
            preparedStatement.setString(2, image.getImageName());
            preparedStatement.setInt(3, image.getPosition());
            preparedStatement.setInt(4, image.getImage_id());
            preparedStatement.executeUpdate();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an image record from the database by its ID.
     *
     * @param id The ID of the image to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Images WHERE ImageID = ?;";
        String deleteHasIQStmt = "DELETE FROM hasIQ WHERE ImageID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasIQStmt)) {
            // deleting from Images table
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            // deleting from hasIQ table
            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.executeUpdate();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a connection between an image and a question in the database.
     *
     * @param imageId    The ID of the image.
     * @param questionId The ID of the question.
     */
    public void addIQConnection(int imageId, int questionId) {
        String insertStmt = "INSERT INTO hasIQ (ImageID, QuestionID) VALUES (?, ?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setInt(1, imageId);
            preparedStatement.setInt(2, questionId);
            preparedStatement.executeUpdate();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a connection between an image and a question in the database.
     *
     * @param imageId    The ID of the image.
     * @param questionId The ID of the question.
     */
    public void removeIQConnection(int imageId, int questionId) {
        String deleteStmt = "DELETE FROM hasIQ WHERE ImageID = ? AND QuestionID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt)) {
            preparedStatement.setInt(1, imageId);
            preparedStatement.setInt(2, questionId);
            preparedStatement.executeUpdate();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an Image object from a ResultSet obtained from the database.
     *
     * @param resultSet The ResultSet containing image data.
     * @return An Image object created from the ResultSet.
     * @throws SQLException If a database access error occurs or the column labels are not valid.
     */
    @Override
    public Image createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Image(
                resultSet.getInt("ImageID"),
                resultSet.getString("Link"),
                resultSet.getString("Imagename"),
                resultSet.getInt("Position")
        );
    }
}
