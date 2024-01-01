package com.example.backend.db.repositories;

import com.example.backend.db.daos.QuestionDAO;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class QuestionRepository implements Repository<Question> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    QuestionDAO questionDAO;

    @Override
    public ArrayList<Question> getAll() {
        return getQuestionDAO().readAll();
    }

    public ArrayList<Question> getAll(Topic topic) {
        return getQuestionDAO().readAll(topic.getTopic());
    }

    @Override
    public Question get(int id) {
        return getQuestionDAO().read(id);
    }

    @Override
    public void add(Question question) {
        getQuestionDAO().create(question);
    }

    @Override
    public void update(Question question) {
        getQuestionDAO().update(question);
    }

    @Override
    public void remove(Question question) {
        getQuestionDAO().delete(question.getQuestion_id());
    }
}
