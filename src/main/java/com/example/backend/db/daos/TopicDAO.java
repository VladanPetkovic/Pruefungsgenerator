package com.example.backend.db.daos;

import com.example.backend.db.models.StudyProgram;
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
        String insertStmt = "INSERT into Topics (Topic) VALUES (?);";
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

        String insertStmt = "SELECT Topic from Topics;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Topic newTopic = new Topic(
                        resultSet.getString(1));
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
        return null;
    }

    @Override
    public void update(Topic topic) {

    }

    @Override
    public void delete(int id) {

    }
}
