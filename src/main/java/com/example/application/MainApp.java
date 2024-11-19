package com.example.application;

import com.example.application.backend.app.Language;
import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Setting;
import com.example.application.backend.db.services.SettingService;
import com.example.application.frontend.controller.ControllerFactory;
import com.example.application.frontend.controller.SwitchScene;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


import java.io.File;
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
    public static ConfigurableApplicationContext springContext;     // needed for Dependency-Injection when working with SpringBoot
    public static ResourceBundle resourceBundle;                    // needed for adding languages
    public static ControllerFactory controllerFactory;              // needed for switching screens

    public static void main(String[] args) {
        launch(MainApp.class, args);
    }

    @Override
    public void init() {
        springContext = SpringApplication.run(MainApp.class);
        controllerFactory = new ControllerFactory(springContext);
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
        MainApp.stage = stage;
        setStageIcon(stage);
        resourceBundle = getResourceBundle();

        processCrashFile();

        SwitchScene.switchScene(selectScreen());
        setWindowSize();

        // set onCloseRequest eventhandler
        stage.setOnCloseRequest(this::handleWindowCloseRequest);
        stage.show();
    }

    private void processCrashFile() throws IOException, ClassNotFoundException {
        if (new File("bin").mkdirs()) {
            Logger.log(getClass().getName(), "Creating bin-folder", LogLevel.INFO);
        }

        Path path = Paths.get(SharedData.getFilepath());
        // checking if the application has crashed last time
        if (Files.exists(path)) {
            Logger.log(getClass().getName(), "Loading existing CrashFile", LogLevel.INFO);
            SharedData.loadFromFile();
            path = Paths.get(SharedData.getFilepath());
            Files.delete(path);
        } else {
            Logger.log(getClass().getName(), "CrashFile does not exist", LogLevel.INFO);
        }
    }

    void setWindowSize() {
        double MIN_HEIGHT = 600.0;
        double MIN_WIDTH = 900.0;

        Double savedWidth = springContext.getBean(SettingService.class).getWidth();
        Double savedHeight = springContext.getBean(SettingService.class).getHeight();

        // set Minimum-Window-Size
        MainApp.stage.setMinHeight(MIN_HEIGHT);
        MainApp.stage.setMinWidth(MIN_WIDTH);
        MainApp.stage.setMaxWidth(10000.0);
        MainApp.stage.setMaxHeight(10000.0);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        if (savedWidth >= screenBounds.getWidth() || savedHeight >= screenBounds.getHeight()) {
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setMaximized(true);
            return;
        }

        // enlarging screen-size
        if (savedWidth <= MIN_WIDTH || savedHeight <= MIN_HEIGHT) {
            stage.setWidth(MIN_WIDTH);
            stage.setHeight(MIN_HEIGHT);
            return;
        }

        // position of stage is in the top left corner
        stage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - savedWidth) / 2);
        stage.setY(screenBounds.getMinY() + (screenBounds.getHeight() - savedHeight) / 2);

        MainApp.stage.setWidth(savedWidth);
        MainApp.stage.setHeight(savedHeight);
    }

    private void saveWindowSize() {
        Double width = MainApp.stage.getWidth();
        Double height = MainApp.stage.getHeight();

        springContext.getBean(SettingService.class).updateWindowSize(width, height);
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
            saveWindowSize();

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

    private String selectScreen() {
        if (SharedData.getCurrentScreen() == null) {
            return SwitchScene.HOME;
        }

        return switch (SharedData.getCurrentScreen()) {
            case CREATE_AUTOMATIC -> SwitchScene.CREATE_TEST_AUTOMATIC;
            case CREATE_MANUAL -> SwitchScene.CREATE_TEST_MANUAL;
            case QUESTION_CREATE -> SwitchScene.CREATE_QUESTION;
            case QUESTION_EDIT -> SwitchScene.EDIT_QUESTION;
            case SETTINGS -> SwitchScene.SETTINGS;
            default -> SwitchScene.HOME;
        };
    }

    public ResourceBundle getResourceBundle() {
        Setting settings = springContext.getBean(SettingService.class).addIfNotExisting(new Setting());
        Locale locale = new Locale("en", "US");

        if (settings == null) {
            int lang = springContext.getBean(SettingService.class).getLanguage();
            String langAbbreviation = Language.getAbbreviation(lang);
            return ResourceBundle.getBundle("common." + langAbbreviation, locale);
        } else {
            String langAbbreviation = Language.getAbbreviation(settings.getLanguage());  // default: english
            return ResourceBundle.getBundle("common." + langAbbreviation, locale);
        }
    }

    private void setStageIcon(Stage stage) {
        File file = new File("src/main/resources/com/example/application/icons/taskbar_icon.png");
        Image languageImage = new Image(file.toURI().toString());
        stage.getIcons().add(languageImage);
    }
}