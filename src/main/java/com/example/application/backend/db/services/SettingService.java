package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.Setting;
import com.example.application.backend.db.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingService {
    private final SettingRepository settingRepository;

    @Autowired
    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public boolean settingExists() {
        return settingRepository.existsBy();
    }

    public Setting addIfNotExisting(Setting setting) {
        if (settingExists()) {
            Logger.log(this.getClass().getName(), "Setting already exists", LogLevel.INFO);
            return null;
        }
        Setting newSetting = settingRepository.save(setting);
        Logger.log(this.getClass().getName(), "Setting saved with ID: " + newSetting.getId(), LogLevel.INFO);
        return newSetting;
    }

    public Setting getFirstSetting() {
        return settingRepository.getFirstSetting();
    }

    public Integer getLanguage() {
        return settingRepository.getFirstLanguage();
    }

    public Double getWidth() {
        return settingRepository.getFirstWidth();
    }

    public Double getHeight() {
        return settingRepository.getFirstHeight();
    }

    public Setting updateLanguage(int newLang) {
        Setting existingSetting = settingRepository.getFirstSetting();
        if (existingSetting != null) {
            existingSetting.setLanguage(newLang);

            Setting updatedSetting = settingRepository.save(existingSetting);
            Logger.log(this.getClass().getName(), "Setting updated successfully for ID: " + existingSetting.getId(), LogLevel.INFO);
            return updatedSetting;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find first setting.", LogLevel.ERROR);
            throw new RuntimeException("Setting not found");
        }
    }

    public Setting updateWindowSize(Double width, Double height) {
        Setting existingSetting = settingRepository.getFirstSetting();
        if (existingSetting != null) {
            existingSetting.setDisplayWidth(width);
            existingSetting.setDisplayHeight(height);

            Setting updatedSetting = settingRepository.save(existingSetting);
            Logger.log(this.getClass().getName(), "Setting updated successfully for ID: " + existingSetting.getId(), LogLevel.INFO);
            return updatedSetting;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find first setting.", LogLevel.ERROR);
            throw new RuntimeException("Setting not found");
        }
    }
}
