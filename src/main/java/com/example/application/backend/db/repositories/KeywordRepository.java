package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    @Query("SELECT k FROM Keyword k JOIN k.course c WHERE c.id = :courseId")
    List<Keyword> findAllByCourseId(@Param("courseId") Long courseId);

    Keyword findKeywordByKeyword(String keyword);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Keyword k JOIN k.course c " +
            "WHERE k.keyword = :name " +
            "AND c.id = :courseId")
    boolean existsKeywordByNameAndCourseId(@Param("name") String name,
                                           @Param("courseId") Long courseId);
}
