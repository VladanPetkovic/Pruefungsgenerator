package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.StudyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyProgramRepository extends JpaRepository<StudyProgram, Long> {
    StudyProgram findStudyProgramByName(String name);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c JOIN c.studyPrograms sp WHERE sp.id = :studyProgramId")
    boolean hasCourses(@Param("studyProgramId") Long studyProgramId);

    @Query("SELECT CASE WHEN COUNT(sp) > 0 THEN true ELSE false END FROM StudyProgram sp WHERE sp.name = :name OR sp.abbreviation = :abbreviation")
    boolean existsByNameOrAbbreviation(@Param("name") String name, @Param("abbreviation") String abbreviation);
}
