package com.example.frontend.controller;


import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.Screen;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.StudyProgram;
import com.example.backend.app.SharedData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

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
        //SharedData.setCurrentScreen(Screen.Home);
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
        SharedData.setCurrentScreen(Screen.CreateAutomatic);
        if (SharedData.getSelectedCourse() != null && SharedData.getSelectedStudyProgram()!= null) {
            Logger.log(getClass().getName(), "Selected Study Program: " + SharedData.getSelectedStudyProgram().getName(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected Study ProgramID: " + SharedData.getSelectedStudyProgram().getId(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected Course: " + SharedData.getSelectedCourse().getName(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected CourseID: " + SharedData.getSelectedCourse().getId(), LogLevel.INFO);
            switchScene(createTestAutomatic, true);
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
            addStudyProgram();
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
            addCourse();
        });
       coursesMenuButton.getItems().add(customMenuItem);
    }

    // method to add a new study program
    private void addStudyProgram() {

        // create a new stage for input
        Stage inputStage = new Stage();
        inputStage.setTitle("Add Study Program");

        // create layout
        VBox inputLayout = new VBox();
        inputLayout.getStyleClass().add("homeScreen_gridPane");

        // create text field for study program name
        TextField inputName = new TextField();
        Label inputNameLabel = new Label("Enter new Study Program Name:");
        inputNameLabel.getStyleClass().add("addCourseAndProgram_label");
        inputLayout.getChildren().add(inputNameLabel);
        inputLayout.getChildren().add(inputName);

        // create text field for study program abbreviation
        TextField inputAbbr = new TextField();
        Label inputAbbrLabel = new Label("Enter new Study Program Abbreviation:");
        inputAbbrLabel.getStyleClass().add("addCourseAndProgram_label");
        inputLayout.getChildren().add(inputAbbrLabel);
        inputLayout.getChildren().add(inputAbbr);

        // create confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> {
            String enteredName = inputName.getText();
            String enteredAbbr = inputAbbr.getText();
            SharedData.getNewStudyProgram().setName(enteredName);
            SharedData.getNewStudyProgram().setAbbreviation(enteredAbbr);

            // check if name or abbreviation already exists in the database
            ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll();
            boolean exists = false;
            for(StudyProgram studyProgram : studyPrograms) {
                if (studyProgram.getName().equals(enteredName)  || studyProgram.getAbbreviation().equals(enteredAbbr)) {
                    exists = true;
                    break;
                }
            }
            // if not, create a new study program entry in the database
            if (!exists) {
                StudyProgram newProgram = new StudyProgram(enteredName, enteredAbbr);
                SQLiteDatabaseConnection.studyProgramRepository.add(newProgram);
            }
            // close the window and reload study programs
            inputStage.close();
            loadStudyPrograms();
        });
        inputLayout.getChildren().add(confirmButton);

        // create the scene and set stylesheets
        Scene inputScene = new Scene(inputLayout, 300, 100);
        inputScene.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());
        inputStage.setScene(inputScene);

        // show the input stage
        inputStage.show();
    }

    // method to add a new course
    private void addCourse() {

        // create a new stage for input
        Stage inputStage = new Stage();
        inputStage.setTitle("Add Course");

        // create layout
        VBox inputLayout = new VBox();
        inputLayout.getStyleClass().add("homeScreen_gridPane");


        // create text field for course name
        TextField inputName = new TextField();
        Label inputNameLabel = new Label("Enter new Course Name:");
        inputNameLabel.getStyleClass().add("addCourseAndProgram_label");
        inputLayout.getChildren().add(inputNameLabel);
        inputLayout.getChildren().add(inputName);


        // create spinner for course number
        Spinner<Integer> numericSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        numericSpinner.setValueFactory(valueFactory);

        Label spinnerLabel = new Label("Enter new course number:");
        spinnerLabel.getStyleClass().add("addCourseAndProgram_label");

        // place spinner and label in HBox
        HBox hbox = new HBox(10); // 10 is the spacing between elements
        hbox.setPadding(new Insets(10));
        hbox.getChildren().addAll(spinnerLabel, numericSpinner);
        // add the HBox to layout
        inputLayout.getChildren().add(hbox);

        // add text field for lecturer
        TextField inputLecturer = new TextField();
        Label inputLecturerLabel = new Label("Enter new Lecturer:");
        inputLecturerLabel.getStyleClass().add("addCourseAndProgram_label");
        inputLayout.getChildren().add(inputLecturerLabel);
        inputLayout.getChildren().add(inputLecturer);

        // create confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> {
            String enteredName = inputName.getText();
            int enteredNumber = numericSpinner.getValue();
            String enteredLecturer = inputLecturer.getText();
            /*
            numericSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                enteredNumber = newValue;
                System.out.println("Selected value: " + newValue);
            });
            */

            SharedData.getNewCourse().setName(enteredName);
            SharedData.getNewCourse().setNumber(enteredNumber);

            // check if course name or course number already exists in the database
            ArrayList<Course> courses = SQLiteDatabaseConnection.courseRepository.getAll(SharedData.getSelectedStudyProgram().getId());
            boolean exists = false;
            for(Course course : courses) {
                if (course.getName().equals(enteredName)  || course.getNumber() == enteredNumber) {
                    exists = true;
                    break;
                }
            }
            // if not, create a new course entry in the database
            if (!exists) {
                Course newCourse= new Course(enteredName, enteredNumber, enteredLecturer);
                SQLiteDatabaseConnection.courseRepository.add(newCourse);
                Course newlyAddedCOurse = SQLiteDatabaseConnection.courseRepository.get(newCourse.getName());
                SQLiteDatabaseConnection.courseRepository.addConnection(SharedData.getSelectedStudyProgram(), newlyAddedCOurse);
            }
            // close the window, reset course menu button, and reload courses
            inputStage.close();
            resetCourseMenuButton();
            loadCourses();
        });
        inputLayout.getChildren().add(confirmButton);

        // create the scene and set stylesheets
        Scene inputScene = new Scene(inputLayout, 380, 150);
        inputScene.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());
        inputStage.setScene(inputScene);

        // show the input stage
        inputStage.show();
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
