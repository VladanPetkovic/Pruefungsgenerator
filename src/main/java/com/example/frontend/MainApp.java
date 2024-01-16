package com.example.frontend;

import com.example.frontend.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
    public static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        setWindowsSize(stage);
        stage.show();
    }

    public void setWindowsSize(Stage stage) {
        // setting window sizes
        // min. window
        stage.setMinWidth(720);
        stage.setMinHeight(405);

        // max. window
        stage.setMaxWidth(1920);
        stage.setMaxHeight(1080);
    }

    public static void main(String[] args) {
        launch();
    }
}