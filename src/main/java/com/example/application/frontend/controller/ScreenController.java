package com.example.application.frontend.controller;

import com.example.application.backend.db.models.*;
import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.SharedData;
import com.example.application.MainApp;
import com.example.application.frontend.components.CustomDoubleSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.*;

import java.io.File;

/**
 * the base class for all screen controllers
 * provides functionality to handle common UI events
 */

public abstract class ScreenController {

    /**
     * This function activates/deactivates/sets to min/sets to max a slider/spinner and changes the image accordingly.
     *
     * @param sliderOrSpinner Either a difficulty or points slider (or some other)
     * @param toggleImage     The image, we want to change (toggle off or toggle on)
     * @param status          The status of the sliderOrSpinner object.
     */
    public void on_toggle_btn_click(Object sliderOrSpinner, ImageView toggleImage, int status) {
        String imagePath = "src/main/resources/com/example/application/icons/";
        Slider slider = null;
        CustomDoubleSpinner spinner = null;
        if (sliderOrSpinner instanceof Slider) {
            slider = (Slider) sliderOrSpinner;
        } else if (sliderOrSpinner instanceof CustomDoubleSpinner) {
            spinner = (CustomDoubleSpinner) sliderOrSpinner;
        }

        switch (status) {
            case 0: // currently disabled --> enable
                if (slider != null) {
                    slider.setDisable(false);
                } else if (spinner != null) {
                    spinner.setDisable(false);
                }
                imagePath += "toggle_on.png";
                break;
            case 1: // currently enabled --> min
                imagePath += "toggle_on_min.png";
                break;
            case 2: // currently min --> max
                imagePath += "toggle_on_max.png";
                break;
            default: // current max --> disable
                if (slider != null) {
                    slider.setDisable(true);
                } else if (spinner != null) {
                    spinner.setDisable(true);
                }
                imagePath += "toggle_off.png";
                break;
        }

        File file = new File(imagePath);
        Image toggleImageFile = new Image(file.toURI().toString());
        toggleImage.setImage(toggleImageFile);
    }

    /**
     * Initializes the auto-completion of the keywords in the search-area of edit-question
     * And displays an add-btn, when the inputted text is changed AND not in the db
     *
     * @param addKeywordBtn When passed null, then we cannot add keywords
     */
    protected void initializeKeywords(TextField keywordTextField, List<Keyword> keywords, Button addKeywordBtn) {
//        if (keywords.isEmpty()) {     // maybe when the application crashed this code is necessary - to be checked/tested
//            return;
//        }

        ArrayList<String> items = new ArrayList<>();
        for (Keyword k : keywords) {
            if (!items.contains(k.getKeyword())) {
                items.add(k.getKeyword());
            }
        }
        TextFields.bindAutoCompletion(keywordTextField, items);

        if (addKeywordBtn != null) {
            keywordTextField.textProperty().addListener((obsrevable, oldValue, newValue) -> {
                if (!items.contains(keywordTextField.getText()) && !Objects.equals(keywordTextField.getText(), "")) {
                    addKeywordBtn.setDisable(false);
                } else {
                    addKeywordBtn.setDisable(true);
                }
            });
        }
    }

