package com.example.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
    /** The primary stage of the application. */
    public static Stage stage;

    /**
     * Starts the JavaFX application.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        setWindowsSize(stage);
        stage.show();
    }

    /**
     * Sets the minimum and maximum sizes for the application window.
     *
     * @param stage The primary stage for the application.
     */
    public void setWindowsSize(Stage stage) {
        // setting window sizes
        // min. window
        stage.setMinWidth(720);
        stage.setMinHeight(550);

        // max. window
        stage.setMaxWidth(1920);
        stage.setMaxHeight(1080);
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}