package com.example.application.backend.db.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryWrapper {
    private Category category;
    private Long questionCount;

    public CategoryWrapper(Category category, Long questionCount) {
        setCategory(category);
        setQuestionCount(questionCount);
    }
}
