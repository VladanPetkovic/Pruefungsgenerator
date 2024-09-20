package com.example.application.frontend.controller;

import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Question;
import com.example.application.MainApp;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SwitchScene {
    // define and initialize screens for different functionalities
    public static String CREATE_TEST_AUTOMATIC = "sites/create_automatic.fxml";
    public static String CREATE_TEST_MANUAL = "sites/create_manual.fxml";
    public static String CREATE_QUESTION = "sites/question_create.fxml";
    public static String EDIT_QUESTION = "sites/question_edit.fxml";
    public static String HOME = "sites/home.fxml";
    public static String PDF_PREVIEW = "sites/pdf_preview.fxml";
    public static String SETTINGS = "sites/settings.fxml";


    // Define a hashmap for the title banner for the different pages
    private static final Map<String, String> SCREEN_TITLES = new HashMap<>();
    private static final Map<String, String> SCREEN_TOOLTIPS = new HashMap<>();

    static {
        loadTitles();
    }

    private static void loadTitles() {
        SCREEN_TITLES.clear();
        SCREEN_TITLES.put(CREATE_TEST_AUTOMATIC, MainApp.resourceBundle.getString("create_test_automatic"));
        SCREEN_TITLES.put(CREATE_TEST_MANUAL, MainApp.resourceBundle.getString("create_test_manual"));
        SCREEN_TITLES.put(CREATE_QUESTION, MainApp.resourceBundle.getString("create_question"));
        SCREEN_TITLES.put(EDIT_QUESTION, MainApp.resourceBundle.getString("edit_question"));
        SCREEN_TITLES.put(HOME, MainApp.resourceBundle.getString("home"));
        SCREEN_TITLES.put(PDF_PREVIEW, MainApp.resourceBundle.getString("pdf_preview"));
        SCREEN_TITLES.put(SETTINGS, MainApp.resourceBundle.getString("settings"));
    }

    private static void loadTooltips() {
        SCREEN_TOOLTIPS.clear();
        SCREEN_TOOLTIPS.put(CREATE_TEST_AUTOMATIC, MainApp.resourceBundle.getString("help_tooltip_create_test_automatic"));
        SCREEN_TOOLTIPS.put(CREATE_TEST_MANUAL, MainApp.resourceBundle.getString("help_tooltip_create_test_manual"));
        SCREEN_TOOLTIPS.put(CREATE_QUESTION, MainApp.resourceBundle.getString("help_tooltip_create_question"));
        SCREEN_TOOLTIPS.put(EDIT_QUESTION, MainApp.resourceBundle.getString("help_tooltip_edit_question"));
        SCREEN_TOOLTIPS.put(HOME, MainApp.resourceBundle.getString("help_tooltip_home"));
        SCREEN_TOOLTIPS.put(PDF_PREVIEW, MainApp.resourceBundle.getString("help_tooltip_pdf_preview"));
        SCREEN_TOOLTIPS.put(SETTINGS, MainApp.resourceBundle.getString("help_tooltip_settings"));
    }

    /**
     * switches to the specified screen and optionally refreshes its components
     *
     * @param path the path to the specific screen
     */
    public static void switchScene(String path) throws IOException {
        // resetting filterquestion
        SharedData.setFilterQuestion(new Question());

        // Update pageTitle and helpTooltip in SharedData
        loadTitles();
        String pageTitle = SCREEN_TITLES.get(path);
        if (pageTitle != null) {
            SharedData.setPageTitle(pageTitle);
        }
        loadTooltips();
        String tooltipText = SCREEN_TOOLTIPS.get(path);
        if (tooltipText != null) {
            SharedData.setHelpTooltip(tooltipText);
        }

        Parent newParent = null;
        try {
            newParent = FXMLDependencyInjection.load(path, MainApp.resourceBundle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // set the scene and display it
        MainApp.stage.setHeight(MainApp.stage.getHeight());
        MainApp.stage.setWidth(MainApp.stage.getWidth());
        Scene newScene = new Scene(newParent);
        MainApp.stage.setScene(newScene);
        MainApp.stage.show();
    }
}
