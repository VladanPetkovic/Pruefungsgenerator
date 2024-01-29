package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class QuestionCreate_ScreenController extends ScreenController implements Initializable {

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
    private TextArea question;
    @FXML
    private TextArea remarks;
    @FXML
    private MenuButton keyword;
    private ArrayList<Keyword> keywords;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();
    @FXML
    private HBox keywordsHBox;
    private ArrayList<Category> categories;
    private Category selectedCategory = null;
    private ArrayList<TextArea> answers = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categories = SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getCourse_id());
        if(categories.size() == 0){
            showErrorAlert("Error","No categories found","Please create categories first before accessing upload question");
        }
        fillCategoryWithCategories();
        difficulty.setValue(5);
        points.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        points.getValueFactory().setValue(10);
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
                    b.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            keywordsHBox.getChildren().remove(b);
                            selectedKeywords.remove(k);
                        }
                    });
                    keywordsHBox.getChildren().add(b);
                }
            });
            keyword.getItems().add(menuItem);
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
        button.setFocusTraversable(false);
        return button;
    }

    @FXML
    private void onActionUpload() {
        String s = checkIfFilled();
        if (s != null) {
            showErrorAlert("Error","Not all fields filled",s);
            return;
        }
        Question q = new Question(
                selectedCategory,
                (int) difficulty.getValue(),
                points.getValue(),
                question.getText(),
                multipleChoice.isSelected() ? 1 : 0,
                "",
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

    private boolean checkIfEmptyAnswers(){
        if(multipleChoice.isSelected()){
            for(TextArea t : answers){
                if(t.getText().isEmpty()) return true;
            }
        }
        return false;
    }

    private boolean checkIfQuestionIsEmpty(){
        return question.getText().isEmpty();
    }

    private String checkIfFilled() {
        if (selectedCategory == null) return "Category needs to be selected.";
        if (checkIfEmptyAnswers()) return "You selected multiple choice but at least one answer is not filled out.";
        if (checkIfQuestionIsEmpty()) return "Question needs to be filled out.";
        return null;
    }
}
