package com.example.frontend.controller;

import com.example.backend.db.daos.TopicDAO;
import com.example.backend.db.models.Topic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.action.Action;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateAutomatic_ScreenController extends ScreenController implements Initializable {
    @FXML
    private MenuButton topicMenuButton;
    @FXML
    private Slider difficultySlider;
    @FXML
    private Spinner<Integer> pointsSpinner;

    private TopicDAO topicDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        topicDAO = new TopicDAO();
        ArrayList<Topic> topics = topicDAO.readAll();
        for (Topic topic: topics
        ) {
            MenuItem menuItem = new MenuItem(topic.getTopic());
            menuItem.setOnAction(e -> {
                topicMenuButton.setText(topic.getTopic());
            });
            topicMenuButton.getItems().add(menuItem);
        }
        //difficultySlider.setMajorTickUnit(10);
        //difficultySlider.setMinorTickCount(10);
        difficultySlider.setSnapToTicks(true);
        difficultySlider.setOnMouseReleased(this::onDifficultySliderMouseReleased);
    }

    private void onDifficultySliderMouseReleased(MouseEvent mouseEvent) {
        System.out.println((int)difficultySlider.getValue());
    }

    @Override
    protected void onCreateAutTestBtnClick(ActionEvent event) {
        // Abrufen der ausgewählten Filterparameter
        String selectedTopic = topicMenuButton.getText(); // Hier musst du den ausgewählten Wert richtig abrufen
        int selectedDifficulty = (int) difficultySlider.getValue();
        int selectedPoints = pointsSpinner.getValue();

        // Hier sollte die Logik für die Datenbankabfrage erfolgen
        // Verwende questionRepository.getAll(selectedTopic, selectedDifficulty, selectedPoints)

        // Nach der Datenbankabfrage weiter zur manuellen Erstellung
        switchToManualCreateScreen(); // Implementiere diese Methode entsprechend
    }

    private void switchToManualCreateScreen() {
        // Implementiere die Navigation zur manuellen Erstellung (loadFXML, setScene, etc.)
    }

    // Diese Methode kann in der Initialisierung des Controllers aufgerufen werden,
    // um die verfügbaren Themen im Menü hinzuzufügen
    public void setAvailableTopics(List<String> topics) {
        for (String topic : topics) {
            MenuItem menuItem = new MenuItem(topic);
            topicMenuButton.getItems().add(menuItem);
        }
    }
}