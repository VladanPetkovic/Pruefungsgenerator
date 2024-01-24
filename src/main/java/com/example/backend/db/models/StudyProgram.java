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
    private int program_id;
    private String program_name;
    private String program_abbr;

    public StudyProgram(String program_name, String program_abbr) {
        setProgram_name(program_name);
        setProgram_abbr(program_abbr);
    }

}
