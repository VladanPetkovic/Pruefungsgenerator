package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import com.example.frontend.components.CustomDoubleSpinner;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class QuestionCreate_ScreenController extends ScreenController implements Initializable {
    @FXML
    private TextField categoryTextField;
    @FXML
    private Button add_category_btn;
    @FXML
    private Slider difficulty;
    @FXML
    private VBox customDoubleSpinnerPlaceholder;
    private CustomDoubleSpinner points;
    @FXML
    public MenuButton questionTypeMenuButton;
    @FXML
    private VBox multipleChoiceAnswerVBox;
    @FXML
    private VBox multipleChoiceVBox;
    @FXML
    private TextArea question;
    @FXML
    private TextArea remarks;
    @FXML
    private TextArea answerTextArea;
    @FXML
    private MenuButton keywordMenuButton;
    @FXML
    private Button addKeywordBtn;
    @FXML
    private TextField keywordTextField;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();
    @FXML
    private HBox keywordsHBox;
    private ArrayList<TextArea> answers = new ArrayList<>();
    private ArrayList<Keyword> keywords = new ArrayList<>();

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is called once all FXML elements have been processed, but before the elements have been
     * rendered on the screen. It initializes the UI elements and retrieves necessary data from the database.
     *
     * @param location  The location used to resolve relative paths for the root object, or {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCategories(this.categoryTextField, SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getId()), add_category_btn);
        keywords = SQLiteDatabaseConnection.keywordRepository.getAllOneCourse(SharedData.getSelectedCourse().getId());
        initializeKeywords(keywordTextField, keywords, addKeywordBtn);

        difficulty.setValue(5);

        points = new CustomDoubleSpinner();
        points.getStyleClass().add("automatic_create_spinner");

        customDoubleSpinnerPlaceholder.getChildren().add(points);

        question.setText("");
        remarks.setText("");

        for (Keyword keyword : keywords) {
            fillKeywordMenuButtonWithKeyword(keyword, selectedKeywords, keywordsHBox, keywordMenuButton);
        }

        initializeMenuButton(questionTypeMenuButton, false);
        initQuestionTypeListener();
    }

    /**
     * This function gets triggered when the questionTypeMenuButton is clicked on.
     */
    public void initQuestionTypeListener() {
        questionTypeMenuButton.textProperty().addListener((observable) -> {
            if (QuestionType.checkExistingType(questionTypeMenuButton.getText())) {
                switch (new QuestionType(questionTypeMenuButton.getText()).getType()) {
                    case MULTIPLE_CHOICE:
                        initMultipleChoiceVBox();
                        break;
                    case TRUE_FALSE:
                        hideMultipleChoiceVBox();
                        answerTextArea.setDisable(true);
                        break;
                    default:
                        hideMultipleChoiceVBox();
                }
            } else {
                hideMultipleChoiceVBox();
            }
        });
    }

    public void onAddNewAnswerBtnClick(ActionEvent actionEvent) {
        addMultipleChoiceAnswerBtnClicked(answers, multipleChoiceAnswerVBox);
    }

    private void initMultipleChoiceVBox() {
        multipleChoiceVBox.setVisible(true);
        answerTextArea.setDisable(true);
    }

    /**
     * This function hides the multipleChoiceVBox and resets our answers-ArrayList.
     */
    private void hideMultipleChoiceVBox() {
        multipleChoiceVBox.setVisible(false);
        // Clear the list of answers
        answers.clear();
        multipleChoiceAnswerVBox.getChildren().clear();
        answerTextArea.setDisable(false);
    }

    /**
     * Handles the action event when the upload button is clicked. Validates if all required fields are filled,
     * displays an error alert if any field is missing, otherwise creates a new Question object and uploads it
     * to the database. It also associates keywords with the question, switches the scene if the question
     * upload is successful.
     */
    @FXML
    private void onActionUpload() {
        // Check if all required fields are filled
        String s = checkIfFilled();
        // If any field is missing, display an error alert and return
        if (s != null) {
            SharedData.setOperation(Message.ERROR_MESSAGE_NOT_ALL_FIELDS_FILLED);
            return;
        }

        QuestionType questionType = new QuestionType(questionTypeMenuButton.getText());
        Category category = SQLiteDatabaseConnection.CategoryRepository.get(categoryTextField.getText());

        // Create a new Question object with the provided details
        Question q = new Question(
                category,
                (int) difficulty.getValue(),
                points.getValue().floatValue(),
                question.getText(),
                questionType,
                remarks.getText(),
                new Timestamp(System.currentTimeMillis()),
                null,               // this can only be changed when editing a question
                getAnswerArrayList(Type.valueOf(questionTypeMenuButton.getText()), answerTextArea, this.answers),
                selectedKeywords,
                new ArrayList<>()           // TODO: placeholder for photos
        );

        int question_id = Question.createNewQuestionInDatabase(q);
        // If the question upload was successful
        if (question_id != 0) {
            // Associate keywords with the uploaded question // TODO: change this to be efficient
            for (Keyword k : selectedKeywords) {
                SQLiteDatabaseConnection.keywordRepository.addConnection(k, question_id);
            }
            // Switch the scene to the question upload screen
            switchScene(questionCreate, true);
        }
    }

    /**
     * Checks if any of the answers provided for the question are empty. If multiple choice is selected,
     * it iterates through the list of answers and returns true if it finds any empty answer. If multiple
     * choice is not selected, it always returns false.
     *
     * @return true if any answer is empty (when multiple choice is selected), false otherwise.
     */
    private boolean checkIfEmptyAnswers() {
        // Check if multiple choice is selected
        if (QuestionType.checkMultipleChoiceType(questionTypeMenuButton.getText())) {
            // Iterate through the list of answers
            for (TextArea t : answers) {
                // Check if the current answer is empty
                if (t.getText().isEmpty()) {
                    // Return true if an empty answer is found
                    return true;
                }
            }
        }
        // Return false if no empty answers are found or if multiple choice is not selected
        return false;
    }

    /**
     * Checks if the question text area is empty.
     * @return true if the question text area is empty, false otherwise.
     */
    private boolean checkIfQuestionIsEmpty() {
        return question.getText().isEmpty();
    }

    /**
     * Checks if all required fields are filled out.
     * @return An error message if any required field is not filled out, otherwise null.
     */
    private String checkIfFilled() {
        if (!SharedData.getSuggestedCategories().contains(categoryTextField.getText())) {
            return "Select an existing category - or add a new category.";
        }
        if (!QuestionType.checkExistingType(questionTypeMenuButton.getText())) {
            return "Question-Type needs to be selected.";
        }
        if (checkIfEmptyAnswers()) {
            return "You selected multiple choice but at least one answer is not filled out.";
        }
        if (checkIfQuestionIsEmpty()) {
            return "Question needs to be filled out.";
        }
        return null;
    }

    public void on_add_category_btn_click(ActionEvent actionEvent) {
        if (Category.checkNewCategory(categoryTextField.getText()) == null) {
            addCategoryBtnClick(categoryTextField, add_category_btn);
        }
    }

    public void onAddKeywordBtnClick(ActionEvent actionEvent) {
        if (Keyword.checkNewKeyword(keywordTextField.getText()) == null) {
            // add to database, if not existing
            Keyword newKeyword = Keyword.createNewKeywordInDatabase(keywordTextField.getText());
            // add to our KeywordMenuButton
            fillKeywordMenuButtonWithKeyword(newKeyword, selectedKeywords, keywordsHBox, keywordMenuButton);
            // return a message
            SharedData.setOperation(Message.CREATE_KEYWORD_SUCCESS_MESSAGE);
            addKeywordBtn.setDisable(true);
        }
    }
}
