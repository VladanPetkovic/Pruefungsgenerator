package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class CreateAutomatic_ScreenController extends ScreenController {
    @FXML
    private VBox addQuestionVBox; // Reference to the VBox containing the "Add Question" button
    private int questionCount = 0; // Variable to keep track of the question count
    private List<VBox> vBoxList;
    @FXML
    private CreateManual_ScreenController createManualScreenController;

    @FXML
    public void initialize() {
        vBoxList = new ArrayList<>();
        onAddQuestionBtnClick();
    }

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

        vBoxList.add(newQuestionVBox);
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
        /*
        slider.setOnMouseReleased(e -> {
            System.out.println((int)slider.getValue());
        });
        */
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
        label.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());

        label.setPrefHeight(150.0);
        label.setPrefWidth(1000.0);
        label.setTextFill(Paint.valueOf("#e8e4e4"));

        parentVBox.getChildren().add(label);
    }

    private void createMenuButton(VBox parentVBox) {
        MenuButton menuButton = new MenuButton("Choose category...");
        menuButton.getStyleClass().add("automatic_create_dropdown");
        menuButton.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());

        VBox innerVBox = new VBox(menuButton);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());

        parentVBox.getChildren().add(innerVBox);
    }

    private void createSpinner(VBox parentVBox) {
        Spinner<Double> spinner = new Spinner<>();

        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1, 0.5);

        spinner.setValueFactory(valueFactory);

        spinner.getStyleClass().add("automatic_create_spinner");
        spinner.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());

        VBox innerVBox = new VBox(spinner);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());

        parentVBox.getChildren().add(innerVBox);
    }

    private void createSlider(VBox parentVBox) {
        Slider slider = new Slider();
        slider.setId("difficulty_slider");
        slider.setMajorTickUnit(1.0);
        slider.setMax(10.0);
        slider.setMin(1.0);
        slider.setMinorTickCount(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
        slider.setStyle("-fx-background-color: #2f2f2f;");
        slider.getStyleClass().add("slider-tool");
        slider.setValue(5.0);

        VBox innerVBox = new VBox(slider);
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        innerVBox.getStyleClass().add("automatic_create_vbox");
        innerVBox.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());

        parentVBox.getChildren().add(innerVBox);
    }

    @FXML
    protected void onCreateAutTestBtnClick(ActionEvent event) {
        // Loop through each VBox in the list
        for (VBox vbox : vBoxList) {
            String selectedCategory = "";
            int selectedDifficulty = 0;
            double selectedPoints = 0;

            // Loop through each node in the current VBox
            for (Node node1 : vbox.getChildren()) {
                if (node1 instanceof VBox) {
                    for (Node node2 : ((VBox) node1).getChildren()) {
                        if (node2 instanceof MenuButton menuButton) {
                            selectedCategory = menuButton.getText();
                            for (int i = 0; i < menuButton.getItems().size(); i++) {
                                MenuItem menuItem = menuButton.getItems().get(i);
                                if (menuItem.getText().equals(selectedCategory)) {
                                    break;
                                }
                            }
                        } else if (node2 instanceof Spinner spinner) {
                            selectedPoints = (double) spinner.getValue();
                        } else if (node2 instanceof Slider slider) {
                            selectedDifficulty = (int) slider.getValue();
                        }
                    }
                }
            }

            // Perform the database query based on the selected values
            Question queryQuestion = new Question();
            if(!Objects.equals(selectedCategory, "")) {
                queryQuestion.setCategory(SQLiteDatabaseConnection.CategoryRepository.get(selectedCategory));
            }


            queryQuestion.setPoints((float) selectedPoints);
            queryQuestion.setDifficulty(selectedDifficulty);

            // Perform the database query and print the results
            ArrayList<Question> queryResult = SQLiteDatabaseConnection.questionRepository.getAll(queryQuestion, "MACS1", false);

            if (!queryResult.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(queryResult.size());
                Question newQuestion = queryResult.get(randomIndex);
                SharedData.getTestQuestions().add(newQuestion);
            }
        }

        // switch scene to createTestManual
        switchScene(createTestManual, true);
    }
}
