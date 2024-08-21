package com.example.application.frontend.modals;

import com.example.application.MainApp;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Category;
import com.example.application.backend.db.models.CategoryWrapper;
import com.example.application.backend.db.services.CategoryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class AddCategory_ScreenController extends ModalController {
    private final CategoryService categoryService;
    public TableView<CategoryWrapper> categoryTable;
    public TextField categoryTextField;
    public TableColumn<CategoryWrapper, String> categoryTableColumn;
    public TableColumn<CategoryWrapper, Long> questionCountTableColumn;
    public Label messageLabel;

    @FXML
    private void initialize() {
        // initialize the TableColumns with the appropriate properties from the CategoryWrapper
        categoryTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().getName())
        );
        questionCountTableColumn.setCellValueFactory(new PropertyValueFactory<>("questionCount"));

        // retrieve the data
        List<CategoryWrapper> categoryWrappers = categoryService.getAllByCourseIdWithQuestionCount(SharedData.getSelectedCourse().getId());

        // convert the list to an ObservableList and set it to the TableView
        ObservableList<CategoryWrapper> observableCategoryWrappers = FXCollections.observableArrayList(categoryWrappers);
        categoryTable.setItems(observableCategoryWrappers);
    }

    public AddCategory_ScreenController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public void onGoBackBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        CategoryWrapper selectedCategory = categoryTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            messageLabel.setText(MainApp.resourceBundle.getString("no_category_selected"));
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // checking, if category still has questions
        if (selectedCategory.getQuestionCount() > 0) {
            messageLabel.setText(MainApp.resourceBundle.getString("category_still_has_questions"));
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // otherwise: delete
        categoryTable.getItems().remove(selectedCategory);
        categoryService.remove(selectedCategory.getCategory().getId());
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setText(MainApp.resourceBundle.getString("delete_category_success_message"));
        SharedData.getSuggestedCategories().remove(selectedCategory.getCategory().getName());
    }

    public void onUpdateBtnClick(ActionEvent actionEvent) {
        CategoryWrapper selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        String newCategory = categoryTextField.getText();

        if (selectedCategory == null) {
            messageLabel.setText(MainApp.resourceBundle.getString("no_category_selected"));
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (!isValid(newCategory)) {
            return;
        }

        // otherwise: update
        SharedData.getSuggestedCategories().remove(selectedCategory.getCategory().getName());
        selectedCategory.getCategory().setName(newCategory);
        categoryTable.refresh();
        SharedData.getSuggestedCategories().add(newCategory);
        categoryService.update(selectedCategory.getCategory());
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setText(MainApp.resourceBundle.getString("update_category_success_message"));
    }

    public void onCreateBtnClick(ActionEvent actionEvent) {
        String newCategory = categoryTextField.getText();

        if (!isValid(newCategory)) {
            return;
        }

        // otherwise create new one
        Category createdCategory = categoryService.add(new Category(newCategory), SharedData.getSelectedCourse());
        categoryTable.getItems().add(new CategoryWrapper(createdCategory, 0L));
        categoryTable.refresh();
        SharedData.getSuggestedCategories().add(newCategory);
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setText(MainApp.resourceBundle.getString("create_category_success_message"));
    }

    /**
     * This function checks, if a category is valid (for update or creation)
     *
     * @param newCategory The String to check
     * @return false, if existing or too long String,... AND true otherwise
     */
    private boolean isValid(String newCategory) {
        String categoryCheck = Category.checkNewCategory(newCategory);
        if (categoryCheck != null) {
            messageLabel.setText(MainApp.resourceBundle.getString("category_not_valid"));
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        Category existingCategory = categoryService.getByName(newCategory, SharedData.getSelectedCourse());
        if (existingCategory != null) {
            messageLabel.setText(MainApp.resourceBundle.getString("category_already_existing"));
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        return true;
    }
}
