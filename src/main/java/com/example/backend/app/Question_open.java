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
                int multipleChoice, String topic, ArrayList<String> keywords,
                  String course, Boolean lang, String remarks, String answer)
    {
        super(difficulty, points, questionString, multipleChoice, topic, keywords, course, lang, remarks);
        setAnswer(answer);
    }
}
