package com.example.application.frontend.controller;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.Screen;
import com.example.application.backend.app.SharedData;
import com.example.application.MainApp;
import com.example.application.backend.db.models.Answer;
import com.example.application.backend.db.models.Message;
import com.example.application.backend.db.models.Question;
import com.example.application.frontend.components.EditorScreenController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.application.frontend.controller.SwitchScene.switchScene;

@Component
@Scope("prototype")
public class CreateManual_ScreenController extends ScreenController {
    @FXML
    private VBox vbox_testQuestionsPreview;
    @FXML
    private VBox vbox_filteredQuestionsPreview;
    @FXML
    private HBox answersHBox;
    private final List<EditorScreenController> questionTextController = new ArrayList<>();

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
    private void applyExportButtonClicked(ActionEvent event) throws IOException {
        // check if there are test questions to export
        if (!SharedData.getTestQuestions().isEmpty()) {
            SwitchScene.switchScene(SwitchScene.PDF_PREVIEW);
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
        int questionCounter = 1;

        // check if there are test questions available
        if (!SharedData.getTestQuestions().isEmpty()) {
            // iterate through each test question
            for (Question question : SharedData.getTestQuestions()) {
                // create the VBox and labels for the question
                VBox questionVbox = new VBox();
                String previewLabel = MessageFormat.format(MainApp.resourceBundle.getString("question_number_label"), questionCounter, question.getPoints());
                Label questionNumberLabel = new Label(previewLabel);
                questionNumberLabel.getStyleClass().add("manualCreate_Question_Label");


                VBox questionHtmlEditor = null;
                try {
                    questionHtmlEditor = createHtmlEditor(question, questionCounter - 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //create buttons
                Button removeButton = getRemoveButton(questionVbox, question);
                Button upButton = getUpButton(questionCounter - 1);
                Button downButton = getDownButton(questionCounter - 1);
                Button editButton = getEditButton(questionCounter - 1);

                //create hbox for questionlabel and remove button
                HBox newQuestionHbox = new HBox();
                //newQuestionHbox.setSpacing(10);
                HBox.setHgrow(questionNumberLabel, Priority.ALWAYS);
                questionNumberLabel.setMaxWidth(Double.MAX_VALUE);

                newQuestionHbox.getChildren().addAll(questionNumberLabel, upButton, downButton, editButton, removeButton);
                newQuestionHbox.setAlignment(Pos.CENTER_RIGHT);

                // add labels to the VBox
                questionVbox.getChildren().addAll(newQuestionHbox, questionHtmlEditor);

                //for multiple choice questions add the answers
                if ("MULTIPLE_CHOICE".equalsIgnoreCase(question.getType())) {
                    VBox answersVBox = createAnswersVBox(question);
                    questionVbox.getChildren().add(answersVBox);
                }

                // add the question VBox to the test preview area (VBox)
                vbox_testQuestionsPreview.getChildren().add(questionVbox);

                // set spacing between questions (serves as answer area)
                vbox_testQuestionsPreview.setSpacing(spacing);
                questionCounter++;
            }
        }
    }


    /**
     * method to display filtered questions in filter window
     *
     * @param questions The ObservableList of questions to show in the preview window.
     */
    @FXML
    private void showFilteredQuestions(ObservableList<Question> questions) {
        double spacing = 20.0;

        if (questions.isEmpty()) {
            this.vbox_filteredQuestionsPreview.getChildren().clear();
            return;
        }

        this.vbox_filteredQuestionsPreview.getChildren().clear();

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


    //when question in filteredArea is clicked
    @FXML
    private void displayClickedQuestion(VBox questionVbox, Question question) {
        questionVbox.setOnMouseClicked(event -> {
            double spacing = 100;

            // remove the question from the filterBox
            SharedData.getFilteredQuestions().remove(question);

            int numberOfQuestion = this.vbox_testQuestionsPreview.getChildren().size();
            // Create a VBox to hold the question details.
            VBox newQuestionVbox = new VBox();
            String previewLabel = MessageFormat.format(MainApp.resourceBundle.getString("question_number_label"), numberOfQuestion + 1, question.getPoints());
            Label questionNumberLabel = new Label(previewLabel);
            questionNumberLabel.getStyleClass().add("manualCreate_Question_Label");


            VBox questionHtmlEditor = null;
            try {
                questionHtmlEditor = createHtmlEditor(question, numberOfQuestion);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //create the buttons
            Button upButton = getUpButton(numberOfQuestion);
            Button downButton = getDownButton(numberOfQuestion);
            Button removeButton = getRemoveButton(questionVbox, question);
            Button editButton = getEditButton(numberOfQuestion);

            //create showAnswers button if multiple-choice
            Logger.log(this.getClass().getName(), "QuestionType: " + question.getType(), LogLevel.DEBUG);
            // Create an editable TextArea to show answers

            //create HBox that contains the label and buttons
            HBox newQuestionHbox = new HBox();
            //newQuestionHbox.setSpacing(10);
            HBox.setHgrow(questionNumberLabel, Priority.ALWAYS);
            questionNumberLabel.setMaxWidth(Double.MAX_VALUE);


            newQuestionHbox.getChildren().addAll(questionNumberLabel, upButton, downButton, editButton, removeButton);
            newQuestionHbox.setAlignment(Pos.CENTER_RIGHT);

            // add labels, buttons and textarea to the questionVBox
            newQuestionVbox.getChildren().addAll(newQuestionHbox, questionHtmlEditor);
            //for multiple choice questions add the answers
            if ("MULTIPLE_CHOICE".equalsIgnoreCase(question.getType())) {
                VBox answersVBox = createAnswersVBox(question);
                newQuestionVbox.getChildren().add(answersVBox);
            }

            if (!containsQuestionWithId(question.getId(), SharedData.getTestQuestions())) {
                // add this question to the vbox_testQuestionsPreview, if not added
                this.vbox_testQuestionsPreview.getChildren().add(newQuestionVbox);
                this.vbox_testQuestionsPreview.setSpacing(spacing);
                SharedData.getTestQuestions().add(question);
            }
        });
    }

    private VBox createHtmlEditor(Question question, int numberOfQuestion) throws IOException {
        FXMLLoader loader = FXMLDependencyInjection.getLoader("components/editor.fxml", MainApp.resourceBundle);
        VBox previewQuestionVbox = loader.load();

        // get the controller for the loaded component
        EditorScreenController controller = loader.getController();
        questionTextController.add(controller);

        controller.setNumberOfQuestion(numberOfQuestion);
        controller.setParentVbox(vbox_testQuestionsPreview);
        // set question-text
        controller.editor.setHtmlText(question.getQuestion());

        return previewQuestionVbox;
    }

    private Button getRemoveButton(VBox questionVbox, Question question) {
        Button removeButton = new Button();
        removeButton.getStyleClass().add("remove-button");
        //set image Icon
        ImageView imageView = new ImageView();
        imageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/com/example/application/icons/remove.png")));
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        removeButton.setGraphic(imageView);


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

    Button getUpButton(int index) {
        Button upButton = new Button();
        //set icon
        ImageView imageView = new ImageView();
        imageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/com/example/application/icons/upButton4.png")));
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        upButton.setGraphic(imageView);

        upButton.getStyleClass().add("position-button");

        upButton.setOnAction(event -> {
            //if a question before exists switch this question with question before
            if (index > 0) {
                Question questionBefore = SharedData.getTestQuestions().get(index - 1);
                Question questiontmp = SharedData.getTestQuestions().get(index);
                SharedData.getTestQuestions().set(index - 1, questiontmp);
                SharedData.getTestQuestions().set(index, questionBefore);
            }
            //clear the test preview area
            this.vbox_testQuestionsPreview.getChildren().clear();
            //reload the preview and filtered questions area
            showTestQuestionsInPreview();
            showFilteredQuestions(SharedData.getFilteredQuestions());
        });
        return upButton;
    }

    Button getDownButton(int index) {
        Button downButton = new Button();
        //set icon
        ImageView imageView = new ImageView();
        imageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/com/example/application/icons/downButton4.png")));
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        downButton.setGraphic(imageView);

        downButton.getStyleClass().add("position-button");

        downButton.setOnAction(event -> {
            //if a question after exists switch this question with question after
            if (index + 1 < this.vbox_testQuestionsPreview.getChildren().size()) {
                Question questionAfter = SharedData.getTestQuestions().get(index + 1);
                Question questiontmp = SharedData.getTestQuestions().get(index);
                SharedData.getTestQuestions().set(index + 1, questiontmp);
                SharedData.getTestQuestions().set(index, questionAfter);
            }
            //clear the test preview area
            this.vbox_testQuestionsPreview.getChildren().clear();
            //reload the preview and filtered questions area
            showTestQuestionsInPreview();
            showFilteredQuestions(SharedData.getFilteredQuestions());
        });
        return downButton;
    }

    Button getEditButton(int index) {
        Button editButton = new Button();
        editButton.getStyleClass().add("edit-button");
        //set image Icon
        ImageView imageView = new ImageView();
        imageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/com/example/application/icons/edit.png")));
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        editButton.setGraphic(imageView);
        editButton.setOnAction(event -> {
            if (index >= 0) {
                try {
                    SharedData.setQuestionToEdit(SharedData.getTestQuestions().get(index));
                    SharedData.setCurrentScreen(Screen.QUESTION_EDIT);
                    switchScene(SwitchScene.EDIT_QUESTION);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return editButton;
    }

    private VBox createAnswersVBox(Question question) {
        VBox answersVBox = new VBox();

        Label answersLabel = new Label(MainApp.resourceBundle.getString("answers_preview"));
        answersLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: white");
        answersVBox.getChildren().add(answersLabel);

        //create textArea for each answer
        for (Answer answer : question.getAnswers()) {

            TextArea answerTextArea = new TextArea(answer.getAnswer());
            answerTextArea.setWrapText(true); // continue on next line if text extends width

            //update answers on user input
            answerTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
                answer.setAnswer(newValue.trim());
            });

            //create Hbox that stores answer textArea and remove button
            HBox answerBox = new HBox();
            answerBox.setMaxHeight(50);
            Button removeButton = new Button("X");
            removeButton.setOnAction(event -> {
                question.getAnswers().remove(answer);
                answersVBox.getChildren().remove(answerBox);
            });
            answerBox.getChildren().addAll(answerTextArea, removeButton);
            answersVBox.getChildren().add(answerBox);
        }

        return answersVBox;
    }
}