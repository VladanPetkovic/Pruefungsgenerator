package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Slider;

import java.util.ArrayList;

public class CreateManual_ScreenController extends ScreenController {

    @FXML
    private TextField categoryTextField;

    @FXML
    private TextField keywordTextField;

    @FXML
    private Slider difficultySlider;

    @FXML
    private Slider pointsSlider;

    @FXML
    private CheckBox multipleChoiceCheckBox;

    @FXML
    private Button applyFilterButton;

    @FXML
    private void initialize() {
        // Set up the event handler for the "Apply Filter" button
        applyFilterButton.setOnAction(this::applyFilterButtonClicked);
    }

    @FXML
    private void applyFilterButtonClicked(ActionEvent event) {
        searchQuestions();
    }

    @FXML
    private void searchQuestions() {
        // Get filter values
        String categoryName = categoryTextField.getText().trim();
        Category category = SQLiteDatabaseConnection.CategoryRepository.get(categoryName);
        String keywordText = keywordTextField.getText().trim();

        int difficulty = (int) difficultySlider.getValue();
        float points = (float) pointsSlider.getValue();
        boolean multipleChoice = multipleChoiceCheckBox.isSelected();

        // Create a Question object with filter values
        Question filterQuestion = new Question();
        filterQuestion.setCategory(category);

        // Handle multiple keywords as ArrayList<Keyword>
        ArrayList<Keyword> keywordsList = new ArrayList<>();

        String[] keywordsArray = keywordText.split(","); // Split by commas
        if (keywordsArray.length == 1) {
            // If there is only one keyword without commas
            Keyword keywordObj = SQLiteDatabaseConnection.keywordRepository.get(keywordText.trim());
            if (keywordObj != null) {
                keywordsList.add(keywordObj);
            }
        } else {
            // If there are multiple keywords
            for (String keyword : keywordsArray) {
                Keyword keywordObj = SQLiteDatabaseConnection.keywordRepository.get(keyword.trim());
                if (keywordObj != null) {
                    keywordsList.add(keywordObj);
                }
            }
        }

        filterQuestion.setKeywords(keywordsList);

        filterQuestion.setDifficulty(difficulty);
        filterQuestion.setPoints(points);
        filterQuestion.setQuestionString("");
        filterQuestion.setMultipleChoice(multipleChoice);
        filterQuestion.setLanguage("");
        filterQuestion.setRemarks("");
        filterQuestion.setAnswers("");

        // Call Repository to search for questions
        ArrayList<Question> result;
        if (multipleChoice) {
            result = SQLiteDatabaseConnection.questionRepository.getAll(filterQuestion, SharedData.getSelectedCourse().getCourse_name(), true);
        } else {
            result = SQLiteDatabaseConnection.questionRepository.getAll(filterQuestion, SharedData.getSelectedCourse().getCourse_name(), false);
        }

        // Display result in the console
        for (Question question : result) {
            System.out.println("Found Question: " + question);
        }
    }
}
