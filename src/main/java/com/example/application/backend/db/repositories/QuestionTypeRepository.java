package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionTypeRepository extends JpaRepository<QuestionType, Long> {

}
