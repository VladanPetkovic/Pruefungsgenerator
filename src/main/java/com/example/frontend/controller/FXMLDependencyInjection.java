package com.example.frontend.controller;

import com.example.frontend.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class FXMLDependencyInjection {
    public static Parent load(String location) throws IOException {
        FXMLLoader loader = getLoader(location, MainApp.resourceBundle);
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
