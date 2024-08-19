package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Answer findAnswerByAnswer(String answer);

    @Query("SELECT MAX(a.id) FROM Answer a")
    Long getMaxAnswerId();

    List<Answer> findAnswersByQuestionId(Long questionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Answer a WHERE a.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") Long questionId);
}