    /**
     * Initializes the auto-completion of the categories in the search-area of edit-question.
     * TODO: maybe rewrite: initializeKeywords and initializeCategories to one function with additional boolean value
     * And displays an add-btn, when the inputted text is changed AND not in the db
     */
    protected void initializeCategories(TextField categoryTextField, List<Category> categories, Button add_category_btn) {
//        if (categories.isEmpty()) {   // maybe when the application crashed this code is necessary - to be checked/tested
//            return;
//        }

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
     * Function used to add a new category to the suggested-categories when clicked on the plus-button.
     *
     * @param newCategory the created category
     * @param addCategoryBtn  the add-btn that is clicked for adding a new category
     */
    protected void addCategoryBtnClick(Category newCategory, Button addCategoryBtn) throws IOException {
        if (newCategory.getId() != null) {
            SharedData.setOperation(Message.CREATE_CATEGORY_SUCCESS_MESSAGE);
            SharedData.getSuggestedCategories().add(newCategory.getName());

            addCategoryBtn.setDisable(true);
        }
    }

    /**
     * Initializes the auto-completion of the questions in the search-area of edit-question.
     * Shows only 10 questions max
     */
    protected void initializeQuestions(TextField questionTextField) {
        ObservableList<String> items = FXCollections.observableArrayList();
        String course_name = SharedData.getSelectedCourse().getName();
//        ArrayList<Question> questions = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getAll(new Question(), course_name);
//        for (int i = 0; i < 10 && i < questions.size(); i++) {
//            items.add(questions.get(i).getQuestion());
//        }
        TextFields.bindAutoCompletion(questionTextField, items);
    }

    /**
     * This function initializes the MenuButton with the QuestionTypes.
     *
     * @param menuButton - the menuButton used in the scene
     */
    protected void initializeMenuButton(MenuButton menuButton, boolean allowAllTypes, List<Type> types) {
        menuButton.getItems().clear();

        for (Type type : types) {
            MenuItem menuItem = new MenuItem(type.toString());
            menuItem.setOnAction(e -> {
                menuButton.setText(type.toString());
            });
            menuButton.getItems().add(menuItem);
        }

        if (allowAllTypes) {
            // add "showAll" to showAll QuestionTypes
            MenuItem menuItem = new MenuItem(MainApp.resourceBundle.getString("all_types"));
            menuItem.setOnAction(e -> {
                menuButton.setText(MainApp.resourceBundle.getString("all_types"));
            });
            menuButton.getItems().add(menuItem);
        }
    }

    /**
     * Creates a label with the specified text and text fill color.
     *
     * @param text     The text content of the label.
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
        Label questionNumberLabel = createLabel(MainApp.resourceBundle.getString("possible_points") + " " + question.getPoints(), Color.WHITE);
        Label questionDifficultyLabel = createLabel(MainApp.resourceBundle.getString("create_manual_difficulty") + " " + question.getDifficulty(), Color.WHITE);
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
     * Check, if questions contains the question_id
     *
     * @param question_id ID of the question, that is going to be checked.
     * @param questions,  the array of questions to be checked
     * @return true, if testQuestions contains this question with id = question_id, return false otherwise.
     */
    protected boolean containsQuestionWithId(Long question_id, List<Question> questions) {
        for (Question question : questions) {
            if (question.getId() == question_id) {
                return true;
            }
        }
        return false;
    }

    //
    // START REGION QUESTION-CREATE AND QUESTION-EDIT
    //

    /**
     * Converts the answers provided either in mc-TextAreas or in the one simple-answer-Textarea to
     * an ArrayList of Answer/s.
     *
     * @return An Arraylist of Answer-objects
     */
    protected Set<Answer> getAnswerArrayList(Type type, TextArea simple_answer, ArrayList<TextArea> mc_answers) {
        Set<Answer> answerHashSet = new HashSet<>();

        if (type == Type.MULTIPLE_CHOICE) {
            for (TextArea answerTextArea : mc_answers) {
                answerHashSet.add(new Answer(answerTextArea.getText()));
            }
        } else if (simple_answer != null) {
            answerHashSet.add(new Answer(simple_answer.getText()));
        }
        return answerHashSet;
    }

    /**
     * Creates a JavaFX Button with the given text and disables focus traversal.
     *
     * @param text The text to display on the button.
     * @return The created Button
     */
    protected Button createButton(String text) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        return button;
    }

    /**
     * This function fills the MenuButton with a keyword.
     * It sets an ActionEvent, when clicked --> the keyword is displayed.
     * The displayed keyword can be removed via button click.
     */
    protected void fillKeywordMenuButtonWithKeyword(Keyword newKeyword, Set<Keyword> selectedKeywords, HBox keywordsHBox, MenuButton keywordMenuButton) {
        // Create keyword MenuItem
        MenuItem menuItem = new MenuItem(newKeyword.getKeyword());
        // Add action event for keyword MenuItem
        menuItem.setOnAction(event -> addSelectedKeyword(newKeyword, selectedKeywords, keywordsHBox));
        // Add MenuItem to keywordMenuButton
        keywordMenuButton.getItems().add(menuItem);
    }

    protected void addSelectedKeyword(Keyword newKeyword, Set<Keyword> selectedKeywords, HBox keywordsHBox) {
        if (selectedKeywords.contains(newKeyword)) {
            return;
        }

        selectedKeywords.add(newKeyword);
        Button removalButton = createRemovalButton(newKeyword, keywordsHBox, selectedKeywords);
        keywordsHBox.getChildren().add(removalButton);
    }

