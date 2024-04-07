package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Keyword {
    private int id;
    private String keyword;

    public Keyword(String keyword) {
        setKeyword(keyword);
    }
}
