package com.example.backend.db.repositories;

import com.example.backend.db.daos.QuestionTypeDAO;
import com.example.backend.db.models.QuestionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class QuestionTypeRepository implements Repository<QuestionType> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    QuestionTypeDAO questionTypeDAO;

    public QuestionTypeRepository(QuestionTypeDAO questionTypeDAO) {
        setQuestionTypeDAO(questionTypeDAO);
    }

    @Override
    public ArrayList<QuestionType> getAll() {
        return getQuestionTypeDAO().readAll();
    }

    @Override
    public QuestionType get(int id) {
        return getQuestionTypeDAO().read(id);
    }

    public QuestionType get(String name) {
        return getQuestionTypeDAO().read(name);
    }

    @Override
    public void add(QuestionType type) {
        getQuestionTypeDAO().create(type);
    }

    @Override
    public void update(QuestionType type) {
        getQuestionTypeDAO().update(type);
    }

    @Override
    public void remove(QuestionType type) {
        getQuestionTypeDAO().delete(type.getId());
    }
}
