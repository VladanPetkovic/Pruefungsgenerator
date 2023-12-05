package com.example.frontend.controller;

import javafx.event.ActionEvent;


public class Home_ScreenController extends ScreenController {
    @Override
    protected void onHomeButtonClick(ActionEvent event)
    {
        someText.setText("clicked home screen");
    }
}
