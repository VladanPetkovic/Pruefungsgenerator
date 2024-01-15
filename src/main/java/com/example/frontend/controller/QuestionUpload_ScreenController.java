package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    private static final String[] LANGUAGES = {"Deutsch", "Englisch", "Spanisch", "Französisch"};

    @FXML
    private MenuButton category;
    @FXML
    private Slider difficulty;
    @FXML
    private Spinner<Integer> points;
    @FXML
    private CheckBox multipleChoice;
    @FXML
    private VBox multipleChoiceVBox;
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
    private ArrayList<Keyword> keywords;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();
    @FXML
    private VBox keywordVBox;
    @FXML
    private Button upload;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private HBox keywordsHBox;

    private String currentLanguage = null;
    private ArrayList<Category> categories;
    private Category selectedCategory = null;
    private ArrayList<TextArea> answers = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categories = SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getCourse_id());
        if(categories.size() == 0){
            questionUpload.disableScene(true);
            showErrorAlert("Error","No categories found","Please create categories first before accessing upload question");
        }
        fillCategoryWithCategories();
        difficulty.setValue(5);
        points.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        points.getValueFactory().setValue(10);
        fillLanguageWithLanguages();
        question.setText("");
        remarks.setText("");
        keywords = SQLiteDatabaseConnection.keywordRepository.getAll();
        fillKeywordWithKeywords();
    }

    public void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void fillCategoryWithCategories() {
        for (Category c : categories) {
            MenuItem menuItem = createMenuItem(c.getCategory());
            menuItem.setOnAction(event -> {
                selectedCategory = c;
                category.setText(c.getCategory());
            });
            category.getItems().add(menuItem);
        }
    }

    private void fillKeywordWithKeywords() {
        for (Keyword k : keywords) {
            MenuItem menuItem = createMenuItem(k.getKeyword_text());
            menuItem.setOnAction(event -> {
                if (!selectedKeywords.contains(k)) {
                    selectedKeywords.add(k);
                    Button b = createButton(k.getKeyword_text() + " X");
                    b.setOnAction(actionEvent -> keywordsHBox.getChildren().remove(b));
                    keywordsHBox.getChildren().add(b);
                }
            });
            keyword.getItems().add(menuItem);
        }
    }

    private void fillLanguageWithLanguages() {
        for (String l : LANGUAGES) {
            MenuItem menuItem = createMenuItem(l);
            menuItem.setOnAction(event -> {
                currentLanguage = l;
                language.setText(l);
            });
            language.getItems().add(menuItem);
        }
    }

    private MenuItem createMenuItem(String text) {
        MenuItem menuItem = new MenuItem(text);
        return menuItem;
    }

    @FXML
    private void onActionMultipleChoice() {
        if (multipleChoice.isSelected()) {
            createMultipleChoiceButton();
        } else {
            CheckBox checkBox = multipleChoice;
            multipleChoiceVBox.getChildren().clear();
            multipleChoiceVBox.getChildren().add(checkBox);
            answers.clear();
        }
    }

    private void createMultipleChoiceButton() {
        Button button = createButton("Add answer");
        button.setOnAction(event -> {
            HBox hBoxAnswerRemove = new HBox();
            TextArea textAreaAnswer = new TextArea();
            answers.add(textAreaAnswer);
            Button buttonRemove = createButton("X");
            buttonRemove.setOnAction(e -> {
                answers.remove(textAreaAnswer);
                multipleChoiceVBox.getChildren().remove(hBoxAnswerRemove);
                if (answers.size() == 10) createMultipleChoiceButton();
            });
            hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
            multipleChoiceVBox.getChildren().add(hBoxAnswerRemove);
            multipleChoiceVBox.getChildren().remove(button);
            if (answers.size() > 10) return;
            createMultipleChoiceButton();
        });
        multipleChoiceVBox.getChildren().add(button);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        return button;
    }

    @FXML
    private void onActionUpload() {
        Node n = checkIfFilled();
        if (n != null) {
            showErrorAlert("Error","Not all fields filled","Fill out: "+n);
            return;
        }
        Question q = new Question(
                selectedCategory,
                (int) difficulty.getValue(),
                points.getValue(),
                question.getText(),
                multipleChoice.isSelected() ? 1 : 0,
                currentLanguage,
                remarks.getText(),
                answersToDatabaseString(),
                selectedKeywords,
                new ArrayList<>()
        );
        SQLiteDatabaseConnection.questionRepository.add(q);
        for (Keyword k : selectedKeywords) {
            SQLiteDatabaseConnection.keywordRepository.addConnection(k, q);
        }
        switchScene(questionUpload, true);
    }

    private String answersToDatabaseString() {
        StringBuilder s = new StringBuilder();
        if (multipleChoice.isSelected()) {
            for (TextArea t : answers) {
                s.append(t.getText()).append("\n");
            }
        }
        return s.toString();
    }

    private Node checkIfFilled() {
        if (selectedCategory == null) return category;
        if (multipleChoice.isSelected()) {
            for (TextArea t : answers) {
                if (t.getText().isEmpty()) return multipleChoice;
            }
        }
        if (currentLanguage == null) return language;
        if (question.getText().isEmpty()) return question;
        return null;
    }
}
