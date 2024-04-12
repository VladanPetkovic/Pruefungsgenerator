package com.example.frontend.controller;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Timestamp;
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
    public MenuButton questionTypeMenuButton;
    private ArrayList<TextArea> answers = new ArrayList<>();
    @FXML
    private VBox previewVBox;
    @FXML
    private MenuButton chooseCategory;
    @FXML
    private Spinner<Double> choosePoints;
    @FXML
    public VBox multipleChoiceAnswerVBox;
    @FXML
    public VBox multipleChoiceVBox;
    @FXML
    public MenuButton questionTypeMenuButtonEdit;
    @FXML
    public TextArea chooseAnswerTextArea;
    @FXML
    private Slider chooseDifficulty;
    @FXML
    private TextArea chooseQuestion;
    @FXML
    private TextArea chooseRemarks;
    @FXML
    private ScrollPane chooseScrollPane;
    @FXML
    private HBox keywordsHBox;
    @FXML
    private MenuButton chooseKeywords;

    private ArrayList<Keyword> keywords;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();
    private ArrayList<Keyword> startState = new ArrayList<>();
    private ArrayList<Category> categories;
    private Category selectedCategory = null;
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
        label_selectedCourse.setText(SharedData.getSelectedCourse().getName());

        // Retrieves all categories for the selected course from the database.
        categories = SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getId());

        // Displays an error alert if no categories are found for the selected course.
        if (categories.size() == 0)
            showErrorAlert("Error", "No categories found", "Please create categories first before accessing upload question");

        // Fills the category menu with the retrieved categories.
        fillCategoryWithCategories();

        keywords = SQLiteDatabaseConnection.keywordRepository.getAllOneCourse(SharedData.getSelectedCourse().getId());
        fillKeywordWithKeywords();

        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1, 0.5);
        choosePoints.setValueFactory(valueFactory);

        getDifficultyFromSlider(this.difficultySlider);
        getPointsFromSlider(this.pointsSlider);

        initializeKeywords(keywordTextField, keywords);
        initializeCategories(categoryTextField, categories);
        initializeQuestions(questionTextField);
        initializeMenuButton(questionTypeMenuButton, true);
    }

    /**
     * Displays an error alert dialog.
     *
     * @param title       The title of the error alert dialog.
     * @param headerText  The header text of the error alert dialog.
     * @param contentText The content text of the error alert dialog.
     */
    public void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
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
                SharedData.getSelectedCourse().getName());

        // Displays the filtered questions in the console.
        printQuestions(result);
    }

    /**
     * Creates a filter question based on the selected filter criteria.
     * Retrieves the category name, keyword text, question text and multiple choice checkbox status.
     * Sets the category filter, keyword filter, question filter, points and difficulty filter, and questionType.
     * @return The constructed filter question.
     */
    private Question createFilterQuestion() {
        // Creates a new instance of the filter question.
        Question filterQuestion = new Question();

        String categoryName = categoryTextField.getText().trim();
        String keywordText = keywordTextField.getText().trim();
        String questionText = questionTextField.getText();
        String questionTypeString = questionTypeMenuButton.getText();

        setCategoryFilter(categoryName, filterQuestion);

        setKeywordFilter(keywordText, filterQuestion);

        setQuestionFilter(questionText, filterQuestion);

        setPointsAndDifficultyFilter(filterQuestion);

        // set questionType value
        if (QuestionType.checkExistingType(questionTypeString)) {
            QuestionType filterQuestionType = new QuestionType(questionTypeString);
            filterQuestion.setType(filterQuestionType);
        }

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

        filterQuestion.setQuestion(questionText);
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
            MenuItem menuItem = createMenuItem(category.getName());
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
            MenuItem menuItem = createMenuItem(keyword.getKeyword());
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
        chooseCategory.setText(category.getName());
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
            Button button = createButton(keyword.getKeyword() + " X");
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
            if (keyword.getId() == k.getId())
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
     * This function initializes the questionType and answers, when a question is selected for editing.
     * @param selectedQuestion the question, that was selected
     */
    private void initSelectedQuestionType(Question selectedQuestion) {
        // question = MC
        if (selectedQuestion.getType().checkQuestionType(Type.MULTIPLE_CHOICE)) {
            questionTypeMenuButton.textProperty().setValue(Type.MULTIPLE_CHOICE.name());
            // TODO: we are using this already in ScreenController - refactor to dont duplicate code (maybe)
            for (Answer answer : selectedQuestion.getAnswers()) {
                HBox hBoxAnswerRemove = new HBox();
                TextArea textAreaAnswer = new TextArea(answer.getAnswer());
                answers.add(textAreaAnswer);
                Button buttonRemove = createButton("X");
                buttonRemove.setOnAction(e -> {
                    answers.remove(textAreaAnswer);
                    multipleChoiceAnswerVBox.getChildren().remove(hBoxAnswerRemove);
                });
                hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
                multipleChoiceAnswerVBox.getChildren().add(hBoxAnswerRemove);
            }
            multipleChoiceVBox.setVisible(true);
            chooseAnswerTextArea.setDisable(true);
        } else {
            multipleChoiceVBox.setVisible(false);
            chooseAnswerTextArea.setDisable(false);
            chooseAnswerTextArea.setText(selectedQuestion.getAnswersAsString());
        }
    }

    public void onAddNewAnswerBtnClick(ActionEvent actionEvent) {
        addMultipleChoiceAnswerBtnClicked(answers, multipleChoiceAnswerVBox);
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
            Logger.log(getClass().getName(), "No questions found", LogLevel.INFO);
            previewVBox.getChildren().clear();
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
            chooseCategory.setText(question.getCategory().getName());
            selectedCategory = question.getCategory();
            chooseDifficulty.setValue(question.getDifficulty());
            choosePoints.getValueFactory().setValue((double) question.getPoints());

            initSelectedQuestionType(question);

            // Set the question text and remarks to the values of the selected question.
            chooseQuestion.setText(question.getQuestion());
            chooseRemarks.setText(question.getRemark());

            // Clear and set up the keyword UI elements based on the selected question.
            selectedKeywords.clear();
            startState.clear();
            keywordsHBox.getChildren().clear();

            for (Keyword k : question.getKeywords()) {
                if(k.getKeyword() != null) {
                    startState.add(k);
                    selectedKeywords.add(k);
                    Button b = createButton(k.getKeyword() + " X");
                    b.setOnAction(e -> {
                        keywordsHBox.getChildren().remove(b);
                        selectedKeywords.remove(k);
                    });
                    keywordsHBox.getChildren().add(b);
                }
            }

            // Set the question ID.
            questionId = question.getId();
        });
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
        question.setId(questionId);

        // Update the question in the database
        SQLiteDatabaseConnection.questionRepository.update(question);

        // Compare the keywords and add/remove connections accordingly
        compareAndAddOrRemove(question);

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
                if (keyword1.getKeyword().equals(keyword2.getKeyword()))
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
                if (keyword1.getKeyword().equals(keyword2.getKeyword()))
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
        if (QuestionType.checkMultipleChoiceType(questionTypeMenuButtonEdit.getText()) && checkIfEmptyAnswers()) {
            return "You selected multiple choice but at least one answer is not filled out.";
        }
        if (checkIfQuestionIsEmpty()) {
            return "Question needs to be filled out.";
        }

        return null;
    }

    /**
     * Creates a Question object using the inputs provided by the user in the GUI.
     * @return A Question object initialized with the values from the input fields.
     */
    private Question createQuestionFromInputs() {
        return new Question(
                selectedCategory,
                (int) chooseDifficulty.getValue(),
                choosePoints.getValue().floatValue(),
                chooseQuestion.getText(),
                null,                                  // questionType cannot be changed
                chooseRemarks.getText(),
                null,                             // created_at cannot be changed
                new Timestamp(System.currentTimeMillis()),  // updated_at
                getAnswerArrayList(Type.OPEN, chooseAnswerTextArea, this.answers),
                selectedKeywords,
                new ArrayList<>()                           // images // TODO: implement this here
        );
    }

    /**
     * Checks if any of the answers provided in the multiple choice question are empty.
     * @return {@code true} if at least one answer is empty, {@code false} otherwise.
     */
    private boolean checkIfEmptyAnswers() {
        return answers.stream().anyMatch(answerTextArea -> answerTextArea.getText().isEmpty());
    }

    /**
     * Checks if the question field is empty.
     * @return {@code true} if the question field is empty, {@code false} otherwise.
     */
    private boolean checkIfQuestionIsEmpty() {
        return chooseQuestion.getText().isEmpty();
    }
}
