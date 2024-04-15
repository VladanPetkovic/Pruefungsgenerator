package com.example.frontend.controller;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.QuestionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
    private void initialize() {
        // initialize points and difficulty from the slider by a listener
        // --> only when the value changes, the value is updated
        getPointsFromSlider(this.pointsSlider);
        getDifficultyFromSlider(this.difficultySlider);

        // init auto-completion
        initializeKeywords(this.keywordTextField, SQLiteDatabaseConnection.keywordRepository.getAllOneCourse(SharedData.getSelectedCourse().getId()));
        initializeCategories(this.categoryTextField, SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getId()));
        initializeQuestions(this.questionTextField);
        initializeMenuButton(this.questionTypeMenuButton, true);

        // displays the selected course above the filter window
        label_selectedCourse.setText(SharedData.getSelectedCourse().getName());
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
        if (SharedData.getFilterQuestion().getPoints() != 0) {
            filterQuestion.setPoints(SharedData.getFilterQuestion().getPoints());
        }
        if (SharedData.getFilterQuestion().getDifficulty() != 0) {
            filterQuestion.setDifficulty(SharedData.getFilterQuestion().getDifficulty());
        }

        // set questionType value
        if (QuestionType.checkExistingType(questionTypeString)) {
            QuestionType filterQuestionType = new QuestionType(questionTypeString);
            filterQuestion.setType(filterQuestionType);
        }

        // call Repository to search for questions corresponding to filter values
        ArrayList<Question> result = SQLiteDatabaseConnection.questionRepository.getAll(filterQuestion, SharedData.getSelectedCourse().getName());

        // display filtered questions in filter window
        showFilteredQuestions(result);
    }

    /**
     * method to display filtered questions in filter window
     * @param questions The ArrayList of questions to show in the preview window.
     */
    @FXML
    void showFilteredQuestions(ArrayList<Question> questions) {
        // TODO: change this to save the questions in an ObserverableList in SharedData
        // Check if the list of questions is empty.
//        if (questions.isEmpty()) {
//            // If the list is empty, print a message indicating no questions found.
//            Logger.log(getClass().getName(), "No questions found", LogLevel.INFO);
//            this.vbox_filteredQuestionsPreview.getChildren().clear();
//            return;
//        }
//
//        // Define the spacing between question boxes.
//        double spacing = 20.0;
//
//        // Clear the existing content in the preview VBox.
//        this.vbox_filteredQuestionsPreview.getChildren().clear();
//
//        // Iterate through each question in the list.
//        for (Question question : questions) {
//            // Create a VBox to hold the question details.
//            VBox questionVbox = createQuestionVBox(question);
//            questionVbox.getStyleClass().add("filter_question_preview_vbox");
//
//            if (!containsQuestionWithId(question.getId())) {
//                // Add the question VBox to the preview VBox, if the question is not already in preview.
//                this.vbox_filteredQuestionsPreview.getChildren().add(questionVbox);
//                // display the clicked question in the test_preview_pane
//                displayClickedQuestion(questionVbox, question);
//                // Set the spacing between question boxes.
//                this.vbox_filteredQuestionsPreview.setSpacing(spacing);
//            }
//        }
    }
}
