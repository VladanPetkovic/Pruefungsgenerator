package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    @Query("SELECT k FROM Keyword k JOIN k.questions q JOIN q.category cat JOIN cat.courses c WHERE c.id = :courseId")
    List<Keyword> findAllByCourseId(@Param("courseId") Long courseId);

    Keyword findKeywordByKeyword(String keyword);
}
