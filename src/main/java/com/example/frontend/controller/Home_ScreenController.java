package com.example.frontend.controller;


import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.Screen;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.StudyProgram;
import com.example.backend.app.SharedData;
import com.example.frontend.modals.AddCourse_ScreenController;
import com.example.frontend.modals.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.frontend.controller.SwitchScene.switchScene;

/**
 * controller class for the home screen
 * manages interaction and functionality of the home screen UI components
 */
public class Home_ScreenController extends ScreenController {

    @FXML
    private MenuButton studyProgramMenuButton;

    @FXML
    private MenuButton coursesMenuButton;


    /**
     * initializes the home screen
     * resets and loads study programs and sets up initial UI state
     */
    @FXML
    private void initialize() {
        resetStudyProgramMenuButton();
        loadStudyPrograms();

        resetCourseMenuButton();
        MenuItem menuItem = new MenuItem("Choose a StudyProgram first");
        coursesMenuButton.getItems().add(menuItem);
    }

    // event handler for study program button click
    @FXML
    public void onStudyProgramBtnClick(ActionEvent event) {
        loadStudyPrograms();
    }

    // event handler for courses button click
    @FXML
    public void onCoursesBtnClick(ActionEvent event) {
        System.out.println("Selected Study Program ID (loadCourses): " + SharedData.getSelectedStudyProgram().getId());
        loadCourses();
    }

    // event handler for continue button click
    @FXML
    public void onContinueBtnClick(ActionEvent event) throws IOException {
        SharedData.setCurrentScreen(Screen.CREATE_AUTOMATIC);
        if (SharedData.getSelectedCourse() != null && SharedData.getSelectedStudyProgram() != null) {
            Logger.log(getClass().getName(), "Selected Study Program: " + SharedData.getSelectedStudyProgram().getName(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected Study ProgramID: " + SharedData.getSelectedStudyProgram().getId(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected Course: " + SharedData.getSelectedCourse().getName(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected CourseID: " + SharedData.getSelectedCourse().getId(), LogLevel.INFO);
            switchScene(SwitchScene.CREATE_TEST_AUTOMATIC);
        }
    }

    // loads available study programs into the menu
    private void loadStudyPrograms() {
        ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll();
        studyProgramMenuButton.getItems().clear();

        for (StudyProgram studyProgram : studyPrograms) {
            MenuItem menuItem = new MenuItem(studyProgram.getName());
            menuItem.setOnAction(e -> {
                studyProgramMenuButton.setText(studyProgram.getName());
                SharedData.setSelectedStudyProgram(studyProgram);
                // if the study program menu item is selected then the course menu and the course selection (variable)
                // is cleared/reset and the associated courses will get loaded in the course menu
                resetCourseMenuButton();
                loadCourses();
            });
            studyProgramMenuButton.getItems().add(menuItem);
        }

        // add option to add a new study program
        Button customButton = new Button("add Study Program");
        //todo
        customButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        CustomMenuItem customMenuItem = new CustomMenuItem(customButton);
        customButton.setOnAction(e -> {
            studyProgramMenuButton.setText("add Study Program");
            try {
                addStudyProgram();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        //studyProgramMenuButton.getItems().add(menuItem);
        studyProgramMenuButton.getItems().add(customMenuItem);
    }

    // loads available courses into the menu
    private void loadCourses() {
        ArrayList<Course> courses = SQLiteDatabaseConnection.courseRepository.getAll(SharedData.getSelectedStudyProgram().getId());
        //System.out.println("Selected Study ProgramID in loadCourses(): " + courses.get(0).getCourse_name());
        //coursesMenuButton.getItems().clear(); // Clear existing items

        for (Course course : courses) {
            MenuItem menuItem = new MenuItem(course.getName());
            menuItem.setOnAction(e -> {
                coursesMenuButton.setText(course.getName());
                SharedData.setSelectedCourse(course);
            });
            coursesMenuButton.getItems().add(menuItem);
        }

        // add button to add a new course
        Button customButton = new Button("add Course");
        customButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        CustomMenuItem customMenuItem = new CustomMenuItem(customButton);
        customButton.setOnAction(e -> {
            coursesMenuButton.setText("add Course");
            try {
                addCourse();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        coursesMenuButton.getItems().add(customMenuItem);
    }

    // method to add a new study program
    private void addStudyProgram() throws IOException {
        Stage newStage = new Stage();
        Modal<AddCourse_ScreenController> new_study_program_modal = new Modal<>("modals/add_StudyProgram.fxml");
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Add Study Program");
        newStage.setScene(new_study_program_modal.scene);

        //listener for when the stage is closed
        newStage.setOnHidden(event -> {
            loadStudyPrograms();
        });
        newStage.show();
    }

    // method to add a new course
    private void addCourse() throws IOException {
        Stage newStage = new Stage();
        Modal<AddCourse_ScreenController> new_course_modal = new Modal<>("modals/add_Course.fxml");
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Add Study Program");
        newStage.setScene(new_course_modal.scene);

        //listener for when the stage is closed
        newStage.setOnHidden(event -> {
            resetCourseMenuButton();
            loadCourses();
        });
        newStage.show();
    }


    // helper function to reset study program menu button
    void resetStudyProgramMenuButton() {
        studyProgramMenuButton.getItems().clear();
        studyProgramMenuButton.setText("Study Programs");
        SharedData.setSelectedStudyProgram(null);
    }

    // helper function to reset course menu button
    void resetCourseMenuButton() {
        coursesMenuButton.getItems().clear();
        coursesMenuButton.setText("Courses");
        SharedData.setSelectedCourse(null);
    }
}
