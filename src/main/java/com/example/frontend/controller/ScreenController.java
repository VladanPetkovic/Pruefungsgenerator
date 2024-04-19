package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;
import com.example.frontend.MainApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * the base class for all screen controllers
 * provides functionality to switch between screens and handle common UI events
 */
public abstract class ScreenController {

    // define and initialize screens for different functionalities
    public static Screen<CreateAutomatic_ScreenController> createTestAutomatic = new Screen<>("sites/create_automatic.fxml");
    public static Screen<CreateManual_ScreenController> createTestManual = new Screen<>("sites/create_manual.fxml");
    public static Screen<QuestionCreate_ScreenController> questionUpload = new Screen<>("sites/question_create.fxml");
    public static Screen<QuestionEdit_ScreenController> questionEdit = new Screen<>("sites/question_edit.fxml");
    public static Screen<QuestionEdit_ScreenController> home = new Screen<>("sites/home.fxml");
    public static Screen<PdfPreview_ScreenController> pdf_preview = new Screen<>("sites/pdf_preview.fxml");


    /**
     * switches to the specified screen and optionally refreshes its components
     * @param screen the screen to switch to
     * @param refresh indicates whether to refresh the screen components
     */
    public static void switchScene(Screen screen, boolean refresh){
        // reload components, if refresh is true
        if(refresh){
            screen.loadComponents();
        }

        // resetting filterquestion
        SharedData.setFilterQuestion(new Question());
        // set the scene and display it
        MainApp.stage.setHeight(MainApp.stage.getHeight());
        MainApp.stage.setWidth(MainApp.stage.getWidth());
        MainApp.stage.setScene(screen.scene);
        MainApp.stage.show();
    }

    /**
     * handles click event for navigating to the create automatic test screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onCreateAutTestNavBtnClick(MouseEvent event) throws IOException {
        switchScene(createTestAutomatic,true);
    }

    /**
     * handles click event for navigating to the create manual test screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onCreateManTestNavBtnClick(MouseEvent event) throws IOException {
        switchScene(createTestManual,true);
    }

    /**
     * handles click event for navigating to the question upload screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onUploadQuestionNavBtnClick(MouseEvent event) throws IOException
    {
        switchScene(questionUpload,true);
    }

    /**
     * handles click event for navigating to the question edit screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onEditQuestionNavBtnClick(MouseEvent event) throws IOException
    {
        switchScene(questionEdit,true);
    }

    /**
     * handles click event for navigating to the home screen and resetting shared data
     */
    public void onFHTWLogoClick() {
        // navigate to the home screen and reset shared data
        switchScene(home,true);
        SharedData.resetAll();
    }

