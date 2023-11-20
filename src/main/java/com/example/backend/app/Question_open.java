package com.example.backend.app;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Question_open extends Question {
    private String answer;

    Question_open()
    {

    }

    Question_open(int difficulty, int points, String questionString,
                Boolean multipleChoice, String topic, ArrayList<String> keywords,
                Boolean lang, String remarks, String answer)
    {
        super(difficulty, points, questionString, multipleChoice, topic, keywords, lang, remarks);
        setAnswer(answer);
    }
}
