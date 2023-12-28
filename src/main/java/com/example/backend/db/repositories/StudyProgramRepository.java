package com.example.backend.db.repositories;

import com.example.backend.db.daos.StudyProgramDAO;
import com.example.backend.db.models.StudyProgram;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class StudyProgramRepository implements Repository<StudyProgram> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    StudyProgramDAO studyProgramDAO;

    public StudyProgramRepository(StudyProgramDAO studyProgramDAO) {
        setStudyProgramDAO(studyProgramDAO);
    }
    @Override
    public ArrayList<StudyProgram> getAll() {
        return getStudyProgramDAO().readAll();
    }

    @Override
    public StudyProgram get(int id) {
        return null;
    }

    @Override
    public void add(StudyProgram type) {

    }

    @Override
    public void update(StudyProgram program) {

    }

    @Override
    public void remove(StudyProgram program) {

    }
}