    /**
     * Method to update points value when the slider value changes.
     * The value of the slider is saved in SharedData filterQuestion.
     * Use this function in the init()-function of your screen - otherwise it won't listen to the user input.
     * @param pointsSlider
     */
    protected void getPointsFromSlider(Slider pointsSlider) {
        // add a listener to the value property of the points slider
        pointsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                // retrieve the new points value from the slider
                int points = (int) pointsSlider.getValue();
                // update the points value in the filter question object stored in SharedData
                SharedData.getFilterQuestion().setPoints(points);
            }
        });
    }

    /**
     * Method to update difficulty value when the slider value changes.
     * The value of the slider is saved in SharedData filterQuestion.
     * Use this function in the init()-function of your screen - otherwise it won't listen to the user input.
     * @param difficultySlider
     */
    protected void getDifficultyFromSlider(Slider difficultySlider) {
        // add a listener to the value property of the difficulty slider
        difficultySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                // retrieve the new difficulty value from the slider
                int difficulty = (int) difficultySlider.getValue();
                // update the difficulty value in the filter question object stored in SharedData
                SharedData.getFilterQuestion().setDifficulty(difficulty);
            }
        });
    }

    /**
     * Initializes the auto-completion of the keywords in the search-area of edit-question
     * And displays an add-btn, when the inputted text is changed AND not in the db
     */
    protected void initializeKeywords(TextField keywordTextField, ArrayList<Keyword> keywords) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Keyword k : keywords) {
            items.add(k.getKeyword());
        }
        TextFields.bindAutoCompletion(keywordTextField, items);
    }

    /**
     * Initializes the auto-completion of the categories in the search-area of edit-question.
     * TODO: maybe rewrite: initializeKeywords and initializeCategories to one function with additional boolean value
     * And displays an add-btn, when the inputted text is changed AND not in the db
     */
    protected void initializeCategories(TextField categoryTextField, ArrayList<Category> categories, Button add_category_btn) {
        // in java everything is passed by reference, so changes in items make changes in SharedData
        ObservableList<String> items = SharedData.getSuggestedCategories();
        for (Category c : categories) {
            // don't add existing categories --> good for, when switching scenes
            if (!items.contains(c.getName())) {
                items.add(c.getName());
            }
        }
        TextFields.bindAutoCompletion(categoryTextField, items);

        categoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!items.contains(categoryTextField.getText()) && !Objects.equals(categoryTextField.getText(), "")) {
                add_category_btn.setDisable(false);
            } else {
                add_category_btn.setDisable(true);
            }
        });
    }

    /**
     * Initializes the auto-completion of the questions in the search-area of edit-question.
     * Shows only 10 questions max
     */
    protected void initializeQuestions(TextField questionTextField) {
        ObservableList<String> items = FXCollections.observableArrayList();
        String course_name = SharedData.getSelectedCourse().getName();
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll(new Question(), course_name);
        for (int i = 0; i < 10; i++) {
            items.add(questions.get(i).getQuestion());
        }
        TextFields.bindAutoCompletion(questionTextField, items);
    }

    /**
     * This function initializes the MenuButton with the QuestionTypes.
     * @param menuButton - the menuButton used in the scene
     */
    protected void initializeMenuButton(MenuButton menuButton, boolean allowAllTypes) {
        ArrayList<QuestionType> questionTypes = SQLiteDatabaseConnection.QUESTION_TYPE_REPOSITORY.getAll();
        menuButton.getItems().clear();

        for (QuestionType questionType : questionTypes) {
            MenuItem menuItem = new MenuItem(questionType.getName());
            menuItem.setOnAction(e -> {
                menuButton.setText(questionType.getName());
            });
            menuButton.getItems().add(menuItem);
        }

        if (allowAllTypes) {
            // add "showAll" to showAll QuestionTypes
            MenuItem menuItem = new MenuItem("all types");
            menuItem.setOnAction(e -> {
                menuButton.setText("all types");
            });
            menuButton.getItems().add(menuItem);
        }
    }

    /**
     * Creates a label with the specified text and text fill color.
     *
     * @param text The text content of the label.
     * @param textFill The color used to fill the label's text.
     * @return The created label with the specified text and text fill color, or null if the text is null.
     */
    protected Label createLabel(String text, Paint textFill) {
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
    protected void addIfNotNull(VBox vBox, Node node) {
        if (node != null) {
            vBox.getChildren().add(node);
        }
    }

    /**
     * Creates a VBox to display the details of a question.
     *
     * @param question The question object containing the details to be displayed.
     * @return The VBox containing the question details.
     */
    protected VBox createQuestionVBox(Question question) {
        // Create a new VBox to hold the question details.
        VBox questionVbox = new VBox();

        // Create labels to display question information.
        Label questionNumberLabel = createLabel("Erreichbare Punkte: " + question.getPoints(), Color.WHITE);
        Label questionDifficultyLabel = createLabel("Difficulty: " + question.getDifficulty(), Color.WHITE);
        Label questionTextLabel = createLabel(question.getQuestion(), Color.WHITE);
        Label questionAnswersLabel = createLabel(question.getAnswersAsString(), Color.WHITE);
        Label questionRemarksLabel = createLabel(question.getRemark(), Color.WHITE);

        // Allow the question text label to wrap text if necessary.
        questionTextLabel.setWrapText(true);

        // Add question details to the VBox.
        questionVbox.getChildren().addAll(questionNumberLabel, questionDifficultyLabel, questionTextLabel);
        addIfNotNull(questionVbox, questionAnswersLabel);
        addIfNotNull(questionVbox, questionRemarksLabel);

        return questionVbox;
    }

    /**
     * Check, if the testQuestions-Array contains a question.
     * @param question_id ID of the question, that is going to be checked.
     * @return true, if testQuestions contains this question with id = question_id, return false otherwise.
     */
    protected boolean containsQuestionWithId(int question_id) {
        for (Question question : SharedData.getTestQuestions()) {
            if (question.getId() == question_id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the answers provided either in mc-TextAreas or in the one simple-answer-Textarea to
     * an ArrayList of Answer/s.
     * @return An Arraylist of Answer-objects
     */
    protected ArrayList<Answer> getAnswerArrayList(Type type, TextArea simple_answer, ArrayList<TextArea> mc_answers) {
        ArrayList<Answer> answerArrayList = new ArrayList<>();

        if (type == Type.MULTIPLE_CHOICE) {
            for (TextArea answerTextArea : mc_answers) {
                answerArrayList.add(new Answer(answerTextArea.getText()));
            }
        } else if (simple_answer != null) {
            answerArrayList.add(new Answer(simple_answer.getText()));
        }
        return answerArrayList;
    }

    protected void addMultipleChoiceAnswerBtnClicked(ArrayList<TextArea> answers, VBox multipleChoiceAnswerVBox) {
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

    /**
     * Creates a JavaFX Button with the given text and disables focus traversal.
     * @param text The text to display on the button.
     * @return The created Button
     */
    protected Button createButton(String text) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        return button;
    }
}
