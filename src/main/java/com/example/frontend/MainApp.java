package com.example.frontend;

import com.example.backend.app.SharedData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Optional;

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
        SharedData.setPageTitle("Exam Generator");
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        setWindowsSize(stage);

        //set onCloseRequest eventhandler
        stage.setOnCloseRequest(event -> handleWindowCloseRequest(event));
        stage.show();
    }

    private void handleWindowCloseRequest(WindowEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Are you sure you want to exit?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        okButton.getStyleClass().add("btn_grey");
        cancelButton.getStyleClass().add("btn_grey");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && ((Optional<?>) result).get() == ButtonType.OK) {
            //user clicks OK
        } else {
            //user closes dialog or clicks cancel
            event.consume();
        }
    }

    /**
     * Sets the minimum size for the application window.
     *
     * @param stage The primary stage for the application.
     */
    public void setWindowsSize(Stage stage) {
        // setting window sizes
        // min. window
        stage.setMinWidth(720);
        stage.setMinHeight(550);
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