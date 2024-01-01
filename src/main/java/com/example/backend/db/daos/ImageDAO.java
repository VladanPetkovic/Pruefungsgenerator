package com.example.backend.db.daos;

import com.example.backend.db.models.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ImageDAO implements DAO<Image> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Image> ImageCache;
    @Override
    public void create(Image image) {
        String insertStmt =
                "INSERT INTO Images (Link, Imagename, Position) " +
                        "VALUES (?, ?, ?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, image.getLink());
            preparedStatement.setString(2, image.getImageName());
            preparedStatement.setInt(3, image.getPosition());
            preparedStatement.execute();
            getConnection().close();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Image> readAll() {
        ArrayList<Image> images = new ArrayList<>();

        String selectStmt =
                "SELECT ImageID, Link, Imagename, Position " +
                        "FROM Images;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Image newImage = new Image(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4));
                images.add(newImage);
            }
            setImageCache(images);
            getConnection().close();
            return images;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Image> readAllForOneQuestion(int question_id) {
        ArrayList<Image> images = new ArrayList<>();

        String selectStmt =
            "SELECT Images.ImageID, Link, Imagename, Position " +
            "FROM Images" +
            "JOIN hasIQ ON Images.ImageID = hasIQ.ImageID " +
            "JOIN Questions ON hasIQ.QuestionID = Questions.QuestionID " +
            "WHERE hasIQ.QuestionID = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, question_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Image newImage = new Image(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4));
                images.add(newImage);
            }
            setImageCache(images);
            getConnection().close();
            return images;
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
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(readStmt);
            preparedStatement.setInt(1, id);

            ResultSet result = preparedStatement.executeQuery();

            if(result.next()) {
                image = new Image(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4)
                );
            }

            getConnection().close();
            setImageCache(null);
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
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, image.getLink());
            preparedStatement.setString(2, image.getImageName());
            preparedStatement.setInt(3, image.getPosition());
            preparedStatement.execute();
            getConnection().close();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Images WHERE ImageID = ?;";
        String deleteHasIQStmt = "DELETE FROM hasIQ WHERE ImageID = ?;";
        try {
            // deleting from Images table
            PreparedStatement preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            // deleting from hasIQ table
            PreparedStatement secondPpStmt = getConnection().prepareStatement(deleteHasIQStmt);
            secondPpStmt.setInt(1, id);
            secondPpStmt.execute();

            getConnection().close();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addIQConnection(int image_id, int question_id) {
        String insertStmt = "INSERT INTO hasIQ (ImageID, QuestionID) VALUES (?, ?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setInt(1, image_id);
            preparedStatement.setInt(2, question_id);
            preparedStatement.execute();
            getConnection().close();
            setImageCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
