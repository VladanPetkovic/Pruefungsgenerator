package com.example.frontend.controller;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Topic;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class QuestionUpload_ScreenController extends ScreenController implements Initializable {

    private static final String[] LANGUAGES = {"Deutsch", "Englisch", "Spansich", "Französisch"};

    @FXML
    private Label topicLabel;
    @FXML
    private MenuButton topic;
    @FXML
    private Slider difficulty;
    @FXML
    private Spinner<Integer> points;
    @FXML
    private CheckBox multibleChoice;
    @FXML
    private VBox multibleChoiceVBox;
    @FXML
    private Label languageLabel;
    @FXML
    private MenuButton language;
    @FXML
    private TextArea question;
    @FXML
    private TextArea remarks;
    @FXML
    private MenuButton keyword;
    @FXML
    private VBox keywordVBox;
    @FXML
    private Button upload;
    @FXML
    private ScrollPane scrollPane;

    private String currentLanguage = null;
    private ArrayList<Topic> topics;
    private Topic selectedTopic = null;
    private ArrayList<TextArea> answers = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topics = SQLiteDatabaseConnection.TopicRepository.getAll();
        fillTopicWithTopics();
        difficulty.setValue(5);
        fillLanguageWithLanguages();
        question.setText("");
        remarks.setText("");
        //TODO Set all
    }

    private void fillTopicWithTopics() {
        for (Topic t : topics) {
            MenuItem menuItem = createMenuItem(t.getTopic());
            menuItem.setOnAction(event -> selectedTopic = t);
            topic.getItems().add(menuItem);
        }
    }

    private void fillLanguageWithLanguages() {
        for (String l : LANGUAGES) {
            MenuItem menuItem = createMenuItem(l);
            menuItem.setOnAction(event -> currentLanguage = l);
            language.getItems().add(menuItem);
        }
    }

    private MenuItem createMenuItem(String text) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(event -> currentLanguage = text);
        return menuItem;
    }

    @FXML
    private void onActionMultibleChoice() {
        if (multibleChoice.isSelected()) {
            createMultibleChoiceButton();
        } else {
            CheckBox checkBox = multibleChoice;
            multibleChoiceVBox.getChildren().clear();
            multibleChoiceVBox.getChildren().add(checkBox);
        }
    }

    private void createMultibleChoiceButton() {
        Button button = createButton("Add answer");
        button.setOnAction(event -> {
            HBox hBoxAnswerRemove = new HBox();
            TextArea textAreaAnswer = new TextArea();
            answers.add(textAreaAnswer);
            Button buttonRemove = createButton("X");
            buttonRemove.setOnAction(e -> {
                answers.remove(textAreaAnswer);
                multibleChoiceVBox.getChildren().remove(hBoxAnswerRemove);
                if(answers.size() == 10) createMultibleChoiceButton();
            });
            hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
            multibleChoiceVBox.getChildren().add(hBoxAnswerRemove);
            multibleChoiceVBox.getChildren().remove(button);
            if(answers.size() > 10) return;
            createMultibleChoiceButton();
        });
        multibleChoiceVBox.getChildren().add(button);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setOnAction(event -> {});
        return button;
    }

    private void scrollToNode(Node targetNode) {
        //TODO scroll to node when necesarry and not filled out
    }

    @FXML
    private void onActionUpload() {
        Node n = checkIfFilled();
        if (n != null) {
            System.out.println(n);
            scrollToNode(n);
            return;
        }
        //TODO Question upload
        switchScene(questionUpload, true);
    }

    private Node checkIfFilled() {
        if (selectedTopic == null) return topicLabel;
        if (currentLanguage == null) return languageLabel;
        if (question.getText().isEmpty()) return question;
        return null;
    }
}
