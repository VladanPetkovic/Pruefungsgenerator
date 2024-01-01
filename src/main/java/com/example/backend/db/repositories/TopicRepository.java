package com.example.backend.db.repositories;

import com.example.backend.db.daos.TopicDAO;
import com.example.backend.db.models.Course;
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

    // returning all Topics for a certain course
    public ArrayList<Topic> getAll(int course_id) {
        return getTopicDAO().readAllForOneCourse(course_id);
    }

    @Override
    public Topic get(int id) {
        return getTopicDAO().read(id);
    }

    @Override
    public void add(Topic topic) {
        getTopicDAO().create(topic);
    }

    // addConnection: used for adding a new hasCT entry
    // --> we are adding a Topic to a Course and not vice versa
    // --> therefor addConnection is implemented in TopicRepository and not in CourseRepository
    public void addConnection(Course course, Topic topic) {
        getTopicDAO().addCTConnection(course.getCourse_id(), topic.getTopic_id());
    }

    @Override
    public void update(Topic topic) {
        getTopicDAO().update(topic);
    }

    @Override
    public void remove(Topic topic) {
        getTopicDAO().delete(topic.getTopic_id());
    }
}
