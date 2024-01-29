package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class QuestionEdit_ScreenController extends ScreenController implements Initializable {

    @FXML
    private Label label_selectedCourse;

    @FXML
    private TextField categoryTextField;

    @FXML
    private TextField keywordTextField;

    @FXML
    private Slider difficultySlider;

    @FXML
    private Slider pointsSlider;

    @FXML
    private CheckBox multipleChoiceCheckBox;

    @FXML
    private CheckBox searchMultipleChoice;

    private ArrayList<TextArea> answers = new ArrayList<>();

    @FXML
    private Button searchButton;

    @FXML
    private VBox previewVBox;

    @FXML
    private MenuButton chooseCategory;

    @FXML
    private Spinner<Double> choosePoints;

    @FXML
    private CheckBox chooseMultipleChoice;

    @FXML
    private Slider chooseDifficulty;

    @FXML
    private TextArea chooseQuestion;

    @FXML
    private TextArea chooseRemarks;

    @FXML
    private Button chooseButton;

    private Question choosenQuestion = null;

    private ArrayList<Keyword> keywords;
    private ArrayList<Keyword> selectedKeywords = new ArrayList<>();

    private ArrayList<Category> categories;
    private Category selectedCategory = null;

    @FXML
    private ScrollPane chooseScrollPane;

    @FXML
    private HBox keywordsHBox;

    @FXML
    private MenuButton chooseKeywords;

    @FXML
    private VBox multipleChoiceVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chooseScrollPane.setMouseTransparent(true);
        label_selectedCourse.setText(SharedData.getSelectedCourse().getCourse_name());
        categories = SQLiteDatabaseConnection.CategoryRepository.getAll(SharedData.getSelectedCourse().getCourse_id());
        if(categories.size() == 0)
            showErrorAlert("Error","No categories found","Please create categories first before accessing upload question");
        fillCategoryWithCategories();
        keywords = SQLiteDatabaseConnection.keywordRepository.getAll();
        fillKeywordWithKeywords();
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1, 0.5);
        choosePoints.setValueFactory(valueFactory);

    }
    public void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    private void applyFilterButtonClicked(ActionEvent event) {
        searchQuestions();
    }

    @FXML
    private void searchQuestions() {
        Question filterQuestion = createFilterQuestion();

        // Call Repository to search for questions
        ArrayList<Question> result = SQLiteDatabaseConnection.questionRepository.getAll(
                filterQuestion,
                SharedData.getSelectedCourse().getCourse_name(),
                multipleChoiceCheckBox.isSelected()
        );

        // Display result in the console
        printQuestions(result);
    }

    private Question createFilterQuestion() {
        Question filterQuestion = new Question();

        // Get filter values
        String categoryName = categoryTextField.getText().trim();
        String keywordText = keywordTextField.getText().trim();
        boolean multipleChoice = multipleChoiceCheckBox.isSelected();

        setCategoryFilter(categoryName, filterQuestion);
        setKeywordFilter(keywordText, filterQuestion);
        //setPointsAndDifficultyFilter(filterQuestion);

        // Set multiple choice filter
        filterQuestion.setMultipleChoice(multipleChoice ? 1 : 0);

        return filterQuestion;
    }

    private void setCategoryFilter(String categoryName, Question filterQuestion) {
        if (!categoryName.isEmpty()) {
            Category category = SQLiteDatabaseConnection.CategoryRepository.get(categoryName);
            if (category != null) {
                filterQuestion.setCategory(category);
            }
        }
    }

    private void setKeywordFilter(String keywordText, Question filterQuestion) {
        if (!keywordText.isEmpty()) {
            String[] keywordsArray = keywordText.split("[,\\s]+");
            ArrayList<Keyword> keywordsList = new ArrayList<>();

            for (String keyword : keywordsArray) {
                Keyword keywordObj = SQLiteDatabaseConnection.keywordRepository.get(keyword.trim());
                if (keywordObj != null) {
                    keywordsList.add(keywordObj);
                }
            }

            filterQuestion.setKeywords(keywordsList);
        }
    }

    private void setPointsAndDifficultyFilter(Question filterQuestion) {
        filterQuestion.setDifficulty((int)difficultySlider.getValue());
        filterQuestion.setPoints((float)pointsSlider.getValue());
    }
    private void fillCategoryWithCategories() {
        for (Category category : categories) {
            MenuItem menuItem = createMenuItem(category.getCategory());
            menuItem.setOnAction(event -> handleCategorySelection(category));
            chooseCategory.getItems().add(menuItem);
        }
    }

    private void fillKeywordWithKeywords() {
        for (Keyword keyword : keywords) {
            MenuItem menuItem = createMenuItem(keyword.getKeyword_text());
            menuItem.setOnAction(event -> handleKeywordSelection(keyword));
            chooseKeywords.getItems().add(menuItem);
        }
    }

    private MenuItem createMenuItem(String text) {
        MenuItem menuItem = new MenuItem(text);
        return menuItem;
    }

    private void handleCategorySelection(Category category) {
        selectedCategory = category;
        chooseCategory.setText(category.getCategory());
    }

    private void handleKeywordSelection(Keyword keyword) {
        if (!containsKeyword(keyword)) {
            selectedKeywords.add(keyword);
            Button button = createButton(keyword.getKeyword_text() + " X");
            button.setOnAction(event -> handleKeywordRemoval(button, keyword));
            keywordsHBox.getChildren().add(button);
        }
    }

    private boolean containsKeyword(Keyword k){
        for(Keyword keyword : selectedKeywords){
            if(keyword.getKeyword_id() == k.getKeyword_id())
                return true;
        }
        return false;
    }

    private void handleKeywordRemoval(Button button, Keyword keyword) {
        keywordsHBox.getChildren().remove(button);
        selectedKeywords.remove(keyword);
    }

    @FXML
    private void onActionMultipleChoice() {
        if (chooseMultipleChoice.isSelected()) {
            createMultipleChoiceButton();
        } else {
            resetMultipleChoiceView();
        }
    }

    private void resetMultipleChoiceView() {
        CheckBox checkBox = chooseMultipleChoice;
        multipleChoiceVBox.getChildren().clear();
        multipleChoiceVBox.getChildren().add(checkBox);
        answers.clear();
    }

    private void createMultipleChoiceButton() {
        createAndAddMultipleChoiceButton("Add answer", "");
    }

    private void createAndAddMultipleChoiceButton(String buttonText, String initialAnswer) {
        Button button = createButton(buttonText);
        button.setOnAction(event -> {
            HBox hBoxAnswerRemove = new HBox();
            TextArea textAreaAnswer = new TextArea();
            textAreaAnswer.setText(initialAnswer);
            answers.add(textAreaAnswer);
            Button buttonRemove = createButton("X");
            setButtonRemoveAction(buttonRemove, textAreaAnswer, hBoxAnswerRemove);
            hBoxAnswerRemove.getChildren().addAll(textAreaAnswer, buttonRemove);
            multipleChoiceVBox.getChildren().add(hBoxAnswerRemove);
            multipleChoiceVBox.getChildren().remove(button);
            if (answers.size() <= 10) {
                createMultipleChoiceButton();
            }
        });
        multipleChoiceVBox.getChildren().add(button);
    }

    private void setButtonRemoveAction(Button buttonRemove, TextArea textAreaAnswer, HBox hBoxAnswerRemove) {
        buttonRemove.setOnAction(e -> {
            answers.remove(textAreaAnswer);
            multipleChoiceVBox.getChildren().remove(hBoxAnswerRemove);
            if (answers.size() <= 10) {
                createMultipleChoiceButton();
            }
        });
    }

    private void createMultipleChoiceButton(String answer) {
        createAndAddMultipleChoiceButton("Add answer", answer);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        return button;
    }

    @FXML
    void printQuestions(ArrayList<Question> questions) {
        if (questions.isEmpty()) {
            System.out.println("No questions found");
            return;
        }

        double spacing = 10.0;

        previewVBox.getChildren().clear();

        for (Question question : questions) {
            VBox questionVbox = createQuestionVBox(question);
            setQuestionVboxEventHandlers(questionVbox, question);
            previewVBox.getChildren().add(questionVbox);
            previewVBox.setSpacing(spacing);
        }
    }

    private VBox createQuestionVBox(Question question) {
        VBox questionVbox = new VBox();
        Label questionNumberLabel = createLabel("(Erreichbare Punkte: " + question.getPoints(), Color.WHITE);
        Label questionDifficultyLabel = createLabel("Difficulty: " + question.getDifficulty(),Color.WHITE);
        Label questionTextLabel = createLabel(question.getQuestionString(), Color.WHITE);
        Label questionAnswersLabel = createLabel(question.getAnswers(), Color.WHITE);
        Label questionRemarksLabel = createLabel(question.getRemarks(), Color.WHITE);

        questionTextLabel.setWrapText(true);

        questionVbox.getChildren().addAll(questionNumberLabel, questionDifficultyLabel, questionTextLabel);
        addIfNotNull(questionVbox, questionAnswersLabel);
        addIfNotNull(questionVbox, questionRemarksLabel);

        return questionVbox;
    }

    private void setQuestionVboxEventHandlers(VBox questionVbox, Question question) {
        questionVbox.setOnMouseClicked(event -> {
            chooseScrollPane.setMouseTransparent(false);
            chooseCategory.setText(question.getCategory().getCategory());
            selectedCategory = question.getCategory();
            chooseDifficulty.setValue(question.getDifficulty());
            choosePoints.getValueFactory().setValue((double)question.getPoints());

            if (question.getMultipleChoice() == 1) {
                Arrays.stream(question.getAnswers().split("\n")).forEach(this::createMultipleChoiceButton);
            }

            chooseQuestion.setText(question.getQuestionString());
            chooseRemarks.setText(question.getRemarks());

            selectedKeywords.clear();
            keywordsHBox.getChildren().clear();

            for(Keyword k : question.getKeywords()){
                selectedKeywords.add(k);
                Button b = createButton(k.getKeyword_text() + " X");
                b.setOnAction(e -> {
                    keywordsHBox.getChildren().remove(b);
                    selectedKeywords.remove(k);
                });
                keywordsHBox.getChildren().add(b);
            }
        });
    }

    private Label createLabel(String text, Paint textFill) {
        if (text != null) {
            Label label = new Label(text);
            label.setTextFill(textFill);
            return label;
        }
        return null;
    }

    private void addIfNotNull(VBox vBox, Node node) {
        if (node != null) {
            vBox.getChildren().add(node);
        }
    }

    @FXML
    private void onChooseButton() {
        String errorMessage = validateInput();
        if (errorMessage != null) {
            showErrorAlert("Error", "Not all fields filled", errorMessage);
            return;
        }

        Question question = createQuestionFromInputs();
        SQLiteDatabaseConnection.questionRepository.update(question);
    }

    private String validateInput() {
        if (selectedCategory == null) {
            return "Category needs to be selected.";
        }

        if (chooseMultipleChoice.isSelected() && checkIfEmptyAnswers()) {
            return "You selected multiple choice but at least one answer is not filled out.";
        }

        if (checkIfQuestionIsEmpty()) {
            return "Question needs to be filled out.";
        }

        return null;
    }

    private Question createQuestionFromInputs() {
        return new Question(
                selectedCategory,
                (int) chooseDifficulty.getValue(),
                choosePoints.getValue().floatValue(),
                chooseQuestion.getText(),
                chooseMultipleChoice.isSelected() ? 1 : 0,
                "",
                chooseRemarks.getText(),
                answersToDatabaseString(),
                selectedKeywords,
                new ArrayList<>()
        );
    }

    private String answersToDatabaseString() {
        StringBuilder result = new StringBuilder();
        if (chooseMultipleChoice.isSelected()) {
            for (TextArea answerTextArea : answers) {
                result.append(answerTextArea.getText()).append("\n");
            }
        }
        return result.toString();
    }

    private boolean checkIfEmptyAnswers() {
        return answers.stream().anyMatch(answerTextArea -> answerTextArea.getText().isEmpty());
    }

    private boolean checkIfQuestionIsEmpty() {
        return chooseQuestion.getText().isEmpty();
    }

}
