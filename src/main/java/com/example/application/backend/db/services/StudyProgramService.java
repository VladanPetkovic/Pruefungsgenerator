package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.StudyProgram;
import com.example.application.backend.db.repositories.StudyProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyProgramService {
    private final StudyProgramRepository studyProgramRepository;

    @Autowired
    public StudyProgramService(StudyProgramRepository studyProgramRepository) {
        this.studyProgramRepository = studyProgramRepository;
    }

    public boolean studyProgramExists(String name, String abbreviation) {
        return studyProgramRepository.existsStudyProgramByNameOrAbbreviation(name, abbreviation);
    }

    public boolean hasCourses(Long studyProgramId) {
        return studyProgramRepository.hasCourses(studyProgramId);
    }

    public StudyProgram add(StudyProgram studyProgram) {
        if (studyProgramExists(studyProgram.getName(), studyProgram.getAbbreviation())) {
            Logger.log(this.getClass().getName(), "StudyProgram already exists", LogLevel.INFO);
            return null;
        }
        StudyProgram newStudyProgram = studyProgramRepository.save(studyProgram);
        Logger.log(this.getClass().getName(), "StudyProgram saved with ID: " + newStudyProgram.getId(), LogLevel.INFO);
        return newStudyProgram;
    }

    /**
     * This function is currently only used for import.
     * @param name The studyProgram-name
     * @return the studyProgram (either created or found)
     */
    public StudyProgram add(String name) {
        if (studyProgramRepository.existsStudyProgramByName(name)) {
            Logger.log(this.getClass().getName(), "StudyProgram already exists", LogLevel.INFO);
            return studyProgramRepository.findStudyProgramByName(name);
        }
        StudyProgram newStudyProgram = studyProgramRepository.save(new StudyProgram(name, "IMPORT"));       // TODO: IMPORT so lassen?
        Logger.log(this.getClass().getName(), "StudyProgram saved with ID: " + newStudyProgram.getId(), LogLevel.INFO);
        return newStudyProgram;
    }

    public StudyProgram getById(Long id) {
        StudyProgram studyProgram = studyProgramRepository.findById(id).orElse(null);
        if (studyProgram != null) {
            Logger.log(this.getClass().getName(), "StudyProgram found with ID: " + id, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "StudyProgram not found with ID: " + id, LogLevel.WARN);
        }
        return studyProgram;
    }

    public StudyProgram getByName(String name) {
        StudyProgram studyProgram = studyProgramRepository.findStudyProgramByName(name);
        if (studyProgram != null) {
            Logger.log(this.getClass().getName(), "StudyProgram found with name: " + name, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "StudyProgram not found with name: " + name, LogLevel.WARN);
        }
        return studyProgram;
    }

    public List<StudyProgram> getAll() {
        List<StudyProgram> studyPrograms = studyProgramRepository.findAll();
        Logger.log(this.getClass().getName(), "Retrieved all study programs, count: " + studyPrograms.size(), LogLevel.INFO);
        return studyPrograms;
    }

    public StudyProgram update(StudyProgram studyProgram) {
        StudyProgram existingStudyProgram = studyProgramRepository.findById(studyProgram.getId()).orElse(null);
        if (existingStudyProgram != null) {
            existingStudyProgram.setName(studyProgram.getName());
            existingStudyProgram.setAbbreviation(studyProgram.getAbbreviation());

            StudyProgram updatedStudyProgram = studyProgramRepository.save(existingStudyProgram);
            Logger.log(this.getClass().getName(), "StudyProgram updated successfully for ID: " + studyProgram.getId(), LogLevel.INFO);
            return updatedStudyProgram;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find study program with ID: " + studyProgram.getId(), LogLevel.ERROR);
            throw new RuntimeException("Study program not found");
        }
    }

    public void remove(Long id) {
        try {
            studyProgramRepository.deleteById(id);
            Logger.log(this.getClass().getName(), "Study program deleted successfully with ID: " + id, LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(this.getClass().getName(), "Failed to delete study program with ID: " + id, LogLevel.ERROR);
            throw e;
        }
    }
}
