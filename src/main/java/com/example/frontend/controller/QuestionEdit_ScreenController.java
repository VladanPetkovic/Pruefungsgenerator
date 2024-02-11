package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.controlsfx.control.textfield.TextFields;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class QuestionEdit_ScreenController extends ScreenController implements Initializable {

    @FXML
    private Label label_selectedCourse;

    @FXML
    private TextField categoryTextField;

    @FXML
    private TextField keywordTextField;

    @FXML
    private TextField questionTextField;

    @FXML
    private Slider difficultySlider;

    @FXML
    private Slider pointsSlider;

    @FXML
    private CheckBox multipleChoiceCheckBox;

    private ArrayList<TextArea> answers = new ArrayList<>();

    @FXML
    private VBox previewVBox;

    @FXML
    private MenuButton chooseCategory;

    @FXML
    private Spinner<Double> choosePoints;

    @FXML
    private CheckBox chooseMultipleChoice;

    @FXML
    private Slider chooseDifficulty;

    @FXML
    private TextArea chooseQuestion;

    @FXML
    private TextArea chooseRemarks;

    private ArrayList<Keyword> keywords;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();

    private ArrayList<Keyword> startState = new ArrayList<>();

    private ArrayList<Category> categories;
    private Category selectedCategory = null;

    @FXML
    private ScrollPane chooseScrollPane;

    @FXML
    private HBox keywordsHBox;

    @FXML
    private MenuButton chooseKeywords;

    @FXML
    private VBox multipleChoiceVBox;

    private int questionId;

    /**
     * Initializes the Question Edit screen.
     *
     * @param url            The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Makes the scroll pane transparent to mouse events.
        chooseScrollPane.setMouseTransparent(true);

        // Sets the text of the selected course label to the name of the selected course.
        label_selectedCourse.setText(SharedData.getSelectedCourse().getCourse_name());

        // Retrieves all categories for the selected course from the database.
        categories = SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getCourse_id());

        // Displays an error alert if no categories are found for the selected course.
        if (categories.size() == 0)
            showErrorAlert("Error", "No categories found", "Please create categories first before accessing upload question");

        // Fills the category menu with the retrieved categories.
        fillCategoryWithCategories();

        // Retrieves all keywords from the database.
        keywords = SQLiteDatabaseConnection.keywordRepository.getAll();

        // Fills the keyword menu with the retrieved keywords.
        fillKeywordWithKeywords();

        // Configures the spinner value factory for choosing points.
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1, 0.5);

        // Sets the value factory for the choosePoints spinner.
        choosePoints.setValueFactory(valueFactory);

        // Sets the difficulty filter to the current value of the difficulty slider.
        getDifficultyFromSlider(this.difficultySlider);
        // Sets the points filter to the current value of the points slider.
        getPointsFromSlider(this.pointsSlider);

        initializeKeywords(keywordTextField, keywords);
        initializeCategories(categoryTextField, categories);
        initializeQuestions(questionTextField);
    }

    /**
     * Displays an error alert dialog.
     *
     * @param title       The title of the error alert dialog.
     * @param headerText  The header text of the error alert dialog.
     * @param contentText The content text of the error alert dialog.
     */
    public void showErrorAlert(String title, String headerText, String contentText) {
        // Creates a new Alert dialog with ERROR type.
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Sets the title of the error alert dialog.
        alert.setTitle(title);

        // Sets the header text of the error alert dialog.
        alert.setHeaderText(headerText);

        // Sets the content text of the error alert dialog.
        alert.setContentText(contentText);

        // Displays the error alert dialog and waits for user input.
        alert.showAndWait();
    }

    /**
     * Handles the event when the apply filter button is clicked.
     * Calls the searchQuestions method to perform filtering and searching.
     *
     * @param event The ActionEvent object representing the button click event.
     */
    @FXML
    private void applyFilterButtonClicked(ActionEvent event) {
        // Calls the searchQuestions method to perform filtering and searching.
        searchQuestions();
    }

    /**
     * Searches for questions based on the applied filters and displays the result.
     * Constructs a filter question based on the selected filter criteria.
     * Calls the question repository to retrieve the filtered questions.
     * Displays the filtered questions in the console.
     */
    @FXML
    private void searchQuestions() {
        // Constructs a filter question based on the selected filter criteria.
        Question filterQuestion = createFilterQuestion();

        // Calls the question repository to retrieve the filtered questions.
        ArrayList<Question> result = SQLiteDatabaseConnection.questionRepository.getAll(
                filterQuestion,
                SharedData.getSelectedCourse().getCourse_name(),
                true
        );

        // Displays the filtered questions in the console.
        printQuestions(result);
    }

    /**
     * Creates a filter question based on the selected filter criteria.
     * Retrieves the category name, keyword text, question text and multiple choice checkbox status.
     * Sets the category filter, keyword filter, question filter, points and difficulty filter, and multiple choice status.
     * @return The constructed filter question.
     */
    private Question createFilterQuestion() {
        // Creates a new instance of the filter question.
        Question filterQuestion = new Question();

        // Retrieves the category name, keyword text, and multiple choice checkbox status.
        String categoryName = categoryTextField.getText().trim();
        String keywordText = keywordTextField.getText().trim();
        String questionText = questionTextField.getText();
        boolean multipleChoice = multipleChoiceCheckBox.isSelected();

        // Sets the category filter based on the provided category name.
        setCategoryFilter(categoryName, filterQuestion);

        // Sets the keyword filter based on the provided keyword text.
        setKeywordFilter(keywordText, filterQuestion);

        // Sets the question filter based on the provided question text.
        setQuestionFilter(questionText, filterQuestion);

        // Sets the points and difficulty filter.
        setPointsAndDifficultyFilter(filterQuestion);

        // Sets the multiple choice status based on the checkbox selection.
        filterQuestion.setMultipleChoice(multipleChoice ? 1 : 0);

        // Returns the constructed filter question.
        return filterQuestion;
    }

    /**
     * Sets the category filter for the filter question.
     * If the provided category name is not empty:
     *     - Retrieves the category object from the database based on the category name.
     *     - If the category exists, sets it as the category filter for the filter question.
     * @param categoryName The name of the category to set as the filter.
     * @param filterQuestion The filter question object to set the category filter.
     */
    private void setCategoryFilter(String categoryName, Question filterQuestion) {
        // Checks if the provided category name is not empty.
        if (!categoryName.isEmpty()) {
            // Retrieves the category object from the database based on the category name.
            Category category = SQLiteDatabaseConnection.CategoryRepository.get(categoryName);
            // If the category exists, sets it as the category filter for the filter question.
            if (category != null) {
                filterQuestion.setCategory(category);
            }
        }
    }

    /**
     * Sets the keyword filter for the filter question based on the provided keyword text.
     * If the provided keyword text is not empty:
     *     - Splits the keyword text into an array of keywords using comma or whitespace as delimiters.
     *     - Retrieves keyword objects from the database for each keyword in the array.
     *     - Adds the retrieved keyword objects to a list.
     *     - Sets the list of keywords as the keyword filter for the filter question.
     * @param keywordText The text containing keywords to set as the filter.
     * @param filterQuestion The filter question object to set the keyword filter.
     */
    private void setKeywordFilter(String keywordText, Question filterQuestion) {
        // Checks if the provided keyword text is not empty.
        if (keywordText != null) {
            // Splits the keyword text into an array of keywords using comma or whitespace as delimiters.
            String[] keywordsArray = keywordText.split("[,\\s]+");
            // Initializes a list to store keyword objects.
            ArrayList<Keyword> keywordsList = new ArrayList<>();

            // Iterates through each keyword in the array.
            for (String keyword : keywordsArray) {
                // Retrieves the keyword object from the database for the current keyword in the array.
                Keyword keywordObj = SQLiteDatabaseConnection.keywordRepository.get(keyword.trim());
                // If the keyword object exists, adds it to the list.
                if (keywordObj != null) {
                    keywordsList.add(keywordObj);
                }
            }

            // Sets the list of keywords as the keyword filter for the filter question.
            if(!keywordsList.isEmpty()) {
                filterQuestion.setKeywords(keywordsList);
            }
        }
    }

    /**
     * Sets the filterQuestion with the provided questionText-String.
     * @param questionText the question text inputted for getting all questions.
     * @param filterQuestion the filter question used for SQL-query.
     */
    private void setQuestionFilter(String questionText, Question filterQuestion) {
        if (Objects.equals(questionText, "") || questionText == null) {
            return;
        }

        filterQuestion.setQuestionString(questionText);
    }

    /**
     * Sets the difficulty and points filter for the filter question.
     * Sets the difficulty filter to the current value of the difficulty slider, if the slider-value was changed.
     * Sets the points filter to the current value of the points slider, if the slider-value was changed.
     * @param filterQuestion The filter question object to set the difficulty and points filters.
     */
    private void setPointsAndDifficultyFilter(Question filterQuestion) {
        int points = (int) SharedData.getFilterQuestion().getPoints();
        int difficulty = SharedData.getFilterQuestion().getDifficulty();
        if(points != 0) {
            filterQuestion.setPoints(points);
        }
        if(difficulty != 0) {
            filterQuestion.setDifficulty(difficulty);
        }
    }

    /**
     * Fills the category menu with available categories.
     * Iterates through the list of categories and creates a menu item for each category.
     * Associates an event handler with each menu item to handle category selection.
     */
    private void fillCategoryWithCategories() {
        for (Category category : categories) {
            // Create a menu item for the current category.
            MenuItem menuItem = createMenuItem(category.getCategory());
            // Associate an event handler with the menu item to handle category selection.
            menuItem.setOnAction(event -> handleCategorySelection(category));
            // Add the menu item to the category menu.
            chooseCategory.getItems().add(menuItem);
        }
    }

    /**
     * Fills the keyword menu with available keywords.
     * Iterates through the list of keywords and creates a menu item for each keyword.
     * Associates an event handler with each menu item to handle keyword selection.
     */
    private void fillKeywordWithKeywords() {
        for (Keyword keyword : keywords) {
            // Create a menu item for the current keyword.
            MenuItem menuItem = createMenuItem(keyword.getKeyword_text());
            // Associate an event handler with the menu item to handle keyword selection.
            menuItem.setOnAction(event -> handleKeywordSelection(keyword));
            // Add the menu item to the keyword menu.
            chooseKeywords.getItems().add(menuItem);
        }
    }

    /**
     * Creates a menu item with the specified text.
     *
     * @param text The text for the menu item.
     * @return The created MenuItem object.
     */
    private MenuItem createMenuItem(String text) {
        // Create a new menu item with the specified text.
        MenuItem menuItem = new MenuItem(text);
        // Return the created menu item.
        return menuItem;
    }

    /**
     * Handles the selection of a category.
     *
     * @param category The selected category.
     */
    private void handleCategorySelection(Category category) {
        // Set the selected category to the specified category.
        selectedCategory = category;
        // Set the text of the chooseCategory menu button to the name of the selected category.
        chooseCategory.setText(category.getCategory());
    }

    /**
     * Handles the selection of a keyword.
     *
     * @param keyword The selected keyword.
     */
    private void handleKeywordSelection(Keyword keyword) {
        // Check if the selected keyword is not already in the list of selected keywords.
        if (!containsKeyword(keyword)) {
            // Add the selected keyword to the list of selected keywords.
            selectedKeywords.add(keyword);
            // Create a button with the keyword text and an "X" to remove it.
            Button button = createButton(keyword.getKeyword_text() + " X");
            // Set an action event for the button to handle the removal of the keyword.
            button.setOnAction(event -> handleKeywordRemoval(button, keyword));
            // Add the button to the keywordsHBox.
            keywordsHBox.getChildren().add(button);
        }
    }

    /**
     * Checks if a keyword is already present in the list of selected keywords.
     *
     * @param k The keyword to check.
     * @return True if the keyword is already present, false otherwise.
     */
    private boolean containsKeyword(Keyword k) {
        // Iterate through the list of selected keywords.
        for (Keyword keyword : selectedKeywords) {
            // Check if the keyword IDs match.
            if (keyword.getKeyword_id() == k.getKeyword_id())
                return true;
        }
        return false;
    }

    /**
     * Removes a keyword from the list of selected keywords and its corresponding button from the UI.
     *
     * @param button The button associated with the keyword.
     * @param keyword The keyword to remove.
     */
    private void handleKeywordRemoval(Button button, Keyword keyword) {
        // Remove the button from the UI.
        keywordsHBox.getChildren().remove(button);
        // Remove the keyword from the list of selected keywords.
        selectedKeywords.remove(keyword);
    }

    /**
     * Handles the action when the multiple choice checkbox is selected or deselected.
     * If selected, creates the UI elements for adding multiple choice answers.
     * If deselected, resets the UI elements for multiple choice.
     */
    @FXML
    private void onActionChooseMultipleChoice() {
        // If the multiple choice checkbox is selected, create multiple choice UI elements.
        if (chooseMultipleChoice.isSelected()) {
            createMultipleChoiceButton();
        } else {
            // If deselected, reset the UI elements for multiple choice.
            resetMultipleChoiceView();
        }
    }

    /**
     * Resets the view for multiple choice questions.
     * Clears the existing UI elements and adds back the multiple choice checkbox.
     * Clears the list of answers.
     */
    private void resetMultipleChoiceView() {
        // Create a reference to the multiple choice checkbox.
        CheckBox checkBox = chooseMultipleChoice;
        // Clear the existing UI elements in the multiple choice VBox.
        multipleChoiceVBox.getChildren().clear();
        // Add the multiple choice checkbox back to the VBox.
        multipleChoiceVBox.getChildren().add(checkBox);
        // Clear the list of answers.
        answers.clear();
    }

    /**
     * Creates a button to add a new multiple choice answer entry.
     * Calls the method createAndAddMultipleChoiceButton() with default parameters.
     */
    private void createMultipleChoiceButton() {
        // Call createAndAddMultipleChoiceButton() with default parameters.
        createAndAddMultipleChoiceButton("Add answer", "");
    }

    /**
     * Creates a new multiple choice answer entry with the specified initial answer.
     * Adds the created answer entry to the multipleChoiceVBox.
     *
     * @param initialAnswer The initial answer text for the answer entry.
     */
    private void createMultipleChoiceEntry(String initialAnswer) {
        // Create an HBox to hold the answer entry and remove button.
        HBox hBoxAnswerRemove = new HBox();
        // Create a TextArea for the answer entry and set its initial text.
        TextArea textAreaAnswer = new TextArea();
        textAreaAnswer.setText(initialAnswer);
        // Add the TextArea to the list of answers.
        answers.add(textAreaAnswer);
        // Create a button to remove the answer entry.
        Button buttonRemove = createButton("X");
        // Set the action for the remove button.
        setButtonRemoveAction(buttonRemove, textAreaAnswer, hBoxAnswerRemove);
        // Add the answer entry and remove button to the HBox.
        hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
        // Add the HBox to the multipleChoiceVBox.
        multipleChoiceVBox.getChildren().add(hBoxAnswerRemove);
    }

    /**
     * Creates and adds a new multiple choice answer entry to the multipleChoiceVBox.
     * This method is called when the "Add answer" button is clicked.
     *
     * @param buttonText    The text to display on the button.
     * @param initialAnswer The initial answer text for the answer entry.
     */
    private void createAndAddMultipleChoiceButton(String buttonText, String initialAnswer) {
        // Create a button with the specified text.
        Button button = createButton(buttonText);
        // Set the action for the button.
        button.setOnAction(event -> {
            // Create an HBox to hold the answer entry and remove button.
            HBox hBoxAnswerRemove = new HBox();
            // Create a TextArea for the answer entry and set its initial text.
            TextArea textAreaAnswer = new TextArea();
            textAreaAnswer.setText(initialAnswer);
            // Add the TextArea to the list of answers.
            answers.add(textAreaAnswer);
            // Create a button to remove the answer entry.
            Button buttonRemove = createButton("X");
            // Set the action for the remove button.
            setButtonRemoveAction(buttonRemove, textAreaAnswer, hBoxAnswerRemove);
            // Add the answer entry and remove button to the HBox.
            hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
            // Add the HBox to the multipleChoiceVBox.
            multipleChoiceVBox.getChildren().add(hBoxAnswerRemove);
            // Remove the "Add answer" button if the maximum number of answers is reached.
            multipleChoiceVBox.getChildren().remove(button);
            // Create another "Add answer" button if the maximum number of answers is not reached.
            if (answers.size() <= 10) {
                createMultipleChoiceButton();
            }
        });
        // Add the button to the multipleChoiceVBox.
        multipleChoiceVBox.getChildren().add(button);
    }

    /**
     * Sets the action for the remove button associated with a multiple choice answer entry.
     * This method is called when a remove button is created.
     *
     * @param buttonRemove      The remove button.
     * @param textAreaAnswer    The TextArea associated with the answer entry.
     * @param hBoxAnswerRemove  The HBox containing the answer entry and remove button.
     */
    private void setButtonRemoveAction(Button buttonRemove, TextArea textAreaAnswer, HBox hBoxAnswerRemove) {
        // Set the action for the remove button.
        buttonRemove.setOnAction(e -> {
            // Remove the answer entry from the list of answers.
            answers.remove(textAreaAnswer);
            // Remove the HBox containing the answer entry and remove button from the multipleChoiceVBox.
            multipleChoiceVBox.getChildren().remove(hBoxAnswerRemove);
            // Create another "Add answer" button if the maximum number of answers is not reached.
            if (answers.size() <= 10) {
                createMultipleChoiceButton();
            }
        });
    }

    /**
     * Creates a Button with the specified text.
     *
     * @param text  The text to be displayed on the Button.
     * @return      The created Button.
     */
    private Button createButton(String text) {
        // Create a new Button with the specified text.
        Button button = new Button(text);
        // Set focus traversal to false to prevent the Button from being focused when navigating through UI controls.
        button.setFocusTraversable(false);
        // Return the created Button.
        return button;
    }

    /**
     * Prints the list of questions to the UI.
     *
     * @param questions The list of questions to be printed.
     */
    @FXML
    void printQuestions(ArrayList<Question> questions) {
        // Check if the list of questions is empty.
        if (questions.isEmpty()) {
            // If the list is empty, print a message indicating no questions found.
            System.out.println("No questions found");
            return;
        }

        // Define the spacing between question boxes.
        double spacing = 10.0;

        // Clear the existing content in the preview VBox.
        previewVBox.getChildren().clear();

        // Iterate through each question in the list.
        for (Question question : questions) {
            // Create a VBox to hold the question details.
            VBox questionVbox = createQuestionVBox(question);
            // Set event handlers for the question VBox.
            setQuestionVboxEventHandlers(questionVbox, question);
            // Add the question VBox to the preview VBox.
            previewVBox.getChildren().add(questionVbox);
            // Set the spacing between question boxes.
            previewVBox.setSpacing(spacing);
        }
    }

    /**
     * Creates a VBox to display the details of a question.
     *
     * @param question The question object containing the details to be displayed.
     * @return The VBox containing the question details.
     */
    private VBox createQuestionVBox(Question question) {
        // Create a new VBox to hold the question details.
        VBox questionVbox = new VBox();

        // Create labels to display question information.
        Label questionNumberLabel = createLabel("(Erreichbare Punkte: " + question.getPoints(), Color.WHITE);
        Label questionDifficultyLabel = createLabel("Difficulty: " + question.getDifficulty(), Color.WHITE);
        Label questionTextLabel = createLabel(question.getQuestionString(), Color.WHITE);
        Label questionAnswersLabel = createLabel(question.getAnswers(), Color.WHITE);
        Label questionRemarksLabel = createLabel(question.getRemarks(), Color.WHITE);

        // Allow the question text label to wrap text if necessary.
        questionTextLabel.setWrapText(true);

        // Add question details to the VBox.
        questionVbox.getChildren().addAll(questionNumberLabel, questionDifficultyLabel, questionTextLabel);
        addIfNotNull(questionVbox, questionAnswersLabel);
        addIfNotNull(questionVbox, questionRemarksLabel);

        return questionVbox;
    }

    /**
     * Sets event handlers for the VBox containing question details.
     *
     * @param questionVbox The VBox containing the question details.
     * @param question The question object associated with the VBox.
     */
    private void setQuestionVboxEventHandlers(VBox questionVbox, Question question) {
        questionVbox.setOnMouseClicked(event -> {
            // Make the scroll pane transparent to allow interaction with underlying elements.
            chooseScrollPane.setMouseTransparent(false);

            // Set category, difficulty, and points to the values of the selected question.
            chooseCategory.setText(question.getCategory().getCategory());
            selectedCategory = question.getCategory();
            chooseDifficulty.setValue(question.getDifficulty());
            choosePoints.getValueFactory().setValue((double) question.getPoints());

            // If the question is multiple choice, set the corresponding UI elements.
            if (question.getMultipleChoice() == 1) {
                chooseMultipleChoice.setSelected(true);
                Arrays.stream(question.getAnswers().split("\n")).forEach(this::createMultipleChoiceEntry);
                createMultipleChoiceButton();
            }

            // Set the question text and remarks to the values of the selected question.
            chooseQuestion.setText(question.getQuestionString());
            chooseRemarks.setText(question.getRemarks());

            // Clear and set up the keyword UI elements based on the selected question.
            selectedKeywords.clear();
            startState.clear();
            keywordsHBox.getChildren().clear();

            for (Keyword k : question.getKeywords()) {
                if(k.getKeyword_text() != null) {
                    startState.add(k);
                    selectedKeywords.add(k);
                    Button b = createButton(k.getKeyword_text() + " X");
                    b.setOnAction(e -> {
                        keywordsHBox.getChildren().remove(b);
                        selectedKeywords.remove(k);
                    });
                    keywordsHBox.getChildren().add(b);
                }
            }

            // Set the question ID.
            questionId = question.getQuestion_id();
        });
    }

    /**
     * Creates a label with the specified text and text fill color.
     *
     * @param text The text content of the label.
     * @param textFill The color used to fill the label's text.
     * @return The created label with the specified text and text fill color, or null if the text is null.
     */
    private Label createLabel(String text, Paint textFill) {
        if (text != null) {
            Label label = new Label(text);
            label.setTextFill(textFill);
            return label;
        }
        return null;
    }

    /**
     * Adds the specified node to the VBox if it is not null.
     *
     * @param vBox The VBox container to which the node will be added.
     * @param node The node to add to the VBox.
     */
    private void addIfNotNull(VBox vBox, Node node) {
        if (node != null) {
            vBox.getChildren().add(node);
        }
    }

    /**
     * Handles the action when the "Choose" button is clicked.
     * Validates input fields and updates the question accordingly.
     * If there is any validation error, it shows an error alert.
     */
    @FXML
    private void onChooseButton() {
        // Validate input fields
        String errorMessage = validateInput();
        if (errorMessage != null) {
            // Show error alert if validation fails
            showErrorAlert("Error", "Not all fields filled", errorMessage);
            return;
        }

        // Create a Question object from the inputs
        Question question = createQuestionFromInputs();
        question.setQuestion_id(questionId);

        // Update the question in the database
        SQLiteDatabaseConnection.questionRepository.update(question);

        // Compare the keywords and add/remove connections accordingly
        compareAndAddOrRemove(question);

        // Switch to the question edit screen
        switchScene(questionEdit, true);
    }

    /**
     * Compares the keywords between the current state and the initial state,
     * and adds or removes connections accordingly in the database.
     *
     * @param question The Question object representing the question being edited.
     */
    private void compareAndAddOrRemove(Question question){
        // Remove connections for keywords that are not selected anymore
        for (Keyword keyword1 : startState) {
            boolean keywordFound = false;
            for (Keyword keyword2 : selectedKeywords) {
                if (keyword1.getKeyword_text().equals(keyword2.getKeyword_text()))
                    keywordFound = true;
            }
            if (!keywordFound) {
                SQLiteDatabaseConnection.keywordRepository.removeConnection(keyword1, question);
            }
        }

        // Add connections for new keywords that are selected
        for (Keyword keyword1 : selectedKeywords) {
            boolean keywordNotFound = true;
            for (Keyword keyword2 : startState) {
                if (keyword1.getKeyword_text().equals(keyword2.getKeyword_text()))
                    keywordNotFound = false;
            }
            if (keywordNotFound) {
                SQLiteDatabaseConnection.keywordRepository.addConnection(keyword1,question);
            }
        }
    }

    /**
     * Validates the input fields before submitting the question for editing.
     * Checks if the category is selected, if multiple choice is selected but at least one answer is not filled out,
     * and if the question is empty.
     *
     * @return A string containing an error message if validation fails, or null if validation passes.
     */
    private String validateInput() {
        if (selectedCategory == null) {
            return "Category needs to be selected.";
        }

        if (chooseMultipleChoice.isSelected() && checkIfEmptyAnswers()) {
            return "You selected multiple choice but at least one answer is not filled out.";
        }

        if (checkIfQuestionIsEmpty()) {
            return "Question needs to be filled out.";
        }

        return null;
    }

    /**
     * Creates a Question object using the inputs provided by the user in the GUI.
     *
     * @return A Question object initialized with the values from the input fields.
     */
    private Question createQuestionFromInputs() {
        return new Question(
                selectedCategory,                           // Selected category for the question
                (int) chooseDifficulty.getValue(),          // Difficulty level of the question
                choosePoints.getValue().floatValue(),       // Points awarded for the question
                chooseQuestion.getText(),                   // Text of the question
                chooseMultipleChoice.isSelected() ? 1 : 0,  // Indicator if the question is multiple choice
                "",                                         // Placeholder for additional information (unused)
                chooseRemarks.getText(),                    // Remarks or additional information for the question
                answersToDatabaseString(),                  // Answers provided for the question
                selectedKeywords,                           // Keywords associated with the question
                new ArrayList<>()                           // Placeholder for additional attributes (unused)
        );
    }

    /**
     * Converts the answers provided in the multiple choice question to a formatted string
     * for storage in the database.
     *
     * @return A string representation of the answers separated by newline characters.
     */
    private String answersToDatabaseString() {
        StringBuilder result = new StringBuilder();
        if (chooseMultipleChoice.isSelected()) {
            for (TextArea answerTextArea : answers) {
                result.append(answerTextArea.getText()).append("\n"); // Appending each answer followed by a newline
            }
        }
        return result.toString(); // Returning the formatted string
    }

    /**
     * Checks if any of the answers provided in the multiple choice question are empty.
     *
     * @return {@code true} if at least one answer is empty, {@code false} otherwise.
     */
    private boolean checkIfEmptyAnswers() {
        return answers.stream().anyMatch(answerTextArea -> answerTextArea.getText().isEmpty()); // Checking if any answer text area is empty
    }

    /**
     * Checks if the question field is empty.
     *
     * @return {@code true} if the question field is empty, {@code false} otherwise.
     */
    private boolean checkIfQuestionIsEmpty() {
        return chooseQuestion.getText().isEmpty(); // Checking if the question text area is empty
    }
}
