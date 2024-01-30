package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class QuestionCreate_ScreenController extends ScreenController implements Initializable {

    @FXML
    private MenuButton category;
    @FXML
    private Slider difficulty;
    @FXML
    private Spinner<Double> points;
    @FXML
    private CheckBox multipleChoice;
    @FXML
    private VBox multipleChoiceVBox;
    @FXML
    private TextArea question;
    @FXML
    private TextArea remarks;
    @FXML
    private MenuButton keyword;
    private ArrayList<Keyword> keywords;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();
    @FXML
    private HBox keywordsHBox;
    private ArrayList<Category> categories;
    private Category selectedCategory = null;
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
        // retrieve categories from the database based on the selected course ID
        categories = SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getCourse_id());

        // display an error alert if no categories are found
        if(categories.size() == 0){
            showErrorAlert("Error","No categories found","Please create categories first before accessing upload question");
        }

        // fill the category menu button with retrieved categories
        fillCategoryWithCategories();

        // set the default value for the difficulty slider
        difficulty.setValue(5);

        // set up the spinner for points with a default value factory
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1, 0.5);
        points.setValueFactory(valueFactory);

        // clear the text areas for question and remarks
        question.setText("");
        remarks.setText("");

        // retrieve keywords from the database
        keywords = SQLiteDatabaseConnection.keywordRepository.getAll();

        // fill the keyword menu button with retrieved keywords
        fillKeywordWithKeywords();
    }

    /**
     * Displays an error alert dialog with the specified title, header text, and content text.
     * The dialog is modal, meaning it will block user interaction with other windows until closed.
     *
     * @param title       The title of the error alert dialog.
     * @param headerText  The header text displayed in the error alert dialog.
     * @param contentText The main content text displayed in the error alert dialog.
     */
    public void showErrorAlert(String title, String headerText, String contentText) {
        // Create a new instance of Alert with an error type
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Set the title of the error alert dialog
        alert.setTitle(title);

        // Set the header text of the error alert dialog
        alert.setHeaderText(headerText);

        // Set the main content text of the error alert dialog
        alert.setContentText(contentText);

        // Display the error alert dialog and wait for user interaction
        alert.showAndWait();
    }

    /**
     * Fills the MenuButton for selecting categories with the available categories.
     * This method iterates through the list of categories retrieved from the database
     * and adds each category as a MenuItem to the MenuButton for category selection.
     * When a category is selected from the MenuButton, the corresponding category
     * is assigned to the selectedCategory variable, and its name is displayed in the MenuButton.
     */
    private void fillCategoryWithCategories() {
        // Iterate through the list of categories
        for (Category c : categories) {
            // Create a MenuItem for the category
            MenuItem menuItem = createMenuItem(c.getCategory());

            // Set the action event for the MenuItem
            menuItem.setOnAction(event -> {
                // When the MenuItem is clicked, assign the selected category to selectedCategory
                selectedCategory = c;

                // Set the text of the MenuButton to the name of the selected category
                category.setText(c.getCategory());
            });

            // Add the MenuItem to the MenuButton's items
            category.getItems().add(menuItem);
        }
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
            MenuItem menuItem = createMenuItem(k.getKeyword_text());

            // Set the action event for the MenuItem
            menuItem.setOnAction(event -> {
                // Check if the selected keyword is not already in the list of selected keywords
                if (!selectedKeywords.contains(k)) {
                    // If not, add the keyword to the list of selected keywords
                    selectedKeywords.add(k);

                    // Create a button to display the selected keyword with a removal option
                    Button b = createButton(k.getKeyword_text() + " X");

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
     * Creates a MenuItem with the specified text.
     *
     * @param text The text to be displayed on the MenuItem.
     * @return The created MenuItem with the specified text.
     */
    private MenuItem createMenuItem(String text) {
        // Create a new MenuItem with the specified text
        MenuItem menuItem = new MenuItem(text);
        // Return the created MenuItem
        return menuItem;
    }

    /**
     * Handles the action event when the multiple choice CheckBox is selected or deselected.
     * If the CheckBox is selected, it calls the createMultipleChoiceButton() method.
     * If the CheckBox is deselected, it clears the multipleChoiceVBox and adds the CheckBox back, while clearing the answers list.
     */
    @FXML
    private void onActionMultipleChoice() {
        // Check if the multiple choice CheckBox is selected
        if (multipleChoice.isSelected()) {
            // If selected, call the method to create multiple choice buttons
            createMultipleChoiceButton();
        } else {
            // If not selected, proceed with deselection handling
            // Store the reference to the CheckBox
            CheckBox checkBox = multipleChoice;
            // Clear the contents of the multiple choice VBox
            multipleChoiceVBox.getChildren().clear();
            // Add the CheckBox back to the VBox
            multipleChoiceVBox.getChildren().add(checkBox);
            // Clear the list of answers
            answers.clear();
        }
    }

    /**
     * Creates multiple choice answer input fields dynamically.
     * Adds a new TextArea for each answer and a corresponding removal button.
     * Limits the number of answer fields to 10.
     * Handles removal of answer fields when the removal button is clicked.
     */
    private void createMultipleChoiceButton() {
        // Create a button for adding answers
        Button button = createButton("Add answer");
        // Set action event for the button
        button.setOnAction(event -> {
            // Create an HBox to contain each answer and its removal button
            HBox hBoxAnswerRemove = new HBox();
            // Create a new TextArea for the answer
            TextArea textAreaAnswer = new TextArea();
            // Add the answer TextArea to the answers list
            answers.add(textAreaAnswer);
            // Create a removal button for the answer
            Button buttonRemove = createButton("X");
            // Set action event for the removal button
            buttonRemove.setOnAction(e -> {
                // Remove the answer TextArea from the answers list
                answers.remove(textAreaAnswer);
                // Remove the HBox containing the answer and its removal button
                multipleChoiceVBox.getChildren().remove(hBoxAnswerRemove);
                // Check if the maximum number of answers (10) has been reached, and create a new button if necessary
                if (answers.size() == 10) createMultipleChoiceButton();
            });
            // Add the answer TextArea and its removal button to the HBox
            hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
            // Add the HBox containing the answer and its removal button to the multiple choice VBox
            multipleChoiceVBox.getChildren().add(hBoxAnswerRemove);
            // Remove the "Add answer" button after adding a new answer field
            multipleChoiceVBox.getChildren().remove(button);
            // Check if the maximum number of answers (10) has been exceeded and prevent further creation if so
            if (answers.size() > 10) return;
            // Recursively call the method to create additional answer fields
            createMultipleChoiceButton();
        });
        // Add the "Add answer" button to the multiple choice VBox
        multipleChoiceVBox.getChildren().add(button);
    }

    /**
     * Creates a JavaFX Button with the given text and disables focus traversal.
     *
     * @param text The text to display on the button.
     * @return The created Button with the specified text and disabled focus traversal.
     */
    private Button createButton(String text) {
        // Create a new Button with the specified text
        Button button = new Button(text);
        // Disable focus traversal for the button
        button.setFocusTraversable(false);
        // Return the created Button
        return button;
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
        // Create a new Question object with the provided details
        Question q = new Question(
                selectedCategory,
                (int) difficulty.getValue(),
                points.getValue().floatValue(),
                question.getText(),
                multipleChoice.isSelected() ? 1 : 0,
                "",
                remarks.getText(),
                answersToDatabaseString(),
                selectedKeywords,
                new ArrayList<>()
        );
        // Add the question to the database
        SQLiteDatabaseConnection.questionRepository.add(q);
        // Create a Question object to search for the uploaded question
        Question questionSearch = new Question();
        questionSearch.setCategory(selectedCategory);
        questionSearch.setDifficulty((int) difficulty.getValue());
        questionSearch.setQuestionString(question.getText());
        questionSearch.setMultipleChoice(multipleChoice.isSelected() ? 1 : 0);
        // Retrieve the uploaded question from the database
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll(
                questionSearch, SharedData.getSelectedCourse().getCourse_name(), multipleChoice.isSelected());
        // If the question upload was successful
        if (questions.size() != 0) {
            // Set the question ID for the uploaded question
            q.setQuestion_id(questions.get(0).getQuestion_id());
            // Associate keywords with the uploaded question
            for (Keyword k : selectedKeywords) {
                SQLiteDatabaseConnection.keywordRepository.addConnection(k, q);
            }
            // Switch the scene to the question upload screen
            switchScene(questionUpload, true);
        }
    }

    /**
     * Generates a string representation of the answers provided for the question, suitable for storing
     * in the database. If multiple choice is selected, it iterates through the list of answers,
     * appends each answer followed by a newline character to the StringBuilder, and returns the resulting string.
     * If multiple choice is not selected, it returns an empty string.
     *
     * @return A string representing the answers for the question, formatted for database storage.
     */
    private String answersToDatabaseString() {
        StringBuilder s = new StringBuilder();
        // Check if multiple choice is selected
        if (multipleChoice.isSelected()) {
            // Iterate through the list of answers
            for (TextArea t : answers) {
                // Append each answer followed by a newline character
                s.append(t.getText()).append("\n");
            }
        }
        // Return the generated string
        return s.toString();
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
        if (multipleChoice.isSelected()) {
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
        // Check if the category is selected
        if (selectedCategory == null) {
            return "Category needs to be selected.";
        }
        // Check if multiple choice is selected and at least one answer is not filled out
        if (checkIfEmptyAnswers()) {
            return "You selected multiple choice but at least one answer is not filled out.";
        }
        // Check if the question text area is empty
        if (checkIfQuestionIsEmpty()) {
            return "Question needs to be filled out.";
        }
        // Return null if all required fields are filled out
        return null;
    }
}
