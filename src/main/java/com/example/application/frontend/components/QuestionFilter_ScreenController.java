package com.example.application.frontend.components;

import com.example.application.MainApp;
import com.example.application.backend.app.SortType;
import com.example.application.backend.db.models.*;
import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.services.CategoryService;
import com.example.application.backend.db.services.KeywordService;
import com.example.application.backend.db.services.QuestionService;
import com.example.application.frontend.controller.ScreenController;
import com.example.application.frontend.modals.ModalOpener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

@Component
@Scope("prototype")
public class QuestionFilter_ScreenController extends ScreenController {
    private final QuestionService questionService;
    private final KeywordService keywordService;
    private final CategoryService categoryService;
    @FXML
    public MenuButton questionTypeMenuButton;
    @FXML
    public Slider pointsSlider;
    @FXML
    public Slider difficultySlider;
    @FXML
    public TextField questionTextField;
    @FXML
    public Label label_selectedCourse;
    @FXML
    public ImageView difficulty_toggle_image_view;
    @FXML
    public ImageView points_toggle_image_view;
    public MenuButton sortMenuButton;
    public ImageView sortDirectionImageView;
    public ComboBox<String> categoryComboBox;
    public ComboBox<String> keywordComboButton;
    public HBox keywordsHBox;

    private Set<Keyword> selectedKeywords = new HashSet<>();    // used to keep track of the selected keywords for filtering
    private int pointsFilterMethod = 0;     // 0 = disabled; 1 = enabled; 2 = min; 3 = max
    private int difficultyFilterMethod = 0;     // 0 = disabled; 1 = enabled; 2 = min; 3 = max
    private int sortDirectionStatus = 0;       // 0 = ASC; 1 = DESC

    public QuestionFilter_ScreenController(QuestionService questionService, KeywordService keywordService, CategoryService categoryService) {
        super();
        this.questionService = questionService;
        this.keywordService = keywordService;
        this.categoryService = categoryService;
    }

    @FXML
    private void initialize() {
        initCategoryComboBox(categoryComboBox, categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId()));
        List<Keyword> keywords = keywordService.getAllByCourseId(SharedData.getSelectedCourse().getId());
        initKeywordComboBox(keywords, selectedKeywords, keywordsHBox, keywordComboButton);
        List<Type> questionTypes = Arrays.asList(Type.values());
        initializeMenuButton(this.questionTypeMenuButton, true, questionTypes, this::searchQuestions);
        initializeMenuButton(this.sortMenuButton);

        // displays the selected course above the filter window
        label_selectedCourse.setText(MessageFormat.format(
                MainApp.resourceBundle.getString("question_filter_selected_course"),
                SharedData.getSelectedCourse().getName())
        );
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

    public void onToggleDifficultyBtnClick(ActionEvent actionEvent) {
        on_toggle_btn_click(difficultySlider, difficulty_toggle_image_view, difficultyFilterMethod);
        difficultyFilterMethod = (difficultyFilterMethod + 1) % 4;
        searchQuestions();
    }

    public void onTogglePointsBtnClick(ActionEvent actionEvent) {
        on_toggle_btn_click(pointsSlider, points_toggle_image_view, pointsFilterMethod);
        pointsFilterMethod = (pointsFilterMethod + 1) % 4;
        searchQuestions();
    }

    public void applyFilter(ActionEvent actionEvent) {
        searchQuestions();
    }

    public void onKeyTyped(KeyEvent keyEvent) {
        searchQuestions();
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        searchQuestions();
    }

    private void searchQuestions() {
        // set filter values, if given
        SortType sortType;
        if (SortType.checkType(sortMenuButton.getText())) {
            sortType = SortType.valueOf(sortMenuButton.getText());
        } else {
            sortType = SortType.CREATED_AT;
        }

        // create a new Question object to hold filter values
        Question filterQuestion = new Question();

        // get filter values from text fields and checkboxes
        String categoryName = categoryComboBox.getSelectionModel().getSelectedItem();   // this returns null, if nothing was selected
        String questionText = questionTextField.getText();
        String questionTypeString = questionTypeMenuButton.getText();

        // set category value if provided
        if (categoryName != null) {
            Category category = categoryService.getByName(categoryName, SharedData.getSelectedCourse());
            filterQuestion.setCategory(category);
        }

        // set keywords (zero, one or multiple)
        if (!selectedKeywords.isEmpty()) {
            filterQuestion.setKeywords(selectedKeywords);
        }

        // set the question text
        if (!(Objects.equals(questionText, "") || questionText == null)) {
            filterQuestion.setQuestion(questionText);
        }

        // set points and difficulty if set
        if (!pointsSlider.isDisabled()) {
            filterQuestion.setPoints((float) pointsSlider.getValue());
        }
        if (!difficultySlider.isDisabled()) {
            filterQuestion.setDifficulty((int) difficultySlider.getValue());
        }

        // set questionType value
        if (Type.checkType(questionTypeString)) {
            filterQuestion.setType(questionTypeString);
        }

        // call Repository to search for questions corresponding to filter values
        List<Question> result = questionService.getByFilters(filterQuestion, SharedData.getSelectedCourse().getId(), pointsFilterMethod, difficultyFilterMethod, sortType, sortDirectionStatus);
        SharedData.getFilteredQuestions().clear();

        if (result.isEmpty()) {
            SharedData.setOperation(Message.NO_QUESTIONS_FOUND);
            Logger.log(getClass().getName(), "No questions found", LogLevel.INFO);
            return;
        }

        // save to our SharedData
        for (Question question : result) {
            SharedData.getFilteredQuestions().add(question);
        }
    }

    public void onSortDirectionBtnClick(ActionEvent actionEvent) {
        sortDirectionStatus = (sortDirectionStatus + 1) % 2;
        changeSortArrows(sortDirectionImageView, sortDirectionStatus);
        searchQuestions();
    }

    public void changeSortArrows(ImageView arrowImageView, int status) {
        String imagePath = "src/main/resources/com/example/application/icons/";

        if (status == 0) {      // ASCENDING
            imagePath += "arrow_up.png";
        } else {                // DESCENDING
            imagePath += "arrow_down.png";
        }

        File file = new File(imagePath);
        javafx.scene.image.Image toggleImageFile = new Image(file.toURI().toString());
        arrowImageView.setImage(toggleImageFile);
    }

    private void initializeMenuButton(MenuButton menuButton) {
        menuButton.getItems().clear();

        for (SortType type : SortType.values()) {
            String displayedSortType = "sort_" + SortType.getSortTypeLowercase(type);
            MenuItem menuItem = new MenuItem(MainApp.resourceBundle.getString(displayedSortType));
            menuItem.setOnAction(e -> {
                menuButton.setText(type.toString());
                searchQuestions();
            });
            menuButton.getItems().add(menuItem);
        }

        // setting createdAt as default value (when switching screen)
        menuButton.setText(SortType.CREATED_AT.toString());
    }
}
