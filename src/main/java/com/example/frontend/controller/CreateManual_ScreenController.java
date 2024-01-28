package com.example.frontend.controller;

import com.example.backend.app.Export;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import com.example.frontend.MainApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;

public class CreateManual_ScreenController extends ScreenController {

    @FXML
    private Label label_selectedCourse;

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
    private VBox vbox_testQuestionsPreview;

    @FXML
    private VBox vbox_filteredQuestionsPreview;

    @FXML
    private void initialize() {
        // init points and difficulty from the slider by a listener
        // --> only when the value changes, the value is updated
        getPointsFromSlider();
        getDifficultyFromSlider();
        // Set up the event handler for the "Apply Filter" button
        applyFilterButton.setOnAction(this::applyFilterButtonClicked);

        //displays the selected course above the filter window
        label_selectedCourse.setText(SharedData.getSelectedCourse().getCourse_name());
        // check whether questions from automaticTestCreate are available for showing in testPreview
        if (!SharedData.getTestQuestions().isEmpty()) {
            //display the questions from automaticTestCreate in the testPreviewArea
            showQuestionsInPreview();
        }
    }

    private void getPointsFromSlider() {
        pointsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                int points = (int) pointsSlider.getValue();
                SharedData.getFilterQuestion().setPoints(points);
            }
        });
    }

    private void getDifficultyFromSlider() {
        difficultySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                int difficulty = (int) difficultySlider.getValue();
                SharedData.getFilterQuestion().setDifficulty(difficulty);
            }
        });
    }

    @FXML
    private void applyFilterButtonClicked(ActionEvent event) {
        searchQuestions();
    }

    @FXML
    private void applyExportButtonClicked(ActionEvent event) {
        if (!SharedData.getTestQuestions().isEmpty()) {
            Export export = new Export();
            export.exportToPdf(SharedData.getTestQuestions(), "Test: " + SharedData.getSelectedCourse().getCourse_name());
            SharedData.resetQuestions();
            switchScene(createTestAutomatic, true);
        }
    }

    @FXML
    private void searchQuestions() {
        // Create a Question object with filter values
        Question filterQuestion = new Question();

        // Get filter values
        String categoryName = categoryTextField.getText().trim();
        String keywordText = keywordTextField.getText().trim();
        boolean multipleChoice = multipleChoiceCheckBox.isSelected();

        if(!categoryName.isEmpty()) {
            Category category = SQLiteDatabaseConnection.CategoryRepository.get(categoryName);
            if(category != null) {
                filterQuestion.setCategory(category);
            }
        }

        // Handle multiple keywords as ArrayList<Keyword>
        ArrayList<Keyword> keywordsList = new ArrayList<>();

        if (!keywordText.isEmpty()) {
            // Split by commas or spaces
            String[] keywordsArray = keywordText.split("[,\\s]+");

            if (keywordsArray.length == 1) {
                // If there is only one keyword without commas or spaces
                Keyword keywordObj = SQLiteDatabaseConnection.keywordRepository.get(keywordsArray[0].trim());
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
        }

        if(!keywordsList.isEmpty()) {
            filterQuestion.setKeywords(keywordsList);
        }

        // setting points and difficulty, if it was set
        if(SharedData.getFilterQuestion().getPoints() != 0) {
            filterQuestion.setPoints(SharedData.getFilterQuestion().getPoints());
        }
        if(SharedData.getFilterQuestion().getDifficulty() != 0) {
            filterQuestion.setDifficulty(SharedData.getFilterQuestion().getDifficulty());
        }


        if (multipleChoice) {
            filterQuestion.setMultipleChoice(1);
        } else {
            filterQuestion.setMultipleChoice(0);
        }

        // Call Repository to search for questions
        ArrayList<Question> result;
        if (multipleChoice) {
            result = SQLiteDatabaseConnection.questionRepository.getAll(filterQuestion, SharedData.getSelectedCourse().getCourse_name(), true);
        } else {
            result = SQLiteDatabaseConnection.questionRepository.getAll(filterQuestion, SharedData.getSelectedCourse().getCourse_name(), false);
        }

        // Display result in the console
        printQuestions(result);
    }


    @FXML
    private void showQuestionsInPreview() {
        //vbox_labels.getChildren().clear();
        //spacing between each spacing (serves as an area for the answers)
        double spacing = 100.0;
        System.out.println("questions: ");
        System.out.println("questions: "+ SharedData.getTestQuestions().get(0).getQuestionString());
        //counter for question number
        int i = 1;
        if (!SharedData.getTestQuestions().isEmpty()) {
            for (Question question : SharedData.getTestQuestions()) {
                System.out.println("this is the vbox: "+ vbox_testQuestionsPreview.getChildren());
                //create the Vbox and the component for the question
                VBox questionVbox = new VBox();
                Label questionNumberLabel = new Label("Question "+ i +" (Erreichbare Punkte: "+ question.getPoints() + ")");
                Label questionTextLabel = new Label(question.getQuestionString());

                System.out.println("question String:" + SharedData.getTestQuestions().get(0).getQuestionString());
                //add elements to the Vbox
                questionVbox.getChildren().add(questionNumberLabel);
                questionVbox.getChildren().add(questionTextLabel);

                //add the question Vbox to the testPreview area (vbox)
                vbox_testQuestionsPreview.getChildren().add(questionVbox);

                //set spacing between questions (serves as answer area)
                vbox_testQuestionsPreview.setSpacing(spacing);
                System.out.println("this is the vbox: "+ vbox_testQuestionsPreview.getChildren());
                i++;
            }
            // after the questions are displayed delete the questions from the sharedData class
            //SharedData.resetQuestions();
        }
    }

    @FXML
    void printQuestions(ArrayList<Question> questions) {
        if(questions.isEmpty()) {
            System.out.println("No questions found");
            return;
        }
        double spacing = 10.0;
        for(Question question : questions) {

            /*
            System.out.println("_----------------------------------_");
            System.out.println("ID: " + question.getQuestion_id());
            System.out.println("QuestionString: " + question.getQuestionString());
            System.out.print("Keywords: ");
            for(Keyword keyword : question.getKeywords()) {
                System.out.print(keyword.getKeyword_text() + " ");
            }
            System.out.println();
            System.out.println("Answer: " + question.getAnswers());
            System.out.println("MC: " + question.getMultipleChoice());
            System.out.println("Category: " + question.getCategory().getCategory());
            System.out.println("Language: " + question.getLanguage());
            System.out.println("Difficulty: " + question.getDifficulty());
            System.out.println("Points: " + question.getPoints());

             */

            System.out.println("this is the vbox: "+ vbox_filteredQuestionsPreview.getChildren());
            //create the Vbox and the elements for the question
            VBox questionVbox = new VBox();

            Label questionNumberLabel = new Label("(Erreichbare Punkte: "+ question.getPoints() + ")");
            questionNumberLabel.setTextFill(Color.WHITE);

            Label questionDifficultyLabel = new Label("Difficulty: " + question.getDifficulty());
            questionDifficultyLabel.setTextFill(Color.WHITE);

            Label questionTextLabel = new Label(question.getQuestionString());
            questionTextLabel.setTextFill(Color.WHITE);

            //adds a newline if text is too long
            questionTextLabel.setWrapText(true);

            Label questionAnswersLabel = null;
            if (question.getAnswers() != null) {
                questionAnswersLabel = new Label(question.getAnswers());
                questionAnswersLabel.setTextFill(Color.WHITE);
            }

            Label questionRemarksLabel = null;
            if(question.getRemarks() != null) {
                questionRemarksLabel = new Label(question.getRemarks());
                questionRemarksLabel.setTextFill(Color.WHITE);
            }

            //add elements to the Vbox
            questionVbox.getChildren().add(questionNumberLabel);
            questionVbox.getChildren().add(questionDifficultyLabel);
            questionVbox.getChildren().add(questionTextLabel);
            if (questionAnswersLabel!= null) {
                questionVbox.getChildren().add(questionAnswersLabel);
            }
            if (questionRemarksLabel!= null) {
                questionVbox.getChildren().add(questionAnswersLabel);
            }

            //add the question Vbox to the testPreview area (vbox)
            vbox_filteredQuestionsPreview.getChildren().add(questionVbox);
            vbox_filteredQuestionsPreview.setSpacing(spacing);
        }
    }
}
