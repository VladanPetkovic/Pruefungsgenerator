package com.example.backend.db.models;

import com.example.backend.db.SQLiteDatabaseConnection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private int id;
    private String name;
    private int number;
    private String lector;

    public Course(String name, int number, String lector) {
        setName(name);
        setNumber(number);
        setLector(lector);
    }

    public static Course createNewCourseInDatabase(String course, StudyProgram studyProgram) {
        // check for existence
        Course newCourse = SQLiteDatabaseConnection.courseRepository.get(course);

        if (newCourse == null) {
            Course addToDatabase = new Course();
            addToDatabase.setName(course);
            SQLiteDatabaseConnection.courseRepository.add(addToDatabase);
            newCourse = SQLiteDatabaseConnection.courseRepository.get(course);
            SQLiteDatabaseConnection.courseRepository.addConnection(studyProgram, newCourse);
        } else {
            SQLiteDatabaseConnection.courseRepository.addConnection(studyProgram, newCourse);
        }

        return newCourse;
    }

}
