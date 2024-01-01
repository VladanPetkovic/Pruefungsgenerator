package com.example.backend.db.daos;

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
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Topic> topicCache;

    @Override
    public void create(Topic topic) {
        String insertStmt = "INSERT INTO Topics (Topic) VALUES (?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, topic.getTopic());
            preparedStatement.execute();
            getConnection().close();
            setTopicCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Topic> readAll() {
        ArrayList<Topic> topics = new ArrayList<>();

        String selectStmt = "SELECT TopicID, Topic FROM Topics;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Topic newTopic = new Topic(
                    resultSet.getInt(1),
                    resultSet.getString(2)
                );
                topics.add(newTopic);
            }
            setTopicCache(topics);
            getConnection().close();
            return topics;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Topic> readAllForOneCourse(int course_id) {
        ArrayList<Topic> topics = new ArrayList<>();

        String selectStmt =
            "SELECT TopicID, Topic " +
            "FROM Topics" +
            "JOIN hasCT ON Topics.TopicID = hasCT.TopicID" +
            "JOIN Courses ON hasCT.CourseID = Courses.CourseID" +
            "WHERE hasCT.CourseID = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, course_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Topic newTopic = new Topic(
                    resultSet.getInt(1),
                    resultSet.getString(2)
                );
                topics.add(newTopic);
            }
            setTopicCache(topics);
            getConnection().close();
            return topics;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Topic read(int id) {
        Topic topic = null;

        String readStmt =
            "SELECT TopicID, Topic " +
            "FROM Topics " +
            "WHERE TopicID = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(readStmt);
            preparedStatement.setInt(1, id);

            ResultSet result = preparedStatement.executeQuery();

            if(result.next()) {
                topic = new Topic(
                    result.getInt(1),
                    result.getString(2)
                );
            }

            getConnection().close();
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
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(readStmt);
            preparedStatement.setString(1, topic_text);

            ResultSet result = preparedStatement.executeQuery();

            if(result.next()) {
                topic = new Topic(
                        result.getInt(1),
                        result.getString(2)
                );
            }

            getConnection().close();
            setTopicCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topic;
    }

    @Override
    public void update(Topic topic) {
        String updateStmt = "UPDATE Topics SET Topic = ? WHERE TopicID = ?";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, topic.getTopic());
            preparedStatement.setInt(2, topic.getTopic_id());
            preparedStatement.execute();
            getConnection().close();
            setTopicCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Topics WHERE TopicID = ?;";
        String deleteHasCTStmt = "DELETE FROM hasCT WHERE TopicID = ?;";
        try {
            // deleting from Topics table
            PreparedStatement preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            // deleting from hasCT table
            PreparedStatement secondPpStmt = getConnection().prepareStatement(deleteHasCTStmt);
            secondPpStmt.setInt(1, id);
            secondPpStmt.execute();

            getConnection().close();
            setTopicCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCTConnection(int course_id, int topic_id) {
        String insertStmt = "INSERT INTO hasCT (CourseID, TopicID) VALUES (?, ?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setInt(1, course_id);
            preparedStatement.setInt(2, topic_id);
            preparedStatement.execute();
            getConnection().close();
            setTopicCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
