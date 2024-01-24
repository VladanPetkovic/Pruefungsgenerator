package com.example.backend.app;

import com.example.backend.db.models.Category;
import com.example.backend.db.models.Image;
import com.example.backend.db.models.Keyword;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Question {
    int question_id;
    Category category;
    int difficulty;
    float points;
    String questionString;
    int multipleChoice;
    String language;
    String remarks;
    String answers;
    ArrayList<Keyword> keywords;
    ArrayList<com.example.backend.db.models.Image> images;

    public Question(Category category, int difficulty, float points, String questionString,
                    int multipleChoice, String language, String remarks,
                    String answers, ArrayList<Keyword> keywords,
                    ArrayList<Image> images) {
        setCategory(category);
        setDifficulty(difficulty);
        setPoints(points);
        setQuestionString(questionString);
        setMultipleChoice(multipleChoice);
        setLanguage(language);
        setRemarks(remarks);
        setAnswers(answers);
        setKeywords(keywords);
        setImages(images);
    }
}