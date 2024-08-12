package com.example.backend.db.services;

import com.example.backend.db.models.Question;
import com.example.backend.db.repositories.QuestionRepository;
import com.example.backend.db.repositories.CategoryRepository;
import com.example.backend.db.repositories.QuestionTypeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private static final Logger logger = LogManager.getLogger(QuestionService.class);
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionTypeRepository questionTypeRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository,
                           CategoryRepository categoryRepository,
                           QuestionTypeRepository questionTypeRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.questionTypeRepository = questionTypeRepository;
    }

    public Question add(Question question, Long categoryId, Long questionTypeId) {
        question.setCategory(categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.error("Category not found with ID: {}", categoryId);
            return new RuntimeException("Category not found");
        }));

        question.setType(questionTypeRepository.findById(questionTypeId).orElseThrow(() -> {
            logger.error("QuestionType not found with ID: {}", questionTypeId);
            return new RuntimeException("QuestionType not found");
        }));

        Question newQuestion = questionRepository.save(question);
        logger.info("Question saved with ID: {}", newQuestion.getId());
        return newQuestion;
    }

    public Question getById(Long id) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question != null) {
            logger.info("Question found with ID: {}", id);
        } else {
            logger.warn("Question not found with ID: {}", id);
        }
        return question;
    }

    public List<Question> getAll() {
        List<Question> questions = questionRepository.findAll();
        logger.info("Retrieved all questions, count: {}", questions.size());
        return questions;
    }

    public Question update(Question question, Long categoryId, Long questionTypeId) {
        Question existingQuestion = questionRepository.findById(question.getId()).orElse(null);
        if (existingQuestion != null) {
            existingQuestion.setCategory(categoryRepository.findById(categoryId).orElseThrow(() -> {
                logger.error("Category not found with ID: {}", categoryId);
                return new RuntimeException("Category not found");
            }));

            existingQuestion.setType(questionTypeRepository.findById(questionTypeId).orElseThrow(() -> {
                logger.error("QuestionType not found with ID: {}", questionTypeId);
                return new RuntimeException("QuestionType not found");
            }));

            existingQuestion.setDifficulty(question.getDifficulty());
            existingQuestion.setPoints(question.getPoints());
            existingQuestion.setQuestion(question.getQuestion());
            existingQuestion.setRemark(question.getRemark());

            Question updatedQuestion = questionRepository.save(existingQuestion);
            logger.info("Question updated successfully for ID: {}", question.getId());
            return updatedQuestion;
        } else {
            logger.error("Failed to find question with ID: {}", question.getId());
            throw new RuntimeException("Question not found");
        }
    }

    public void remove(Long id) {
        try {
            questionRepository.deleteById(id);
            logger.info("Question deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete question with ID: {}", id, e);
            throw e;
        }
    }
}
