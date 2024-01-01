package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TopicDAO implements DAO<Topic> {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    ArrayList<Topic> topicCache;

    @Override
    public void create(Topic topic) {
        String insertStmt = "INSERT INTO Topics (Topic) VALUES (?);";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setString(1, topic.getTopic());
            preparedStatement.execute();
            setTopicCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Topic> readAll() {
        ArrayList<Topic> topics = new ArrayList<>();

        String selectStmt = "SELECT TopicID, Topic FROM Topics;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Topic newTopic = createModelFromResultSet(resultSet);
                topics.add(newTopic);
            }

            setTopicCache(topics);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topics;
    }

    public ArrayList<Topic> readAllForOneCourse(int course_id) {
        ArrayList<Topic> topics = new ArrayList<>();

        String selectStmt =
                "SELECT TopicID, Topic " +
                        "FROM Topics " +
                        "JOIN hasCT ON Topics.TopicID = hasCT.TopicID " +
                        "JOIN Courses ON hasCT.CourseID = Courses.CourseID " +
                        "WHERE hasCT.CourseID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {

            preparedStatement.setInt(1, course_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Topic newTopic = createModelFromResultSet(resultSet);
                topics.add(newTopic);
            }

            setTopicCache(topics);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topics;
    }

    @Override
    public Topic read(int id) {
        Topic topic = null;

        String readStmt =
                "SELECT TopicID, Topic " +
                        "FROM Topics " +
                        "WHERE TopicID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {

            preparedStatement.setInt(1, id);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    topic = createModelFromResultSet(result);
                }
            }

            setTopicCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topic;
    }

    public Topic read(String topic_text) {
        Topic topic = null;

        String readStmt =
                "SELECT TopicID, Topic " +
                        "FROM Topics " +
                        "WHERE Topic = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {

            preparedStatement.setString(1, topic_text);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    topic = createModelFromResultSet(result);
                }
            }

            setTopicCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topic;
    }

    @Override
    public void update(Topic topic) {
        String updateStmt = "UPDATE Topics SET Topic = ? WHERE TopicID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {

            preparedStatement.setString(1, topic.getTopic());
            preparedStatement.setInt(2, topic.getTopic_id());
            preparedStatement.execute();
            setTopicCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Topics WHERE TopicID = ?;";
        String deleteHasCTStmt = "DELETE FROM hasCT WHERE TopicID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPpStmt = connection.prepareStatement(deleteHasCTStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.execute();

            secondPpStmt.setInt(1, id);
            secondPpStmt.execute();

            setTopicCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCTConnection(int course_id, int topic_id) {
        String insertStmt = "INSERT INTO hasCT (CourseID, TopicID) VALUES (?, ?);";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setInt(1, course_id);
            preparedStatement.setInt(2, topic_id);
            preparedStatement.execute();
            setTopicCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Topic createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Topic(
                resultSet.getInt("TopicID"),
                resultSet.getString("Topic")
        );
    }
}
