package com.example.application.frontend.components;

import com.example.application.backend.db.models.*;
import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.services.CategoryService;
import com.example.application.backend.db.services.KeywordService;
import com.example.application.backend.db.services.QuestionService;
import com.example.application.frontend.controller.ScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    public TextField keywordTextField;
    @FXML
    public TextField questionTextField;
    @FXML
    public TextField categoryTextField;
    @FXML
    public Label label_selectedCourse;
    @FXML
    public Button add_category_btn;
    @FXML
    public ImageView difficulty_toggle_image_view;
    @FXML
    public ImageView points_toggle_image_view;

    private int pointsFilterMethod = 0;     // 0 = disabled; 1 = enabled; 2 = min; 3 = max
    private int difficultyFilterMethod = 0;     // 0 = disabled; 1 = enabled; 2 = min; 3 = max

    public QuestionFilter_ScreenController(QuestionService questionService, KeywordService keywordService, CategoryService categoryService) {
        super();
        this.questionService = questionService;
        this.keywordService = keywordService;
        this.categoryService = categoryService;
    }

    @FXML
    private void initialize() {
        // init auto-completion
        initializeKeywords(this.keywordTextField, keywordService.getAllByCourseId(SharedData.getSelectedCourse().getId()), null);
        initializeCategories(this.categoryTextField, categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId()), add_category_btn);
        initializeQuestions(this.questionTextField, questionService.getAllByCourseId(SharedData.getSelectedCourse().getId()));
        List<Type> questionTypes = Arrays.asList(Type.values());
        initializeMenuButton(this.questionTypeMenuButton, true, questionTypes);

        // displays the selected course above the filter window
        label_selectedCourse.setText(SharedData.getSelectedCourse().getName());
    }

    public void on_add_category_btn_click(ActionEvent actionEvent) throws IOException {
        if (Category.checkNewCategory(categoryTextField.getText()) == null) {
            Category newCategory = categoryService.add(new Category(categoryTextField.getText()), SharedData.getSelectedCourse());
            addCategoryBtnClick(newCategory, add_category_btn);
        }
    }

    public void on_toggle_difficulty_btn_click(ActionEvent actionEvent) {
        on_toggle_btn_click(difficultySlider, difficulty_toggle_image_view, difficultyFilterMethod);
        difficultyFilterMethod = (difficultyFilterMethod + 1) % 4;
    }

    public void on_toggle_points_btn_click(ActionEvent actionEvent) {
        on_toggle_btn_click(pointsSlider, points_toggle_image_view, pointsFilterMethod);
        pointsFilterMethod = (pointsFilterMethod + 1) % 4;
    }

    public void applyFilterButtonClicked(ActionEvent actionEvent) {
        searchQuestions();
    }

    private void searchQuestions() {
        // create a new Question object to hold filter values
        Question filterQuestion = new Question();

        // get filter values from text fields and checkboxes
        String categoryName = categoryTextField.getText().trim();
        String keywordText = keywordTextField.getText().trim();
        String questionText = questionTextField.getText();
        String questionTypeString = questionTypeMenuButton.getText();

        // set category value if provided
        if (!categoryName.isEmpty()) {
            Category category = categoryService.getByName(categoryName, SharedData.getSelectedCourse());
            filterQuestion.setCategory(category);
        }

        // set keyword value if provided
        // handle multiple keywords as ArrayList<Keyword>
        Set<Keyword> keywordHashSet = new HashSet<>();
        if (!keywordText.isEmpty()) {
            // split by commas or spaces
            String[] keywordsArray = keywordText.split("[,\\s]+");
            for (String keyword : keywordsArray) {
                Keyword keywordObj = keywordService.getByName(keyword.trim());
                if (keywordObj != null) {
                    keywordHashSet.add(keywordObj);
                }
            }
        }
        if (!keywordHashSet.isEmpty()) {
            filterQuestion.setKeywords(keywordHashSet);
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
        List<Question> result = questionService.getByFilters(filterQuestion, SharedData.getSelectedCourse().getId(), pointsFilterMethod, difficultyFilterMethod);
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
}
