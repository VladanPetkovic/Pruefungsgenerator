package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Answer findAnswerByAnswer(String answer);

    @Query("SELECT MAX(a.id) FROM Answer a")
    Long getMaxAnswerId();
}
