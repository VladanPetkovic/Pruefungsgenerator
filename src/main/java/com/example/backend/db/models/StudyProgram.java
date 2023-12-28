package com.example.backend.db.models;

import com.example.backend.db.repositories.StudyProgramRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class StudyProgram {
    int program_id;
    String program_name;
    String program_abbr;

    public StudyProgram(String program_name, String program_abbr) {
        setProgram_name(program_name);
        setProgram_abbr(program_abbr);
    }
}
