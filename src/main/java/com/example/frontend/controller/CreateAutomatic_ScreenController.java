package com.example.frontend.controller;

import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.daos.CategoryDAO;
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
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Random;

public class CreateAutomatic_ScreenController extends ScreenController {

    private CategoryDAO categoryDAO;

    @FXML
    private VBox addQuestionVBox; // Reference to the VBox containing the "Add Question" button

    private int questionCount = 0; // Variable to keep track of the question count

    private List<VBox> vBoxList;

    private Question selectedQuestion;

    @FXML
    private CreateManual_ScreenController createManualScreenController;

    /*@FXML
    public void initialize() {
        vBoxList = new ArrayList<>();
        categoryDAO = new CategoryDAO();
        onAddQuestionBtnClick();

        createManualScreenController = new CreateManual_ScreenController();
    }*/

    @FXML
    public void initialize() {
        vBoxList = new ArrayList<>();
        categoryDAO = new CategoryDAO();
        createManualScreenController = new CreateManual_ScreenController();
        VBox vbox_labels = createManualScreenController.getVbox_labels();
        onAddQuestionBtnClick();
    }

    /*@FXML
    public void initialize() {
        vBoxList = new ArrayList<>();
        categoryDAO = new CategoryDAO();
        createManualScreenController = new CreateManual_ScreenController();
        VBox vbox_labels = createManualScreenController.getVbox_labels();
        // Weitere Initialisierung, wenn erforderlich
        onAddQuestionBtnClick();
    }*/

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
        ArrayList<Category> categories = categoryDAO.readAll();
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
        spinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 20, 0.5, 0.5);
        spinner.setValueFactory(valueFactory);
        //TextFormatter<Double> textFormatter = new TextFormatter<>(new DoubleStringConverter());
        //spinner.getEditor().setTextFormatter(textFormatter);
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
        VBox vbox_labels = createManualScreenController.getVbox_labels();
        // Loop through each VBox in the list
        for (VBox vbox : vBoxList) {
            String selectedCategory = "";
            int selectedCategoryId = 0;
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
                                    selectedCategoryId = i + 1;
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
            queryQuestion.setCategory(new Category(selectedCategoryId, selectedCategory)); // Assuming Category has an appropriate constructor

            queryQuestion.setPoints((float) selectedPoints);
            queryQuestion.setDifficulty(selectedDifficulty);

            // Perform the database query and print the results
            ArrayList<Question> queryResult = SQLiteDatabaseConnection.questionRepository.getAll(queryQuestion, "MACS1", true);

            if (queryResult.isEmpty()) {
                System.out.println("No questions found");
            } else {
                Random random = new Random();
                int randomIndex = random.nextInt(queryResult.size());
                selectedQuestion = queryResult.get(randomIndex);
                printQuestion(selectedQuestion);

                // NEW
                createManualScreenController.addLabelToVBox(selectedQuestion.getPoints() + "\n" + selectedQuestion.getQuestionString());
                // switchToManualCreateScreen(); // Implement this method accordingly.

                Label label = new Label(selectedQuestion.getPoints() + "\n" +
                        selectedQuestion.getQuestionString());
                label.getStyleClass().add("automatic_create_label");
                label.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());
                vbox_labels.getChildren().add(label);
            }
        }

        // After handling the database query results, you can proceed with other actions as needed.
        switchToManualCreateScreen(); // Implement this method accordingly.
    }

    private void printQuestion(Question question) {
        System.out.println("_----------------------------------_");
        System.out.println("ID: " + question.getQuestion_id());
        System.out.println("QuestionString: " + question.getQuestionString());
        System.out.print("Keywords: ");
        for(Keyword keyword : question.getKeywords()) {
            System.out.print(keyword.getKeyword_text() + " ");
        }
        System.out.println();
        System.out.println("Answer: " + question.getAnswers());
        System.out.println("MC: " + question.getMultipleChoice());
        System.out.println("Category: " + question.getCategory().getCategory());
        System.out.println("Language: " + question.getLanguage());
        System.out.println("Difficulty: " + question.getDifficulty());
        System.out.println("Points: " + question.getPoints());
    }

    /*private void switchToManualCreateScreen() {
        // Implementiere die Navigation zur manuellen Erstellung (loadFXML, setScene, etc.)
        switchScene(createTestManual, true);
    }*/

    @FXML
    private void switchToManualCreateScreen() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/sites/create_manual.fxml"));
            Parent root = loader.load();

            // Get the controller for the CreateManual_Screen.fxml
            CreateManual_ScreenController createManualController = loader.getController();

            // Perform any other actions before switching screens

            // Iterate through vBoxList and add labels to vbox_labels
            for (VBox vbox : vBoxList) {
                // Your other actions...

                createManualController.addLabelToVBox(selectedQuestion.getPoints() + "\n" + selectedQuestion.getQuestionString());
            }

            // Finally, switch the scene to the new screen
            Scene scene = new Scene(root);
            Stage stage = new Stage(); // Create a new stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
    }

}
