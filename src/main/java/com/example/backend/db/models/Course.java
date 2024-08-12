package com.example.backend.db.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private String lector;

    @ManyToMany
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    private Set<StudyProgram> studyPrograms = new HashSet<>();

    public Course(String name, int number, String lector) {
        setName(name);
        setNumber(number);
        setLector(lector);
    }

//    public static Course createNewCourseInDatabase(String course, StudyProgram studyProgram) throws IOException {
//        // check for existence
//        Course newCourse = SQLiteDatabaseConnection.COURSE_REPOSITORY.get(course);
//
//        if (newCourse == null) {
//            Course addToDatabase = new Course();
//            addToDatabase.setName(course);
//            SQLiteDatabaseConnection.COURSE_REPOSITORY.add(addToDatabase);
//            newCourse = SQLiteDatabaseConnection.COURSE_REPOSITORY.get(course);
//            SQLiteDatabaseConnection.COURSE_REPOSITORY.addConnection(studyProgram, newCourse);
//        } else {
//            SQLiteDatabaseConnection.COURSE_REPOSITORY.addConnection(studyProgram, newCourse);
//        }
//
//        return newCourse;
//    }

}
