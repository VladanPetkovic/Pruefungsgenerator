package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;

public class ImageDAO implements DAO<Image> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Image> imageCache;

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
            preparedStatement.execute();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            preparedStatement.execute();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Images WHERE ImageID = ?;";
        String deleteHasIQStmt = "DELETE FROM hasIQ WHERE ImageID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasIQStmt)) {
            // deleting from Images table
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            // deleting from hasIQ table
            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.execute();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addIQConnection(int imageId, int questionId) {
        String insertStmt = "INSERT INTO hasIQ (ImageID, QuestionID) VALUES (?, ?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setInt(1, imageId);
            preparedStatement.setInt(2, questionId);
            preparedStatement.execute();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
