package com.example.application.frontend.components;

import com.example.application.MainApp;
import com.example.application.backend.db.models.Category;
import com.example.application.backend.db.models.Keyword;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.models.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * This class has all methods, where controls (Slider, Spinner, MenuButton,...) are initialized.
 */
public abstract class ControlsInitializer {
    /**
     * This function is copied from the CustomDoubleSpinner-class. Later on it should be used for
     * initialization of the pointsSpinner.
     * The point is to not initialize fxml-code in java - and write fxml-code in fxml-files (when possible).
     */
    protected void initDoubleSpinner(Spinner<Double> doubleSpinner, double min, double max, double initialValue, double amountToStepBy) {
        doubleSpinner.setEditable(true);
        doubleSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, amountToStepBy));

        // Get the TextField portion of the Spinner
        TextField textField = doubleSpinner.getEditor();

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

        doubleSpinner.setOnMouseClicked(event -> {
            if (doubleSpinner.getEditor().getText().isEmpty()) {
                // If the text field is empty and the user clicks on the spinner,
                // set the value to the initial value
                doubleSpinner.getValueFactory().setValue(initialValue);
            }
        });

        doubleSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                // If the new value is null, set it to the initial value
                doubleSpinner.getValueFactory().setValue(initialValue);
            } else {
                // Round the new value to the nearest multiple of amountToStepBy
                double roundedValue = Math.round(newValue / amountToStepBy) * amountToStepBy;
                // Ensure the rounded value is within the specified range
                roundedValue = Math.max(min, Math.min(max, roundedValue));
                // Set the spinner's value to the rounded value
                doubleSpinner.getValueFactory().setValue(roundedValue);
            }
        });
    }

    /**
     * This function initializes an integerSpinner. The user can only enter 10 digits and is then blocked.
     */
    protected void initIntegerSpinner(Spinner<Integer> integerSpinner, int min, int max, int initialValue, int amountToStepBy) {
        integerSpinner.setEditable(true);
        integerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue, amountToStepBy));

        TextField textField = integerSpinner.getEditor();

        TextFormatter<Integer> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            try {
                if (newText.matches("-?\\d*")) {
                    if (!newText.isEmpty()) {
                        long parsedValue = Long.parseLong(newText);

                        // Check if value exceeds Integer limits
                        if (parsedValue > Integer.MAX_VALUE || parsedValue < Integer.MIN_VALUE) {
                            return null; // Reject the input if it exceeds bounds
                        }
                    }
                    return change; // Accept valid input
                }
            } catch (NumberFormatException e) {
                return null; // Reject invalid inputs
            }
            return null; // Reject if it doesn't match the pattern
        });

        textField.setTextFormatter(formatter);

        integerSpinner.setOnMouseClicked(event -> {
            if (integerSpinner.getEditor().getText().isEmpty()) {
                integerSpinner.getValueFactory().setValue(initialValue);
            }
        });

        integerSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                integerSpinner.getValueFactory().setValue(initialValue);
            }
        });
    }

    /**
     * This function activates/deactivates/sets to min/sets to max a slider/spinner and changes the image accordingly.
     *
     * @param sliderOrSpinner Either a difficulty or points slider (or some other)
     * @param toggleImage     The image, we want to change (toggle off or toggle on)
     * @param status          The status of the sliderOrSpinner object.
     */
    public void on_toggle_btn_click(Object sliderOrSpinner, ImageView toggleImage, int status) {
        String imagePath = "src/main/resources/com/example/application/icons/";
        Slider slider = null;
        Spinner<?> spinner = null;
        if (sliderOrSpinner instanceof Slider) {
            slider = (Slider) sliderOrSpinner;
        } else if (sliderOrSpinner instanceof Spinner) {
            spinner = (Spinner<?>) sliderOrSpinner;
        }

        switch (status) {
            case 0: // currently disabled --> enable
                if (slider != null) {
                    slider.setDisable(false);
                } else if (spinner != null) {
                    spinner.setDisable(false);
                }
                imagePath += "toggle_on.png";
                break;
            case 1: // currently enabled --> min
                imagePath += "toggle_on_min.png";
                break;
            case 2: // currently min --> max
                imagePath += "toggle_on_max.png";
                break;
            default: // current max --> disable
                if (slider != null) {
                    slider.setDisable(true);
                } else if (spinner != null) {
                    spinner.setDisable(true);
                }
                imagePath += "toggle_off.png";
                break;
        }

        File file = new File(imagePath);
        Image toggleImageFile = new Image(file.toURI().toString());
        toggleImage.setImage(toggleImageFile);
    }

    /**
     * Initializes the category-combobox
     */
    protected void initCategoryComboBox(ComboBox<String> categoryComboBox, List<Category> categories) {
        categoryComboBox.getItems().clear();

        // add categories
        for (Category category : categories) {
            categoryComboBox.getItems().add(category.getName());
        }
    }

    /**
     * Initializes the auto-completion of the questions in the search-area of edit-question.
     * Shows only 10 questions max
     */
    protected void initializeQuestions(TextField questionTextField, List<Question> questions) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < 10 && i < questions.size(); i++) {
            items.add(questions.get(i).getQuestion());
        }
        TextFields.bindAutoCompletion(questionTextField, items);
    }

    /**
     * This function initializes the MenuButton with the QuestionTypes.
     *
     * @param menuButton - the menuButton used in the scene
     */
    protected void initializeMenuButton(MenuButton menuButton, boolean allowAllTypes, List<Type> types) {
        menuButton.getItems().clear();

        for (Type type : types) {
            MenuItem menuItem = new MenuItem(type.toString());
            menuItem.setOnAction(e -> {
                menuButton.setText(type.toString());
            });
            menuButton.getItems().add(menuItem);
        }

        if (allowAllTypes) {
            // add "showAll" to showAll QuestionTypes
            MenuItem menuItem = new MenuItem(MainApp.resourceBundle.getString("all_types"));
            menuItem.setOnAction(e -> {
                menuButton.setText(MainApp.resourceBundle.getString("all_types"));
            });
            menuButton.getItems().add(menuItem);
        }
    }

    /**
     * This function fills the ComboBox with a keyword.
     * It sets an ActionEvent, when clicked --> the keyword is displayed.
     * The displayed keyword can be removed via button click.
     */
    protected void initKeywordComboBox(List<Keyword> newKeywords, Set<Keyword> selectedKeywords, HBox keywordsHBox, ComboBox<String> keywordComboBox) {
        // removing old listener
        keywordComboBox.getSelectionModel().selectedItemProperty().removeListener((observable, oldValue, newValue) -> {
        });

        keywordComboBox.getItems().clear();

        // init combobox
        for (Keyword keyword : newKeywords) {
            keywordComboBox.getItems().add(keyword.getKeyword());
        }

        // add a listener that triggers when an item is selected
        keywordComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // find the Keyword object corresponding to the selected name
            Keyword selectedKeyword = newKeywords.stream()
                    .filter(keyword -> keyword.getKeyword().equals(newValue))
                    .findFirst()
                    .orElse(null);

            if (selectedKeyword != null) {
                addSelectedKeyword(selectedKeyword, selectedKeywords, keywordsHBox);
            }
        });
    }

    protected void addSelectedKeyword(Keyword newKeyword, Set<Keyword> selectedKeywords, HBox keywordsHBox) {
        // don't add, if already added
        for (Keyword keyword : selectedKeywords) {
            if (keyword.getKeyword().equals(newKeyword.getKeyword())) {
                return;
            }
        }

        selectedKeywords.add(newKeyword);
        Button removalButton = createRemovalButton(newKeyword, keywordsHBox, selectedKeywords);
        keywordsHBox.getChildren().add(removalButton);
    }

    protected Button createRemovalButton(Keyword newKeyword, HBox keywordsHBox, Set<Keyword> selectedKeywords) {
        Button button = createButton(newKeyword.getKeyword() + " X");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // When the removal button is clicked, remove the keyword from the list of selected keywords
                keywordsHBox.getChildren().remove(button);
                selectedKeywords.remove(newKeyword);
            }
        });

        return button;
    }

    /**
     * Creates a JavaFX Button with the given text and disables focus traversal.
     *
     * @param text The text to display on the button.
     * @return The created Button
     */
    protected Button createButton(String text) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        return button;
    }
}
