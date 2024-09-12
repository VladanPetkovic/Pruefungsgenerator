package com.example.application.frontend.components;

import com.example.application.MainApp;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.services.CategoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.text.MessageFormat;

public class CreateTestOptions_ScreenController extends ControlsInitializer {
    private final CategoryService categoryService;

    public Spinner<Double> pointsSpinner;
    public Slider difficultySlider;
    public ComboBox<String> categoryComboBox;
    public Label questionNumberLabel;
    public ImageView pointsToggleImageView;
    public ImageView difficultyToggleImageView;

    public int pointsSpinnerStatus = 0;        // 0 = disabled; 1 = enabled; 2 = min; 3 = max
    public int difficultySliderStatus = 0;     // 0 = disabled; 1 = enabled; 2 = min; 3 = max

    public CreateTestOptions_ScreenController(CategoryService categoryService) {
        super();
        this.categoryService = categoryService;
    }

    @FXML
    private void initialize() {
        initCategoryComboBox(categoryComboBox, categoryService.getAllByCourseId(SharedData.getSelectedCourse().getId()));
        initDoubleSpinner(this.pointsSpinner, 1, 10, 1, 0.5);
    }

    public void setQuestionNumberLabel(int questionCount) {
        String questionAndNumber = MessageFormat.format(MainApp.resourceBundle.getString("question_with_number"), questionCount);
        questionNumberLabel.setText(questionAndNumber);
    }

    public void onTogglePointsSpinnerClick(ActionEvent actionEvent) {
        on_toggle_btn_click(pointsSpinner, pointsToggleImageView, pointsSpinnerStatus);
        pointsSpinnerStatus = (pointsSpinnerStatus + 1) % 4;
    }

    public void onToggleDifficultyClick(ActionEvent actionEvent) {
        on_toggle_btn_click(difficultySlider, difficultyToggleImageView, difficultySliderStatus);
        difficultySliderStatus = (difficultySliderStatus + 1) % 4;
    }
}
