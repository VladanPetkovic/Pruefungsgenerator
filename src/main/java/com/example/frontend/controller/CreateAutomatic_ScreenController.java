package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.models.Question;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.SearchObject;
import com.example.frontend.components.CustomDoubleSpinner;
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
    private VBox addQuestionVBox; // reference to the VBox containing the "Add Question" button
    private int questionCount = 0; // variable to keep track of the question count

    @FXML
    public void initialize() {
        onAddQuestionBtnClick();
    }

    @FXML
    private void onAddQuestionBtnClick() {
        questionCount++;

        VBox newQuestionVBox = createNewQuestionVBox();
        SharedData.getSearchObjectsAutTestCreate().add(new ArrayList<>());

        // set the event handlers for the components within the new VBox
        setEventHandlers(newQuestionVBox, this.questionCount);
        VBox grandparentVBox = (VBox) addQuestionVBox.getParent().getParent();

        // get the index of the parent of addQuestionVBox in its grandparent
        int parentIndex = grandparentVBox.getChildren().indexOf(addQuestionVBox.getParent());

        // add the new VBox just before the addQuestionVBox
        grandparentVBox.getChildren().add(parentIndex, newQuestionVBox);
    }

    // set event handlers recursively for components within a VBox
    private void setEventHandlers(VBox questionVBox, int vBoxNumber) {
        // iterate over all nodes within the VBox
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

    // set event handler for MenuButton to select category
    private void setMenuButtonHandler(MenuButton menuButton, int vBoxNumber) {
        // create a new SearchObject to store category selection
        SearchObject<String> searchObject = new SearchObject<>();
        menuButton.getItems().clear();
        // retrieve categories for the selected course
        int course_id = SharedData.getSelectedCourse().getId();
        ArrayList<Category> categories = SQLiteDatabaseConnection.CategoryRepository.getAll(course_id);
        // populate the MenuButton with category options and set event handlers for selection
        for (Category category : categories) {
            MenuItem menuItem = new MenuItem(category.getName());
            menuItem.setOnAction(e -> {
                String categoryName = category.getName();
                menuButton.setText(categoryName);
                searchObject.setObjectName("CAT");
                searchObject.setValueOfObject(categoryName);
                searchObject.setSet(true);
            });
            menuButton.getItems().add(menuItem);
        }
        // add the selected category to the appropriate ArrayList in SharedData
        SharedData.getSearchObjectsAutTestCreate().get(vBoxNumber - 1).add(searchObject);
    }

    // set event handler for Spinner to select points
    private void setSpinnerHandler(Spinner<Double> spinner, int vBoxNumber) {
        // create a new SearchObject to store points selection
        SearchObject<Float> searchObject = new SearchObject<>();
        // listen for changes in the spinner value and update the SearchObject accordingly
        spinner.valueFactoryProperty().addListener((observable, oldNumber, newNumber) -> {
            double points = (Double) spinner.getValue();
            searchObject.setObjectName("POINT");
            searchObject.setValueOfObject((float) points);
            searchObject.setSet(true);
        });
        // add the selected points to the appropriate ArrayList in SharedData
        SharedData.getSearchObjectsAutTestCreate().get(vBoxNumber - 1).add(searchObject);
    }

    // set event handler for Slider to select difficulty
    private void setSliderHandler(Slider slider, int vBoxNumber) {
        // create a new SearchObject to store difficulty selection
        SearchObject<Integer> searchObject = new SearchObject<>();
        // listen for changes in the slider value and update the SearchObject accordingly
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                int difficulty = (int) slider.getValue();
                searchObject.setObjectName("DIFF");
                searchObject.setValueOfObject(difficulty);
                searchObject.setSet(true);
            }
        });
        // add the selected difficulty to the appropriate ArrayList in SharedData
        SharedData.getSearchObjectsAutTestCreate().get(vBoxNumber - 1).add(searchObject);
    }

    // create a new VBox for adding a question with required components
    private VBox createNewQuestionVBox() {
        // create a new VBox with the specified structure
        VBox questionVBox = new VBox();

        // create and add the label indicating the question number
        createLabel("Question " + questionCount, questionVBox);

        // create and add components to the new VBox
        createLabel("Category", questionVBox);
        createMenuButton(questionVBox);
        createLabel("Points", questionVBox);
        createSpinner(questionVBox);
        createLabel("Difficulty", questionVBox);
        createSlider(questionVBox);

        return questionVBox;
    }

    // helper method to create a label with custom styling
    private void createLabel(String labelText, VBox parentVBox) {
        // create a new label with the specified text
        Label label = new Label(labelText);
        // add custom styling to the label
        label.getStyleClass().add("automatic_create_label");

        // set preferred height and width for the label // TODO: don't use inline css - make css-class
        label.setPrefHeight(150.0);
        label.setPrefWidth(1000.0);
        // set text color
        label.setTextFill(Paint.valueOf("#e8e4e4"));

        // add the label to the parent VBox
        parentVBox.getChildren().add(label);
    }

    // helper method to create a MenuButton with custom styling
    private void createMenuButton(VBox parentVBox) {
        // create a new MenuButton with default text
        MenuButton menuButton = new MenuButton("Choose category...");
        // add custom styling to the MenuButton
        menuButton.getStyleClass().add("menuButton_dark");

        // create a new VBox to contain the MenuButton
        VBox innerVBox = new VBox(menuButton);
        // set preferred height and width for the VBox
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        // add custom styling to the VBox
        innerVBox.getStyleClass().add("automatic_create_vbox");

        // add the VBox containing the MenuButton to the parent VBox
        parentVBox.getChildren().add(innerVBox);
    }

    // helper method to create a Spinner with custom styling
    private void createSpinner(VBox parentVBox) {
        // create a new Spinner
        CustomDoubleSpinner spinner = new CustomDoubleSpinner();

        // add custom styling to the Spinner
        spinner.getStyleClass().add("automatic_create_spinner");

        // create a new VBox to contain the Spinner
        VBox innerVBox = new VBox(spinner);
        // set preferred height and width for the VBox
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        // add custom styling to the VBox
        innerVBox.getStyleClass().add("automatic_create_vbox");

        // add the VBox containing the Spinner to the parent VBox
        parentVBox.getChildren().add(innerVBox);
    }

    // helper method to create a Slider with custom styling
    private void createSlider(VBox parentVBox) {
        // create a new Slider
        Slider slider = new Slider();
        // set the ID for the slider
        slider.setId("difficulty_slider");
        // set major tick unit, max, min, and other properties for the Slider
        slider.setMajorTickUnit(1.0);
        slider.setMax(10.0);
        slider.setMin(1.0);
        slider.setMinorTickCount(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
        slider.setStyle("-fx-background-color: #2f2f2f;");
        // add custom styling to the Slider
        slider.getStyleClass().add("slider-tool");
        // set default value for the Slider
        slider.setValue(5.0);

        // create a new VBox to contain the Slider
        VBox innerVBox = new VBox(slider);
        // set preferred height and width for the VBox
        innerVBox.setPrefHeight(33.0);
        innerVBox.setPrefWidth(1000.0);
        // add custom styling to the VBox
        innerVBox.getStyleClass().add("automatic_create_vbox");

        // add the VBox containing the Slider to the parent VBox
        parentVBox.getChildren().add(innerVBox);
    }

    // method triggered when the "Create Test" button is clicked
    @FXML
    protected void onCreateAutTestBtnClick(ActionEvent event) {
        // remove any empty elements from the searchObjectArray
        SharedData.getSearchObjectsAutTestCreate().removeIf(ArrayList::isEmpty);

        // loop through each array of searchOptions
        for (ArrayList<SearchObject<?>> searchObjectArrayList : SharedData.getSearchObjectsAutTestCreate()) {
            // initialize variables to store selected category, difficulty, and points
            String selectedCategory = "";
            int selectedDifficulty = 0;
            float selectedPoints = 0;

            // iterate through each searchObject in the current array
            for (SearchObject<?> searchObject : searchObjectArrayList) {
                // check if the searchObject represents points and is set
                if (Objects.equals(searchObject.getObjectName(), "POINT") && searchObject.isSet()) {
                    // set the selected points
                    selectedPoints = (float) searchObject.getValueOfObject();
                } else if (Objects.equals(searchObject.getObjectName(), "DIFF") && searchObject.isSet()) {
                    // set the selected difficulty
                    selectedDifficulty = (int) searchObject.getValueOfObject();
                } else if (Objects.equals(searchObject.getObjectName(), "CAT") && searchObject.isSet()) {
                    // set the selected category
                    selectedCategory = (String) searchObject.getValueOfObject();
                }
            }

            // create a new Question object to query the database
            Question queryQuestion = new Question();
            // check if a category is selected
            if (!Objects.equals(selectedCategory, "")) {
                // set the category of the query question from the database
                queryQuestion.setCategory(SQLiteDatabaseConnection.CategoryRepository.get(selectedCategory));
            }
            // check if points are selected
            if (selectedPoints != 0) {
                // set the points of the query question
                queryQuestion.setPoints(selectedPoints);
            }
            // check if difficulty is selected
            if (selectedDifficulty != 0) {
                // set the difficulty of the query question
                queryQuestion.setDifficulty(selectedDifficulty);
            }

            // perform the database query to retrieve questions based on the criteria
            ArrayList<Question> queryResult = SQLiteDatabaseConnection.questionRepository.getAll(queryQuestion, SharedData.getSelectedCourse().getName());

            // check if query result is not empty
            if (!queryResult.isEmpty()) {
                // generate a random index to select a question from the result
                Random random = new Random();
                int randomIndex = random.nextInt(queryResult.size());
                // get the randomly selected question
                Question newQuestion = queryResult.get(randomIndex);
                if (!containsQuestionWithId(newQuestion.getId())) {
                    // add the selected question to the test questions list, if not already existing in the testQuestions-array
                    SharedData.getTestQuestions().add(newQuestion);
                }
            }
        }

        // clear the search options from the SearchObjects array
        SharedData.getSearchObjectsAutTestCreate().clear();
        // reset the question count to zero
        this.questionCount = 0;

        switchScene(createTestManual, true);

    }

}
