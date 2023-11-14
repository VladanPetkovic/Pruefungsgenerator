package com.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeScreenController
{
    @FXML
    private Label someText;

    @FXML
    protected void onSettingsButtonClick()
    {
        someText.setText("Clicked Settings Button");
    }
    @FXML
    protected void onHomeButtonClick()
    {
        someText.setText("Clicked Home Button");
    }
    @FXML
    protected void onProfileButtonClick()
    {
        someText.setText("Clicked Profile Button");
    }
    @FXML
    protected void onCreateButtonClick()
    {
        someText.setText("Clicked Create new test Button");
    }
    @FXML
    protected void onUploadButtonClick()
    {
        someText.setText("Clicked Upload Button");
    }
    @FXML
    protected void onUserButtonClick()
    {
        someText.setText("Clicked User Button");
    }

}