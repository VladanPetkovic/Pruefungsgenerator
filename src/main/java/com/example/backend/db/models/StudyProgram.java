package com.example.backend.db.models;

import com.example.backend.db.SQLiteDatabaseConnection;
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

    public static StudyProgram createNewStudyProgramInDatabase(String studyProgram) {
        // check for existence
        StudyProgram newStudyProgram = SQLiteDatabaseConnection.studyProgramRepository.get(studyProgram);

        if (newStudyProgram == null) {
            StudyProgram addToDatabase = new StudyProgram();
            addToDatabase.setName(studyProgram);
            SQLiteDatabaseConnection.studyProgramRepository.add(addToDatabase);
            newStudyProgram = SQLiteDatabaseConnection.studyProgramRepository.get(studyProgram);
        }

        return newStudyProgram;
    }

}
