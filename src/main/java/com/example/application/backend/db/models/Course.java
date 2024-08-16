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

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Keyword> keywords = new HashSet<>();

    @ManyToMany(mappedBy = "courses")
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "study_programs_courses",
            joinColumns = @JoinColumn(name = "fk_course_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_study_program_id")
    )
    private Set<StudyProgram> studyPrograms = new HashSet<>();

    public Course(Long id, String name, int number, String lector) {
        setId(id);
        setName(name);
        setNumber(number);
        setLector(lector);
    }

    public Course(String name, int number, String lector) {
        setName(name);
        setNumber(number);
        setLector(lector);
    }
}
