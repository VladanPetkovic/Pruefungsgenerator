package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.repositories.QuestionRepository;
import com.example.application.backend.db.repositories.CategoryRepository;
import com.example.application.backend.db.repositories.QuestionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
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
        checkCategoryAndQuestionType(question, categoryId, questionTypeId);

        Question newQuestion = questionRepository.save(question);
        Logger.log(this.getClass().getName(), "Question saved with ID: " + newQuestion.getId(), LogLevel.INFO);
        return newQuestion;
    }

    public Question getById(Long id) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question != null) {
            Logger.log(this.getClass().getName(), "Question found with ID: " + id, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Question not found with ID: " + id, LogLevel.WARN);
        }
        return question;
    }

    public List<Question> getAll() {
        List<Question> questions = questionRepository.findAll();
        Logger.log(this.getClass().getName(), "Retrieved all questions, count: " + questions.size(), LogLevel.INFO);
        return questions;
    }

    public List<Question> getAllByCategory(Long categoryId) {
        List<Question> questions = questionRepository.findQuestionsByCategoryId(categoryId);
        Logger.log(this.getClass().getName(), "Retrieved all questions for a category, count: " + questions.size(), LogLevel.INFO);
        return questions;
    }

    /**
     * This db-call is solely used when exporting to avoid a stack-overflow (getting to many questions at once)
     * @param courseId The id for the course the questions belong to.
     * @param minQuestionId The next questionId to get another 250 questions.
     * @return List of questions.
     */
    public List<Question> getAllByCourseAndIdGreaterThan(Long courseId, Long minQuestionId) {
        Pageable pageable = PageRequest.of(0, 250);
        List<Question> questions = questionRepository.findByCourseIdAndIdGreaterThan(courseId, minQuestionId, pageable);
        Logger.log(this.getClass().getName(), "Retrieved all questions for a category, count: " + questions.size(), LogLevel.INFO);
        return questions;
    }

    public long getCount() {
        return questionRepository.count();
    }

    public long getCountByStudyProgram(Long studyProgramId) {
        return questionRepository.getCountByStudyProgramId(studyProgramId);
    }

    public long getCountByCourse(Long courseId) {
        return questionRepository.getCountByCourseId(courseId);
    }

    public Long getMaxQuestionId() {
        return questionRepository.getMaxQuestionId();
    }

    public Question update(Question question, Long categoryId, Long questionTypeId) {
        Question existingQuestion = questionRepository.findById(question.getId()).orElse(null);
        if (existingQuestion != null) {
            checkCategoryAndQuestionType(existingQuestion, categoryId, questionTypeId);

            existingQuestion.setDifficulty(question.getDifficulty());
            existingQuestion.setPoints(question.getPoints());
            existingQuestion.setQuestion(question.getQuestion());
            existingQuestion.setRemark(question.getRemark());

            Question updatedQuestion = questionRepository.save(existingQuestion);
            Logger.log(this.getClass().getName(), "Question updated successfully for ID: " + question.getId(), LogLevel.INFO);
            return updatedQuestion;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find question with ID: " + question.getId(), LogLevel.ERROR);
            throw new RuntimeException("Question not found");
        }
    }

    public void remove(Long id) {
        try {
            questionRepository.deleteById(id);
            Logger.log(this.getClass().getName(), "Question deleted successfully with ID: " + id, LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(this.getClass().getName(), "Failed to delete question with ID: " + id, LogLevel.ERROR);
            throw e;
        }
    }

    private void checkCategoryAndQuestionType(Question question, Long categoryId, Long questionTypeId) {
        question.setCategory(categoryRepository.findById(categoryId).orElseThrow(() -> {
            Logger.log(this.getClass().getName(), "Category not found with ID: " + categoryId, LogLevel.ERROR);
            return new RuntimeException("Category not found");
        }));

        question.setType(questionTypeRepository.findById(questionTypeId).orElseThrow(() -> {
            Logger.log(this.getClass().getName(), "QuestionType not found with ID: " + questionTypeId, LogLevel.ERROR);
            return new RuntimeException("QuestionType not found");
        }));
    }
}
