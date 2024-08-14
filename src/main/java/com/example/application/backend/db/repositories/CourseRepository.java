package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findCourseByName(String name);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category cat JOIN cat.courses c WHERE c.id = :courseId")
    boolean hasCategories(@Param("courseId") Long courseId);
}
