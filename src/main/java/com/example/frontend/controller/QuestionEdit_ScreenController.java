package com.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class QuestionEdit_ScreenController extends ScreenController implements Initializable {

    @FXML
    private TextField searchCategory;

    @FXML
    private TextField searchKeyword;

    @FXML
    private Slider searchDifficulty;

    @FXML
    private Slider searchPoints;

    @FXML
    private CheckBox searchMultipleChoice;

    @FXML
    private Button searchButton;

    @FXML
    private VBox previewVBox;

    @FXML
    private Spinner<Integer> choosePoints;

    @FXML
    private CheckBox chooseMultipleChoice;

    @FXML
    private Slider chooseDifficulty;

    @FXML
    private TextArea chooseQuestion;

    @FXML
    private TextArea chooseRemarks;

    @FXML
    private Button chooseButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization code goes here
    }

    public void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    private void onSearchButton() {

    }

    @FXML
    private void onChooseButton() {
        // Handle choose button click
    }
}
