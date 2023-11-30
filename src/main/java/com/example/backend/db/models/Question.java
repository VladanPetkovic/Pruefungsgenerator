package com.example.backend.db.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Question {
    @JsonAlias({"question_id"})
    int question_id;
    @JsonAlias({"fk_topic_id"})
    int fk_topic_id;
    @JsonAlias({"difficulty"})
    int difficulty;
    @JsonAlias({"points"})
    int points;
    @JsonAlias({"question"})
    String question;
    @JsonAlias({"multipleChoice"})
    boolean multipleChoice;
    @JsonAlias({"language"})
    String language;
    @JsonAlias({"remarks"})
    String remarks;
    @JsonAlias({"answers"})
    String answers;

    // Jackson needs the default constructor
    public Question() {}
}