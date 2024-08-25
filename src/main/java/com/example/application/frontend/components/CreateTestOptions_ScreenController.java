package com.example.application.frontend.components;

import com.example.application.MainApp;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.services.CategoryService;
import com.example.application.frontend.controller.ScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.text.MessageFormat;

public class CreateTestOptions_ScreenController extends ScreenController {
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
        initPointsSpinner();
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

    /**
     * this function is copied from the CustomDoubleSpinner-class. Later on it should be used for
     * initialization of the pointsSpinner and can be moved to the ScreenController-base-class.
     * The point is to not initialize fxml-code in java - and write fxml-code in fxml-files (when possible).
     */
    private void initPointsSpinner() {
        double min = 1;
        double max = 10;
        double initialValue = 1;
        double amountToStepBy = 0.5;

        pointsSpinner.setEditable(true);
        pointsSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, amountToStepBy));

        // Get the TextField portion of the Spinner
        TextField textField = pointsSpinner.getEditor();

        // Create a TextFormatter to allow only double input
        TextFormatter<Double> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("-?\\d*(\\,\\d*)?")) {
                return change;
            } else {
                return null;
            }
        });

        // Set the TextFormatter to the TextField
        textField.setTextFormatter(formatter);

        pointsSpinner.setOnMouseClicked(event -> {
            if (pointsSpinner.getEditor().getText().isEmpty()) {
                // If the text field is empty and the user clicks on the spinner,
                // set the value to the initial value
                pointsSpinner.getValueFactory().setValue(initialValue);
            }
        });

        pointsSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                // If the new value is null, set it to the initial value
                pointsSpinner.getValueFactory().setValue(initialValue);
            } else {
                // Round the new value to the nearest multiple of amountToStepBy
                double roundedValue = Math.round(newValue / amountToStepBy) * amountToStepBy;
                // Ensure the rounded value is within the specified range
                roundedValue = Math.max(min, Math.min(max, roundedValue));
                // Set the spinner's value to the rounded value
                pointsSpinner.getValueFactory().setValue(roundedValue);
            }
        });
    }
}
