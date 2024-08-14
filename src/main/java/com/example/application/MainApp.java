package com.example.application;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.SharedData;
import com.example.application.frontend.controller.ControllerFactory;
import com.example.application.frontend.controller.FXMLDependencyInjection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@SpringBootApplication
public class MainApp extends Application {
    /**
     * The primary stage of the application.
     */
    public static Stage stage;
    private Parent root;
    public static ConfigurableApplicationContext springContext;     // needed for Dependency-Injection when working with SpringBoot
    public static ResourceBundle resourceBundle;                    // needed for adding languages
    public static ControllerFactory controllerFactory;              // needed for switching screens

    public static void main(String[] args) {
        launch(MainApp.class, args);
    }

    @Override
    public void init() throws IOException {
        springContext = SpringApplication.run(MainApp.class);
        controllerFactory = new ControllerFactory(springContext);
        FXMLLoader fxmlLoader = selectScreen();
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void stop() {
        springContext.stop();
    }

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
        if (Files.exists(path)) {
            Logger.log(getClass().getName(), "Loading existing CrashFile", LogLevel.INFO);
            SharedData.loadFromFile();

            path = Paths.get(SharedData.getFilepath());
            Files.delete(path);
        } else {
            Logger.log(getClass().getName(), "CrashFile does not exist", LogLevel.INFO);
            SharedData.setPageTitle(MainApp.resourceBundle.getString("home"));
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        setWindowsSize(stage);

        // set onCloseRequest eventhandler
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
            // user closes dialog or clicks cancel
            event.consume();
        } else {
            // user clicks OK

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

    private FXMLLoader selectScreen() {
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

        if (SharedData.getCurrentScreen() == null) {
            return FXMLDependencyInjection.getLoader("sites/home.fxml", resourceBundle);
        }

        return switch (SharedData.getCurrentScreen()) {
            case CREATE_AUTOMATIC -> FXMLDependencyInjection.getLoader("sites/create_automatic.fxml", resourceBundle);
            case CREATE_MANUAL -> FXMLDependencyInjection.getLoader("sites/create_manual.fxml", resourceBundle);
            case QUESTION_CREATE -> FXMLDependencyInjection.getLoader("sites/question_create.fxml", resourceBundle);
            case QUESTION_EDIT -> FXMLDependencyInjection.getLoader("sites/question_edit.fxml", resourceBundle);
            case SETTINGS -> FXMLDependencyInjection.getLoader("sites/settings.fxml", resourceBundle);
            default -> FXMLDependencyInjection.getLoader("sites/home.fxml", resourceBundle);
        };
    }

}