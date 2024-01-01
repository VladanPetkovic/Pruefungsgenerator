package com.example.backend.db.repositories;

import com.example.backend.db.daos.ImageDAO;
import com.example.backend.db.models.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class ImageRepository implements Repository<Image> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    ImageDAO imageDAO;

    public ImageRepository(ImageDAO newImageDAO) {
        setImageDAO(newImageDAO);
    }

    @Override
    public ArrayList<Image> getAll() {
        return getImageDAO().readAll();
    }

    // needed for adding images to a question, when searching for a question
    public ArrayList<Image> getAll(int question_id) {
        return getImageDAO().readAllForOneQuestion(question_id);
    }

    @Override
    public Image get(int id) {
        return getImageDAO().read(id);
    }

    @Override
    public void add(Image image) {
        getImageDAO().create(image);
    }

    @Override
    public void update(Image image) {
        getImageDAO().update(image);
    }

    @Override
    public void remove(Image image) {
        getImageDAO().delete(image.getImage_id());
    }
}
