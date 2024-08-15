package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
//    public void removeUnused() {
//        getCategoryDAO().delete();
//    }

    @Query("SELECT cat FROM Category cat JOIN cat.courses c WHERE c.id = :courseId")
    List<Category> findAllByCourseId(@Param("courseId") Long courseId);

    Category findCategoryByName(String name);
}
