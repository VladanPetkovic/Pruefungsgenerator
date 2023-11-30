package com.example.frontend.controller;

import javafx.event.ActionEvent;


public class TestCreate_ScreenController extends ScreenController
{

    @Override
    protected void onCreateTestButtonClick(ActionEvent event) {
        someText.setText("something");
    }
}