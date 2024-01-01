package com.example.backend.db.repositories;

import com.example.backend.db.daos.KeywordDAO;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class KeywordRepository implements Repository<Keyword> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    KeywordDAO keywordDAO;

    public KeywordRepository(KeywordDAO keywordDAO) {
        setKeywordDAO(keywordDAO);
    }

    @Override
    public ArrayList<Keyword> getAll() {
        return getKeywordDAO().readAll();
    }

    @Override
    public Keyword get(int id) {
        return getKeywordDAO().read(id);
    }

    // needed for adding keywords to a question, when searching for a question
    // return all keywords for a certain question
    public ArrayList<Keyword> getAll(int question_id) {
        return getKeywordDAO().readAllForOneQuestion(question_id);
    }

    @Override
    public void add(Keyword keyword) {
        getKeywordDAO().create(keyword);
    }

    public void addConnection(Keyword keyword, Question question) {
        getKeywordDAO().addKQConnection(keyword.getKeyword_id(), question.getQuestion_id());
    }

    @Override
    public void update(Keyword keyword) {
        getKeywordDAO().update(keyword);
    }

    @Override
    public void remove(Keyword keyword) {
        getKeywordDAO().delete(keyword.getKeyword_id());
    }
}
