package com.example.application.frontend.modals;

import com.example.application.MainApp;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Keyword;
import com.example.application.backend.db.models.KeywordWrapper;
import com.example.application.backend.db.services.KeywordService;
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
public class AddKeyword_ScreenController extends ModalController {
    private final KeywordService keywordService;
    public TableView<KeywordWrapper> keywordTable;
    public TableColumn<KeywordWrapper, String> keywordTableColumn;
    public TableColumn<KeywordWrapper, Long> questionCountTableColumn;
    public TextField keywordTextField;
    public Label messageLabel;

    public AddKeyword_ScreenController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @FXML
    private void initialize() {
        // initialize the TableColumns with the appropriate properties from the KeywordWrapper
        keywordTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKeyword().getKeyword())
        );
        questionCountTableColumn.setCellValueFactory(new PropertyValueFactory<>("questionCount"));

        // retrieve the data
        List<KeywordWrapper> keywordWrappers = keywordService.getAllByCourseIdWithQuestionCount(SharedData.getSelectedCourse().getId());

        // convert the list to an ObservableList and set it to the TableView
        ObservableList<KeywordWrapper> observableKeywordWrappers = FXCollections.observableArrayList(keywordWrappers);
        keywordTable.setItems(observableKeywordWrappers);
    }

    public void onGoBackBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        KeywordWrapper selectedKeyword = keywordTable.getSelectionModel().getSelectedItem();

        if (selectedKeyword == null) {
            messageLabel.setText(MainApp.resourceBundle.getString("no_keyword_selected"));
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // checking, if keyword still belongs to questions
        if (selectedKeyword.getQuestionCount() > 0) {
            messageLabel.setText(MainApp.resourceBundle.getString("keyword_still_has_questions"));
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // otherwise: delete
        keywordTable.getItems().remove(selectedKeyword);
        keywordService.remove(selectedKeyword.getKeyword().getId());
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setText(MainApp.resourceBundle.getString("delete_keyword_success_message"));
    }

    public void onUpdateBtnClick(ActionEvent actionEvent) {
        KeywordWrapper selectedKeyword = keywordTable.getSelectionModel().getSelectedItem();
        String newKeyword = keywordTextField.getText();

        if (selectedKeyword == null) {
            messageLabel.setText(MainApp.resourceBundle.getString("no_keyword_selected"));
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (!isValid(newKeyword)) {
            return;
        }

        // otherwise: update
        selectedKeyword.getKeyword().setKeyword(newKeyword);
        keywordTable.refresh();
        keywordService.update(selectedKeyword.getKeyword());
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setText(MainApp.resourceBundle.getString("update_keyword_success_message"));
    }

    public void onCreateBtnClick(ActionEvent actionEvent) {
        String newKeyword = keywordTextField.getText();

        if (!isValid(newKeyword)) {
            return;
        }

        // otherwise create new one
        Keyword createdKeyword = keywordService.add(new Keyword(newKeyword), SharedData.getSelectedCourse());
        keywordTable.getItems().add(new KeywordWrapper(createdKeyword, 0L));
        keywordTable.refresh();
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setText(MainApp.resourceBundle.getString("create_keyword_success_message"));
    }

    /**
     * This function checks, if a keyword is valid (for update or creation)
     *
     * @param newKeyword The String to check
     * @return false, if existing or too long String,... AND true otherwise
     */
    private boolean isValid(String newKeyword) {
        String keywordCheck = Keyword.checkNewKeyword(newKeyword);
        if (keywordCheck != null) {
            messageLabel.setText(MainApp.resourceBundle.getString("keyword_not_valid"));
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        Keyword existingKeyword = keywordService.getByName(newKeyword, SharedData.getSelectedCourse());
        if (existingKeyword != null) {
            messageLabel.setText(MainApp.resourceBundle.getString("keyword_already_existing"));
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        return true;
    }
}
