package com.example.frontend.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;


public class CreateManual_ScreenController extends ScreenController
{
    @FXML
    private Label label_question_count;
    @FXML
    private Label label_difficulty;
    @FXML
    private Slider difficulty_slider;
    @FXML
    private Slider question_count_slider;
    int difficulty = 5;
    int question_count = 5;
    /*@Override
    protected void onCreateManTestNavBtnClick(ActionEvent event) {

    }
*/
    public void initialize() {
/*
        label_difficulty.setText("5");
        label_question_count.setText("10");

        difficulty = (int) difficulty_slider.getValue();
        question_count = (int) question_count_slider.getValue();

        difficulty_slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                difficulty = (int) difficulty_slider.getValue();
                label_difficulty.setText(Integer.toString(difficulty));
            }
        });
        question_count_slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                question_count = (int) question_count_slider.getValue();
                label_question_count.setText(Integer.toString(question_count));
            }
        });
        */

    }
}