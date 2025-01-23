package com.example.application.frontend.controller;

import com.example.application.backend.db.models.*;
import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.MainApp;
import com.example.application.frontend.components.ControlsInitializer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.util.*;

import java.io.File;

/**
 * the base class for all screen controllers
 * provides functionality to handle common UI events
 */

public abstract class ScreenController extends ControlsInitializer {
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
        VBox questionVbox = new VBox();

        // Create labels to display question information.
        Label questionNumberLabel = createLabel(MainApp.resourceBundle.getString("possible_points") + " " + question.getPoints(), Color.WHITE);
        Label questionDifficultyLabel = createLabel(MainApp.resourceBundle.getString("create_manual_difficulty") + " " + question.getDifficulty(), Color.WHITE);

        // add question details to the VBox.
        questionVbox.getChildren().addAll(questionNumberLabel, questionDifficultyLabel);

        WebView questionWebView = new WebView();
        questionWebView.setMinHeight(150);
        String htmlContent = question.getQuestion();
        questionWebView.getEngine().loadContent(htmlContent);
        questionWebView.setOnScroll(Event::consume);
        questionWebView.setMouseTransparent(true);
        // remove the vertical scrollbar
        questionWebView.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> change) {
                Set<Node> deadSeaScrolls = questionWebView.lookupAll(".scroll-bar");
                for (Node scroll : deadSeaScrolls) {
                    scroll.setVisible(false);
                }
            }
        });

        questionVbox.getChildren().add(questionWebView);
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

    /**
     * Converts the answers provided either in mc-TextAreas or in the one simple-answer-Textarea to
     * an ArrayList of Answer/s.
     *
     * @return An Arraylist of Answer-objects
     */
    protected Set<Answer> getAnswersSet(Type type, TextArea simple_answer, ArrayList<TextArea> mc_answers) {
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
