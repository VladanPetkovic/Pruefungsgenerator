package com.example.backend.db.repositories;

import com.example.backend.db.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

//    // returning all Categories for a certain course
//    public ArrayList<Category> getAll(int course_id) {
//        return getCategoryDAO().readAllForOneCourse(course_id);
//    }
//
//    public Category get(String categoryName) {
//        return getCategoryDAO().read(categoryName);
//    }
//
//    public void removeUnused() {
//        getCategoryDAO().delete();
//    }
}
