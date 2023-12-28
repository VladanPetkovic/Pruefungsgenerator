package com.example.backend.app;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public abstract class Question {
    private int difficulty;
    private int points;
    private String questionString;
    private int multipleChoice;
    private String topic;
    private ArrayList<String> keywords;
    private String course;

    // language
    /*
    *   german = 0
    *   english = 1
    */
    private Boolean language;
    private String remarks;

    // default constructor
    public Question() {}

    // constructor with values
    public Question(int difficulty, int points, String questionString,
                    int multipleChoice, String topic, ArrayList<String> keywords,
                    String course, Boolean lang, String remarks) {
        setDifficulty(difficulty);
        setPoints(points);
        setQuestionString(questionString);
        setMultipleChoice(multipleChoice);
        setTopic(topic);
        setKeywords(keywords);
        setCourse(course);
        setLanguage(lang);
        setRemarks(remarks);
    }
}