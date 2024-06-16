package com.example.frontend.modals;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.StudyProgram;
import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

public class AddStudyProgram_ScreenController extends ModalController {
    public Button deleteBtn;
    public Button saveBtn;
    @FXML
    private TextField inputName;
    @FXML
    private TextField inputAbbr;
    @FXML
    private VBox inputLayout;

    @FXML
    private void initialize() {
        // check, if a studyProgram was selected for editing
        StudyProgram toEdit = SharedData.getSelectedEditStudyProgram();
        if (toEdit.getId() != 0) {
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
            if (SharedData.getSelectedEditStudyProgram().getId() != 0) {
                updateStudyProgram(enteredName, enteredAbbr);
            } else {
                createStudyProgram(enteredName, enteredAbbr);
            }

            Stage stage = (Stage) inputLayout.getScene().getWindow();
            stage.close();
        }
    }

    private void createStudyProgram(String enteredName, String enteredAbbr) {
        //check if name or abbreviation already exists in the database
        ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.getAll();
        boolean exists = false;
        for (StudyProgram studyProgram : studyPrograms) {
            if (studyProgram.getName().equals(enteredName) || studyProgram.getAbbreviation().equals(enteredAbbr)) {
                exists = true;
                break;
            }
        }

        //if not, create a new study program entry in the database
        if (!exists) {
            StudyProgram newProgram = new StudyProgram(enteredName, enteredAbbr);
            SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.add(newProgram);
        }
    }

    private void updateStudyProgram(String enteredName, String enteredAbbr) {
        int id = SharedData.getSelectedEditStudyProgram().getId();
        StudyProgram toUpdate = new StudyProgram(id, enteredName, enteredAbbr);
        SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.update(toUpdate);
    }

    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        Stage confirmStage = ModalOpener.openModal(ModalOpener.CONFIRM_DELETION);

        confirmStage.setOnHidden((WindowEvent event) -> {
            // question was deleted
            if (SharedData.getSelectedEditStudyProgram().getId() == 0) {
                closeStage(actionEvent);
            }
        });
    }
}
