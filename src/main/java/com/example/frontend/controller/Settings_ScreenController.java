package com.example.frontend.controller;

import com.example.backend.app.ExportCSV;
import com.example.backend.app.ImportCSV;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.Message;
import com.example.backend.db.models.StudyProgram;
import com.example.frontend.MainApp;
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
    public Label label_selectedFile;
    @FXML
    Button settingsImportBtn;

    @FXML
    private void initialize() {
        ArrayList<String> studyPrograms = SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.getAll().stream()
                .map(StudyProgram::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> courses = SQLiteDatabaseConnection.COURSE_REPOSITORY.getAll(SharedData.getSelectedStudyProgram().getId())
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
        if (Objects.equals(this.label_selectedFile.getText(), "\"\"")) {
            SharedData.setOperation(Message.ERROR_MESSAGE_FILE_NOT_SELECTED);
            return;
        }
        ImportCSV importCSV = new ImportCSV(this.label_selectedFile.getText());

        // Start the import process
        boolean isSuccess = importCSV.importData();

        // Update SharedData with a success or error message
        if (isSuccess) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_DATA_IMPORTED);
        } else {
            SharedData.setOperation(Message.ERROR_MESSAGE_IMPORT_FAILED);
        }
    }

    public void onChooseFileBtnClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(MainApp.resourceBundle.getString("select_csv_file"));

        // Set extension filter to only allow CSV files
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(MainApp.resourceBundle.getString("csv_files"), "*.csv")
        );

        File file = fileChooser.showOpenDialog(MainApp.stage);
        if (file != null) {
            label_selectedFile.setText(file.toString());
        }
    }

    public void chooseDirectoryBtnClicked(ActionEvent actionEvent) {
        chooseDirectory(this.label_selectedDirectory);
    }

    public void allQuestionsSelectedForExport(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("all_questions"));
        this.chooseStudyProgramMenuBtn.setVisible(false);
        this.chooseCourseMenuButton.setVisible(false);
        this.chooseQuestionsLabel.setVisible(false);
    }

    /**
     * SP ... StudyProgram
     */
    public void questionsOfSPselected(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("questions_of_study_program"));
        this.chooseStudyProgramMenuBtn.setVisible(true);
        this.chooseCourseMenuButton.setVisible(false);
        this.chooseQuestionsLabel.setText(MainApp.resourceBundle.getString("select_study_program"));
        this.chooseQuestionsLabel.setVisible(true);
    }

    public void questionsOfCourseSelected(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("questions_of_course"));
        this.chooseCourseMenuButton.setVisible(true);
        this.chooseStudyProgramMenuBtn.setVisible(false);
        this.chooseQuestionsLabel.setText(MainApp.resourceBundle.getString("select_course"));
        this.chooseQuestionsLabel.setVisible(true);
    }

    public void applyExportBtnClicked(ActionEvent actionEvent) {
        if (!allFieldsSetProperly()) {
            return;
        }

        ExportCSV exportCSV = new ExportCSV(this.label_selectedDirectory.getText());
        int exportType = 0;     // all questions
        if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_study_program"))) {
            exportType = 1;     // only for studyProgram
            exportCSV.initStudyProgram(chooseStudyProgramMenuBtn.getText());
        } else if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_course"))) {
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
        if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_course"))) {
            if (Objects.equals(chooseCourseMenuButton.getText(), "")) {
                SharedData.setOperation(Message.ERROR_MESSAGE_INPUT_ALL_FIELDS);
                return false;
            }
        }
        if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_study_program"))) {
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
