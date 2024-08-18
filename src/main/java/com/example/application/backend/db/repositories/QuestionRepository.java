package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT COUNT(q) FROM Question q JOIN q.category cat JOIN cat.courses c JOIN c.studyPrograms sp WHERE sp.id = :studyProgramId")
    long getCountByStudyProgramId(@Param("studyProgramId") Long studyProgramId);

    @Query("SELECT COUNT(q) FROM Question q JOIN q.category cat JOIN cat.courses c WHERE c.id = :courseId")
    long getCountByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT q FROM Question q JOIN q.category cat JOIN cat.courses c WHERE c.id = :courseId")
    List<Question> findQuestionsByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT q FROM Question q JOIN q.category cat JOIN cat.courses c WHERE c.id = :courseId AND q.id > :minQuestionId")
    List<Question> findByCourseIdAndIdGreaterThan(@Param("courseId") Long courseId, @Param("minQuestionId") Long minQuestionId, Pageable pageable);

    @Query("SELECT MAX(q.id) FROM Question q")
    Long getMaxQuestionId();

    @Query("SELECT q FROM Question q " +
            "JOIN q.category cat JOIN cat.courses c " +
            "LEFT JOIN q.keywords k " +
            "WHERE (:question IS NULL OR q.question = :question) " +
            "AND (:difficulty IS NULL OR q.difficulty = :difficulty) " +
            "AND (:minDifficulty IS NULL OR q.difficulty >= :minDifficulty) " +
            "AND (:maxDifficulty IS NULL OR q.difficulty <= :maxDifficulty) " +
            "AND (:points IS NULL OR q.points = :points) " +
            "AND (:minPoints IS NULL OR q.points >= :minPoints) " +
            "AND (:maxPoints IS NULL OR q.points <= :maxPoints) " +
            "AND (:categoryId IS NULL OR q.category.id = :categoryId) " +
            "AND (:type IS NULL OR q.type = :type) " +
            "AND (:keywords IS NULL OR k.keyword IN :keywords) " +
            "AND c.id = :courseId")
    List<Question> findByFilters(
            @Param("question") String question,
            @Param("difficulty") Integer difficulty,
            @Param("minDifficulty") Integer minDifficulty,
            @Param("maxDifficulty") Integer maxDifficulty,
            @Param("points") Float points,
            @Param("minPoints") Float minPoints,
            @Param("maxPoints") Float maxPoints,
            @Param("categoryId") Long categoryId,
            @Param("type") String type,
            @Param("keywords") Set<String> keywords,
            @Param("courseId") Long courseId
    );
}
