package com.example.application.frontend.controller;

import com.example.application.backend.db.models.*;
import com.example.application.backend.app.SharedData;
import com.example.application.MainApp;
import com.example.application.backend.db.services.*;

import com.example.application.frontend.components.EditorScreenController;
import com.example.application.frontend.modals.ModalOpener;
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
public class QuestionCreate_ScreenController extends ScreenController {
    private final CategoryService categoryService;
    private final KeywordService keywordService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ImageService imageService;
    public ComboBox<String> keywordComboButton;
    public ComboBox<String> categoryComboBox;
    public VBox editorParentVBox;
    @FXML
    private Slider difficulty;
    public Spinner<Double> pointsSpinner;
    @FXML
    public MenuButton questionTypeMenuButton;
    @FXML
    private VBox multipleChoiceAnswerVBox;
    @FXML
    private VBox multipleChoiceVBox;
    @FXML
    private TextArea remarks;
    @FXML
    private TextArea answerTextArea;
    @FXML
    private HBox keywordsHBox;
    private Set<Keyword> selectedKeywords = new HashSet<>();
    private ArrayList<TextArea> answers = new ArrayList<>();
    @FXML
    private EditorScreenController editorScreenController;

    public QuestionCreate_ScreenController(KeywordService keywordService, CategoryService categoryService, QuestionService questionService, AnswerService answerService, ImageService imageService) {
        this.categoryService = categoryService;
        this.keywordService = keywordService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.imageService = imageService;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is called once all FXML elements have been processed, but before the elements have been
     * rendered on the screen. It initializes the UI elements and retrieves necessary data from the database.
     */
    @FXML
    public void initialize() throws IOException {
        initCategoryComboBox(categoryComboBox, categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId()));
        List<Keyword> keywords = keywordService.getAllByCourseId(SharedData.getSelectedCourse().getId());
        initKeywordComboBox(keywords, selectedKeywords, keywordsHBox, keywordComboButton);
        initDoubleSpinner(pointsSpinner, 1, 10, 1, 0.5);

        initEditor();
        remarks.setText("");

        List<Type> questionTypes = Arrays.asList(Type.values());
        initializeMenuButton(questionTypeMenuButton, false, questionTypes, null);
        initQuestionTypeListener();
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
     * This function gets triggered when the questionTypeMenuButton is clicked on.
     */
    public void initQuestionTypeListener() {
        questionTypeMenuButton.textProperty().addListener((observable) -> {
            if (Type.checkType(questionTypeMenuButton.getText())) {
                switch (Type.getType(questionTypeMenuButton.getText())) {
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
    private void onActionUpload() throws IOException {
        // Check if all required fields are filled
        String s = checkIfFilled();
        // If any field is missing, display an error alert and return
        if (s != null) {
            SharedData.setOperation(s, true);
            return;
        }

        Category category = categoryService.getByName(categoryComboBox.getSelectionModel().getSelectedItem(), SharedData.getSelectedCourse());

        // Create a new Question object with the provided details
        Question q = new Question(
                category,
                (int) difficulty.getValue(),
                pointsSpinner.getValue().floatValue(),
                editorScreenController.editor.getHtmlText(),
                questionTypeMenuButton.getText(),
                remarks.getText(),
                LocalDateTime.now(),    // createdAt
                LocalDateTime.now(),    // updatedAt
                selectedKeywords
        );

        Question newQuestion = questionService.add(q);
        if (newQuestion.getId() != null) {
            answerService.addAnswers(newQuestion.getId(), getAnswersSet(Type.valueOf(questionTypeMenuButton.getText()), answerTextArea, this.answers));
            imageService.addImages(newQuestion.getId(), editorScreenController.getImages());
            SwitchScene.switchScene(SwitchScene.CREATE_QUESTION);
            SharedData.setOperation(Message.CREATE_QUESTION_SUCCESS_MESSAGE);
        }
    }

    /**
     * Checks if all required fields are filled out.
     *
     * @return An error message if any required field is not filled out, otherwise null.
     */
    private String checkIfFilled() {
        if (categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            return MainApp.resourceBundle.getString("error_message_no_category");
        }
        if (!Type.checkType(questionTypeMenuButton.getText())) {
            return MainApp.resourceBundle.getString("error_message_question_type_not_selected");
        }
        if (checkIfEmptyAnswers(questionTypeMenuButton, answers)) {
            return MainApp.resourceBundle.getString("error_message_mc_no_answer");
        }
        if (answers.size() < 2 && Type.isMultipleChoice(questionTypeMenuButton.getText())) {
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

    public void onAddCategoryBtnClick(ActionEvent actionEvent) {
        ModalOpener modalOpener = new ModalOpener();
        Stage addCategoryStage = modalOpener.openModal(ModalOpener.ADD_CATEGORY);

        addCategoryStage.setOnHidden((WindowEvent event) -> {
            initCategoryComboBox(categoryComboBox, categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId()));
        });
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
}