    protected Button createRemovalButton(Keyword newKeyword, HBox keywordsHBox, Set<Keyword> selectedKeywords) {
        Button button = createButton(newKeyword.getKeyword() + " X");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // When the removal button is clicked, remove the keyword from the list of selected keywords
                keywordsHBox.getChildren().remove(button);
                selectedKeywords.remove(newKeyword);
            }
        });

        return button;
    }

    /**
     * Checks if any of the answers provided for the question are empty. If multiple choice is selected,
     * it iterates through the list of answers and returns true if it finds any empty answer. If multiple
     * choice is not selected, it always returns false.
     *
     * @return true if any answer is empty (when multiple choice is selected), false otherwise.
     */
    protected boolean checkIfEmptyAnswers(MenuButton questionTypeMenuButton, ArrayList<TextArea> answers) {
        // Check if multiple choice is selected
        if (Type.isMultipleChoice(questionTypeMenuButton.getText())) {
            // Iterate through the list of answers
            for (TextArea t : answers) {
                // Check if the current answer is empty
                if (t.getText() == null) {
                    return true;
                }
                if (t.getText().isEmpty()) {
                    return true;
                }
            }
        }
        // Return false if no empty answers are found or if multiple choice is not selected
        return false;
    }

    //
    // END REGION QUESTION-CREATE AND QUESTION-EDIT
    //


    //
    // START REGION EXPORT FILE
    //

    /**
     * This function opens a new Dialog to get the destination folder for saving the export-file.
     * If a folder was chosen previously, then it will set the previous choice as default.
     *
     * @param label_selectedDirectory Label, which shows the selected directory.
     */
    protected void chooseDirectory(Label label_selectedDirectory) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(MainApp.resourceBundle.getString("choose_folder_to_save_file"));
        if (!label_selectedDirectory.getText().equals("\"\"")) {
            chooser.setInitialDirectory(new File(label_selectedDirectory.getText()));
        }
        File directory = chooser.showDialog(MainApp.stage);
        if (directory != null) {
            label_selectedDirectory.setText(directory.toString());
            Logger.log(getClass().getName(), label_selectedDirectory.getText(), LogLevel.INFO);
        }
    }
    //
    // END REGION EXPORT FILE
    //

    @FXML
    protected void onButtonPressed(MouseEvent event) {
        Node source = (Node) event.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            if (button.getStyleClass().contains("btn_red")) {
                handleRedButtonPressed(button);
            } else if (button.getStyleClass().contains("btn_grey")) {
                handleGreyButtonPressed(button);
            } else if (button.getStyleClass().contains("btn_dark")) {
                handleDarkButtonPressed(button);
            }
        } else if (source instanceof MenuButton) {
            MenuButton menuButton = (MenuButton) source;
            handleMenuButtonPressed(menuButton);
        }
    }

    private void handleRedButtonPressed(Button button) {
        // Instantly change background color when pressed
        button.setStyle("-fx-background-color: red;");

        // Depress or shrink animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(button.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.ZERO, new KeyValue(button.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(button.scaleXProperty(), 0.9)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(button.scaleYProperty(), 0.9))
        );
        timeline.play();

        // Ripple effect
        DropShadow dropShadow = new DropShadow();
        button.setEffect(dropShadow);
    }

    private void handleGreyButtonPressed(Button button) {
        // Instantly change background color when pressed
        button.setStyle("-fx-background-color: #646464;");

        // Depress or shrink animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(button.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.ZERO, new KeyValue(button.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(button.scaleXProperty(), 0.9)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(button.scaleYProperty(), 0.9))
        );
        timeline.play();

        // Ripple effect
        DropShadow dropShadow = new DropShadow();
        button.setEffect(dropShadow);
    }

    private void handleDarkButtonPressed(Button button) {
        // Instantly change background color when pressed
        button.setStyle("-fx-background-color: #2f2f2f;");

        // Depress or shrink animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(button.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.ZERO, new KeyValue(button.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(button.scaleXProperty(), 0.9)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(button.scaleYProperty(), 0.9))
        );
        timeline.play();

        // Ripple effect
        DropShadow dropShadow = new DropShadow();
        button.setEffect(dropShadow);
    }

    private void handleMenuButtonPressed(MenuButton menuButton) {
        // Instantly change background color when pressed
        menuButton.setStyle("-fx-background-color: #2f2f2f;");

        // Ripple effect
        DropShadow dropShadow = new DropShadow();
        menuButton.setEffect(dropShadow);
    }

    @FXML
    protected void onButtonReleased(MouseEvent event) {
        Node source = (Node) event.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            handleButtonReleased(button);
        } else if (source instanceof MenuButton) {
            MenuButton menuButton = (MenuButton) source;
            handleMenuButtonReleased(menuButton);
        }
    }

    private void handleButtonReleased(Button button) {
        // Release animation (return to original state)
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), new KeyValue(button.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(button.scaleYProperty(), 1.0))
        );
        timeline.play();

        // Remove ripple effect
        button.setEffect(null);
    }

    private void handleMenuButtonReleased(MenuButton menuButton) {
        // Return to original background color gradually
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.1), menuButton);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        // Remove ripple effect
        menuButton.setEffect(null);
    }
}
