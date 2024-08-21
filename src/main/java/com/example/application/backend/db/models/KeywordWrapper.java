package com.example.application.backend.db.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordWrapper {
    private Keyword keyword;
    private Long questionCount;

    public KeywordWrapper(Keyword keyword, Long questionCount) {
        setKeyword(keyword);
        setQuestionCount(questionCount);
    }
}
