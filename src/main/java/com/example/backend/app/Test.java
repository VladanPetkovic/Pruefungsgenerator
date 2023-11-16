package com.example.backend.app;

import java.util.ArrayList;

public class Test {
    private int countOfQuestions;
    private int overallPoints;
    private String subject;
    private ArrayList<Question> questions;

    Test()
    {

    }
    public int getCountOfQuestions()
    {
        return this.countOfQuestions;
    }
    public int getOverallPoints()
    {
        return this.overallPoints;
    }
    public String getSubject()
    {
        return this.subject;
    }
    ArrayList<Question> getQuestions()
    {
        return this.questions;
    }
    public void setCountOfQuestions(int count)
    {
        this.countOfQuestions = count;
    }
    public void setOverallPoints(int points)
    {
        this.overallPoints = points;
    }
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    public void setQuestions(ArrayList<Question> questions)
    {
        this.questions = questions;
    }
}