package com.example.frontend.controller;

import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class TestCreate_ScreenController extends ScreenController
{

    @Override
    protected void onCreateTestButtonClick() {
        someText.setText("something");
    }
}