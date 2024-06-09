package com.example.backend.db.repositories;

import com.example.backend.db.daos.CategoryDAO;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Course;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class CategoryRepository implements Repository<Category> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    CategoryDAO categoryDAO;

    public CategoryRepository(CategoryDAO categoryDAO) {
        setCategoryDAO(categoryDAO);
    }
    @Override
    public ArrayList<Category> getAll() {
        return getCategoryDAO().readAll();
    }

    // returning all Categories for a certain course
    public ArrayList<Category> getAll(int course_id) {
        return getCategoryDAO().readAllForOneCourse(course_id);
    }

    @Override
    public Category get(int id) {
        return getCategoryDAO().read(id);
    }

    public Category get(String categoryName) {
        return getCategoryDAO().read(categoryName);
    }

    @Override
    public void add(Category category) {
        getCategoryDAO().create(category);
    }

    // addConnection: used for adding a new hasCC entry
    // --> we are adding a Category to a Course and not vice versa
    // --> therefor addConnection is implemented in CategoryRepository and not in CourseRepository
    public void addConnection(Course course, Category category) {
        getCategoryDAO().addCCConnection(course.getId(), category.getId());
    }

    @Override
    public void update(Category category) {
        getCategoryDAO().update(category);
    }

    @Override
    public void remove(Category category) {
        getCategoryDAO().delete(category.getId());
    }

    public void removeUnused() {
        getCategoryDAO().delete();
    }
}
