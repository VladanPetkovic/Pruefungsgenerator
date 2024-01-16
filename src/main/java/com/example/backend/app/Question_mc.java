package com.example.backend.app;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public class Question_mc extends Question {
    private int countOfAnswers;
    private ArrayList<String> answers;

    Question_mc()
    {

    }

    Question_mc(int difficulty, int points, String questionString,
                int multipleChoice, String category, ArrayList<String> keywords,
                Boolean lang, String remarks, String course, int countOfAnswers, ArrayList<String> answers)
    {
        super(difficulty, points, questionString, multipleChoice, category, keywords, course, lang, remarks);
        setCountOfAnswers(countOfAnswers);
        setAnswers(answers);
    }
}
