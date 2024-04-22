package com.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import com.example.backend.app.SharedData;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;

public class TitleBanner_ScreenController extends ScreenController {

    @FXML
    private ImageView fhtw_logo;

    @FXML
    private Label titleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // bind the text property of titleLabel to the pageTitleProperty in SharedData
        titleLabel.textProperty().bind(SharedData.pageTitleProperty());
        // bind the text property of statusLabel to the operationStatusProperty in SharedData
        statusLabel.textProperty().bind(SharedData.operationStatusProperty());

        // bind the textFill property of statusLabel to the error type in SharedData
        statusLabel.textFillProperty().bind(
                Bindings.when(SharedData.operationIsErrorTypeProperty())
                        .then(Color.RED)
                        .otherwise(Color.FORESTGREEN)
        );
    }

    /**
     * handles click event for navigating to the home screen and resetting shared data
     */
    public void onFHTWLogoClick() {
        // navigate to the home screen and reset shared data
        switchScene(home,true);
        SharedData.resetAll();
    }

}

