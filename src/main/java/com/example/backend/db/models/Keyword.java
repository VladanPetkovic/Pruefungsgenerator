package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Keyword {
    String keyword_id;
    String keyword_text;

    public Keyword(String keyword) {
        setKeyword_text(keyword);
    }
}
