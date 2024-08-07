package com.example.backend.app;

import com.example.backend.db.models.Question;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
public class Test {
    private int countOfQuestions;
    private int overallPoints;
    private ArrayList<Question> questions;

    Test(int countOfQuestions, int overallPoints, ArrayList<Question> questions)
    {
        setCountOfQuestions(countOfQuestions);
        setOverallPoints(overallPoints);
        setQuestions(questions);
    }
}