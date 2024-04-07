package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudyProgram {
    private int id;
    private String name;
    private String abbreviation;

    public StudyProgram(String name, String abbreviation) {
        setName(name);
        setAbbreviation(abbreviation);
    }

}
