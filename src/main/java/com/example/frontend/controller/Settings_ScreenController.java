package com.example.frontend.controller;

import com.example.backend.app.*;
import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class Settings_ScreenController extends ScreenController {

    @FXML
    VBox settingsNav_goBackBtn;

    @FXML
    Button settingsImportBtn;
    @FXML
    private void initialize() {

    }
    @FXML
    public void onImportBtnClicked() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog( settingsImportBtn.getScene().getWindow());
    }
}
