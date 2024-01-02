package com.example.frontend.controller;

import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public abstract class ScreenController {

    @FXML
    protected void onCreateAutTestBtnClick(ActionEvent event) throws IOException {

    }
    @FXML
    protected void onCreateManTestBtnClick(ActionEvent event) throws IOException {

    }
    @FXML
    protected void onUploadQuestionBtnClick(ActionEvent event) throws IOException
    {
        MainApp.switchScene(MainApp.questionUpload,true);
    }
    @FXML
    protected void onEditQuestionBtnClick(ActionEvent event) throws IOException
    {

    }
}
