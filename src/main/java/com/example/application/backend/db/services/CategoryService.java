package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.Category;
import com.example.application.backend.db.repositories.CategoryRepository;
import com.example.application.backend.db.repositories.CourseRepository;
import com.example.application.backend.db.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,
                           QuestionRepository questionRepository,
                           CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
    }

    public Category add(Category category) {
        Category newCategory = categoryRepository.save(category);
        Logger.log(this.getClass().getName(), "Category saved with ID: " + newCategory.getId(), LogLevel.INFO);
        return newCategory;
    }

    public Category getById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            Logger.log(this.getClass().getName(), "Category found with ID: " + id, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Category not found with ID: " + id, LogLevel.WARN);
        }
        return category;
    }

    public Category getByName(String name) {
        Category category = categoryRepository.findCategoryByName(name);
        if (category != null) {
            Logger.log(this.getClass().getName(), "Category found with name: " + name, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Category not found with name: " + name, LogLevel.WARN);
        }
        return category;
    }

    public List<Category> getAll() {
        List<Category> categories = categoryRepository.findAll();
        Logger.log(this.getClass().getName(), "Retrieved all categories, count: " + categories.size(), LogLevel.INFO);
        return categories;
    }

    public List<Category> getAllByCourseId(Long courseId) {
        List<Category> categories = categoryRepository.findAllByCourseId(courseId);
        Logger.log(this.getClass().getName(), "Retrieved all categories for one course, count: " + categories.size(), LogLevel.INFO);
        return categories;
    }

    public Category update(Category category) {
        Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
        if (existingCategory != null) {
            existingCategory.setName(category.getName());

            Category updatedCategory = categoryRepository.save(existingCategory);
            Logger.log(this.getClass().getName(), "Category updated successfully for ID: " + category.getId(), LogLevel.INFO);
            return updatedCategory;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find category with ID: " + category.getId(), LogLevel.ERROR);
            throw new RuntimeException("Category not found");
        }
    }

    public void remove(Long id) {
        try {
            categoryRepository.deleteById(id);
            Logger.log(this.getClass().getName(), "Category deleted successfully with ID: " + id, LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(this.getClass().getName(), "Failed to delete category with ID: " + id, LogLevel.ERROR);
            throw e;
        }
    }
}
