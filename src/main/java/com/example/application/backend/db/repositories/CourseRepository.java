package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c JOIN c.studyPrograms sp " +
            "WHERE c.name = :name AND sp.id = :studyProgramId")
    Course findCourseByNameAndStudyPrograms(String name, Long studyProgramId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category cat JOIN cat.courses c WHERE c.id = :courseId")
    boolean hasCategories(@Param("courseId") Long courseId);

    @Query("SELECT c FROM Course c JOIN c.studyPrograms sp WHERE sp.id = :studyProgramId")
    List<Course> findAllCoursesByStudyProgramId(@Param("studyProgramId") Long studyProgramId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Course c JOIN c.studyPrograms sp " +
            "WHERE (c.number = :number OR c.name = :name) " +
            "AND sp.id = :studyProgramId")
    boolean existsCourseByNumberOrNameAndStudyProgramId(@Param("number") Integer number,
                                                        @Param("name") String name,
                                                        @Param("studyProgramId") Long studyProgramId);

}
