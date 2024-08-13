package com.example.application.backend.db.services;

import com.example.application.backend.db.models.StudyProgram;
import com.example.application.backend.db.repositories.StudyProgramRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyProgramService {
    private static final Logger logger = LogManager.getLogger(StudyProgramService.class);
    private final StudyProgramRepository studyProgramRepository;

    @Autowired
    public StudyProgramService(StudyProgramRepository studyProgramRepository) {
        this.studyProgramRepository = studyProgramRepository;
    }

    public boolean studyProgramExists(String name, String abbreviation) {
        return studyProgramRepository.existsByNameOrAbbreviation(name, abbreviation);
    }

    public StudyProgram add(StudyProgram studyProgram) {
        if (studyProgramExists(studyProgram.getName(), studyProgram.getAbbreviation())) {
            logger.info("StudyProgram already exists");
            return null;
        }
        StudyProgram newStudyProgram = studyProgramRepository.save(studyProgram);
        logger.info("StudyProgram saved with ID: {}", newStudyProgram.getId());
        return newStudyProgram;
    }

    public StudyProgram getById(Long id) {
        StudyProgram studyProgram = studyProgramRepository.findById(id).orElse(null);
        if (studyProgram != null) {
            logger.info("StudyProgram found with ID: {}", id);
        } else {
            logger.warn("StudyProgram not found with ID: {}", id);
        }
        return studyProgram;
    }

    public List<StudyProgram> getAll() {
        List<StudyProgram> studyPrograms = studyProgramRepository.findAll();
        logger.info("Retrieved all study programs, count: {}", studyPrograms.size());
        return studyPrograms;
    }

    public StudyProgram update(StudyProgram studyProgram) {
        StudyProgram existingStudyProgram = studyProgramRepository.findById(studyProgram.getId()).orElse(null);
        if (existingStudyProgram != null) {
            existingStudyProgram.setName(studyProgram.getName());
            existingStudyProgram.setAbbreviation(studyProgram.getAbbreviation());

            StudyProgram updatedStudyProgram = studyProgramRepository.save(existingStudyProgram);
            logger.info("StudyProgram updated successfully for ID: {}", studyProgram.getId());
            return updatedStudyProgram;
        } else {
            logger.error("Failed to find study program with ID: {}", studyProgram.getId());
            throw new RuntimeException("Study program not found");
        }
    }

    public void remove(Long id) {
        try {
            studyProgramRepository.deleteById(id);
            logger.info("Study program deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete study program with ID: {}", id, e);
            throw e;
        }
    }
}
