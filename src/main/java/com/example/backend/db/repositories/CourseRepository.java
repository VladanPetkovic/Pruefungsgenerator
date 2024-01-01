package com.example.backend.db.repositories;

import com.example.backend.db.daos.CourseDAO;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.StudyProgram;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class CourseRepository implements Repository<Course> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    CourseDAO courseDAO;

    public CourseRepository(CourseDAO courseDAO) {
        setCourseDAO(courseDAO);
    }

    @Override
    public ArrayList<Course> getAll() {
        return getCourseDAO().readAll();
    }

    // needed for selecting all courses for one Study-program
    public ArrayList<Course> getAll(int studyProgram_id) {
        return getCourseDAO().readAllForOneProgram(studyProgram_id);
    }

    @Override
    public Course get(int id) {
        return getCourseDAO().read(id);
    }

    @Override
    public void add(Course course) {
        getCourseDAO().create(course);
    }

    // addConnection: used for adding a new hasSC entry
    public void addConnection(StudyProgram studyProgram, Course course) {
        getCourseDAO().addSCConnection(studyProgram.getProgram_id(), course.getCourse_id());
    }

    @Override
    public void update(Course course) {
        getCourseDAO().update(course);
    }

    @Override
    public void remove(Course course) {
        getCourseDAO().delete(course.getCourse_id());
    }
}
