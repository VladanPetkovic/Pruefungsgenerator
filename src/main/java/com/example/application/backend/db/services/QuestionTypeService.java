package com.example.application.backend.db.services;

import com.example.application.backend.db.models.QuestionType;
import com.example.application.backend.db.repositories.QuestionTypeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionTypeService {
    private static final Logger logger = LogManager.getLogger(QuestionTypeService.class);
    private final QuestionTypeRepository questionTypeRepository;

    @Autowired
    public QuestionTypeService(QuestionTypeRepository questionTypeRepository) {
        this.questionTypeRepository = questionTypeRepository;
    }

    public QuestionType add(QuestionType questionType) {
        QuestionType newQuestionType = questionTypeRepository.save(questionType);
        logger.info("QuestionType saved with ID: {}", newQuestionType.getId());
        return newQuestionType;
    }

    public QuestionType getById(Long id) {
        QuestionType questionType = questionTypeRepository.findById(id).orElse(null);
        if (questionType != null) {
            logger.info("QuestionType found with ID: {}", id);
        } else {
            logger.warn("QuestionType not found with ID: {}", id);
        }
        return questionType;
    }

    public List<QuestionType> getAll() {
        List<QuestionType> questionTypes = questionTypeRepository.findAll();
        logger.info("Retrieved all question types, count: {}", questionTypes.size());
        return questionTypes;
    }

    public QuestionType update(QuestionType questionType) {
        QuestionType existingQuestionType = questionTypeRepository.findById(questionType.getId()).orElse(null);
        if (existingQuestionType != null) {
            existingQuestionType.setName(questionType.getName());

            QuestionType updatedQuestionType = questionTypeRepository.save(existingQuestionType);
            logger.info("QuestionType updated successfully for ID: {}", questionType.getId());
            return updatedQuestionType;
        } else {
            logger.error("Failed to find question type with ID: {}", questionType.getId());
            throw new RuntimeException("Question type not found");
        }
    }

    public void remove(Long id) {
        try {
            questionTypeRepository.deleteById(id);
            logger.info("QuestionType deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete question type with ID: {}", id, e);
            throw e;
        }
    }
}
