package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.Course;
import com.example.application.backend.db.models.Keyword;
import com.example.application.backend.db.models.KeywordWrapper;
import com.example.application.backend.db.repositories.CourseRepository;
import com.example.application.backend.db.repositories.KeywordRepository;
import com.example.application.backend.db.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public KeywordService(KeywordRepository keywordRepository, QuestionRepository questionRepository, CourseRepository courseRepository) {
        this.keywordRepository = keywordRepository;
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
    }

    public boolean keywordExists(String name, Long courseId) {
        return keywordRepository.existsKeywordByNameAndCourseId(name, courseId);
    }

    public Keyword add(Keyword keyword, Course course) {
        if (keywordExists(keyword.getKeyword(), course.getId())) {
            Logger.log(this.getClass().getName(), "Keyword already exists for this course", LogLevel.INFO);
            return keywordRepository.findByNameAndCourseId(keyword.getKeyword(), course.getId());
        }
        keyword.setCourse(course);
        Keyword newKeyword = keywordRepository.save(keyword);
        Logger.log(this.getClass().getName(), "Keyword saved with ID: " + newKeyword.getId(), LogLevel.INFO);
        return newKeyword;
    }

    public Keyword getById(Long id) {
        Keyword keyword = keywordRepository.findById(id).orElse(null);
        if (keyword != null) {
            Logger.log(this.getClass().getName(), "Keyword found with ID: " + id, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Keyword not found with ID: " + id, LogLevel.WARN);
        }
        return keyword;
    }

    public Keyword getByName(String name, Course course) {
        Keyword keyword = keywordRepository.findByNameAndCourseId(name, course.getId());
        if (keyword != null) {
            Logger.log(this.getClass().getName(), "Keyword found with name: " + name, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Keyword not found with name: " + name, LogLevel.WARN);
        }
        return keyword;
    }

    public List<Keyword> getAll() {
        List<Keyword> keywords = keywordRepository.findAll();
        Logger.log(this.getClass().getName(), "Retrieved all keywords, count: " + keywords.size(), LogLevel.INFO);
        return keywords;
    }

    public List<Keyword> getAllByCourseId(Long courseId) {
        List<Keyword> keywords = keywordRepository.findAllByCourseId(courseId);
        Logger.log(this.getClass().getName(), "Retrieved all keywords for one course, count: " + keywords.size(), LogLevel.INFO);
        return keywords;
    }

    public List<KeywordWrapper> getAllByCourseIdWithQuestionCount(Long courseId) {
        List<KeywordWrapper> keywords = keywordRepository.findKeywordsWithQuestionCountByCourseId(courseId);
        Logger.log(this.getClass().getName(), "Retrieved " + keywords.size() + " keywords for one course with question-count.", LogLevel.INFO);
        return keywords;
    }

    public Keyword update(Keyword keyword) {
        Keyword existingKeyword = keywordRepository.findById(keyword.getId()).orElse(null);
        if (existingKeyword != null) {
            existingKeyword.setKeyword(keyword.getKeyword());

            Keyword updatedKeyword = keywordRepository.save(existingKeyword);
            Logger.log(this.getClass().getName(), "Keyword updated successfully for ID: " + keyword.getId(), LogLevel.INFO);
            return updatedKeyword;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find keyword with ID: " + keyword.getId(), LogLevel.ERROR);
            throw new RuntimeException("Keyword not found");
        }
    }

    public void remove(Long id) {
        try {
            keywordRepository.deleteById(id);
            Logger.log(this.getClass().getName(), "Keyword deleted successfully with ID: " + id, LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(this.getClass().getName(), "Failed to delete keyword with ID: " + id, LogLevel.ERROR);
            throw e;
        }
    }
}
