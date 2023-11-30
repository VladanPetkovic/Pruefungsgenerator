package com.example.frontend.controller;

import javafx.event.ActionEvent;

public class QuestionEdit_ScreenController extends ScreenController {
    @Override
    protected void onEditQuestionButtonClick(ActionEvent event)
    {
        someText.setText("edit question");
    }


}
