package com.example.frontend.controller;

import com.example.backend.app.Screen;
import com.example.backend.app.SharedData;
import com.example.backend.db.models.Question;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.frontend.components.CustomDoubleSpinner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.util.*;

public class CreateAutomatic_ScreenController extends ScreenController {
    @FXML
    private VBox addQuestionVBox; // reference to the VBox containing the "Add Question" button
    private int questionCount = 0;
    private ArrayList<MenuButton> categoriesMenuButtons = new ArrayList<>();
    private ArrayList<Category> categories;
    private ArrayList<Slider> difficultySliders = new ArrayList<>();
    private ArrayList<CustomDoubleSpinner> pointsSpinners = new ArrayList<>();

    private void setButtonEventHandlers(List<Node> nodes) {
        for (Node node : nodes) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnMousePressed(this::onButtonPressed);
                button.setOnMouseReleased(this::onButtonReleased);
            } else if (node instanceof VBox) {
                setButtonEventHandlers(((VBox) node).getChildren());
            }
        }
    }

    @FXML
    public void initialize() {
        int course_id = SharedData.getSelectedCourse().getId();
        categories = SQLiteDatabaseConnection.CategoryRepository.getAll(course_id);

        // set (press & release) event handlers for all buttons that are dynamically generated
        setButtonEventHandlers(addQuestionVBox.getChildren());
        onAddQuestionBtnClick();
    }

    @FXML
    private void onAddQuestionBtnClick() {
        questionCount++;

        VBox newQuestionVBox = createNewQuestionVBox();
        VBox grandparentVBox = (VBox) addQuestionVBox.getParent().getParent();

        // get the index of the parent of addQuestionVBox in its grandparent
        int parentIndex = grandparentVBox.getChildren().indexOf(addQuestionVBox.getParent());

        // add the new VBox just before the addQuestionVBox
        grandparentVBox.getChildren().add(parentIndex, newQuestionVBox);
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
        //set maxwidth to MAX_VALUE (1.7976931348623157E308 == MAX_VALUE in SceneBuilder)
        label.setMaxWidth(1.7976931348623157E308);
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
        // populate the MenuButton with category options and set event handlers for selection
        for (Category category : categories) {
            MenuItem menuItem = new MenuItem(category.getName());
            menuItem.setOnAction(e -> {
                String categoryName = category.getName();
                menuButton.setText(categoryName);
            });
            menuButton.getItems().add(menuItem);
        }
        categoriesMenuButtons.add(menuButton);

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
        spinner.setDisable(true);
        pointsSpinners.add(spinner);

        // create a new HBox to contain the Slider and the toggle btn
        HBox innerHBox = new HBox(spinner);
        // set preferred height and width for the VBox
        innerHBox.setPrefHeight(33.0);
        innerHBox.setPrefWidth(1000.0);
        // add custom styling to the VBox
        innerHBox.getStyleClass().add("automatic_create_vbox");
        addToggleBtn(innerHBox, spinner);

        // add the VBox containing the Slider to the parent VBox
        parentVBox.getChildren().add(innerHBox);
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
        slider.setDisable(true);
        difficultySliders.add(slider);

        // create a new HBox to contain the Slider and the toggle btn
        HBox innerHBox = new HBox(slider);
        // set preferred height and width for the VBox
        innerHBox.setPrefHeight(33.0);
        innerHBox.setPrefWidth(1000.0);
        // add custom styling to the VBox
        innerHBox.getStyleClass().add("automatic_create_vbox");
        addToggleBtn(innerHBox, slider);

        // add the VBox containing the Slider to the parent VBox
        parentVBox.getChildren().add(innerHBox);
    }

    private void addToggleBtn(HBox hbox, Object sliderOrSpinner) {
        Button button = new Button();
        button.getStyleClass().add("btn_add_icon");
        ImageView toggleIcon = new ImageView();
        toggleIcon.setImage(new Image(getClass().getResourceAsStream("/com/example/frontend/icons/toggle_off.png")));
        toggleIcon.setFitWidth(34.0);
        toggleIcon.setFitHeight(36.0);
        button.setGraphic(toggleIcon);
        button.setPrefHeight(35.0);
        button.setPrefWidth(34.0);

        button.setOnAction(event -> {
            if (sliderOrSpinner instanceof Slider slider) {
                on_toggle_btn_click(slider, toggleIcon);
            } else if (sliderOrSpinner instanceof CustomDoubleSpinner customDoubleSpinner) {
                on_toggle_btn_click(customDoubleSpinner, toggleIcon);
            }
        });

        hbox.getChildren().add(button);
    }

    // method triggered when the "Create Test" button is clicked
    @FXML
    protected void onCreateAutTestBtnClick(ActionEvent event) {
        for (int i = 0; i < questionCount; i++) {
            // initialize variables to store selected category, difficulty, and points
            String selectedCategory = "";
            double selectedPoints = 0;
            Question queryQuestion = new Question();

            // get the category
            if (!Objects.equals(categoriesMenuButtons.get(i).getText(), "Choose category...")) {
                selectedCategory = categoriesMenuButtons.get(i).getText();
                queryQuestion.setCategory(SQLiteDatabaseConnection.CategoryRepository.get(selectedCategory));
            }
            // get the points
            if (!pointsSpinners.get(i).isDisabled()) {
                selectedPoints = pointsSpinners.get(i).getValue();
                queryQuestion.setPoints((float) selectedPoints);
            }
            // get the difficulty
            if (!difficultySliders.get(i).isDisabled()) {
                queryQuestion.setDifficulty((int) difficultySliders.get(i).getValue());
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
                if (!containsQuestionWithId(newQuestion.getId(), SharedData.getTestQuestions())) {
                    // add the selected question to the test questions list, if not already existing in the testQuestions-array
                    SharedData.getTestQuestions().add(newQuestion);
                }
            }
        }

        // reset the question count to zero
        this.questionCount = 0;
        SharedData.setCurrentScreen(Screen.CreateManual);
        switchScene(createTestManual, true);
    }
}