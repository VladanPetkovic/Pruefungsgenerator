package com.example.frontend.controller;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Objects;

public class CreateManual_ScreenController extends ScreenController {
    @FXML
    private Label label_selectedCourse;
    @FXML
    private TextField categoryTextField;
    @FXML
    private TextField questionTextField;
    @FXML
    private TextField keywordTextField;
    @FXML
    private Slider difficultySlider;
    @FXML
    private Slider pointsSlider;
    @FXML
    public MenuButton questionTypeMenuButton;
    @FXML
    private Button applyFilterButton;
    @FXML
    private VBox vbox_testQuestionsPreview;
    @FXML
    private VBox vbox_filteredQuestionsPreview;

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

        // set up the event handler for the "Apply Filter" button
        // applyFilterButton.setOnAction(this::applyFilterButtonClicked);

        // displays the selected course above the filter window
        label_selectedCourse.setText(SharedData.getSelectedCourse().getName());

        // check whether questions from automaticTestCreate are available for showing in testPreview
        if (!SharedData.getTestQuestions().isEmpty()) {
            // display the questions from automaticTestCreate in the testPreviewArea
            showTestQuestionsInPreview();
        }
    }

    // event handler for the "Apply Filter" button click
    @FXML
    private void applyFilterButtonClicked(ActionEvent event) {
        // call the searchQuestions method to apply the filter
        searchQuestions();
    }

    // event handler for the "Apply Export" button click
    @FXML
    private void applyExportButtonClicked(ActionEvent event) {
        // check if there are test questions to export
        if (!SharedData.getTestQuestions().isEmpty()) {
            // switch the scene to the pdf-preview screen
            switchScene(pdf_preview, true);
        }
    }

    // event handler to search questions based on filters
    @FXML
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


    // method to display test questions in preview area
    @FXML
    private void showTestQuestionsInPreview() {
        // spacing between each spacing (serves as an area for the answers)
        double spacing = 100.0;

        // counter for question number
        int i = 1;

        // check if there are test questions available
        if (!SharedData.getTestQuestions().isEmpty()) {
            // iterate through each test question
            for (Question question : SharedData.getTestQuestions()) {
                // create the VBox and labels for the question
                VBox questionVbox = new VBox();
                Label questionNumberLabel = new Label("Question "+ i +" (Erreichbare Punkte: "+ question.getPoints() + ")");
                Label questionTextLabel = new Label(question.getQuestion());

                // add labels to the VBox
                questionVbox.getChildren().add(questionNumberLabel);
                questionVbox.getChildren().add(questionTextLabel);

                // add the question VBox to the test preview area (VBox)
                vbox_testQuestionsPreview.getChildren().add(questionVbox);

                // set spacing between questions (serves as answer area)
                vbox_testQuestionsPreview.setSpacing(spacing);
                i++;
            }
        }
    }

    // method to display filtered questions in filter window
    @FXML
    void showFilteredQuestions(ArrayList<Question> questions) {
        // Check if the list of questions is empty.
        if (questions.isEmpty()) {
            // If the list is empty, print a message indicating no questions found.
            Logger.log(getClass().getName(), "No questions found", LogLevel.INFO);
            this.vbox_filteredQuestionsPreview.getChildren().clear();
            return;
        }

        // Define the spacing between question boxes.
        double spacing = 20.0;

        // Clear the existing content in the preview VBox.
        this.vbox_filteredQuestionsPreview.getChildren().clear();

        // Iterate through each question in the list.
        for (Question question : questions) {
            // Create a VBox to hold the question details.
            VBox questionVbox = createQuestionVBox(question);
            questionVbox.getStyleClass().add("filter_question_preview_vbox");

            if (!containsQuestionWithId(question.getId())) {
                // Add the question VBox to the preview VBox, if the question is not already in preview.
                this.vbox_filteredQuestionsPreview.getChildren().add(questionVbox);
                // display the clicked question in the test_preview_pane
                displayClickedQuestion(questionVbox, question);
                // Set the spacing between question boxes.
                this.vbox_filteredQuestionsPreview.setSpacing(spacing);
            }
        }
    }

    @FXML
    private void displayClickedQuestion(VBox questionVbox, Question question) {
        questionVbox.setOnMouseClicked(event -> {
            double spacing = 100;
            int numberOfQuestions = this.vbox_testQuestionsPreview.getChildren().size();
            // Create a VBox to hold the question details.
            VBox newQuestionVbox = new VBox();
            Label questionNumberLabel = new Label("Question "+ (numberOfQuestions + 1) +" (Erreichbare Punkte: "+ question.getPoints() + ")");
            Label questionTextLabel = new Label(question.getQuestion());
            // add labels to the VBox
            newQuestionVbox.getChildren().add(questionNumberLabel);
            newQuestionVbox.getChildren().add(questionTextLabel);

            if (!containsQuestionWithId(question.getId())) {
                // add this question to the vbox_testQuestionsPreview, if not added
                this.vbox_testQuestionsPreview.getChildren().add(newQuestionVbox);
                this.vbox_testQuestionsPreview.setSpacing(spacing);
                SharedData.getTestQuestions().add(question);
            }
        });
    }
}
