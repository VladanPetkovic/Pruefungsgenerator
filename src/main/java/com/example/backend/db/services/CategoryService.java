package com.example.backend.db.services;

import com.example.backend.db.models.Category;
import com.example.backend.db.repositories.CategoryRepository;
import com.example.backend.db.repositories.CourseRepository;
import com.example.backend.db.repositories.QuestionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private static final Logger logger = LogManager.getLogger(CategoryService.class);
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
        logger.info("Category saved with ID: {}", newCategory.getId());
        return newCategory;
    }

    public Category getById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            logger.info("Category found with ID: {}", id);
        } else {
            logger.warn("Category not found with ID: {}", id);
        }
        return category;
    }

    public List<Category> getAll() {
        List<Category> categories = categoryRepository.findAll();
        logger.info("Retrieved all categories, count: {}", categories.size());
        return categories;
    }

    public Category update(Category category) {
        Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
        if (existingCategory != null) {
            existingCategory.setName(category.getName());

            Category updatedCategory = categoryRepository.save(existingCategory);
            logger.info("Category updated successfully for ID: {}", category.getId());
            return updatedCategory;
        } else {
            logger.error("Failed to find category with ID: {}", category.getId());
            throw new RuntimeException("Category not found");
        }
    }

    public void remove(Long id) {
        try {
            categoryRepository.deleteById(id);
            logger.info("Category deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete category with ID: {}", id, e);
            throw e;
        }
    }
}
