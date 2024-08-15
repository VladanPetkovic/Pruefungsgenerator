package com.example.application.frontend.modals;

import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.StudyProgram;
import com.example.application.MainApp;
import com.example.application.backend.db.services.StudyProgramService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AddStudyProgram_ScreenController extends ModalController {
    private final StudyProgramService studyProgramService;
    public Button deleteBtn;
    public Button saveBtn;
    @FXML
    private TextField inputName;
    @FXML
    private TextField inputAbbr;
    @FXML
    private VBox inputLayout;

    public AddStudyProgram_ScreenController(StudyProgramService studyProgramService) {
        super();
        this.studyProgramService = studyProgramService;
    }

    @FXML
    private void initialize() {
        // check, if a studyProgram was selected for editing
        StudyProgram toEdit = SharedData.getSelectedEditStudyProgram();
        if (toEdit.getId() != null) {
            initData(toEdit);
        } else {
            resetData();
        }
    }

    private void initData(StudyProgram studyProgram) {
        inputName.setText(studyProgram.getName());
        inputAbbr.setText(studyProgram.getAbbreviation());
        deleteBtn.setVisible(true);
        saveBtn.setText(MainApp.resourceBundle.getString("save"));
    }

    private void resetData() {
        inputName.setText("");
        inputAbbr.setText("");
        deleteBtn.setVisible(false);
        saveBtn.setText(MainApp.resourceBundle.getString("create"));
    }

    @FXML
    private void handleConfirmButtonAction(ActionEvent event) {
        String enteredName = inputName.getText();
        String enteredAbbr = inputAbbr.getText();

        if (!enteredName.isEmpty() && !enteredAbbr.isEmpty()) {
            if (SharedData.getSelectedEditStudyProgram().getId() != null) {
                updateStudyProgram(enteredName, enteredAbbr);
            } else {
                createStudyProgram(enteredName, enteredAbbr);
            }

            Stage stage = (Stage) inputLayout.getScene().getWindow();
            stage.close();
        }
    }

    private void createStudyProgram(String enteredName, String enteredAbbr) {
        StudyProgram newProgram = new StudyProgram(enteredName, enteredAbbr);
        studyProgramService.add(newProgram);
        // TODO: the add() method returns a studyProgram -> check whether null and display an appropriate message
    }

    private void updateStudyProgram(String enteredName, String enteredAbbr) {
        Long id = SharedData.getSelectedEditStudyProgram().getId();
        StudyProgram toUpdate = new StudyProgram(id, enteredName, enteredAbbr);
        studyProgramService.update(toUpdate);
    }

    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        Stage confirmStage = ModalOpener.openModal(ModalOpener.CONFIRM_DELETION);

        confirmStage.setOnHidden((WindowEvent event) -> {
            // question was deleted
            if (SharedData.getSelectedEditStudyProgram().getId() == null) {
                closeStage(actionEvent);
            }
        });
    }
}
