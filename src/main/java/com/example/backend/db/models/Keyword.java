package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Keyword {
    private int keyword_id;
    private String keyword_text;

    public Keyword(String keyword) {
        setKeyword_text(keyword);
    }
}
