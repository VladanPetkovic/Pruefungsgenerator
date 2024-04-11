package com.example.backend.db.repositories;

import com.example.backend.db.daos.AnswerDAO;
import com.example.backend.db.models.Answer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class AnswerRepository implements Repository<Answer> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    AnswerDAO answerDAO;

    public AnswerRepository(AnswerDAO answerDAO) {
        setAnswerDAO(answerDAO);
    }

    @Override
    public ArrayList<Answer> getAll() {
        return getAnswerDAO().readAll();
    }

    @Override
    public Answer get(int id) {
        return getAnswerDAO().read(id);
    }

    public Answer get(String answer) {
        return getAnswerDAO().read(answer);
    }

    @Override
    public void add(Answer answer) {
        getAnswerDAO().create(answer);
    }

    public void add(ArrayList<Answer> answers, int question_id) {
        getAnswerDAO().create(answers);
        getAnswerDAO().addHasAQConnection(answers, question_id);
    }

    @Override
    public void update(Answer answer) {
        getAnswerDAO().update(answer);
    }

    @Override
    public void remove(Answer answer) {
        getAnswerDAO().delete(answer.getId());
    }
}
