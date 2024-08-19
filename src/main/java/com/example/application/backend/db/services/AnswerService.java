package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.Answer;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.repositories.AnswerRepository;
import com.example.application.backend.db.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    public Set<Answer> addAnswers(Long questionId, Set<Answer> answers) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            Logger.log(this.getClass().getName(), "Question not found with ID: " + questionId, LogLevel.ERROR);
            return new RuntimeException("Question not found");
        });
        for (Answer answer : answers) {
            answer.setQuestion(question);
        }
        Logger.log(this.getClass().getName(), "Saving multiple Answers", LogLevel.INFO);
        return new HashSet<>(answerRepository.saveAll(answers));
    }

    public Answer add(Answer answer, Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            Logger.log(this.getClass().getName(), "Question not found with ID: " + questionId, LogLevel.ERROR);
            return new RuntimeException("Question not found");
        });
        answer.setQuestion(question);
        Answer newAnswer = answerRepository.save(answer);
        Logger.log(this.getClass().getName(), "Saving Answer with ID: " + answer.getId(), LogLevel.INFO);
        return newAnswer;
    }

    public Answer getById(Long id) {
        Answer answer = answerRepository.findById(id).orElse(null);
        if (answer != null) {
            Logger.log(this.getClass().getName(), "Answer found with ID: " + id, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Answer not found with ID: " + id, LogLevel.WARN);
        }
        return answer;
    }

    public Answer getByAnswer(String answer) {
        Answer result = answerRepository.findAnswerByAnswer(answer);
        if (result != null) {
            Logger.log(this.getClass().getName(), "Answer found with answer: " + answer, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Answer not found with answer: " + answer, LogLevel.WARN);
        }
        return result;
    }

    public Long getMaxAnswerId() {
        return answerRepository.getMaxAnswerId();
    }

    public List<Answer> getAll() {
        List<Answer> answers = answerRepository.findAll();
        Logger.log(this.getClass().getName(), "Retrieved all answers, count: " + answers.size(), LogLevel.INFO);
        return answers;
    }

    public Answer update(Answer answer) {
        Answer existingAnswer = answerRepository.findById(answer.getId()).orElse(null);
        if (existingAnswer != null) {
            existingAnswer.setAnswer(answer.getAnswer());

            Answer updatedAnswer = answerRepository.save(existingAnswer);
            Logger.log(this.getClass().getName(), "Answer updated successfully for ID: " + answer.getId(), LogLevel.INFO);
            return updatedAnswer;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find answer with ID: " + answer.getId(), LogLevel.ERROR);
            throw new RuntimeException("Answer not found");
        }
    }

    public void remove(Long id) {
        try {
            answerRepository.deleteById(id);
            Logger.log(this.getClass().getName(), "Answer deleted successfully with ID: " + id, LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(this.getClass().getName(), "Failed to delete answer with ID: " + id, LogLevel.ERROR);
            throw e;
        }
    }

    public void removeAllByQuestionId(Long questionId) {
        answerRepository.deleteByQuestionId(questionId);
        Logger.log(this.getClass().getName(), "Deleting answers for question with id: " + questionId, LogLevel.INFO);
    }
}
