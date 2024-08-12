package com.example.backend.db.services;

import com.example.backend.db.models.Course;
import com.example.backend.db.repositories.CategoryRepository;
import com.example.backend.db.repositories.CourseRepository;
import com.example.backend.db.repositories.StudyProgramRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private static final Logger logger = LogManager.getLogger(CourseService.class);
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
        logger.info("Course saved with ID: {}", newCourse.getId());
        return newCourse;
    }

    public Course getById(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null) {
            logger.info("Course found with ID: {}", id);
        } else {
            logger.warn("Course not found with ID: {}", id);
        }
        return course;
    }

    public List<Course> getAll() {
        List<Course> courses = courseRepository.findAll();
        logger.info("Retrieved all courses, count: {}", courses.size());
        return courses;
    }

    public Course update(Course course) {
        Course existingCourse = courseRepository.findById(course.getId()).orElse(null);
        if (existingCourse != null) {
            existingCourse.setName(course.getName());
            existingCourse.setNumber(course.getNumber());
            existingCourse.setLector(course.getLector());

            Course updatedCourse = courseRepository.save(existingCourse);
            logger.info("Course updated successfully for ID: {}", course.getId());
            return updatedCourse;
        } else {
            logger.error("Failed to find course with ID: {}", course.getId());
            throw new RuntimeException("Course not found");
        }
    }

    public void remove(Long id) {
        try {
            courseRepository.deleteById(id);
            logger.info("Course deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete course with ID: {}", id, e);
            throw e;
        }
    }
}
