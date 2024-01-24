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
    int course_id;
    String course_name;
    int course_number;
    String lector;

    public Course(String course_name, int course_number, String lector) {
        setCourse_name(course_name);
        setCourse_number(course_number);
        setLector(lector);
    }

}
