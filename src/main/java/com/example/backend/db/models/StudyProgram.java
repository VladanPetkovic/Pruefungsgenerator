package com.example.backend.db.models;

import com.example.backend.db.SQLiteDatabaseConnection;
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
@Table(name = "study_programs")
public class StudyProgram implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String abbreviation;

    @ManyToMany
    private Set<Course> courses = new HashSet<>();

    public StudyProgram(String name, String abbreviation) {
        setName(name);
        setAbbreviation(abbreviation);
    }

//    public static StudyProgram createNewStudyProgramInDatabase(String studyProgram) {
//        // check for existence
//        StudyProgram newStudyProgram = SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.get(studyProgram);
//
//        if (newStudyProgram == null) {
//            StudyProgram addToDatabase = new StudyProgram();
//            addToDatabase.setName(studyProgram);
//            SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.add(addToDatabase);
//            newStudyProgram = SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.get(studyProgram);
//        }
//
//        return newStudyProgram;
//    }

}
