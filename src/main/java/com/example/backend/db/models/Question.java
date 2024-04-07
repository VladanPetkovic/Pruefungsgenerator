package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    private int id;
    private Category category;
    private int difficulty;
    private float points;
    private String question;
    private QuestionType type;
    private String remark;
    private Timestamp created_at;
    private Timestamp updated_at;
    private ArrayList<Answer> answers;
    private ArrayList<Keyword> keywords;
    private ArrayList<Image> images;

    public Question(Category category, int difficulty, float points, String question,
                    QuestionType type, String remark, Timestamp created_at,
                    Timestamp updated_at, ArrayList<Answer> answers,
                    ArrayList<Keyword> keywords, ArrayList<Image> images) {
        setCategory(category);
        setDifficulty(difficulty);
        setPoints(points);
        setQuestion(question);
        setType(type);
        setRemark(remark);
        setCreated_at(created_at);
        setUpdated_at(updated_at);
        setAnswers(answers);
        setKeywords(keywords);
        setImages(images);
    }

    public String getAnswersAsString() {
        StringBuilder answersCombined = new StringBuilder();

        // return the first answer of answers, if only one is available
        if (answers.size() == 1) {
            return answers.get(0).getAnswer();
        }

        for (Answer answer : answers) {
            if (!Objects.equals(answer.getAnswer(), "")) {
                answersCombined.append(answer.getAnswer());
                answersCombined.append("\n");
            }
        }

        return String.valueOf(answersCombined);
    }


}
