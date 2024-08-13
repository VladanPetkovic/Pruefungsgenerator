package com.example.application.frontend.controller;

import com.example.application.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ResourceBundle;

public class FXMLDependencyInjection {
    public static Parent load(String location, ResourceBundle resourceBundle) throws IOException {
        FXMLLoader loader = getLoader(location, resourceBundle);
        return loader.load();
    }

    public static FXMLLoader getLoader(String location, ResourceBundle resourceBundle) {
        return new FXMLLoader(
                MainApp.class.getResource(location),
                resourceBundle,
                new JavaFXBuilderFactory(),
                controllerClass-> MainApp.controllerFactory.create(controllerClass)
        );
    }
}
