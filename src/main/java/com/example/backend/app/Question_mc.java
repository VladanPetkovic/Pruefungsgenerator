package com.example.backend.app;

import java.util.ArrayList;

public class Question_mc extends Question {
    private int countOfAnswers;
    private ArrayList<String> answers;

    Question_mc()
    {

    }
    public int getCountOfAnswers()
    {
        return this.countOfAnswers;
    }
    public ArrayList<String> getAnswers()
    {
        return this.answers;
    }
    public void setCountOfAnswers(int count)
    {
        this.countOfAnswers = count;
    }
    public void setAnswers(ArrayList<String> answers)
    {
        this.answers = answers;
    }
}
