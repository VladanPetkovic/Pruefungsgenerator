package com.example.backend.app;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;


@Getter
@Setter
public class Test {
    private int countOfQuestions;
    private int overallPoints;
    private String subject;
    private ArrayList<Question> questions;

    Test()
    {

    }

    Test(int countOfQuestions, int overallPoints,
         String subject, ArrayList<Question> questions)
    {
        setCountOfQuestions(countOfQuestions);
        setOverallPoints(overallPoints);
        setSubject(subject);
        setQuestions(questions);
    }
}