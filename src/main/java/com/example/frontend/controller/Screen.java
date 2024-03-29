package com.example.frontend.controller;

import com.example.frontend.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * utility class for managing JavaFX scenes
 * @param <T> The type of controller associated with the scene
 */
public class Screen<T> {
    private FXMLLoader fxmlLoader; // FXMLLoader for loading FXML files
    private Parent root; // root node of the scene
    private String path; // path to the FXML file
    public Scene scene; // JavaFX scene
    public T controller; // Controller associated with the scene

    /**
     * constructs a new Screen object
     * @param path the path to the FXML file
     */
    public Screen(String path){
        this.path = path;
        loadComponents(); // load components upon instantiation
    }

    /**
     * loads the components of the scene
     * initializes FXMLLoader to set the root, scene, and controller variables
     */
    public void loadComponents(){
        fxmlLoader = new FXMLLoader(MainApp.class.getResource(path));
        try {
            // load FXML file
            root = fxmlLoader.load();
            // create scene from loaded root
            scene = new Scene(root);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // get the controller associated with the scene
        controller = fxmlLoader.getController();
    }

    /**
     * disables or enables the current scene
     * @param state if true, disables the scene; otherwise enables it
     */
    public void disableScene(boolean state){
        scene.getRoot().setDisable(state);
    }
}
