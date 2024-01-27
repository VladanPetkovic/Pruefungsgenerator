package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.SearchObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import java.util.*;

public class CreateAutomatic_ScreenController extends ScreenController {
    @FXML
    private VBox addQuestionVBox; // Reference to the VBox containing the "Add Question" button
    private int questionCount = 0; // Variable to keep track of the question count
    @FXML
    private CreateManual_ScreenController createManualScreenController;

    @FXML
    public void initialize() {
        onAddQuestionBtnClick();
    }

    @FXML
    private void onAddQuestionBtnClick() {
        // Increment the question count
        questionCount++;

        // Create a new VBox with the required structure
        VBox newQuestionVBox = createNewQuestionVBox();

        // Add new ArrayList of SearchObjects to our searchData-array in SharedData
        SharedData.getSearchObjectsAutTestCreate().add(new ArrayList<>());

        // Set the event handlers for the components within the new VBox
        setEventHandlers(newQuestionVBox, this.questionCount);

        // Get the parent of the parent (grandparent) of addQuestionVBox
        VBox grandparentVBox = (VBox) addQuestionVBox.getParent().getParent();

        // Get the index of the parent of addQuestionVBox in its grandparent
        int parentIndex = grandparentVBox.getChildren().indexOf(addQuestionVBox.getParent());

        // Add the new VBox just before the addQuestionVBox
        grandparentVBox.getChildren().add(parentIndex, newQuestionVBox);
    }

    private void setEventHandlers(VBox questionVBox, int vBoxNumber) {
        for (Node node : questionVBox.getChildren()) {
            if (node instanceof VBox) {
                setEventHandlers((VBox) node, vBoxNumber);
            } else if (node instanceof MenuButton) {
                MenuButton menuButton = (MenuButton) node;
                setMenuButtonHandler(menuButton, vBoxNumber);
            } else if (node instanceof Spinner) {
                Spinner spinner = (Spinner) node;
                setSpinnerHandler(spinner, vBoxNumber);
            } else if (node instanceof Slider) {
                Slider slider = (Slider) node;
                setSliderHandler(slider, vBoxNumber);
            }
        }
    }

    private void setMenuButtonHandler(MenuButton menuButton, int vBoxNumber) {
        SearchObject<String> searchObject = new SearchObject<>();
        menuButton.getItems().clear();
        int course_id = SharedData.getSelectedCourse().getCourse_id();
        ArrayList<Category> categories = SQLiteDatabaseConnection.CategoryRepository.getAll(course_id);
        for (Category category : categories) {
            MenuItem menuItem = new MenuItem(category.getCategory());
            menuItem.setOnAction(e -> {
                String categoryName = category.getCategory();
                menuButton.setText(categoryName);
                searchObject.setObjectName("CAT");
                searchObject.setValueOfObject(categoryName);
                searchObject.setSet(true);
            });
            menuButton.getItems().add(menuItem);
        }
        SharedData.getSearchObjectsAutTestCreate().get(vBoxNumber - 1).add(searchObject);
    }

    private void setSpinnerHandler(Spinner<Double> spinner, int vBoxNumber) {
        SearchObject<Float> searchObject = new SearchObject<>();
        spinner.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                double points = (Double) spinner.getValue();
                searchObject.setObjectName("POINT");
                searchObject.setValueOfObject((float) points);
                searchObject.setSet(true);
            }
        });
        SharedData.getSearchObjectsAutTestCreate().get(vBoxNumber - 1).add(searchObject);
    }

    private void setSliderHandler(Slider slider, int vBoxNumber) {
        SearchObject<Integer> searchObject = new SearchObject<>();
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                int difficulty = (int) slider.getValue();
                searchObject.setObjectName("DIFF");
                searchObject.setValueOfObject(difficulty);
                searchObject.setSet(true);
            }
        });
        SharedData.getSearchObjectsAutTestCreate().get(vBoxNumber - 1).add(searchObject);
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
        // remove one redundant element of our searchObjectArray
        SharedData.getSearchObjectsAutTestCreate().removeIf(ArrayList::isEmpty);

        // Loop through each array of searchOptions
        for (ArrayList<SearchObject<?>> searchObjectArrayList : SharedData.getSearchObjectsAutTestCreate()) {
            String selectedCategory = "";
            int selectedDifficulty = 0;
            float selectedPoints = 0;

            for(SearchObject<?> searchObject : searchObjectArrayList) {
                // setting points
                if (Objects.equals(searchObject.getObjectName(), "POINT") && searchObject.isSet()) {
                    selectedPoints = (float) searchObject.getValueOfObject();
                } else if (Objects.equals(searchObject.getObjectName(), "DIFF") && searchObject.isSet()) {
                    // setting difficulty
                    selectedDifficulty = (int) searchObject.getValueOfObject();
                } else if (Objects.equals(searchObject.getObjectName(), "CAT") && searchObject.isSet()) {
                    // setting category
                    selectedCategory = (String) searchObject.getValueOfObject();
                }
            }

            // get category from DB
            Question queryQuestion = new Question();
            if(!Objects.equals(selectedCategory, "")) {
                queryQuestion.setCategory(SQLiteDatabaseConnection.CategoryRepository.get(selectedCategory));
            }
            if(selectedPoints != 0) {
                queryQuestion.setPoints(selectedPoints);
            }
            if(selectedDifficulty != 0) {
                queryQuestion.setDifficulty(selectedDifficulty);
            }

            // Perform the database query and print the results
            ArrayList<Question> queryResult = SQLiteDatabaseConnection.questionRepository.getAll(queryQuestion, "MACS1", false);

            if (!queryResult.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(queryResult.size());
                Question newQuestion = queryResult.get(randomIndex);
                SharedData.getTestQuestions().add(newQuestion);
            }
        }

        // testing our results
        // printQuestions(SharedData.getTestQuestions());
        // deleting our options from our SearchObjects array
        SharedData.getSearchObjectsAutTestCreate().clear();
        this.questionCount = 0;
        // switch scene to createTestManual
        switchScene(createTestManual, true);
    }

    private void printQuestions(ArrayList<Question> questions) {
        if(questions.isEmpty()) {
            System.out.println("No questions found");
            return;
        }

        for(Question question : questions) {
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
    }
}
