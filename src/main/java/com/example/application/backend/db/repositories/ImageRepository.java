package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
