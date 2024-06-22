package com.example.backend.db.models;

import com.example.backend.db.SQLiteDatabaseConnection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Course implements Serializable {
    private int id;
    private String name;
    private int number;
    private String lector;

    public Course(String name, int number, String lector)  {
        setName(name);
        setNumber(number);
        setLector(lector);
    }

    public static Course createNewCourseInDatabase(String course, StudyProgram studyProgram) throws IOException {
        // check for existence
        Course newCourse = SQLiteDatabaseConnection.COURSE_REPOSITORY.get(course);

        if (newCourse == null) {
            Course addToDatabase = new Course();
            addToDatabase.setName(course);
            SQLiteDatabaseConnection.COURSE_REPOSITORY.add(addToDatabase);
            newCourse = SQLiteDatabaseConnection.COURSE_REPOSITORY.get(course);
            SQLiteDatabaseConnection.COURSE_REPOSITORY.addConnection(studyProgram, newCourse);
        } else {
            SQLiteDatabaseConnection.COURSE_REPOSITORY.addConnection(studyProgram, newCourse);
        }

        return newCourse;
    }

}
