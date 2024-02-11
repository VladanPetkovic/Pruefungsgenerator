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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
    private CheckBox multipleChoiceCheckBox;

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
        initializeKeywords(this.keywordTextField, SQLiteDatabaseConnection.keywordRepository.getAll());
        initializeCategories(this.categoryTextField, SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getCourse_id()));
        initializeQuestions(this.questionTextField);

        // set up the event handler for the "Apply Filter" button
        applyFilterButton.setOnAction(this::applyFilterButtonClicked);

        // displays the selected course above the filter window
        label_selectedCourse.setText(SharedData.getSelectedCourse().getCourse_name());

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
        boolean multipleChoice = multipleChoiceCheckBox.isSelected();

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
            filterQuestion.setQuestionString(questionText);
        }

        // set points and difficulty if set
        if (SharedData.getFilterQuestion().getPoints() != 0) {
            filterQuestion.setPoints(SharedData.getFilterQuestion().getPoints());
        }
        if (SharedData.getFilterQuestion().getDifficulty() != 0) {
            filterQuestion.setDifficulty(SharedData.getFilterQuestion().getDifficulty());
        }

        // set multiple choice value
        filterQuestion.setMultipleChoice(multipleChoice ? 1 : 0);

        // call Repository to search for questions corresponding to filter values
        ArrayList<Question> result = SQLiteDatabaseConnection.questionRepository.getAll(filterQuestion, SharedData.getSelectedCourse().getCourse_name(), multipleChoice);

        // display filtered questions in filter window
        showFilteredQuestions(result);
    }


    // method to display test questions in preview area
    @FXML
    private void showTestQuestionsInPreview() {
        // clear the existing content
        // vbox_labels.getChildren().clear();

        // spacing between each spacing (serves as an area for the answers)
        double spacing = 100.0;
//        System.out.println("questions: ");
//        System.out.println("questions: "+ SharedData.getTestQuestions().get(0).getQuestionString());

        // counter for question number
        int i = 1;

        // check if there are test questions available
        if (!SharedData.getTestQuestions().isEmpty()) {
            // iterate through each test question
            for (Question question : SharedData.getTestQuestions()) {
//                System.out.println("this is the vbox: "+ vbox_testQuestionsPreview.getChildren());
                // create the VBox and labels for the question
                VBox questionVbox = new VBox();
                Label questionNumberLabel = new Label("Question "+ i +" (Erreichbare Punkte: "+ question.getPoints() + ")");
                Label questionTextLabel = new Label(question.getQuestionString());

//                System.out.println("question String:" + SharedData.getTestQuestions().get(0).getQuestionString());
                // add labels to the VBox
                questionVbox.getChildren().add(questionNumberLabel);
                questionVbox.getChildren().add(questionTextLabel);

                // add the question VBox to the test preview area (VBox)
                vbox_testQuestionsPreview.getChildren().add(questionVbox);

                // set spacing between questions (serves as answer area)
                vbox_testQuestionsPreview.setSpacing(spacing);
//                System.out.println("this is the vbox: "+ vbox_testQuestionsPreview.getChildren());
                i++;
            }
            // after the questions are displayed delete the questions from the sharedData class
            //SharedData.resetQuestions();
        }
    }

    // method to display filtered questions in filter window
    @FXML
    void showFilteredQuestions(ArrayList<Question> questions) {
        // check if the list of questions is empty
        if (questions.isEmpty()) {
            System.out.println("No questions found");
            return;
        }

        // spacing between each question
        double spacing = 10.0;

        // iterate through each filtered question
        for (Question question : questions) {

            // create the VBox and labels for the question
//            System.out.println("this is the vbox: "+ vbox_filteredQuestionsPreview.getChildren());
            VBox questionVbox = new VBox();

            Label questionPointsLabel = new Label("(Erreichbare Punkte: "+ question.getPoints() + ")");
            questionPointsLabel.setTextFill(Color.WHITE);

            Label questionDifficultyLabel = new Label("Difficulty: " + question.getDifficulty());
            questionDifficultyLabel.setTextFill(Color.WHITE);

            Label questionTextLabel = new Label(question.getQuestionString());
            questionTextLabel.setTextFill(Color.WHITE);

            // adds a newline if text is too long
            questionTextLabel.setWrapText(true);

            // add answers label if available
            Label questionAnswersLabel = null;
            if (question.getAnswers() != null) {
                questionAnswersLabel = new Label(question.getAnswers());
                questionAnswersLabel.setTextFill(Color.WHITE);
            }

            // add remarks label if available
            Label questionRemarksLabel = null;
            if (question.getRemarks() != null) {
                questionRemarksLabel = new Label(question.getRemarks());
                questionRemarksLabel.setTextFill(Color.WHITE);
            }

            // add labels to the VBox
            questionVbox.getChildren().add(questionPointsLabel);
            questionVbox.getChildren().add(questionDifficultyLabel);
            questionVbox.getChildren().add(questionTextLabel);
            if (questionAnswersLabel!= null) {
                questionVbox.getChildren().add(questionAnswersLabel);
            }
            if (questionRemarksLabel!= null) {
                questionVbox.getChildren().add(questionRemarksLabel);
            }

            // add the question VBox to the filter window preview area (VBox)
            vbox_filteredQuestionsPreview.getChildren().add(questionVbox);
            vbox_filteredQuestionsPreview.setSpacing(spacing);
        }
    }
}
