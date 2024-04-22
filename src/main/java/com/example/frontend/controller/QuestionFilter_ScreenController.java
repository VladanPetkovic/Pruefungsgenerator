package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionFilter_ScreenController extends ScreenController {
    @FXML
    public MenuButton questionTypeMenuButton;
    @FXML
    public Slider pointsSlider;
    @FXML
    public Slider difficultySlider;
    @FXML
    public TextField keywordTextField;
    @FXML
    public TextField questionTextField;
    @FXML
    public TextField categoryTextField;
    @FXML
    public Label label_selectedCourse;
    @FXML
    public Button add_category_btn;
    @FXML
    public ImageView difficulty_toggle_image_view;
    @FXML
    public ImageView points_toggle_image_view;

    @FXML
    private void initialize() {
        // init auto-completion
        initializeKeywords(this.keywordTextField, SQLiteDatabaseConnection.keywordRepository.getAllOneCourse(SharedData.getSelectedCourse().getId()));
        initializeCategories(this.categoryTextField, SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getId()), add_category_btn);
        initializeQuestions(this.questionTextField);
        initializeMenuButton(this.questionTypeMenuButton, true);

        // displays the selected course above the filter window
        label_selectedCourse.setText(SharedData.getSelectedCourse().getName());
    }

    public void on_add_category_btn_click(ActionEvent actionEvent) {
        if (Category.checkNewCategory(categoryTextField.getText()) == null) {
            SharedData.setOperation(Message.CREATE_CATEGORY_SUCCESS_MESSAGE);
            Category newCategory = Category.createNewCategoryInDatabase(categoryTextField.getText(), SharedData.getSelectedCourse());

            // add category to categories-autoCompletion
            SharedData.getSuggestedCategories().add(newCategory.getName());

            add_category_btn.setDisable(true);
        }
    }

    public void on_toggle_difficulty_btn_click(ActionEvent actionEvent) {
        on_toggle_btn_click(difficultySlider, difficulty_toggle_image_view);
    }

    public void on_toggle_points_btn_click(ActionEvent actionEvent) {
        on_toggle_btn_click(pointsSlider, points_toggle_image_view);
    }

    public void applyFilterButtonClicked(ActionEvent actionEvent) {
        searchQuestions();
    }

    private void searchQuestions() {
        // create a new Question object to hold filter values
        Question filterQuestion = new Question();

        // get filter values from text fields and checkboxes
        String categoryName = categoryTextField.getText().trim();
        String keywordText = keywordTextField.getText().trim();
        String questionText = questionTextField.getText();
        String questionTypeString = questionTypeMenuButton.getText();

        // set category value if provided
        if (!categoryName.isEmpty()) {
            Category category = SQLiteDatabaseConnection.CategoryRepository.get(categoryName);
            if (category != null) {
                filterQuestion.setCategory(category);
            }
        }

        // set keyword value if provided
        // handle multiple keywords as ArrayList<Keyword>
        ArrayList<Keyword> keywordsList = new ArrayList<>();
        if (!keywordText.isEmpty()) {
            // split by commas or spaces
            String[] keywordsArray = keywordText.split("[,\\s]+");
            for (String keyword : keywordsArray) {
                Keyword keywordObj = SQLiteDatabaseConnection.keywordRepository.get(keyword.trim());
                if (keywordObj != null) {
                    keywordsList.add(keywordObj);
                }
            }
        }
        if (!keywordsList.isEmpty()) {
            filterQuestion.setKeywords(keywordsList);
        }

        // set the question text
        if (!(Objects.equals(questionText, "") || questionText == null)) {
            filterQuestion.setQuestion(questionText);
        }

        // set points and difficulty if set
        if (!pointsSlider.isDisabled()) {
            filterQuestion.setPoints((float) pointsSlider.getValue());
        }
        if (!difficultySlider.isDisabled()) {
            filterQuestion.setDifficulty((int) difficultySlider.getValue());
        }

        // set questionType value
        if (QuestionType.checkExistingType(questionTypeString)) {
            QuestionType filterQuestionType = new QuestionType(questionTypeString);
            filterQuestion.setType(filterQuestionType);
        }

        // call Repository to search for questions corresponding to filter values
        ArrayList<Question> result = SQLiteDatabaseConnection.questionRepository.getAll(filterQuestion, SharedData.getSelectedCourse().getName());

        // save to our SharedData
        SharedData.getFilteredQuestions().clear();
        for (Question question : result) {
            SharedData.getFilteredQuestions().add(question);
        }
    }
}
