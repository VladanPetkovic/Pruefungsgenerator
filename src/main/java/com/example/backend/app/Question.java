package com.example.backend.app;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public class Question {
    private int difficulty;
    private float points;
    private String questionString;
    private int multipleChoice;
    private String category;
    private ArrayList<String> keywords;
    private String course;

    //
    // THIS IS DEPRECATED --> USE WITH CAUTION & REFACTOR BEFORE USAGE
    //

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
    public Question(int difficulty, float points, String questionString,
                    int multipleChoice, String category, ArrayList<String> keywords,
                    String course, Boolean lang, String remarks) {
        setDifficulty(difficulty);
        setPoints(points);
        setQuestionString(questionString);
        setMultipleChoice(multipleChoice);
        setCategory(category);
        setKeywords(keywords);
        setCourse(course);
        setLanguage(lang);
        setRemarks(remarks);
    }
}