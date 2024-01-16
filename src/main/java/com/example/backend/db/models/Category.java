package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Category {
    private int category_id;
    private String category;

    public Category(Category other) {
        this.category_id = other.category_id;
        this.category = other.category;
    }
}
