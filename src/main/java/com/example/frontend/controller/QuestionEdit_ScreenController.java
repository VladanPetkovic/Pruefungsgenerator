package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import com.example.frontend.MainApp;
import com.example.frontend.components.CustomDoubleSpinner;

import com.example.frontend.components.PicturePickerController;
import com.example.frontend.modals.ConfirmDeletion_ScreenController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class QuestionEdit_ScreenController extends ScreenController implements Initializable {
    @FXML
    private VBox vbox_filteredQuestionsPreview;
    @FXML
    public Label updated_at_label;
    @FXML
    public Label created_at_label;
    @FXML
    private MenuButton chooseCategory;
    @FXML
    private VBox customDoubleSpinnerPlaceholder;
    private CustomDoubleSpinner choosePoints;
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
    private MenuButton keywordMenuButton;
    @FXML
    private TextField keywordTextField;
    @FXML
    private Button addKeywordBtn;

    private ArrayList<TextArea> answers = new ArrayList<>();
    private ArrayList<Answer> originalAnswers = new ArrayList<>();
    private Question selectedQuestion = new Question();
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();
    private Category selectedCategory = null;

    @FXML
    private VBox picturePickerPlaceholder;
    private PicturePickerController picturePickerController;

    /**
     * Initializes the Question Edit screen.
     *
     * @param url            The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // you can't scroll when no question has been selected
        chooseScrollPane.setMouseTransparent(true);

        // display filtered questions
        SharedData.getFilteredQuestions().addListener((ListChangeListener<Question>) change -> {
            showFilteredQuestions(SharedData.getFilteredQuestions());
        });

        // Retrieves all categories for the selected course from the database.
        ArrayList<Category> categories = SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getId());
        ArrayList<Keyword> keywords = SQLiteDatabaseConnection.keywordRepository.getAllOneCourse(SharedData.getSelectedCourse().getId());
        initializeKeywords(keywordTextField, keywords, addKeywordBtn);

        // Displays an error alert if no categories are found for the selected course.
        if (categories.isEmpty()) {
            SharedData.setOperation(Message.NO_CATEGORIES_FOR_SELECTED_COURSE);
        }

        // Fills the category menu with the retrieved categories.
        fillCategoryWithCategories(categories);

        for (Keyword keyword : keywords) {
            fillKeywordMenuButtonWithKeyword(keyword, selectedKeywords, keywordsHBox, keywordMenuButton);
        }

        choosePoints = new CustomDoubleSpinner();
        choosePoints.getStyleClass().add("automatic_create_spinner");

        customDoubleSpinnerPlaceholder.getChildren().add(choosePoints);

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("components/picture_picker.fxml"));
            VBox picturePicker = loader.load();
            picturePickerController = loader.getController();
            picturePickerPlaceholder.getChildren().add(picturePicker);
            picturePickerController.setTextArea(chooseQuestion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to display filtered questions in filter window
     *
     * @param questions The ObservableList of questions to show in the preview window.
     */
    private void showFilteredQuestions(ObservableList<Question> questions) {
        double spacing = 20.0;

        // Check if the list of questions is empty.
        if (questions.isEmpty()) {
            this.vbox_filteredQuestionsPreview.getChildren().clear();
            return;
        }

        // Clear the existing content in the preview VBox.
        this.vbox_filteredQuestionsPreview.getChildren().clear();

        for (Question question : questions) {
            VBox questionVbox = createQuestionVBox(question);
            questionVbox.getStyleClass().add("filter_question_preview_vbox");

            // add question to preview-box
            this.vbox_filteredQuestionsPreview.getChildren().add(questionVbox);
            // display the clicked question in the test_preview_pane
            displayClickedQuestion(questionVbox, question);
            // set spacing
            this.vbox_filteredQuestionsPreview.setSpacing(spacing);
        }
    }

    /**
     * This function displays the question, that was clicked in the filter-preview.
     * Thus, it sets all event-handlers for the question for editing.
     *
     * @param questionVbox    The VBox containing the question details.
     * @param clickedQuestion The question object associated with the VBox.
     */
    @FXML
    private void displayClickedQuestion(VBox questionVbox, Question clickedQuestion) {
        questionVbox.setOnMouseClicked(event -> {
            // save the value of the clicked question
            selectedQuestion = clickedQuestion;
            SharedData.setSelectedEditQuestion(selectedQuestion);

            // Make the scroll pane transparent to allow interaction with underlying elements.
            chooseScrollPane.setMouseTransparent(false);

            // Set category, difficulty, and points to the values of the selected question.
            chooseCategory.setText(clickedQuestion.getCategory().getName());
            selectedCategory = clickedQuestion.getCategory();
            chooseDifficulty.setValue(clickedQuestion.getDifficulty());
            choosePoints.getValueFactory().setValue((double) clickedQuestion.getPoints());

            initSelectedQuestionType(clickedQuestion);

            // Set the question text and remarks to the values of the selected question.
            chooseQuestion.setText(clickedQuestion.getQuestion());
            chooseRemarks.setText(clickedQuestion.getRemark());

            // Clear and set up the keyword UI elements based on the selected question.
            selectedKeywords.clear();
            keywordsHBox.getChildren().clear();

            for (Keyword k : clickedQuestion.getKeywords()) {
                if (k.getKeyword() != null && !containsKeyword(k, selectedKeywords)) {
                    selectedKeywords.add(k);
                    Button b = createButton(k.getKeyword() + " X");
                    b.setOnAction(e -> {
                        keywordsHBox.getChildren().remove(b);
                        selectedKeywords.remove(k);
                    });
                    keywordsHBox.getChildren().add(b);
                }
            }

            picturePickerController.addPreExistingImages(clickedQuestion.getImages());

            initTimeStamps(clickedQuestion);
        });
    }

    /**
     * Fills the category menu with available categories.
     * Iterates through the list of categories and creates a menu item for each category.
     * Associates an event handler with each menu item to handle category selection.
     */
    private void fillCategoryWithCategories(ArrayList<Category> categories) {
        for (Category category : categories) {
            MenuItem menuItem = new MenuItem(category.getName());
            menuItem.setOnAction(event -> {
                selectedCategory = category;
                // setting the category of the clickedQuestion
                chooseCategory.setText(category.getName());
            });
            chooseCategory.getItems().add(menuItem);
        }
    }

    /**
     * This function initializes the questionType and answers, when a question is selected for editing.
     *
     * @param selectedQuestion the question, that was selected
     */
    private void initSelectedQuestionType(Question selectedQuestion) {
        multipleChoiceAnswerVBox.getChildren().clear();
        chooseAnswerTextArea.setText("");
        questionTypeMenuButtonEdit.textProperty().setValue(selectedQuestion.getType().getName());

        // question = MC
        if (selectedQuestion.getType().checkQuestionType(Type.MULTIPLE_CHOICE)) {
            for (Answer answer : selectedQuestion.getAnswers()) {
                addNewAnswer(answer);
            }
            multipleChoiceVBox.setVisible(true);
            chooseAnswerTextArea.setDisable(true);
        } else if (selectedQuestion.getType().getType() == Type.TRUE_FALSE) {
            multipleChoiceVBox.setVisible(false);
            chooseAnswerTextArea.setDisable(true);
        } else {
            multipleChoiceVBox.setVisible(false);
            chooseAnswerTextArea.setDisable(false);
            chooseAnswerTextArea.setText(selectedQuestion.getAnswersAsString());
            // update the answer from the selectedQuestion
            chooseAnswerTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
                selectedQuestion.getAnswers().get(0).setAnswer(newValue);
            });
        }
    }

    /**
     * This function displays the timestamps of one question, when clicked to be edited.
     *
     * @param question The question, that has been selected.
     */
    private void initTimeStamps(Question question) {
        // setting the timestamps
        if (question.getCreated_at() == null) {
            created_at_label.setText("Created at: ");
        } else {
            created_at_label.setText("Created at: " + question.getTimeStampFormatted(question.getCreated_at()));
        }
        if (question.getUpdated_at() == null) {
            updated_at_label.setText("Updated at: ");
        } else {
            updated_at_label.setText("Updated at: " + question.getTimeStampFormatted(question.getUpdated_at()));
        }
    }

    public void onAddNewAnswerBtnClick(ActionEvent actionEvent) {
        addNewAnswer(new Answer());
    }

    /**
     * This function adds a new answer. Used for initialization (with real answer) and when the addNewAnswerBtn is clicked.
     *
     * @param answer Can be new or given from a selectedQuestion
     */
    private void addNewAnswer(Answer answer) {
        HBox hBoxAnswerRemove = new HBox();
        TextArea textAreaAnswer = new TextArea(answer.getAnswer());
        answers.add(textAreaAnswer);
        originalAnswers.add(new Answer(answer));    // copying the old answer

        // add new answers to the selectedQuestion
        if (answer.getId() == 0) {
            selectedQuestion.getAnswers().add(answer);
        }
        // update the answer from the selectedQuestion
        textAreaAnswer.textProperty().addListener((observable, oldValue, newValue) -> {
            answer.setAnswer(newValue);
        });

        Button buttonRemove = createButton("X");
        buttonRemove.setOnAction(e -> {
            answers.remove(textAreaAnswer);
            // remove from the selectedQuestion
            selectedQuestion.getAnswers().remove(answer);
            multipleChoiceAnswerVBox.getChildren().remove(hBoxAnswerRemove);
        });
        hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
        multipleChoiceAnswerVBox.getChildren().add(hBoxAnswerRemove);
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
            // display error message
            SharedData.setOperation(errorMessage, true);
            return;
        }

        // Create a Question object from the inputs
        Question question = createQuestionFromInputs();

        // Update the question in the database
        SQLiteDatabaseConnection.questionRepository.update(question);

        // Compare the keywords and add/remove connections accordingly
        compareKeywords(question);

        //Compare Images
        compareImages(question);

        // compare answers and add/remove connections accordingly
        if (selectedQuestion.getType().getType() == Type.MULTIPLE_CHOICE) {
            compareAnswers();
        } else {
            SQLiteDatabaseConnection.ANSWER_REPOSITORY.removeConnection(selectedQuestion.getAnswers().get(0), selectedQuestion.getId());    // not MC --> we have only one answer
            SQLiteDatabaseConnection.ANSWER_REPOSITORY.add(selectedQuestion.getAnswers(), selectedQuestion.getId());
        }

        switchScene(questionEdit, true);
    }

    private void compareImages(Question question) {
        for (Image image : selectedQuestion.getImages()) {
            boolean imageFound = false;
            for (Image image2 : picturePickerController.getImages()) {
                if (image.getName().equals(image2.getName())) {
                    imageFound = true;
                }
            }
            if (!imageFound) {
                SQLiteDatabaseConnection.imageRepository.removeConnection(image, question);
            }
        }

        ArrayList<Image> images = new ArrayList<>();

        for (Image image : picturePickerController.getImages()) {
            boolean imageIsNew = true;
            for (Image image2 : selectedQuestion.getImages()) {
                if (image.getName().equals(image2.getName())) {
                    imageIsNew = false;
                }
            }
            if (imageIsNew) {
                images.add(image);
            }
        }

        question.setImages(images);

        Image.createImages(question, question.getId());
    }

    /**
     * Compares the keywords between the current state and the initial state,
     * and adds or removes connections accordingly in the database.
     *
     * @param question The Question object representing the question being edited.
     */
    private void compareKeywords(Question question) {
        // Remove connections for keywords that are not selected anymore
        for (Keyword keyword1 : selectedQuestion.getKeywords()) {
            boolean keywordFound = false;
            for (Keyword keyword2 : selectedKeywords) {
                if (keyword1.getKeyword().equals(keyword2.getKeyword())) {
                    keywordFound = true;
                }
            }
            if (!keywordFound) {
                SQLiteDatabaseConnection.keywordRepository.removeConnection(keyword1, question);
            }
        }

        // Add connections for new keywords that are selected
        for (Keyword keyword1 : selectedKeywords) {
            boolean keywordNotFound = true;
            for (Keyword keyword2 : selectedQuestion.getKeywords()) {
                if (keyword1.getKeyword().equals(keyword2.getKeyword())) {
                    keywordNotFound = false;
                }
            }
            if (keywordNotFound) {
                SQLiteDatabaseConnection.keywordRepository.addConnection(keyword1, question.getId());
            }
        }
    }

    private void compareAnswers() {
        boolean createAnswers = false;

        // remove connections for answers, that are deleted
        for (Answer originalAnswer : originalAnswers) {
            boolean answerFound = false;
            for (Answer updatedAnswer : selectedQuestion.getAnswers()) {
                if (updatedAnswer.getId() == originalAnswer.getId()) {
                    answerFound = true;
                    // create new answer, when string has changed
                    // --> we create updated answers, because one answer can have multiple questions
                    if (!Objects.equals(updatedAnswer.getAnswer(), originalAnswer.getAnswer())) {
                        createAnswers = true;
                        SQLiteDatabaseConnection.ANSWER_REPOSITORY.removeConnection(originalAnswer, selectedQuestion.getId());
                    }
                }
            }
            if (!answerFound) {
                SQLiteDatabaseConnection.ANSWER_REPOSITORY.removeConnection(originalAnswer, selectedQuestion.getId());
            }
        }

        // add connections for new answers
        for (Answer updatedAnswer : selectedQuestion.getAnswers()) {
            boolean answerNotFound = true;
            for (Answer originalAnswer : originalAnswers) {
                if (updatedAnswer.getId() == originalAnswer.getId()) {
                    answerNotFound = false;
                }
            }
            if (answerNotFound) {
                createAnswers = true;
            }
        }

        // create new and updated answers --> we do not insert existing answers (done with sql)
        if (createAnswers) {
            SQLiteDatabaseConnection.ANSWER_REPOSITORY.add(selectedQuestion.getAnswers(), selectedQuestion.getId());
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
        if (checkIfEmptyAnswers(questionTypeMenuButtonEdit, answers)) {
            return "You selected multiple choice, but at least one answer is not filled out.";
        }
        if (answers.size() < 2 && QuestionType.checkMultipleChoiceType(questionTypeMenuButtonEdit.getText())) {
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

    /**
     * Creates a Question object using the inputs provided by the user in the GUI.
     *
     * @return A Question object initialized with the values from the input fields.
     */
    private Question createQuestionFromInputs() {
        return new Question(
                selectedQuestion.getId(),
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
                picturePickerController.getImages()         // images
        );
    }

    /**
     * Checks if the question field is empty.
     *
     * @return {@code true} if the question field is empty, {@code false} otherwise.
     */
    private boolean checkIfQuestionIsEmpty() {
        return chooseQuestion.getText().isEmpty();
    }

    public void onAddKeywordBtnClick(ActionEvent actionEvent) {
        // TODO: maybe extract this duplicate method to ScreenController base class --> duplicate in questionCreate
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

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        Stage confirmStage = new Stage();
        Screen<ConfirmDeletion_ScreenController> confirm_modal = new Screen<>("modals/confirm_deletion.fxml");
        confirmStage.setHeight(400);
        confirmStage.setWidth(600);
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.setScene(confirm_modal.scene);

        confirmStage.setOnHidden((WindowEvent event) -> {
            // question was deleted
            if (SharedData.getSelectedEditQuestion().getId() == 0) {
                switchScene(questionEdit, true);
            }
        });

        confirmStage.show();
    }
}
