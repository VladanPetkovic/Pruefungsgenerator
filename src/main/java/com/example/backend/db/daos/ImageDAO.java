package com.example.backend.db.daos;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Answer;
import com.example.backend.db.models.Image;
import com.example.backend.db.models.Message;
import lombok.AccessLevel;
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
                "INSERT INTO images (image, name, position, comment) " +
                "VALUES (?, ?, ?, ?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setBytes(1, image.getImage());
            preparedStatement.setString(2, image.getName());
            preparedStatement.setInt(3, image.getPosition());
            preparedStatement.setString(4, image.getComment());
            preparedStatement.executeUpdate();
            setImageCache(null);

            SharedData.setOperation(Message.CREATE_IMAGE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.CREATE_IMAGE_ERROR_MESSAGE);
        }
    }

    /**
     * ATTENTION: duplicate records will be ignored.
     * @param images the images to insert (one or multiple)
     */
    public void create(ArrayList<Image> images) {
        int imageCount = 0;

        StringBuilder insertStmt = new StringBuilder("INSERT OR IGNORE INTO images (image, name, position, comment) VALUES (?, ?, ?, ?)");
        prepareInsertImageQuery(insertStmt, images);
        Logger.log(getClass().getName(), String.valueOf(insertStmt), LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(String.valueOf(insertStmt))) {

            // insert new images
            for (Image image : images) {
                preparedStatement.setBytes(imageCount + 1, image.getImage());
                preparedStatement.setString(imageCount + 2, image.getName());
                preparedStatement.setInt(imageCount + 3, image.getPosition());
                preparedStatement.setString(imageCount + 4, image.getComment());
                imageCount += 4;
            }

            preparedStatement.executeUpdate();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.CREATE_IMAGES_ERROR_MESSAGE);
        }
    }

    public void addHasIQConnection(ArrayList<Image> images, int question_id) {
        int imageCount = 1;

        StringBuilder insertHasAQStmt = new StringBuilder("" +
                "INSERT OR IGNORE INTO has_iq (fk_image_id, fk_question_id) " +
                "SELECT (SELECT id FROM images WHERE image = ?), ? ");
        prepareInsertConnectionQuery(insertHasAQStmt, images);
        Logger.log(getClass().getName(), String.valueOf(insertHasAQStmt), LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatementHasAQ = connection.prepareStatement(String.valueOf(insertHasAQStmt))) {

            // insert new connections
            for (Image image : images) {
                preparedStatementHasAQ.setBytes(imageCount, image.getImage());
                imageCount++;
                preparedStatementHasAQ.setInt(imageCount, question_id);
                imageCount++;
            }

            preparedStatementHasAQ.executeUpdate();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void prepareInsertImageQuery(StringBuilder insertStmt, ArrayList<Image> images) {
        if (images.size() > 1) {
            // -1, because we already have one insert
            for (int i = 0; i < images.size() - 1; i++) {
                insertStmt.append(", (?, ?, ?, ?)");
            }
        }

        insertStmt.append(";");
    }

    public void prepareInsertConnectionQuery(StringBuilder insertStmt, ArrayList<Image> images) {
        if (images.size() > 1) {
            // -1, because we already have one insert
            for (int i = 0; i < images.size() - 1; i++) {
                insertStmt.append("UNION ALL SELECT (SELECT id FROM images WHERE image = ?), ? ");
            }
        }

        insertStmt.append(";");
    }

    /**
     * Retrieves all images from the database.
     *
     * @return An ArrayList containing all images.
     */
    @Override
    public ArrayList<Image> readAll() {
        String selectStmt = "SELECT * FROM images;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

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
                "SELECT images.* " +
                "FROM images " +
                "JOIN has_iq ON images.id = has_iq.fk_image_id " +
                "JOIN questions ON has_iq.fk_question_id = questions.id " +
                "WHERE has_iq.fk_question_id = ?;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

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

        String readStmt = "SELECT * FROM images WHERE id = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

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
     * Retrieves an image by its name from the database.
     *
     * @param imageName The name of the image to retrieve.
     * @return The Image object corresponding to the given name.
     */
    public Image read(String imageName) {
        Image image = null;

        // SQL statement for selecting an image by link
        String readStmt = "SELECT * FROM images WHERE name = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);

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
                "UPDATE images " +
                "SET image = ?, name = ?, position = ?, comment = ? " +
                "WHERE id = ?";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
            preparedStatement.setBytes(1, image.getImage());
            preparedStatement.setString(2, image.getName());
            preparedStatement.setInt(3, image.getPosition());
            preparedStatement.setString(4, image.getComment());
            preparedStatement.setInt(5, image.getId());
            preparedStatement.executeUpdate();
            setImageCache(null);

            SharedData.setOperation(Message.UPDATE_IMAGE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.UPDATE_IMAGE_ERROR_MESSAGE);
        }
    }

    /**
     * Deletes an image record from the database by its ID.
     *
     * @param id The ID of the image to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM images WHERE id = ?;";
        String deleteHasIQStmt = "DELETE FROM has_iq WHERE fk_image_id = ?;";
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);
        Logger.log(getClass().getName(), deleteHasIQStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasIQStmt)) {
            // deleting from images table
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            // deleting from has_iq table
            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.executeUpdate();
            setImageCache(null);

            SharedData.setOperation(Message.DELETE_IMAGE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.DELETE_IMAGE_ERROR_MESSAGE);
        }
    }

    /**
     * This function deletes unused images.
     */
    public void delete() {
        String deleteStmt = "DELETE FROM images " +
                "WHERE id NOT IN (SELECT has_iq.fk_image_id FROM has_iq);";
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt)) {
            // deleting from Images table
            preparedStatement.executeUpdate();
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
        String insertStmt = "INSERT INTO has_iq (fk_image_id, fk_question_id) VALUES (?, ?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

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
        String deleteStmt = "DELETE FROM has_iq WHERE fk_image_id = ? AND fk_question_id = ?;";
        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);

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
                resultSet.getInt("id"),
                resultSet.getBytes("image"),
                resultSet.getString("name"),
                resultSet.getInt("position"),
                resultSet.getString("comment"));
    }
}
