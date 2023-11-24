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
import java.util.EventObject;

public class Home_ScreenController {

    @FXML
    private Label someText;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;

    @FXML
    protected void onCreateTestButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/test_upload.fxml"));
        root  = fxmlLoader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        someText.setText("creating a new test");
    }
    @FXML
    protected void onUploadQuestionButtonClick()
    {
        someText.setText("Uploading some question");
    }
    @FXML
    protected void onEditQuestionButtonClick()
    {
        someText.setText("Editing some question");
    }
}
