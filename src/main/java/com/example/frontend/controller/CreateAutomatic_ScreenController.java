package com.example.frontend.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class CreateAutomatic_ScreenController extends ScreenController {

    @FXML
    private VBox addQuestionVBox; // Reference to the VBox containing the "Add Question" button

    private int questionCount = 1; // Variable to keep track of the question count

    @FXML
    private void onAddQuestionBtnClick() {
        // Increment the question count
        questionCount++;

        // Create a new VBox with the required structure
        VBox newQuestionVBox = createNewQuestionVBox();

        // Get the parent of the parent (grandparent) of addQuestionVBox
        VBox grandparentVBox = (VBox) addQuestionVBox.getParent().getParent();

        // Get the index of the parent of addQuestionVBox in its grandparent
        int parentIndex = grandparentVBox.getChildren().indexOf(addQuestionVBox.getParent());

        // Add the new VBox just before the addQuestionVBox
        grandparentVBox.getChildren().add(parentIndex, newQuestionVBox);
    }

    private VBox createNewQuestionVBox() {
        // Create a new VBox with the specified structure
        VBox questionVBox = new VBox();

        // Create and add the label indicating the question number
        createLabel("Question " + questionCount, questionVBox);

        // Create and add components to the new VBox
        createLabel("Topic", questionVBox);
        createMenuButton(questionVBox);
        createLabel("Points", questionVBox);
        createSpinner(questionVBox);
        createLabel("Difficulty", questionVBox);
        createSlider(questionVBox);

        return questionVBox;
    }

    private void createLabel(String labelText, VBox parentVBox) {
        Label label = new Label(labelText);
        label.getStyleClass().add("automatic_create_label");
        label.getStylesheets().add("@../css/main.css");

        label.setPrefHeight(150.0);
        label.setPrefWidth(1000.0);
        label.setTextFill(Paint.valueOf("#e8e4e4"));

        parentVBox.getChildren().add(label);
    }

    private void createMenuButton(VBox parentVBox) {
        MenuButton menuButton = new MenuButton("Choose topic...");
        menuButton.getStyleClass().add("automatic_create_dropdown");
        menuButton.getStylesheets().add("@../css/main.css");

        MenuItem action1 = new MenuItem("Action 1");
        MenuItem action2 = new MenuItem("Action 2");

        menuButton.getItems().addAll(action1, action2);

        VBox innerVBox = new VBox(menuButton);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add("@../css/main.css");

        parentVBox.getChildren().add(innerVBox);
    }

    private void createSpinner(VBox parentVBox) {
        Spinner spinner = new Spinner();
        spinner.setEditable(true);
        spinner.getStyleClass().add("automatic_create_spinner");
        spinner.getStylesheets().add("@../css/main.css");

        VBox innerVBox = new VBox(spinner);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add("@../css/main.css");

        parentVBox.getChildren().add(innerVBox);
    }

    private void createSlider(VBox parentVBox) {
        Slider slider = new Slider();
        slider.setId("difficulty_slider");
        slider.setMajorTickUnit(2.0);
        slider.setMax(10.0);
        slider.setMin(1.0);
        slider.setMinorTickCount(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setStyle("-fx-background-color: #2f2f2f;");
        slider.getStyleClass().add("slider-tool");
        slider.setValue(5.5);

        VBox innerVBox = new VBox(slider);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add("@../css/main.css");

        parentVBox.getChildren().add(innerVBox);
    }

    protected void onCreateAutTestBtnClick(ActionEvent event) {
        // Handle the "Create Automatic Test" button click
        // ...
    }
}