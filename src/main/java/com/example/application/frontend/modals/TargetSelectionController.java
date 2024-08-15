package com.example.application.frontend.modals;

import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Course;
import com.example.application.backend.db.models.StudyProgram;
import com.example.application.MainApp;
import com.example.application.backend.db.services.CourseService;
import com.example.application.backend.db.services.StudyProgramService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class TargetSelectionController {
    private final StudyProgramService studyProgramService;
    private final CourseService courseService;
    @FXML
    private MenuButton targetStudyProgramMenuBtn;
    @FXML
    private MenuButton targetCourseMenuBtn;

    private String targetStudyProgram;
    private String targetCourse;

    public TargetSelectionController(StudyProgramService studyProgramService, CourseService courseService) {
        this.studyProgramService = studyProgramService;
        this.courseService = courseService;
    }

    @FXML
    private void initialize() {
        populateStudyProgramMenuBtn();
        MenuItem menuItem = new MenuItem(MainApp.resourceBundle.getString("menuitem_choose_study_program"));
        targetCourseMenuBtn.getItems().add(menuItem);
    }

    private void populateStudyProgramMenuBtn() {
        List<StudyProgram> studyPrograms = studyProgramService.getAll();
        for (StudyProgram studyProgram : studyPrograms) {
            MenuItem menuItem = new MenuItem(studyProgram.getName());
            menuItem.setOnAction(e -> {
                targetStudyProgramMenuBtn.setText(studyProgram.getName());
                targetStudyProgram = studyProgram.getName();
                SharedData.setImportTargetStudyProgram(targetStudyProgram);
                populateCourseMenuBtn(studyProgram.getId());
            });
            targetStudyProgramMenuBtn.getItems().add(menuItem);
        }
    }

    private void populateCourseMenuBtn(Long studyProgramId) {
        List<Course> courses = courseService.getAllByStudyProgram(studyProgramId);
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
