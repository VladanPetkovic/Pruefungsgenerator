package com.example.frontend.modals;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.StudyProgram;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
public class AddStudyProgram_ScreenController {

    @FXML
    private TextField inputName;

    @FXML
    private TextField inputAbbr;

    @FXML
    private Label inputNameLabel;

    @FXML
    private Label inputAbbrLabel;

    @FXML
    private VBox inputLayout;

    @FXML
    private void handleConfirmButtonAction(ActionEvent event) {
        String enteredName = inputName.getText();
        String enteredAbbr = inputAbbr.getText();

        if (!enteredName.isEmpty() && !enteredAbbr.isEmpty()) {
            SharedData.getNewStudyProgram().setName(enteredName);
            SharedData.getNewStudyProgram().setAbbreviation(enteredAbbr);

            //check if name or abbreviation already exists in the database
            ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll();
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
                SQLiteDatabaseConnection.studyProgramRepository.add(newProgram);
            }

            Stage stage = (Stage) inputLayout.getScene().getWindow();
            stage.close();
        }
    }
}
