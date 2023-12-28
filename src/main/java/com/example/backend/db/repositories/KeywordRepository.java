package com.example.backend.db.repositories;

import com.example.backend.db.models.Keyword;

import java.util.ArrayList;

public class KeywordRepository implements Repository<Keyword> {
    @Override
    public ArrayList<Keyword> getAll() {
        return null;
    }

    @Override
    public Keyword get(int id) {
        return null;
    }

    // needed for adding keywords to a question, when searching for a question
    public ArrayList<Keyword> getAll(int question_id) {
        return null;
    }

    @Override
    public void add(Keyword type) {

    }

    @Override
    public void update(Keyword type) {

    }

    @Override
    public void remove(Keyword type) {

    }
}
