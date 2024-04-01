package com.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SuccessModalController {

    @FXML
    private Label messageLabel;

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}


