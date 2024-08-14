package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.QuestionType;
import com.example.application.backend.db.repositories.QuestionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionTypeService {
    private final QuestionTypeRepository questionTypeRepository;

    @Autowired
    public QuestionTypeService(QuestionTypeRepository questionTypeRepository) {
        this.questionTypeRepository = questionTypeRepository;
    }

    public QuestionType add(QuestionType questionType) {
        QuestionType newQuestionType = questionTypeRepository.save(questionType);
        Logger.log(this.getClass().getName(), "QuestionType saved with ID: " + newQuestionType.getId(), LogLevel.INFO);
        return newQuestionType;
    }

    public QuestionType getById(Long id) {
        QuestionType questionType = questionTypeRepository.findById(id).orElse(null);
        if (questionType != null) {
            Logger.log(this.getClass().getName(), "QuestionType found with ID: " + id, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "QuestionType not found with ID: " + id, LogLevel.WARN);
        }
        return questionType;
    }

    public List<QuestionType> getAll() {
        List<QuestionType> questionTypes = questionTypeRepository.findAll();
        Logger.log(this.getClass().getName(), "Retrieved all question types, count: " + questionTypes.size(), LogLevel.INFO);
        return questionTypes;
    }

    public QuestionType update(QuestionType questionType) {
        QuestionType existingQuestionType = questionTypeRepository.findById(questionType.getId()).orElse(null);
        if (existingQuestionType != null) {
            existingQuestionType.setName(questionType.getName());

            QuestionType updatedQuestionType = questionTypeRepository.save(existingQuestionType);
            Logger.log(this.getClass().getName(), "QuestionType updated successfully for ID: " + questionType.getId(), LogLevel.INFO);
            return updatedQuestionType;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find question type with ID: " + questionType.getId(), LogLevel.ERROR);
            throw new RuntimeException("Question type not found");
        }
    }

    public void remove(Long id) {
        try {
            questionTypeRepository.deleteById(id);
            Logger.log(this.getClass().getName(), "QuestionType deleted successfully with ID: " + id, LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(this.getClass().getName(), "Failed to delete question type with ID: " + id, LogLevel.ERROR);
            throw e;
        }
    }
}
