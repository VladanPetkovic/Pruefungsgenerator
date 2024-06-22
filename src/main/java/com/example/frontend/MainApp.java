package com.example.frontend;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.Screen;
import com.example.backend.app.SharedData;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.StudyProgram;
import com.example.frontend.controller.ControllerFactory;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainApp extends Application {
    /**
     * The primary stage of the application.
     */
    public static Stage stage;
    public static ControllerFactory controllerFactory = new ControllerFactory();
    public static ResourceBundle resourceBundle;

    /**
     * Starts the JavaFX application.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        this.stage = stage;
        Locale locale = new Locale("en", "US");
        resourceBundle = ResourceBundle.getBundle("common.en", locale);

        Path path = Paths.get(SharedData.getFilepath());
        Scene scene;
        if (Files.exists(path)) {
            Logger.log(getClass().getName(), "Loading existing CrashFile", LogLevel.INFO);
            SharedData.loadFromFile();

            path = Paths.get(SharedData.getFilepath());
            Files.delete(path);

            System.out.println("PageTitle:"+ SharedData.getPageTitle());
            System.out.println("OPStatus:"+ SharedData.getOperationStatus());
            System.out.println("Lang:"+ SharedData.getCurrentLanguage());
            System.out.println("currentScreen:"+ SharedData.getCurrentScreen());

            if (SharedData.getSelectedCourse() != null && SharedData.getSelectedStudyProgram() != null && SharedData.getCurrentScreen() != null) {
                System.out.println("selectedCourse:" + SharedData.getSelectedCourse().getName());
                //SharedData.setPageTitle(MainApp.resourceBundle.getString(SharedData.getPageTitle()));

                FXMLLoader fxmlLoader = selectScreen();
                scene = new Scene(fxmlLoader.load());

            } else {
                SharedData.setPageTitle(MainApp.resourceBundle.getString("home"));
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/home.fxml"), resourceBundle);
                scene = new Scene(fxmlLoader.load());
            }
            //System.out.println("SelectedStudyProgram"+ SharedData.getSelectedStudyProgram().getName());
        } else {
            System.out.println("CrashFile does not exist.");
            SharedData.setPageTitle(MainApp.resourceBundle.getString("home"));
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("sites/home.fxml"), resourceBundle);
            scene = new Scene(fxmlLoader.load());
        }

        stage.setScene(scene);
        setWindowsSize(stage);

        //set onCloseRequest eventhandler
        stage.setOnCloseRequest(this::handleWindowCloseRequest);
        stage.show();
    }

    private void handleWindowCloseRequest(WindowEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("confirm_exit"));
        alert.setHeaderText(resourceBundle.getString("confirm_exit_info"));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        okButton.getStyleClass().add("btn_grey");
        cancelButton.getStyleClass().add("btn_grey");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || ((Optional<?>) result).get() != ButtonType.OK) {
            //user closes dialog or clicks cancel
            event.consume();
        } else {
            //user clicks OK

            try {
                Path path = Paths.get(SharedData.getFilepath());

                if (Files.exists(path)) {
                    Files.delete(path);
                    Logger.log(getClass().getName(), "CrashFile deleted successfully.", LogLevel.INFO);
                }
            } catch (IOException e) {
                System.err.println("An error occurred while deleting the CrashFile: " + e.getMessage());
            }
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

    private  FXMLLoader selectScreen() {

        Locale locale = new Locale("en", "US");
        int lang = SharedData.getCurrentLanguage();

        switch (lang) {
            case 0:                         // ENGLISH
                MainApp.resourceBundle = ResourceBundle.getBundle("common.en", locale);
                break;
            case 1:                         // GERMAN
                locale = new Locale("de", "AUT");
                MainApp.resourceBundle = ResourceBundle.getBundle("common.de", locale);
                break;
        }

        //enhanced switch case (suggestion from ide)
        return switch (SharedData.getCurrentScreen()) {
            case HOME -> new FXMLLoader(MainApp.class.getResource("sites/home.fxml"), resourceBundle);
            case CREATE_AUTOMATIC -> new FXMLLoader(MainApp.class.getResource("sites/create_automatic.fxml"), resourceBundle);
            case CREATE_MANUAL -> new FXMLLoader(MainApp.class.getResource("sites/create_manual.fxml"), resourceBundle);
            case QUESTION_CREATE -> new FXMLLoader(MainApp.class.getResource("sites/question_create.fxml"), resourceBundle);
            case QUESTION_EDIT -> new FXMLLoader(MainApp.class.getResource("sites/question_edit.fxml"), resourceBundle);
            case SETTINGS -> new FXMLLoader(MainApp.class.getResource("sites/settings.fxml"), resourceBundle);
        };
    }

}