package com.example.frontend.controller;

import com.example.frontend.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;

import java.io.IOException;

public class FXMLDependencyInjection {
    public static Parent load(String location) throws IOException {
        FXMLLoader loader = getLoader(location);
        return loader.load();
    }

    public static FXMLLoader getLoader(String location) {
        return new FXMLLoader(
                MainApp.class.getResource(location),
                null,
                new JavaFXBuilderFactory(),
                controllerClass-> MainApp.controllerFactory.create(controllerClass)
        );
    }
}
