package com.example.backend.app;

import java.util.ArrayList;

public class Question {
    private int difficulty;
    private int points;
    private String questionString;
    private String subject;
    private ArrayList<String> keywords;
    private ArrayList<String> topics;

    // language
    /*
    *   german = 0
    *   english = 1
    */
    private Boolean language;

    Question()
    {

    }
    public int getDifficulty()
    {
        return this.difficulty;
    }
    public int getPoints()
    {
        return this.points;
    }
    public String getQuestionString()
    {
        return this.questionString;
    }
    public String getSubject()
    {
        return this.subject;
    }
    public ArrayList<String> getKeywords()
    {
        return this.keywords;
    }
    public ArrayList<String> getTopics()
    {
        return this.topics;
    }
    public Boolean getLanguage()
    {
        return this.language;
    }
    public void setDifficulty(int difficulty)
    {
        this.difficulty = difficulty;
    }
    public void setPoints(int points)
    {
        this.points = points;
    }
    public void setQuestionsString(String question)
    {
        this.questionString = question;
    }
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    public void setKeywords(ArrayList<String> keywords)
    {
        this.keywords = keywords;
    }
    public void setTopics(ArrayList<String> topics)
    {
        this.topics = topics;
    }
    public void setLanguage(Boolean lang)
    {
        this.language = lang;
    }
}