package com.example.frontend.controller;

import com.example.backend.app.ExportCSV;
import com.example.backend.app.ImportCSV;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.Message;
import com.example.backend.db.models.StudyProgram;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class Settings_ScreenController extends ScreenController {
    @FXML
    public MenuButton chooseQuestionsMenuButton;
    @FXML
    public Label chooseQuestionsLabel;
    @FXML
    public MenuButton chooseStudyProgramMenuBtn;
    @FXML
    public MenuButton chooseCourseMenuButton;
    @FXML
    public Label label_selectedDirectory;
    @FXML
    Button settingsImportBtn;

    @FXML
    private void initialize() {
        ArrayList<String> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll().stream()
                .map(StudyProgram::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> courses = SQLiteDatabaseConnection.courseRepository.getAll(SharedData.getSelectedStudyProgram().getId())
                .stream()
                .map(Course::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        initializeMenuButton(chooseStudyProgramMenuBtn, studyPrograms);
        initializeMenuButton(chooseCourseMenuButton, courses);
    }

    private void initializeMenuButton(MenuButton menuButton, ArrayList<String> menuItems) {
        menuButton.getItems().clear();

        for (String string : menuItems) {
            MenuItem menuItem = new MenuItem(string);
            menuItem.setOnAction(e -> {
                menuButton.setText(string);
            });
            menuButton.getItems().add(menuItem);
        }
    }

    @FXML
    public void onImportBtnClicked() {
        // Create an instance of the ImportCSV class
        ImportCSV importCSV = new ImportCSV();

        // Start the import process
        boolean isSuccess = importCSV.importData();

        // Update SharedData with a success or error message
        if (isSuccess) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_DATA_IMPORTED);
        } else {
            SharedData.setOperation(Message.ERROR_MESSAGE_IMPORT_FAILED);
        }
    }


    public void chooseDirectoryBtnClicked(ActionEvent actionEvent) {
        chooseDirectory(this.label_selectedDirectory);
    }

    public void allQuestionsSelectedForExport(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText("All questions");
        this.chooseStudyProgramMenuBtn.setVisible(false);
        this.chooseCourseMenuButton.setVisible(false);
        this.chooseQuestionsLabel.setVisible(false);
    }

    /**
     * SP ... StudyProgram
     */
    public void questionsOfSPselected(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText("Questions of study-program");
        this.chooseStudyProgramMenuBtn.setVisible(true);
        this.chooseCourseMenuButton.setVisible(false);
        this.chooseQuestionsLabel.setText("Select a study-program:");
        this.chooseQuestionsLabel.setVisible(true);
    }

    public void questionsOfCourseSelected(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText("Questions of course");
        this.chooseCourseMenuButton.setVisible(true);
        this.chooseStudyProgramMenuBtn.setVisible(false);
        this.chooseQuestionsLabel.setText("Select a course:");
        this.chooseQuestionsLabel.setVisible(true);
    }

    public void applyExportBtnClicked(ActionEvent actionEvent) {
        if (!allFieldsSetProperly()) {
            return;
        }

        ExportCSV exportCSV = new ExportCSV(this.label_selectedDirectory.getText());
        int exportType = 0;     // all questions
        if (Objects.equals(chooseQuestionsMenuButton.getText(), "Questions of study-program")) {
            exportType = 1;     // only for studyProgram
            exportCSV.initStudyProgram(chooseStudyProgramMenuBtn.getText());
        } else if (Objects.equals(chooseQuestionsMenuButton.getText(), "Questions of course")) {
            exportType = 2;     // only for course
            exportCSV.initCourse(chooseCourseMenuButton.getText());
        }

        if (exportCSV.export(exportType)) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_QUESTIONS_EXPORTED);
        } else {
            SharedData.setOperation(Message.ERROR_MESSAGE_ERROR_OCCURRED);
        }
    }

    /**
     * Function that checks, if everything was inputted properly.
     * @return True, when user submitted everything, and false otherwise.
     */
    private boolean allFieldsSetProperly() {
        // check if everything was filled out
        if (Objects.equals(chooseQuestionsMenuButton.getText(), "Questions of course")) {
            if (Objects.equals(chooseCourseMenuButton.getText(), "")) {
                SharedData.setOperation(Message.ERROR_MESSAGE_INPUT_ALL_FIELDS);
                return false;
            }
        }
        if (Objects.equals(chooseQuestionsMenuButton.getText(), "Questions of study-program")) {
            if (Objects.equals(chooseStudyProgramMenuBtn.getText(), "")) {
                SharedData.setOperation(Message.ERROR_MESSAGE_INPUT_ALL_FIELDS);
                return false;
            }
        }
        if (this.label_selectedDirectory.getText().equals("\"\"")) {
            SharedData.setOperation(Message.ERROR_MESSAGE_SELECT_A_FOLDER_SAVE_FILE);
            return false;
        }
        return true;
    }
}
