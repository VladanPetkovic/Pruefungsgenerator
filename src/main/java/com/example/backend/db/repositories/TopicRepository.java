package com.example.backend.db.repositories;

import com.example.backend.db.daos.TopicDAO;
import com.example.backend.db.models.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class TopicRepository implements Repository<Topic> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    TopicDAO topicDAO;

    public TopicRepository(TopicDAO topicDAO) {
        setTopicDAO(topicDAO);
    }
    @Override
    public ArrayList<Topic> getAll() {
        return getTopicDAO().readAll();
    }

    @Override
    public Topic get(int id) {
        return null;
    }

    @Override
    public void add(Topic type) {

    }

    @Override
    public void update(Topic type) {

    }

    @Override
    public void remove(Topic type) {

    }
}
