package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

}
