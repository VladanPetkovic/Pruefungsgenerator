package com.example.backend.db.services;

import com.example.backend.db.models.Answer;
import com.example.backend.db.models.Question;
import com.example.backend.db.repositories.AnswerRepository;
import com.example.backend.db.repositories.QuestionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {
    private static final Logger logger = LogManager.getLogger(AnswerService.class);
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    public Answer add(Answer answer, Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
           logger.error("Question not found with ID: {}", questionId);
           return new RuntimeException("Question not found");
        });
        answer.setQuestion(question);
        Answer newAnswer = answerRepository.save(answer);
        logger.info("Saving Answer with ID: {}", answer.getId());
        return newAnswer;
    }

    public Answer getById(Long id) {
        Answer answer = answerRepository.findById(id).orElse(null);
        if (answer != null) {
            logger.info("Answer found with ID: {}", id);
        } else {
            logger.warn("Answer not found with ID: {}", id);
        }
        return answer;
    }

    public List<Answer> getAll() {
        List<Answer> answers = answerRepository.findAll();
        logger.info("Retrieved all tours, count: {}", answers.size());
        return answers;
    }

    public Answer update(Answer answer) {
        Answer existingAnswer = answerRepository.findById(answer.getId()).orElse(null);
        if (existingAnswer != null) {
            existingAnswer.setAnswer(answer.getAnswer());

            Answer updatedAnswer = answerRepository.save(existingAnswer);
            logger.info("Answer updated successfully for ID: {}", answer.getId());
            return updatedAnswer;
        } else {
            logger.error("Failed to find answer with ID: {}", answer.getId());
            throw new RuntimeException("Answer not found");
        }
    }

    public void remove(Long id) {
        try {
            answerRepository.deleteById(id);
            logger.info("Answer deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete answer with ID: {}", id, e);
            throw e;
        }
    }
}
