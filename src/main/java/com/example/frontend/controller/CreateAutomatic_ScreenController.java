package com.example.frontend.controller;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.daos.CategoryDAO;
import com.example.backend.db.models.Category;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
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

    private CategoryDAO categoryDAO;

    @FXML
    private VBox addQuestionVBox; // Reference to the VBox containing the "Add Question" button

    private int questionCount = 1; // Variable to keep track of the question count

    @FXML
    private void onAddQuestionBtnClick() {
        // Increment the question count
        questionCount++;

        // Create a new VBox with the required structure
        VBox newQuestionVBox = createNewQuestionVBox();

        // Set the event handlers for the components within the new VBox
        setEventHandlers(newQuestionVBox);

        // Get the parent of the parent (grandparent) of addQuestionVBox
        VBox grandparentVBox = (VBox) addQuestionVBox.getParent().getParent();

        // Get the index of the parent of addQuestionVBox in its grandparent
        int parentIndex = grandparentVBox.getChildren().indexOf(addQuestionVBox.getParent());

        // Add the new VBox just before the addQuestionVBox
        grandparentVBox.getChildren().add(parentIndex, newQuestionVBox);
    }

    private void setEventHandlers(VBox questionVBox) {
        for (Node node : questionVBox.getChildren()) {
            if (node instanceof VBox) {
                setEventHandlers((VBox) node);
            } else if (node instanceof MenuButton) {
                MenuButton menuButton = (MenuButton) node;
                setMenuButtonHandler(menuButton);
            } else if (node instanceof Spinner) {
                Spinner spinner = (Spinner) node;
                setSpinnerHandler(spinner);
            } else if (node instanceof Slider) {
                Slider slider = (Slider) node;
                setSliderHandler(slider);
            }
        }
    }

    private void setMenuButtonHandler(MenuButton menuButton) {
        menuButton.getItems().clear();
        ArrayList<Category> categories = SQLiteDatabaseConnection.CategoryRepository.getAll();
        for (Category category : categories) {
            MenuItem menuItem = new MenuItem(category.getCategory());
            menuItem.setOnAction(e -> {
                menuButton.setText(category.getCategory());
            });
            menuButton.getItems().add(menuItem);
        }
    }

    private void setSpinnerHandler(Spinner<Integer> spinner) {
        // Add event handlers for the spinner if needed
        // Example: spinner.setOnMouseClicked(event -> handleSpinnerClick(event, spinner));
    }

    private void setSliderHandler(Slider slider) {
        // Add event handlers for the slider if needed
        // Example: slider.setOnMouseReleased(event -> handleSliderMouseReleased(event, slider));
    }

    private VBox createNewQuestionVBox() {
        // Create a new VBox with the specified structure
        VBox questionVBox = new VBox();

        // Create and add the label indicating the question number
        createLabel("Question " + questionCount, questionVBox);

        // Create and add components to the new VBox
        createLabel("Category", questionVBox);
        createMenuButton(questionVBox);
        createLabel("Points", questionVBox);
        createSpinner(questionVBox);
        createLabel("Difficulty", questionVBox);
        createSlider(questionVBox);

        return questionVBox;
    }

    private void createLabel(String labelText, VBox parentVBox) {
        Label label = new Label(labelText);
        label.getStyleClass().add("automatic_create_label");
        label.getStylesheets().add("@../css/main.css");

        label.setPrefHeight(150.0);
        label.setPrefWidth(1000.0);
        label.setTextFill(Paint.valueOf("#e8e4e4"));

        parentVBox.getChildren().add(label);
    }

    private void createMenuButton(VBox parentVBox) {
        MenuButton menuButton = new MenuButton("Choose category...");
        menuButton.getStyleClass().add("automatic_create_dropdown");
        menuButton.getStylesheets().add("@../css/main.css");

        VBox innerVBox = new VBox(menuButton);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add("@../css/main.css");

        parentVBox.getChildren().add(innerVBox);
    }

    private void createSpinner(VBox parentVBox) {
        Spinner spinner = new Spinner();
        spinner.setEditable(true);
        spinner.getStyleClass().add("automatic_create_spinner");
        spinner.getStylesheets().add("@../css/main.css");

        VBox innerVBox = new VBox(spinner);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add("@../css/main.css");

        parentVBox.getChildren().add(innerVBox);
    }

    private void createSlider(VBox parentVBox) {
        Slider slider = new Slider();
        slider.setId("difficulty_slider");
        slider.setMajorTickUnit(2.0);
        slider.setMax(10.0);
        slider.setMin(1.0);
        slider.setMinorTickCount(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setStyle("-fx-background-color: #2f2f2f;");
        slider.getStyleClass().add("slider-tool");
        slider.setValue(5.5);

        VBox innerVBox = new VBox(slider);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add("@../css/main.css");

        parentVBox.getChildren().add(innerVBox);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<Category> categories = SQLiteDatabaseConnection.CategoryRepository.getAll();
        for (Category category: categories) {
            MenuItem menuItem = new MenuItem(category.getCategory());
            menuItem.setOnAction(e -> {
                topicMenuButton.setText(category.getCategory());
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

    @FXML
    protected void onCreateAutTestBtnClick(ActionEvent event) {
        // Abrufen der ausgewählten Filterparameter
        String selectedCategory = topicMenuButton.getText(); // Hier musst du den ausgewählten Wert richtig abrufen
        int selectedDifficulty = (int) difficultySlider.getValue();
        int selectedPoints = pointsSpinner.getValue();

        // Hier sollte die Logik für die Datenbankabfrage erfolgen
        // Verwende questionRepository.getAll(selectedCategory, selectedDifficulty, selectedPoints)

        // Nach der Datenbankabfrage weiter zur manuellen Erstellung
        switchToManualCreateScreen(); // Implementiere diese Methode entsprechend
    }

    private void switchToManualCreateScreen() {
        // Implementiere die Navigation zur manuellen Erstellung (loadFXML, setScene, etc.)
    }
}