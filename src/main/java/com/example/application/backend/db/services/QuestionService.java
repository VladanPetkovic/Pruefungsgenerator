package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.SortType;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.repositories.QuestionRepository;
import com.example.application.backend.db.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository,
                           CategoryRepository categoryRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
    }

    public Question add(Question question) {
        checkCategory(question, question.getCategory().getId());
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

    public List<Question> getAllByCourseId(Long courseId) {
        List<Question> questions = questionRepository.findQuestionsByCourseId(courseId);
        Logger.log(this.getClass().getName(), "Retrieved all questions for a course, count: " + questions.size(), LogLevel.INFO);
        return questions;
    }

    /**
     * Filters questions.
     *
     * @param filterQuestion         The question with fields used for filtering
     * @param courseId               The course we are filtering for
     * @param pointsFilterMethod     0 = disabled; 1 = enabled; 2 = min; 3 = max
     * @param difficultyFilterMethod 0 = disabled; 1 = enabled; 2 = min; 3 = max
     * @param sortType               The type we are sorting by (points, difficulty, createdAt, updatedAt)
     * @param sortDirection          The sort-direction (0 = ASC; 1 = DESC)
     * @return List of questions
     */
    public List<Question> getByFilters(Question filterQuestion, Long courseId, int pointsFilterMethod, int difficultyFilterMethod, SortType sortType, int sortDirection) {
        Long categoryId = filterQuestion.getCategory() != null ? filterQuestion.getCategory().getId() : null;

        // sort via points, difficulty,...
        Sort.Direction direction = sortDirection == 0 ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, SortType.getSortTypeLowercase(sortType));
        Pageable pageable = PageRequest.of(0, 50, sort);

        // set filter values for difficulty and points
        Integer difficulty = difficultyFilterMethod == 1 ? filterQuestion.getDifficulty() : null;
        Integer minDifficulty = difficultyFilterMethod == 2 ? filterQuestion.getDifficulty() : null;
        Integer maxDifficulty = difficultyFilterMethod == 3 ? filterQuestion.getDifficulty() : null;
        Float points = pointsFilterMethod == 1 ? filterQuestion.getPoints() : null;
        Float minPoints = pointsFilterMethod == 2 ? filterQuestion.getPoints() : null;
        Float maxPoints = pointsFilterMethod == 3 ? filterQuestion.getPoints() : null;

        List<Question> questions = questionRepository.findByFilters(
                filterQuestion.getQuestion(),
                difficulty,
                minDifficulty,
                maxDifficulty,
                points,
                minPoints,
                maxPoints,
                categoryId,
                filterQuestion.getType(),
                filterQuestion.getKeywords(),
                (long) filterQuestion.getKeywords().size(),
                courseId,
                pageable);
        Logger.log(this.getClass().getName(), "Retrieved all filtered questions, count: " + questions.size(), LogLevel.INFO);
        return questions;
    }

    /**
     * This db-call is solely used when exporting to avoid a stack-overflow (getting to many questions at once)
     *
     * @param courseId      The id for the course the questions belong to.
     * @param minQuestionId The next questionId to get another 250 questions.
     * @return List of questions.
     */
    public List<Question> getAllByCourseAndIdGreaterThan(Long courseId, Long minQuestionId) {
        Pageable pageable = PageRequest.of(0, 100);
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

    public Question update(Question q) {
        Question existingQuestion = questionRepository.findById(q.getId()).orElse(null);
        if (existingQuestion != null) {
            checkCategory(existingQuestion, q.getCategory().getId());

            existingQuestion.setDifficulty(q.getDifficulty());
            existingQuestion.setPoints(q.getPoints());
            existingQuestion.setQuestion(q.getQuestion());
            existingQuestion.setRemark(q.getRemark());
            existingQuestion.setKeywords(q.getKeywords());
            existingQuestion.setUpdatedAt(q.getUpdatedAt());
            existingQuestion.setImages(q.getImages());
            existingQuestion.setAnswers(q.getAnswers());

            Question updatedQuestion = questionRepository.save(existingQuestion);
            Logger.log(this.getClass().getName(), "Question updated successfully for ID: " + q.getId(), LogLevel.INFO);
            return updatedQuestion;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find question with ID: " + q.getId(), LogLevel.ERROR);
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

    private void checkCategory(Question question, Long categoryId) {
        question.setCategory(categoryRepository.findById(categoryId).orElseThrow(() -> {
            Logger.log(this.getClass().getName(), "Category not found with ID: " + categoryId, LogLevel.ERROR);
            return new RuntimeException("Category not found");
        }));
    }
}
