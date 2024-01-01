package com.example.frontend.controller;

import com.example.frontend.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Screen<T> {
    private FXMLLoader fxmlLoader;
    private Parent root;
    private String path;
    public Scene scene;
    public T controller;

    public Screen(String path){
        this.path = path;
        loadComponents();
    }

    public void loadComponents(){
        fxmlLoader = new FXMLLoader(MainApp.class.getResource(path));
        try {
            root = fxmlLoader.load();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        scene = new Scene(root);
        controller = fxmlLoader.getController();
    }
}
