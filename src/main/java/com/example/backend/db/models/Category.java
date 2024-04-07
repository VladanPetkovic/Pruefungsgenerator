package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private int id;
    private String name;

    public Category(Category other) {
        this.id = other.id;
        this.name = other.name;
    }
}
