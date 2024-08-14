package com.example.application.frontend.modals;

import com.example.application.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * utility class for managing JavaFX scenes
 *
 * @param <T> The type of controller associated with the scene
 */
public class Modal<T> {
    private FXMLLoader fxmlLoader; // FXMLLoader for loading FXML files
    private Parent root; // root node of the scene
    private String path; // path to the FXML file
    public Scene scene; // JavaFX scene
    public T controller; // Controller associated with the scene

    /**
     * constructs a new Modal object
     *
     * @param path the path to the FXML file
     */
    public Modal(String path) {
        this.path = path;
        loadComponents(); // load components upon instantiation
    }

    /**
     * loads the components of the scene
     * initializes FXMLLoader to set the root, scene, and controller variables
     */
    public void loadComponents() {
        fxmlLoader = new FXMLLoader(MainApp.class.getResource(path), MainApp.resourceBundle);
        // TODO: hier auf FXMLDependencyInjection verweisen und nicht direkt so aufrufen
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
}
