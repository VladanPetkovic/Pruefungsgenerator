package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import com.example.frontend.MainApp;
import com.example.frontend.components.CustomDoubleSpinner;

import com.example.frontend.components.PicturePickerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
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
    @FXML
    private HBox keywordsHBox;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();
    private ArrayList<TextArea> answers = new ArrayList<>();

    @FXML
    private VBox picturePickerPlaceholder;
    private PicturePickerController picturePickerController;

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
        ArrayList<Keyword> keywords = SQLiteDatabaseConnection.keywordRepository.getAllOneCourse(SharedData.getSelectedCourse().getId());
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

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("components/picture_picker.fxml"));
            VBox picturePicker = loader.load();
            picturePickerController = loader.getController();
            picturePickerPlaceholder.getChildren().add(picturePicker);
            picturePickerController.setTextArea(question);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (answers.size() <= 10) {
            // Create an HBox to contain each answer and its removal button
            HBox hBoxAnswerRemove = new HBox();
            TextArea textAreaAnswer = new TextArea();
            answers.add(textAreaAnswer);
            Button buttonRemove = createButton("X");
            // Set action event for the removal button
            buttonRemove.setOnAction(e -> {
                answers.remove(textAreaAnswer);
                multipleChoiceAnswerVBox.getChildren().remove(hBoxAnswerRemove);
            });
            // Add the answer TextArea and its removal button to the HBox
            hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
            // Add the HBox containing the answer and its removal button to the multiple choice VBox
            multipleChoiceAnswerVBox.getChildren().add(hBoxAnswerRemove);
        }
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
            SharedData.setOperation(s, true);
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
                picturePickerController.getImages()         // TODO: placeholder for photos
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
     * Checks if the question text area is empty.
     *
     * @return true if the question text area is empty, false otherwise.
     */
    private boolean checkIfQuestionIsEmpty() {
        return question.getText().isEmpty();
    }

    /**
     * Checks if all required fields are filled out.
     *
     * @return An error message if any required field is not filled out, otherwise null.
     */
    private String checkIfFilled() {
        if (!SharedData.getSuggestedCategories().contains(categoryTextField.getText())) {
            return "Select an existing category - or add a new category.";
        }
        if (!QuestionType.checkExistingType(questionTypeMenuButton.getText())) {
            return "Question-Type needs to be selected.";
        }
        if (checkIfEmptyAnswers(questionTypeMenuButton, answers)) {
            return "You selected multiple choice, but at least one answer is not filled out.";
        }
        if (answers.size() < 2 && QuestionType.checkMultipleChoiceType(questionTypeMenuButton.getText())) {
            return "Enter at least two answers, when selecting multiple choice.";
        }
        if (checkIfQuestionIsEmpty()) {
            return "Question needs to be filled out.";
        }
        if (picturePickerController.invalidSyntax()) {
            return "Every image has to be included at least once.";
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
