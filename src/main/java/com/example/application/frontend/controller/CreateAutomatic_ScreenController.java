package com.example.application.frontend.controller;

import com.example.application.backend.app.Screen;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.app.SortType;
import com.example.application.backend.db.models.Question;
import com.example.application.MainApp;
import com.example.application.backend.db.services.CategoryService;
import com.example.application.backend.db.services.QuestionService;
import com.example.application.frontend.components.CreateTestOptions_ScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Scope("prototype")
public class CreateAutomatic_ScreenController extends ScreenController {
    private final QuestionService questionService;
    private final CategoryService categoryService;
    @FXML
    private VBox addQuestionVBox; // reference to the VBox containing the "Add Question" button
    private int questionCount = 0;
    private final List<CreateTestOptions_ScreenController> questionControllers = new ArrayList<>();

    public CreateAutomatic_ScreenController(QuestionService questionService, CategoryService categoryService) {
        super();
        this.questionService = questionService;
        this.categoryService = categoryService;
    }

    @FXML
    public void initialize() throws IOException {
        // set (press & release) event handlers for all buttons that are dynamically generated
        setButtonEventHandlers(addQuestionVBox.getChildren());
        onAddQuestionBtnClick();
    }

    private void setButtonEventHandlers(List<Node> nodes) {
        for (Node node : nodes) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnMousePressed(this::onButtonPressed);
                button.setOnMouseReleased(this::onButtonReleased);
            } else if (node instanceof VBox) {
                setButtonEventHandlers(((VBox) node).getChildren());
            }
        }
    }

    @FXML
    private void onAddQuestionBtnClick() throws IOException {
        questionCount++;

        FXMLLoader loader = FXMLDependencyInjection.getLoader("components/create_test_options.fxml", MainApp.resourceBundle);
        VBox newQuestionVBox = loader.load();

        // get the controller for the loaded component
        CreateTestOptions_ScreenController controller = loader.getController();
        questionControllers.add(controller);

        // add question-number-label
        controller.setQuestionNumberLabel(questionCount);

        VBox grandparentVBox = (VBox) addQuestionVBox.getParent().getParent();
        // get the index of the parent of addQuestionVBox in its grandparent
        int parentIndex = grandparentVBox.getChildren().indexOf(addQuestionVBox.getParent());

        // add the new VBox just before the addQuestionVBox
        grandparentVBox.getChildren().add(parentIndex, newQuestionVBox);
    }

    // method triggered when the "Create Test" button is clicked
    @FXML
    protected void onCreateAutTestBtnClick(ActionEvent event) throws IOException {
        // clear previous questions from manual-create
        SharedData.getTestQuestions().clear();

        for (CreateTestOptions_ScreenController controller : questionControllers) {
            // initialize variables to store selected category, difficulty, and points
            String selectedCategory = controller.categoryComboBox.getSelectionModel().getSelectedItem();
            double selectedPoints = controller.pointsSpinner.getValue();
            Integer selectedDifficulty = (int) controller.difficultySlider.getValue();
            Question queryQuestion = new Question();

            // set the category
            if (selectedCategory != null) {
                queryQuestion.setCategory(categoryService.getByName(selectedCategory, SharedData.getSelectedCourse()));
            }
            // set the points
            queryQuestion.setPoints((float) selectedPoints);

            // set the difficulty
            queryQuestion.setDifficulty(selectedDifficulty);

            // perform the database query to retrieve questions based on the criteria
            int pointStatus = controller.pointsSpinnerStatus;
            int diffStatus = controller.difficultySliderStatus;

            List<Question> queryResult = questionService.getByFilters(queryQuestion, SharedData.getSelectedCourse().getId(), pointStatus, diffStatus, SortType.POINTS, 0);

            // check if query result is not empty
            if (!queryResult.isEmpty()) {

                // TODO: add different methods of test-creation
                // generate a random index to select a question from the result
                Random random = new Random();
                int randomIndex = random.nextInt(queryResult.size());
                // get the randomly selected question
                Question newQuestion = queryResult.get(randomIndex);
                if (!containsQuestionWithId(newQuestion.getId(), SharedData.getTestQuestions())) {
                    // add the selected question to the test questions list, if not already existing in the testQuestions-array
                    SharedData.getTestQuestions().add(newQuestion);
                }
            }
        }

        // reset the question count to zero
        this.questionCount = 0;
        SharedData.setCurrentScreen(Screen.CREATE_MANUAL);
        SwitchScene.switchScene(SwitchScene.CREATE_TEST_MANUAL);
    }
}