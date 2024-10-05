package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    @Query("SELECT s.language FROM Setting s ORDER BY s.id ASC")
    Integer getFirstLanguage();

    @Query("SELECT s.displayWidth FROM Setting s ORDER BY s.id ASC")
    Double getFirstWidth();

    @Query("SELECT s.displayHeight FROM Setting s ORDER BY s.id ASC")
    Double getFirstHeight();

    @Query("SELECT s FROM Setting s ORDER BY s.id ASC")
    Setting getFirstSetting();

    boolean existsBy();
}

