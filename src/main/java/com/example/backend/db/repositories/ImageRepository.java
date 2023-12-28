package com.example.backend.db.repositories;

import com.example.backend.db.models.Image;

import java.util.ArrayList;

public class ImageRepository implements Repository<Image> {
    @Override
    public ArrayList<Image> getAll() {
        return null;
    }

    // needed for adding images to a question, when searching for a question
    public ArrayList<Image> getAll(int question_id)
    {
        return null;
    }

    @Override
    public Image get(int id) {
        return null;
    }

    @Override
    public void add(Image type) {

    }

    @Override
    public void update(Image type) {

    }

    @Override
    public void remove(Image type) {

    }
}
