package com.example.application.backend.db.models;

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

    @ManyToMany(mappedBy = "studyPrograms")
    private Set<Course> courses = new HashSet<>();

    public StudyProgram(String name, String abbreviation) {
        setName(name);
        setAbbreviation(abbreviation);
    }

    public StudyProgram(Long id, String name, String abbreviation) {
        setId(id);
        setName(name);
        setAbbreviation(abbreviation);
    }
}
