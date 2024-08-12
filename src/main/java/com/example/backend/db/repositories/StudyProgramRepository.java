package com.example.backend.db.repositories;

import com.example.backend.db.models.StudyProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyProgramRepository extends JpaRepository<StudyProgram, Long> {
//
//    public StudyProgram get(String name) {
//        return getStudyProgramDAO().read(name);
//    }
//
//    public boolean hasCourses(int studyProgramId) {
//        return getStudyProgramDAO().hasCourses(studyProgramId);
//    }
}
