package com.example.backend.db.models;

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

}
