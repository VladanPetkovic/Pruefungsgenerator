package com.example.application.backend.db.services;

import com.example.application.backend.app.SortType;
import com.example.application.backend.db.models.Category;
import com.example.application.backend.db.models.Keyword;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.repositories.QuestionRepository;
import com.example.application.backend.db.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceTests {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void add_ShouldSaveQuestion_WhenCategoryExists() {
        Question question = new Question();
        Category category = new Category();
        category.setId(1L);
        question.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(questionRepository.save(question)).thenReturn(question);

        Question savedQuestion = questionService.add(question);

        verify(questionRepository, times(1)).save(question);
        assertNotNull(savedQuestion);
    }

    @Test
    void add_ShouldThrowException_WhenCategoryDoesNotExist() {
        Question question = new Question();
        Category category = new Category();
        category.setId(1L);
        question.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> questionService.add(question));
    }

    @Test
    void getById_ShouldReturnQuestion_WhenExists() {
        Question question = new Question();
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Question foundQuestion = questionService.getById(1L);

        assertNotNull(foundQuestion);
        assertEquals(1L, foundQuestion.getId());
    }

    @Test
    void getById_ShouldReturnNull_WhenDoesNotExist() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        Question question = questionService.getById(1L);

        assertNull(question);
    }

    @Test
    void getAll_ShouldReturnAllQuestions() {
        List<Question> questions = List.of(new Question(), new Question());

        when(questionRepository.findAll()).thenReturn(questions);

        List<Question> foundQuestions = questionService.getAll();

        assertEquals(2, foundQuestions.size());
    }

    @Test
    void getAllByCourseId_ShouldReturnQuestionsForCourse() {
        List<Question> questions = List.of(new Question(), new Question());

        when(questionRepository.findQuestionsByCourseId(1L)).thenReturn(questions);

        List<Question> foundQuestions = questionService.getAllByCourseId(1L);

        assertEquals(2, foundQuestions.size());
    }

    @Test
    void getByFilters_ShouldReturnAllQuestions_WhenAllFiltersDisabled() {
        Question filterQuestion = new Question();
        Long courseId = 1L;
        List<Question> questions = List.of(new Question(), new Question());

        when(questionRepository.findByFilters(
                any(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), any(), any(), anyLong(), eq(courseId), any(Pageable.class))
        ).thenReturn(questions);

        List<Question> result = questionService.getByFilters(filterQuestion, courseId, 0, 0, SortType.POINTS, 0);

        assertEquals(2, result.size());
    }

    @Test
    void getByFilters_ShouldReturnFilteredQuestions_WhenPointsMinFilterEnabled() {
        Question filterQuestion = new Question();
        filterQuestion.setPoints(5.0f);
        Long courseId = 1L;
        List<Question> questions = List.of(new Question());

        when(questionRepository.findByFilters(
                any(), isNull(), isNull(), isNull(), isNull(), eq(5.0f), isNull(),
                isNull(), any(), any(), anyLong(), eq(courseId), any(Pageable.class))
        ).thenReturn(questions);

        List<Question> result = questionService.getByFilters(filterQuestion, courseId, 2, 0, SortType.POINTS, 0);

        assertEquals(1, result.size());
    }

    @Test
    void getByFilters_ShouldReturnFilteredQuestions_WhenDifficultyMaxFilterEnabled() {
        Question filterQuestion = new Question();
        filterQuestion.setDifficulty(3);
        Long courseId = 1L;
        List<Question> questions = List.of(new Question(), new Question());

        when(questionRepository.findByFilters(
                any(), isNull(), isNull(), eq(3), isNull(), isNull(), isNull(),
                isNull(), any(), any(), anyLong(), eq(courseId), any(Pageable.class))
        ).thenReturn(questions);

        List<Question> result = questionService.getByFilters(filterQuestion, courseId, 0, 3, SortType.DIFFICULTY, 1);

        assertEquals(2, result.size());
    }

    @Test
    void getByFilters_ShouldReturnFilteredQuestions_WhenCategoryFilterEnabled() {
        Question filterQuestion = new Question();
        Category category = new Category();
        category.setId(1L);
        filterQuestion.setCategory(category);
        Long courseId = 1L;
        List<Question> questions = List.of(new Question());

        when(questionRepository.findByFilters(
                any(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(1L), any(), any(), anyLong(), eq(courseId), any(Pageable.class))
        ).thenReturn(questions);

        List<Question> result = questionService.getByFilters(filterQuestion, courseId, 0, 0, SortType.CREATED_AT, 0);

        assertEquals(1, result.size());
    }

    @Test
    void getByFilters_ShouldReturnFilteredQuestions_WhenKeywordsFilterEnabled() {
        Question filterQuestion = new Question();
        Set<Keyword> keywords = Set.of(new Keyword(), new Keyword());
        filterQuestion.setKeywords(keywords);
        Long courseId = 1L;
        List<Question> questions = List.of(new Question());

        when(questionRepository.findByFilters(
                any(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), any(), eq(keywords), eq((long) keywords.size()), eq(courseId), any(Pageable.class))
        ).thenReturn(questions);

        List<Question> result = questionService.getByFilters(filterQuestion, courseId, 0, 0, SortType.UPDATED_AT, 1);

        assertEquals(1, result.size());
    }

    @Test
    void getByFilters_ShouldSortByPointsDescending() {
        Question filterQuestion = new Question();
        Long courseId = 1L;
        List<Question> questions = List.of(new Question(), new Question());

        Sort expectedSort = Sort.by(Sort.Direction.DESC, "points");
        Pageable expectedPageable = PageRequest.of(0, 50, expectedSort);

        when(questionRepository.findByFilters(
                any(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), any(), any(), anyLong(), eq(courseId), eq(expectedPageable))
        ).thenReturn(questions);

        List<Question> result = questionService.getByFilters(filterQuestion, courseId, 0, 0, SortType.POINTS, 1);

        assertEquals(2, result.size());
    }

    @Test
    void getByFilters_ShouldReturnFilteredQuestions_WhenAllFiltersEnabled() {
        Question filterQuestion = new Question();
        filterQuestion.setQuestion("Sample question");
        filterQuestion.setDifficulty(2);
        filterQuestion.setPoints(10.0f);
        Set<Keyword> keywords = Set.of(new Keyword());
        filterQuestion.setKeywords(keywords);
        Long courseId = 1L;

        List<Question> questions = List.of(new Question(), new Question());

        when(questionRepository.findByFilters(
                eq("Sample question"), eq(2), isNull(), isNull(), eq(10.0f), isNull(), isNull(),
                isNull(), any(), eq(keywords), eq((long) keywords.size()), eq(courseId), any(Pageable.class))
        ).thenReturn(questions);

        List<Question> result = questionService.getByFilters(filterQuestion, courseId, 1, 1, SortType.DIFFICULTY, 0);

        assertEquals(2, result.size());
    }

    @Test
    void getCount_ShouldReturnQuestionCount() {
        when(questionRepository.count()).thenReturn(10L);

        long count = questionService.getCount();

        assertEquals(10L, count);
    }

    @Test
    void getCountByStudyProgram_ShouldReturnCountByStudyProgram() {
        when(questionRepository.getCountByStudyProgramId(1L)).thenReturn(5L);

        long count = questionService.getCountByStudyProgram(1L);

        assertEquals(5L, count);
    }

    @Test
    void update_ShouldUpdateQuestion_WhenQuestionExists() {
        Question existingQuestion = new Question();
        existingQuestion.setId(1L);

        Question updatedQuestion = new Question();
        updatedQuestion.setId(1L);

        Category category = new Category();
        category.setId(1L);
        updatedQuestion.setCategory(category);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(questionRepository.save(existingQuestion)).thenReturn(updatedQuestion);

        Question result = questionService.update(updatedQuestion);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void update_ShouldThrowException_WhenQuestionDoesNotExist() {
        Question question = new Question();
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> questionService.update(question));
    }

    @Test
    void remove_ShouldDeleteQuestion_WhenExists() {
        doNothing().when(questionRepository).deleteById(1L);

        questionService.remove(1L);

        verify(questionRepository, times(1)).deleteById(1L);
    }

    @Test
    void remove_ShouldHandleException_WhenDeleteFails() {
        doThrow(new RuntimeException("Delete failed")).when(questionRepository).deleteById(1L);

        assertThrows(RuntimeException.class, () -> questionService.remove(1L));
    }

    @Test
    void checkCategory_ShouldThrowException_WhenCategoryDoesNotExist() {
        Question question = new Question();
        Category category = new Category();
        category.setId(1L);
        question.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> questionService.add(question));
    }
}

