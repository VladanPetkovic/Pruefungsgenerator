package com.example.backend.app;

import java.util.ArrayList;

public class Question {
    private int difficulty;
    private int points;
    private String questionString;
    private Boolean multipleChoice;
    private String topic;
    private ArrayList<String> keywords;

    // language
    /*
    *   german = 0
    *   english = 1
    */
    private Boolean language;
    private String remarks;

    // default constructor
    public Question() {}

    // constructor with values
    public Question(int difficulty, int points, String questionString, Boolean multipleChoice, String topic, ArrayList<String> keywords, Boolean lang, String remarks) {
        setDifficulty(difficulty);
        setPoints(points);
        setQuestionsString(questionString);
        setMultipleChoice(multipleChoice);
        setTopic(topic);
        setKeywords(keywords);
        setLanguage(lang);
        setRemarks(remarks);
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
    public Boolean getMultipleChoice(){ return.this.multipleChoice; }
    public String getTopic()
    {
        return this.topic;
    }
    public ArrayList<String> getKeywords()
    {
        return this.keywords;
    }
    public Boolean getLanguage()
    {
        return this.language;
    }
    public String getRemarks(){ return this.remarks; }
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
    public void setMultipleChoice(Boolean multipleChoice){ this.multipleChoice = multipleChoice; }
    public void setTopic(String topic)
    {
        this.topic = topic;
    }
    public void setKeywords(ArrayList<String> keywords)
    {
        this.keywords = keywords;
    }
    public void setLanguage(Boolean lang)
    {
        this.language = lang;
    }
    public void setRemarks(String remarks){ this.remarks = remarks; }
}