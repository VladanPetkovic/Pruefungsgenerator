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
    private MenuButton keyword;
    private ArrayList<Keyword> keywords;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();
    @FXML
    private HBox keywordsHBox;
    private ArrayList<TextArea> answers = new ArrayList<>();

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

        difficulty.setValue(5);

        //SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1, 0.5);
        //points.setValueFactory(valueFactory);

        points = new CustomDoubleSpinner();
        points.getStyleClass().add("automatic_create_spinner");

        customDoubleSpinnerPlaceholder.getChildren().add(points);

        question.setText("");
        remarks.setText("");

        keywords = SQLiteDatabaseConnection.keywordRepository.getAllOneCourse(SharedData.getSelectedCourse().getId());

        fillKeywordWithKeywords();
        initializeMenuButton(questionTypeMenuButton, false);
        initQuestionTypeListener();
    }

    /**
     * Fills the MenuButton for selecting keywords with the available keywords.
     * This method iterates through the list of keywords retrieved from the database
     * and adds each keyword as a MenuItem to the MenuButton for keyword selection.
     * When a keyword is selected from the MenuButton, it is added to the list of selected keywords.
     * If a selected keyword is clicked again, it is removed from the list of selected keywords.
     */
    private void fillKeywordWithKeywords() {
        // Iterate through the list of keywords
        for (Keyword k : keywords) {
            // Create a MenuItem for the keyword
            MenuItem menuItem = new MenuItem(k.getKeyword());

            // Set the action event for the MenuItem
            menuItem.setOnAction(event -> {
                // Check if the selected keyword is not already in the list of selected keywords
                if (!selectedKeywords.contains(k)) {
                    // If not, add the keyword to the list of selected keywords
                    selectedKeywords.add(k);

                    // Create a button to display the selected keyword with a removal option
                    Button b = createButton(k.getKeyword() + " X");

                    // Set the action event for the removal button
                    b.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            // When the removal button is clicked, remove the keyword from the list of selected keywords
                            keywordsHBox.getChildren().remove(b);
                            selectedKeywords.remove(k);
                        }
                    });

                    // Add the removal button to the HBox for displaying selected keywords
                    keywordsHBox.getChildren().add(b);
                }
            });

            // Add the MenuItem to the MenuButton's items
            keyword.getItems().add(menuItem);
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
            showErrorAlert("Error", "Not all fields filled", s);
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
            switchScene(questionUpload, true);
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
        // Check if an existing category has been selected
        if (!SharedData.getSuggestedCategories().contains(categoryTextField.getText())) {
            return "Select an existing category - or add a new category.";
        }
        if (!QuestionType.checkExistingType(questionTypeMenuButton.getText())) {
            return "Question-Type needs to be selected.";
        }
        // Check if multiple choice is selected and at least one answer is not filled out
        if (checkIfEmptyAnswers()) {
            return "You selected multiple choice but at least one answer is not filled out.";
        }
        // Check if the question text area is empty
        if (checkIfQuestionIsEmpty()) {
            return "Question needs to be filled out.";
        }
        return null;
    }

    public void on_add_category_btn_click(ActionEvent actionEvent) {
        addCategoryBtnClick(categoryTextField, add_category_btn);
    }
}
