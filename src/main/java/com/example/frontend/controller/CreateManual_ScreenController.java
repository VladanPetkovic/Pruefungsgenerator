package com.example.frontend.controller;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.models.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.example.backend.app.Screen;

public class CreateManual_ScreenController extends ScreenController {
    @FXML
    private VBox vbox_testQuestionsPreview;
    @FXML
    private VBox vbox_filteredQuestionsPreview;

    @FXML
    private void initialize() {
        // check whether questions from automaticTestCreate are available for showing in testPreview
        if (!SharedData.getTestQuestions().isEmpty()) {
            // display the questions from automaticTestCreate in the testPreviewArea
            showTestQuestionsInPreview();
        }

        // display filtered questions
        SharedData.getFilteredQuestions().addListener((ListChangeListener<Question>) change -> {
            showFilteredQuestions(SharedData.getFilteredQuestions());
        });
    }

    // event handler for the "Apply Export" button click
    @FXML
    private void applyExportButtonClicked(ActionEvent event) {
        // check if there are test questions to export
        if (!SharedData.getTestQuestions().isEmpty()) {
            switchScene(pdf_preview, true);
        } else {
            // display error-message
            SharedData.setOperation(Message.NO_QUESTIONS_PROVIDED_ERROR_MESSAGE);
        }
    }


    // method to display test questions in preview area
    @FXML
    private void showTestQuestionsInPreview() {
        //spacing between each spacing (serves as an area for the answers)
        double spacing = 100.0;

        //counter for question number
        int i = 1;

        // check if there are test questions available
        if (!SharedData.getTestQuestions().isEmpty()) {
            // iterate through each test question
            for (Question question : SharedData.getTestQuestions()) {
                // create the VBox and labels for the question
                VBox questionVbox = new VBox();
                Label questionNumberLabel = new Label("Question "+ i +" (Erreichbare Punkte: "+ question.getPoints() + ")");
                Label questionTextLabel = new Label(question.getQuestion());

                // create remove button
                Button removeButton = getRemoveButton(questionVbox, question);

                // add labels to the VBox
                questionVbox.getChildren().addAll(questionNumberLabel,questionTextLabel,removeButton);

                // add the question VBox to the test preview area (VBox)
                vbox_testQuestionsPreview.getChildren().add(questionVbox);

                // set spacing between questions (serves as answer area)
                vbox_testQuestionsPreview.setSpacing(spacing);
                i++;
            }
        }
    }


    /**
     * method to display filtered questions in filter window
     * @param questions The ObservableList of questions to show in the preview window.
     */
    @FXML
    private void showFilteredQuestions(ObservableList<Question> questions) {
        double spacing = 20.0;

        // Check if the list of questions is empty.
        if (questions.isEmpty()) {
            this.vbox_filteredQuestionsPreview.getChildren().clear();
            return;
        }

        this.vbox_filteredQuestionsPreview.getChildren().clear();

        // Iterate through each question in the list.
        for (Question question : questions) {
            VBox questionVbox = createQuestionVBox(question);
            questionVbox.getStyleClass().add("filter_question_preview_vbox");

            if (!containsQuestionWithId(question.getId(), SharedData.getTestQuestions())) {
                // add question to preview-box, if the question is not already in preview.
                this.vbox_filteredQuestionsPreview.getChildren().add(questionVbox);
                // display the clicked question in the test_preview_pane
                displayClickedQuestion(questionVbox, question);
                // set spacing
                this.vbox_filteredQuestionsPreview.setSpacing(spacing);
            }
        }
    }

    @FXML
    private void displayClickedQuestion(VBox questionVbox, Question question) {
        questionVbox.setOnMouseClicked(event -> {
            double spacing = 100;

            // remove the question from the filterBox
            SharedData.getFilteredQuestions().remove(question);

            int numberOfQuestions = this.vbox_testQuestionsPreview.getChildren().size();
            // Create a VBox to hold the question details.
            VBox newQuestionVbox = new VBox();
            Label questionNumberLabel = new Label("Question "+ (numberOfQuestions + 1) +" (Erreichbare Punkte: "+ question.getPoints() + ")");
            Label questionTextLabel = new Label(question.getQuestion());

            //create remove button
            Button removeButton = getRemoveButton(questionVbox, question);

            // add labels to the VBox
            newQuestionVbox.getChildren().addAll(questionNumberLabel,questionTextLabel,removeButton);

            if (!containsQuestionWithId(question.getId(), SharedData.getTestQuestions())) {
                // add this question to the vbox_testQuestionsPreview, if not added
                this.vbox_testQuestionsPreview.getChildren().add(newQuestionVbox);
                this.vbox_testQuestionsPreview.setSpacing(spacing);
                SharedData.getTestQuestions().add(question);
            }
        });
    }

    private Button getRemoveButton(VBox questionVbox, Question question) {
        Button removeButton = new Button("X");
        removeButton.setOnAction(eventRemove -> {
            // remove the question from the vbox_testQuestionsPreview
            vbox_testQuestionsPreview.getChildren().remove(questionVbox);
            // add it back to the filtered questions --> don't add duplicates
            if (!containsQuestionWithId(question.getId(), SharedData.getFilteredQuestions())) {
                SharedData.getFilteredQuestions().add(question);
            }

            //remove the question from the selected testquestions
            SharedData.getTestQuestions().remove(question);
            //clear the test preview area
            this.vbox_testQuestionsPreview.getChildren().clear();
            //reload the preview and filtered questions area
            showTestQuestionsInPreview();
            showFilteredQuestions(SharedData.getFilteredQuestions());
        });
        return removeButton;
    }
}
