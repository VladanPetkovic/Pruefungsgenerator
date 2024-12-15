package com.example.application.frontend.controller;

import com.example.application.backend.db.models.*;
import com.example.application.backend.app.SharedData;
import com.example.application.MainApp;
import com.example.application.backend.db.services.*;

import com.example.application.frontend.components.EditorScreenController;
import com.example.application.frontend.modals.ModalOpener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Scope("prototype")
public class QuestionEdit_ScreenController extends ScreenController {
    private final QuestionService questionService;
    private final KeywordService keywordService;
    private final CategoryService categoryService;
    private final AnswerService answerService;
    private final ImageService imageService;
    public ComboBox<String> keywordComboButton;
    public ComboBox<String> categoryComboBox;
    public VBox editorParentVBox;
    @FXML
    private VBox vbox_filteredQuestionsPreview;
    @FXML
    public Label updated_at_label;
    @FXML
    public Label created_at_label;
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
    public Spinner<Double> pointsSpinner;
    @FXML
    private TextArea chooseRemarks;
    @FXML
    private ScrollPane chooseScrollPane;
    @FXML
    private HBox keywordsHBox;

    private ArrayList<TextArea> answers = new ArrayList<>();
    private Question selectedQuestion = new Question();
    private Set<Keyword> selectedKeywords = new HashSet<>();
    private EditorScreenController editorScreenController;

    public QuestionEdit_ScreenController(KeywordService keywordService, CategoryService categoryService, AnswerService answerService, QuestionService questionService, ImageService imageService) {
        this.questionService = questionService;
        this.keywordService = keywordService;
        this.categoryService = categoryService;
        this.answerService = answerService;
        this.imageService = imageService;
    }

    /**
     * Initializes the Question Edit screen.
     */
    @FXML
    public void initialize() throws IOException {
        // you can't scroll when no question has been selected
        chooseScrollPane.setMouseTransparent(true);

        // initialize the editor
        initEditor();

        // display filtered questions
        SharedData.getFilteredQuestions().addListener((ListChangeListener<Question>) change -> {
            showFilteredQuestions(SharedData.getFilteredQuestions());
        });

        // Retrieves all categories for the selected course from the database.
        List<Category> categories = categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId());
        List<Keyword> keywords = keywordService.getAllByCourseId(SharedData.getSelectedCourse().getId());
        initKeywordComboBox(keywords, selectedKeywords, keywordsHBox, keywordComboButton);
        initDoubleSpinner(pointsSpinner, 1, 10, 1, 0.5);

        // Fills the category menu with the retrieved categories.
        initCategoryComboBox(categoryComboBox, categories);

