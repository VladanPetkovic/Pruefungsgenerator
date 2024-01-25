package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Slider;
import org.w3c.dom.ls.LSOutput;

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
        // init points and difficulty from the slider by a listener
        // --> only when the value changes, the value is updated
        getPointsFromSlider();
        getDifficultyFromSlider();
        // Set up the event handler for the "Apply Filter" button
        applyFilterButton.setOnAction(this::applyFilterButtonClicked);
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

        // not needed anymore
//        // Set difficulty and points only if sliders are moved
//        if (difficultySlider.isValueChanging()) {
//            int difficulty = (int) difficultySlider.getValue();
//            filterQuestion.setDifficulty(difficulty);
//        }
//
//        if (pointsSlider.isValueChanging()) {
//            float points = (float) pointsSlider.getValue();
//            filterQuestion.setPoints(points);
//        }

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
    void printQuestions(ArrayList<Question> questions) {
        if(questions.isEmpty()) {
            System.out.println("No questions found");
            return;
        }

        for(Question question : questions) {
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
        }
    }
}
