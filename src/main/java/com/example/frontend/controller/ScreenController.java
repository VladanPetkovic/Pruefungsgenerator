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

public abstract class ScreenController {
    @FXML
    protected Label someText;
    @FXML
    protected Stage stage;
    @FXML
    protected Scene scene;
    @FXML
    protected Parent root;
    @FXML
    protected void onHomeButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/home.fxml"));
        root  = fxmlLoader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        someText.setText("selecting course");
    }
    @FXML
    protected void onCreateTestButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/test_upload.fxml"));
        root  = fxmlLoader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    protected void onUploadQuestionButtonClick()
    {
        someText.setText("Uploading some question");
    }
    @FXML
    protected void onEditQuestionButtonClick(ActionEvent event) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/question_edit.fxml"));
        root  = fxmlLoader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        someText.setText("edit question");
    }
}
