package com.example.frontend.controller;

import com.example.frontend.components.NavbarController;
import com.example.frontend.components.QuestionFilter_ScreenController;
import com.example.frontend.components.TitleBanner_ScreenController;

public class ControllerFactory {
    public ControllerFactory() {
    }

    /*
     * Factory-Method Pattern
     */
    public Object create(Class<?> controllerClass) {
        return switch (controllerClass.getSimpleName()) {
            /* MAIN SCENES */
            case "CreateAutomatic_ScreenController" -> new CreateAutomatic_ScreenController();
            case "CreateManual_ScreenController" -> new CreateManual_ScreenController();
            case "Home_ScreenController" -> new Home_ScreenController();
            case "PdfPreview_ScreenController" -> new PdfPreview_ScreenController();
            case "QuestionCreate_ScreenController" -> new QuestionCreate_ScreenController();
            case "QuestionEdit_ScreenController" -> new QuestionEdit_ScreenController();
            case "Settings_ScreenController" -> new Settings_ScreenController();
            /* COMPONENTS */
            case "NavbarController" -> new NavbarController();
            case "QuestionFilter_ScreenController" -> new QuestionFilter_ScreenController();
            case "TitleBanner_ScreenController" -> new TitleBanner_ScreenController();
            default -> throw new IllegalArgumentException("Unknown controller class: " + controllerClass);
        };
    }
}
