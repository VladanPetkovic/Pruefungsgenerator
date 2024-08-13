package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {


//    public Course get(String course_name) {
//        return getCourseDAO().read(course_name);
//    }
//
//    public boolean hasCategories(int courseId) {
//        return getCourseDAO().hasCategories(courseId);
//    }
}
