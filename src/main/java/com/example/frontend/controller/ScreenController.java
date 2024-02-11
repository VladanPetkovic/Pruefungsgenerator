package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import com.example.frontend.MainApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.ArrayList;

/**
 * the base class for all screen controllers
 * provides functionality to switch between screens and handle common UI events
 */
public abstract class ScreenController {

    // define and initialize screens for different functionalities
    public static Screen<CreateAutomatic_ScreenController> createTestAutomatic = new Screen<>("sites/create_automatic.fxml");
    public static Screen<CreateManual_ScreenController> createTestManual = new Screen<>("sites/create_manual.fxml");
    public static Screen<QuestionCreate_ScreenController> questionUpload = new Screen<>("sites/question_create.fxml");
    public static Screen<QuestionEdit_ScreenController> questionEdit = new Screen<>("sites/question_edit.fxml");
    public static Screen<QuestionEdit_ScreenController> home = new Screen<>("sites/home.fxml");


    /**
     * switches to the specified screen and optionally refreshes its components
     * @param screen the screen to switch to
     * @param refresh indicates whether to refresh the screen components
     */
    public static void switchScene(Screen screen, boolean refresh){
        // reload components, if refresh is true
        if(refresh){
            screen.loadComponents();
        }

        // resetting filterquestion
        SharedData.setFilterQuestion(new Question());
        // set the scene and display it
        MainApp.stage.setScene(screen.scene);
        MainApp.stage.show();
    }

    /**
     * handles click event for navigating to the create automatic test screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onCreateAutTestNavBtnClick(MouseEvent event) throws IOException {
        switchScene(createTestAutomatic,true);
    }

    /**
     * handles click event for navigating to the create manual test screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onCreateManTestNavBtnClick(MouseEvent event) throws IOException {
        switchScene(createTestManual,true);
    }

    /**
     * handles click event for navigating to the question upload screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onUploadQuestionNavBtnClick(MouseEvent event) throws IOException
    {
        switchScene(questionUpload,true);
    }

    /**
     * handles click event for navigating to the question edit screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onEditQuestionNavBtnClick(MouseEvent event) throws IOException
    {
        switchScene(questionEdit,true);
    }

    /**
     * handles click event for navigating to the home screen and resetting shared data
     */
    public void onFHTWLogoClick() {
        // navigate to the home screen and reset shared data
        switchScene(home,true);
        SharedData.resetAll();
    }

    /**
     * Method to update points value when the slider value changes.
     * The value of the slider is saved in SharedData filterQuestion.
     * Use this function in the init()-function of your screen - otherwise it won't listen to the user input.
     * @param pointsSlider
     */
    protected void getPointsFromSlider(Slider pointsSlider) {
        // add a listener to the value property of the points slider
        pointsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                // retrieve the new points value from the slider
                int points = (int) pointsSlider.getValue();
                // update the points value in the filter question object stored in SharedData
                SharedData.getFilterQuestion().setPoints(points);
            }
        });
    }

    /**
     * Method to update difficulty value when the slider value changes.
     * The value of the slider is saved in SharedData filterQuestion.
     * Use this function in the init()-function of your screen - otherwise it won't listen to the user input.
     * @param difficultySlider
     */
    protected void getDifficultyFromSlider(Slider difficultySlider) {
        // add a listener to the value property of the difficulty slider
        difficultySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                // retrieve the new difficulty value from the slider
                int difficulty = (int) difficultySlider.getValue();
                // update the difficulty value in the filter question object stored in SharedData
                SharedData.getFilterQuestion().setDifficulty(difficulty);
            }
        });
    }

    /**
     * Initializes the auto-completion of the keywords in the search-area of edit-question
     */
    protected void initializeKeywords(TextField keywordTextField, ArrayList<Keyword> keywords) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Keyword k : keywords) {
            items.add(k.getKeyword_text());
        }
        TextFields.bindAutoCompletion(keywordTextField, items);
    }

    /**
     * Initializes the auto-completion of the categories in the search-area of edit-question.
     */
    protected void initializeCategories(TextField categoryTextField, ArrayList<Category> categories) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Category c : categories) {
            items.add(c.getCategory());
        }
        TextFields.bindAutoCompletion(categoryTextField, items);
    }

    /**
     * Initializes the auto-completion of the questions in the search-area of edit-question.
     */
    protected void initializeQuestions(TextField questionTextField) {
        ObservableList<String> items = FXCollections.observableArrayList();
        String course_name = SharedData.getSelectedCourse().getCourse_name();
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll(new Question(), course_name, false);
        for (Question q : questions) {
            items.add(q.getQuestionString());
        }
        TextFields.bindAutoCompletion(questionTextField, items);
    }
}
