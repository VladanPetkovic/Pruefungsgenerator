package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.Course;
import com.example.application.backend.db.repositories.CourseRepository;
import com.example.application.backend.db.repositories.StudyProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudyProgramRepository studyProgramRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository,
                         StudyProgramRepository studyProgramRepository) {
        this.courseRepository = courseRepository;
        this.studyProgramRepository = studyProgramRepository;
    }

    public Course add(Course course, Long studyProgramId) {
        Course newCourse = courseRepository.save(course);
        Logger.log(this.getClass().getName(), "Course saved with ID: " + newCourse.getId(), LogLevel.INFO);
        return newCourse;
    }

    public Course getById(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null) {
            Logger.log(this.getClass().getName(), "Course found with ID: " + id, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Course not found with ID: " + id, LogLevel.WARN);
        }
        return course;
    }

    public List<Course> getAll() {
        List<Course> courses = courseRepository.findAll();
        Logger.log(this.getClass().getName(), "Retrieved all courses, count: " + courses.size(), LogLevel.INFO);
        return courses;
    }

    public Course update(Course course) {
        Course existingCourse = courseRepository.findById(course.getId()).orElse(null);
        if (existingCourse != null) {
            existingCourse.setName(course.getName());
            existingCourse.setNumber(course.getNumber());
            existingCourse.setLector(course.getLector());

            Course updatedCourse = courseRepository.save(existingCourse);
            Logger.log(this.getClass().getName(), "Course updated successfully for ID: " + course.getId(), LogLevel.INFO);
            return updatedCourse;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find course with ID: " + course.getId(), LogLevel.ERROR);
            throw new RuntimeException("Course not found");
        }
    }

    public void remove(Long id) {
        try {
            courseRepository.deleteById(id);
            Logger.log(this.getClass().getName(), "Course deleted successfully with ID: " + id, LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(this.getClass().getName(), "Failed to delete course with ID: " + id, LogLevel.ERROR);
            throw e;
        }
    }
}
