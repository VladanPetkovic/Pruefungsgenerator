package com.example.backend.app;

import java.util.ArrayList;

public class Menu {

    // TODO
    // TODO
    // TODO         implement these methods properly
    // TODO
    // TODO


    public Test makeNewTest()
    {
        return new Test();
    }
    public Question addNewQuestion(boolean isOpenQuestion)
    {
        if(isOpenQuestion)
        {
            return new Question_open();
        }

        return new Question_mc();
    }
    public Question getQuestion(int id)
    {
        return new Question_open();
    }
    public ArrayList<Question> getAllQuestions(String subject)
    {
        return new ArrayList<Question>();
    }
    public void editQuestion(Question newQuestion)
    {

    }
    public String selectSubject()
    {
        return "MACS2";
    }
    public void editTestHeader()
    {

    }
}