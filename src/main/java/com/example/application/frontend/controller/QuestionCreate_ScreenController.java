package com.example.application.frontend.controller;

import com.example.application.backend.db.models.*;
import com.example.application.backend.app.SharedData;
import com.example.application.MainApp;
import com.example.application.backend.db.services.*;

import com.example.application.frontend.components.PicturePickerController;
import com.example.application.frontend.modals.ModalOpener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private TextArea question;
    @FXML
    private TextFlow questionPreview;
    @FXML
    private Button previewQuestion;
    @FXML
    private TextArea remarks;
    @FXML
    private TextArea answerTextArea;
    @FXML
    private HBox keywordsHBox;
    private Set<Keyword> selectedKeywords = new HashSet<>();
    private ArrayList<TextArea> answers = new ArrayList<>();

    @FXML
    private VBox picturePickerPlaceholder;
    private PicturePickerController picturePickerController;

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
    public void initialize() {
        initCategoryComboBox(categoryComboBox, categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId()));
        List<Keyword> keywords = keywordService.getAllByCourseId(SharedData.getSelectedCourse().getId());
        initKeywordComboBox(keywords, selectedKeywords, keywordsHBox, keywordComboButton);
        initDoubleSpinner(pointsSpinner, 1, 10, 1, 0.5);

        difficulty.setValue(5);

        question.setText("");
        question.textProperty().addListener((observableValue, s, t1) -> {
            previewQuestion.setVisible(previewQuestionShouldBeVisible());
        });
        remarks.setText("");

        List<Type> questionTypes = Arrays.asList(Type.values());
        initializeMenuButton(questionTypeMenuButton, false, questionTypes, null);
        initQuestionTypeListener();

        try {
            FXMLLoader loader = FXMLDependencyInjection.getLoader("components/picture_picker.fxml", MainApp.resourceBundle);
            VBox picturePicker = loader.load();
            picturePickerController = loader.getController();
            picturePickerPlaceholder.getChildren().add(picturePicker);
            picturePickerController.setTextArea(question);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean questionPreviewVisible = false;

    @FXML
    private void onActionPreviewQuestion() {
        if (!questionPreviewVisible) {
            question.setVisible(false);
            questionPreview.setVisible(true);
            questionPreview.getChildren().clear();
            parseAndDisplayContent();
            questionPreviewVisible = true;
            return;
        }
        question.setVisible(true);
        questionPreview.setVisible(false);
        questionPreviewVisible = false;
    }

    public void parseAndDisplayContent() {
        Pattern pattern = Pattern.compile("<img name=\"(.*?)\"/>");
        Matcher matcher = pattern.matcher(question.getText());
        int lastIndex = 0;
        while (matcher.find()) {
            String textBeforeImage = question.getText().substring(lastIndex, matcher.start());
            if (!textBeforeImage.isEmpty()) {
                questionPreview.getChildren().add(new Text(textBeforeImage));
            }
            String imageName = matcher.group(1);
            for (PicturePickerController.ButtonAndImage image : picturePickerController.buttonAndImages) {
                if (image.imageName.equals(imageName)) {
                    ImageView imageView = new ImageView(image.image);
                    questionPreview.getChildren().add(imageView);
                }
            }
            lastIndex = matcher.end();
        }
        String textAfterLastImage = question.getText().substring(lastIndex);
        if (!textAfterLastImage.isEmpty()) {
            questionPreview.getChildren().add(new Text(textAfterLastImage));
        }
    }

    private boolean previewQuestionShouldBeVisible() {
        if (picturePickerController.invalidSyntax()) {
            return false;
        }
        if (picturePickerController.buttonAndImages.isEmpty()) {
            return false;
        }
        return true;
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
                question.getText(),
                questionTypeMenuButton.getText(),
                remarks.getText(),
                LocalDateTime.now(),    // createdAt
                LocalDateTime.now(),    // updatedAt
                selectedKeywords
        );

        Question newQuestion = questionService.add(q);
        if (newQuestion.getId() != null) {
            answerService.addAnswers(newQuestion.getId(), getAnswersSet(Type.valueOf(questionTypeMenuButton.getText()), answerTextArea, this.answers));
            imageService.addImages(newQuestion.getId(), picturePickerController.getImages());
            SwitchScene.switchScene(SwitchScene.CREATE_QUESTION);
            SharedData.setOperation(Message.CREATE_QUESTION_SUCCESS_MESSAGE);
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
        if (checkIfQuestionIsEmpty()) {
            return MainApp.resourceBundle.getString("error_message_question_not_set");
        }
        if (picturePickerController.invalidSyntax()) {
            return MainApp.resourceBundle.getString("error_message_image_not_included");
        }
        return null;
    }

    public void onAddCategoryBtnClick(ActionEvent actionEvent) {
        Stage addCategoryStage = ModalOpener.openModal(ModalOpener.ADD_CATEGORY);

        addCategoryStage.setOnHidden((WindowEvent event) -> {
            initCategoryComboBox(categoryComboBox, categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId()));
        });
    }

    public void onAddKeywordBtnClick(ActionEvent actionEvent) {
        Stage addKeywordStage = ModalOpener.openModal(ModalOpener.ADD_KEYWORD);

        // initialize keywords-comboBox when the modal closes
        addKeywordStage.setOnHidden((WindowEvent event) -> {
            List<Keyword> keywords = keywordService.getAllByCourseId(SharedData.getSelectedCourse().getId());
            initKeywordComboBox(keywords, selectedKeywords, keywordsHBox, keywordComboButton);
        });
    }
}