        //if user clicked on the edit button in manualCreate Screen
        if (SharedData.getQuestionToEdit() != null) {
            System.out.println("QUESTION IS HERE");
            System.out.println("Question questiontext: " + SharedData.getQuestionToEdit().getQuestion());
            displayQuestionFromTestCreation();
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
            displayQuestion(selectedQuestion);
            editorScreenController.addPreExistingImages(clickedQuestion.getImages());
        });
    }

    /**
     * Currenlty we need to add the editor dynamically - even if it is not needed.
     * The reason for this: We can't access the controller, if we include the fxml in the parent.
     */
    private void initEditor() throws IOException {
        FXMLLoader loader = FXMLDependencyInjection.getLoader("components/editor.fxml", MainApp.resourceBundle);
        VBox editor = loader.load();

        // get the controller for the loaded component
        editorScreenController = loader.getController();
        editorParentVBox.getChildren().add(editor);
    }

    /**
     * This function initializes the questionType and answers, when a question is selected for editing.
     *
     * @param selectedQuestion the question, that was selected
     */
    private void initSelectedQuestionType(Question selectedQuestion) {
        multipleChoiceAnswerVBox.getChildren().clear();
        chooseAnswerTextArea.setText("");
        questionTypeMenuButtonEdit.textProperty().setValue(selectedQuestion.getType());

        // question = MC
        if (Type.isMultipleChoice(selectedQuestion.getType())) {
            for (Answer answer : selectedQuestion.getAnswers()) {
                addNewAnswer(answer);
            }
            multipleChoiceVBox.setVisible(true);
            chooseAnswerTextArea.setDisable(true);
        } else if (Type.isTrueFalse(selectedQuestion.getType())) {
            multipleChoiceVBox.setVisible(false);
            chooseAnswerTextArea.setDisable(true);
        } else {
            multipleChoiceVBox.setVisible(false);
            chooseAnswerTextArea.setDisable(false);
            chooseAnswerTextArea.setText(selectedQuestion.getAnswersAsString());
            // update the answer from the selectedQuestion
            chooseAnswerTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
                selectedQuestion.getAnswers().iterator().next().setAnswer(newValue);
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
        if (question.getCreatedAt() == null) {
            created_at_label.setText(MainApp.resourceBundle.getString("created_at"));
        } else {
            String createdAt = MainApp.resourceBundle.getString("created_at") + " " + question.getTimeStampFormatted(question.getCreatedAt());
            created_at_label.setText(createdAt);
        }
        if (question.getUpdatedAt() == null) {
            updated_at_label.setText(MainApp.resourceBundle.getString("updated_at"));
        } else {
            String updatedAt = MainApp.resourceBundle.getString("updated_at") + " " + question.getTimeStampFormatted(question.getUpdatedAt());
            updated_at_label.setText(updatedAt);
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

        // add new answers to the selectedQuestion
        if (answer.getId() == null) {
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
    private void onChooseButton() throws IOException {
        // Validate input fields
        String errorMessage = validateInput();
        if (errorMessage != null) {
            // display error message
            SharedData.setOperation(errorMessage, true);
            return;
        }

        // Create a Question object from the inputs
        Question question = createQuestionFromInputs();

        // remove/add answers
        answerService.removeAllByQuestionId(question.getId());
        answerService.addAnswers(question.getId(), question.getAnswers());

        // remove/add images --> not very performance-friendly...
        imageService.removeAllByQuestionId(question.getId());
        imageService.addImages(question.getId(), question.getImages());

        // Update the question in the database
        questionService.update(question);
        SwitchScene.switchScene(SwitchScene.EDIT_QUESTION);
    }

    /**
     * Validates the input fields before submitting the question for editing.
     * Checks if the category is selected, if multiple choice is selected but at least one answer is not filled out,
     * and if the question is empty.
     *
     * @return A string containing an error message if validation fails, or null if validation passes.
     */
    private String validateInput() {
        if (categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            return MainApp.resourceBundle.getString("error_message_no_category");
        }
        if (checkIfEmptyAnswers(questionTypeMenuButtonEdit, answers)) {
            return MainApp.resourceBundle.getString("error_message_mc_no_answer");
        }
        if (answers.size() < 2 && Type.isMultipleChoice(questionTypeMenuButtonEdit.getText())) {
            return MainApp.resourceBundle.getString("error_message_mc_min_two_answers");
        }
        if (editorScreenController.editor.getHtmlText().isEmpty()) {
            return MainApp.resourceBundle.getString("error_message_question_not_set");
        }
        if (editorScreenController.invalidSyntax()) {
            return MainApp.resourceBundle.getString("error_message_image_not_included");
        }
        return null;
    }

    /**
     * Creates a Question object using the inputs provided by the user in the GUI.
     *
     * @return A Question object initialized with the values from the input fields.
     */
    private Question createQuestionFromInputs() {
        Category selectedCategory = categoryService.getByName(categoryComboBox.getSelectionModel().getSelectedItem(), SharedData.getSelectedCourse());

        return new Question(
                selectedQuestion.getId(),
                selectedCategory,
                (int) chooseDifficulty.getValue(),
                pointsSpinner.getValue().floatValue(),
                editorScreenController.editor.getHtmlText(),
                null,                                   // type cannot be changed
                chooseRemarks.getText(),
                null,                               // created_at cannot be changed
                LocalDateTime.now(),  // updated_at
                getAnswersSet(Type.valueOf(selectedQuestion.getType()), chooseAnswerTextArea, this.answers),
                editorScreenController.getImages(),         // images
                selectedKeywords

        );
    }

    public void onAddKeywordBtnClick(ActionEvent actionEvent) {
        ModalOpener modalOpener = new ModalOpener();
        Stage addKeywordStage = modalOpener.openModal(ModalOpener.ADD_KEYWORD);

        // initialize keywords-comboBox when the modal closes
        addKeywordStage.setOnHidden((WindowEvent event) -> {
            List<Keyword> keywords = keywordService.getAllByCourseId(SharedData.getSelectedCourse().getId());
            initKeywordComboBox(keywords, selectedKeywords, keywordsHBox, keywordComboButton);
        });
    }

    public void onAddCategoryBtnClick(ActionEvent actionEvent) {
        ModalOpener modalOpener = new ModalOpener();
        Stage addCategoryStage = modalOpener.openModal(ModalOpener.ADD_CATEGORY);

        addCategoryStage.setOnHidden((WindowEvent event) -> {
            initCategoryComboBox(categoryComboBox, categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId()));
        });
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        ModalOpener modalOpener = new ModalOpener();
        Stage confirmStage = modalOpener.openModal(ModalOpener.CONFIRM_DELETION);

        confirmStage.setOnHidden((WindowEvent event) -> {
            // question was deleted
            if (SharedData.getSelectedEditQuestion().getId() == null) {
                try {
                    SwitchScene.switchScene(SwitchScene.EDIT_QUESTION);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * used to display the Question when the editButton in the ManualCreateScreen is clicked
     */
    private void displayQuestionFromTestCreation() {
        selectedQuestion = SharedData.getQuestionToEdit();
        displayQuestion(selectedQuestion);

        editorScreenController.addPreExistingImages(selectedQuestion.getImages());
        initTimeStamps(selectedQuestion);
        SharedData.setQuestionToEdit(null);
    }

    private void displayQuestion(Question questionToShow) {
        try {
            SharedData.setSelectedEditQuestion(questionToShow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Make the scroll pane transparent to allow interaction with underlying elements.
        chooseScrollPane.setMouseTransparent(false);

        // set all fields
        categoryComboBox.getSelectionModel().select(questionToShow.getCategory().getName());
        chooseDifficulty.setValue(questionToShow.getDifficulty());
        pointsSpinner.getValueFactory().setValue((double) questionToShow.getPoints());
        initSelectedQuestionType(questionToShow);
        editorScreenController.editor.setHtmlText(questionToShow.getQuestion());
        chooseRemarks.setText(questionToShow.getRemark());

        // and keywords
        selectedKeywords.clear();
        keywordsHBox.getChildren().clear();

        for (Keyword k : questionToShow.getKeywords()) {
            if (k.getKeyword() != null && !selectedKeywords.contains(k)) {
                selectedKeywords.add(k);
                Button b = createButton(k.getKeyword() + " X");
                b.setOnAction(e -> {
                    keywordsHBox.getChildren().remove(b);
                    selectedKeywords.remove(k);
                });
                keywordsHBox.getChildren().add(b);
            }
        }
        initTimeStamps(questionToShow);
    }
}