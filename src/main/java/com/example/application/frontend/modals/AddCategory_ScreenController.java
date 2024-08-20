package com.example.application.frontend.modals;

import com.example.application.backend.db.services.CategoryService;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AddCategory_ScreenController extends ModalController {
    private final CategoryService categoryService;
    public Button saveBtn;
    public Button deleteBtn;
    public TableView categoryTable;
    public TextField categoryTextField;

    public AddCategory_ScreenController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public void onGoBackBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onSaveBtnClick(ActionEvent actionEvent) {

    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {

    }
}
