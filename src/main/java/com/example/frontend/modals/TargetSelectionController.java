package com.example.frontend.modals;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.StudyProgram;
import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TargetSelectionController {
    @FXML
    private MenuButton targetStudyProgramMenuBtn;
    @FXML
    private MenuButton targetCourseMenuBtn;

    @FXML
    private Button okBtn;

    @FXML
    private Button cancelBtn;

    private String targetStudyProgram;
    private String targetCourse;
    private int targetStudyProgramId;

    @FXML
    private void initialize() {
        populateStudyProgramMenuBtn();
        MenuItem menuItem = new MenuItem(MainApp.resourceBundle.getString("menuitem_choose_study_program"));
        targetCourseMenuBtn.getItems().add(menuItem);
    }

    private void populateStudyProgramMenuBtn() {
        ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.getAll();
        for (StudyProgram studyProgram : studyPrograms) {
            MenuItem menuItem = new MenuItem(studyProgram.getName());
            menuItem.setOnAction(e -> {
                targetStudyProgramMenuBtn.setText(studyProgram.getName());
                targetStudyProgram = studyProgram.getName();
                SharedData.setImportTargetStudyProgram(targetStudyProgram);
                targetStudyProgramId = studyProgram.getId();
                populateCourseMenuBtn(targetStudyProgramId);
            });
            targetStudyProgramMenuBtn.getItems().add(menuItem);
        }
    }

    private void populateCourseMenuBtn(int studyProgramId) {
        ArrayList<Course> courses = SQLiteDatabaseConnection.COURSE_REPOSITORY.getAll(studyProgramId);
        targetCourseMenuBtn.getItems().clear();
        for (Course course : courses) {
            MenuItem menuItem = new MenuItem(course.getName());
            menuItem.setOnAction(e -> {
                targetCourseMenuBtn.setText(course.getName());
                targetCourse = course.getName();
                SharedData.setImportTargetCourse(targetCourse);
            });
            targetCourseMenuBtn.getItems().add(menuItem);
        }
    }

    @FXML
    private void onOkBtnClick(ActionEvent event) {
        saveSelectedOptions();
        closeWindow();
    }

    private void saveSelectedOptions() {
        if (targetStudyProgram != null && targetCourse != null && SharedData.getImportTargetStudyProgram() == null && SharedData.getImportTargetCourse() == null) {
            SharedData.setImportTargetStudyProgram(targetStudyProgram);
            SharedData.setImportTargetCourse(targetCourse);
        }
    }

    @FXML
    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) targetStudyProgramMenuBtn.getScene().getWindow();
        stage.close();
    }
}
