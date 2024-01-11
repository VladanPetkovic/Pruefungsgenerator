package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor

public class Question {
    int question_id;
    Topic topic;
    int difficulty;
    int points;
    String questionString;
    int multipleChoice;
    String language;
    String remarks;
    String answers;
    ArrayList<Keyword> keywords;
    ArrayList<Image> images;

    public Question() {}

    public Question(Topic topic, int difficulty, int points, String questionString,
                    int multipleChoice, String lang, String remarks,
                    String answers, ArrayList<Keyword> keywords,
                    ArrayList<Image> images) {
        setTopic(topic);
        setDifficulty(difficulty);
        setPoints(points);
        setQuestionString(questionString);
        setMultipleChoice(multipleChoice);
        setLanguage(lang);
        setRemarks(remarks);
        setAnswers(answers);
        setKeywords(keywords);
        setImages(images);
    }
}