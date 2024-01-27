package com.example.frontend.controller;


import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.Question;
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
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Home_ScreenController extends ScreenController {

    @FXML
    private MenuButton studyProgramMenuButton;

    @FXML
    private MenuButton coursesMenuButton;



    @FXML
    private void initialize() {
        loadStudyPrograms();

        coursesMenuButton.getItems().clear();
        //coursesMenuButton.setText("Programs");
        MenuItem menuItem = new MenuItem("Choose a StudyProgram first");
        coursesMenuButton.getItems().add(menuItem);
        //loadCourses();
    }

    @FXML
    public void onStudyProgramBtnClick(ActionEvent event) {
        loadStudyPrograms();
    }

    @FXML
    public void onCoursesBtnClick(ActionEvent event) {
        System.out.println("Selected Study Program ID (loadCourses): " + SharedData.getSelectedStudyProgram().getProgram_id());
        loadCourses();
    }

    @FXML
    public void onContinueBtnClick(ActionEvent event) throws IOException {
        if (SharedData.getSelectedCourse() != null && SharedData.getSelectedStudyProgram()!= null) {
            System.out.println("Selected Study Program: " + SharedData.getSelectedStudyProgram().getProgram_name());
            System.out.println("Selected Study ProgramID: " + SharedData.getSelectedStudyProgram().getProgram_name());
            System.out.println("Selected Course: " + SharedData.getSelectedCourse().getCourse_name());
            System.out.println("Selected CourseID: " + SharedData.getSelectedCourse().getCourse_id());
            switchScene(createTestAutomatic, true);
        }
    }

    private void loadStudyPrograms() {
        ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll();
        studyProgramMenuButton.getItems().clear();

        for (StudyProgram studyProgram : studyPrograms) {
            MenuItem menuItem = new MenuItem(studyProgram.getProgram_name());
            menuItem.setOnAction(e -> {
                studyProgramMenuButton.setText(studyProgram.getProgram_name());
                SharedData.setSelectedStudyProgram(studyProgram);
                //if the studyprogram menu item is selected then the course menu and the course selection (variable)
                // is cleared/reset and the associated courses will get loaded in the course menu
                resetCourseMenuButton();
                loadCourses();
            });
            studyProgramMenuButton.getItems().add(menuItem);
        }
        //MenuItem menuItem = new MenuItem("add StudyProgram");
        Button customButton = new Button("add StudyProgram");
        CustomMenuItem customMenuItem = new CustomMenuItem(customButton);
        customButton.setOnAction(e -> {
            studyProgramMenuButton.setText("add StudyProgram");
            addStudyProgram();
        });
        //studyProgramMenuButton.getItems().add(menuItem);
        studyProgramMenuButton.getItems().add(customMenuItem);
    }

    private void loadCourses() {
        ArrayList<Course> courses = SQLiteDatabaseConnection.courseRepository.getAll(SharedData.getSelectedStudyProgram().getProgram_id());
        //System.out.println("Selected Study ProgramID in loadCourses(): " + courses.get(0).getCourse_name());
        //coursesMenuButton.getItems().clear(); // Clear existing items

        for (Course course : courses) {
            MenuItem menuItem = new MenuItem(course.getCourse_name());
            menuItem.setOnAction(e -> {
                coursesMenuButton.setText(course.getCourse_name());
                SharedData.setSelectedCourse(course);

            });
            coursesMenuButton.getItems().add(menuItem);

        }
        MenuItem menuItem = new MenuItem("add Course");
        menuItem.getStyleClass().add("homeScreen_addButton_menu_item");
        menuItem.setOnAction(e -> {
            coursesMenuButton.setText("add Course");

            addCourse();
        });
        coursesMenuButton.getItems().add(menuItem);


    }

    private void addStudyProgram() {

        //create new stage
        Stage inputStage = new Stage();
        inputStage.setTitle("Add StudyProgram");

        //create layout
        VBox inputLayout = new VBox();
        inputLayout.getStyleClass().add("homeScreen_gridPane");

        //create texfield for studyProgram name
        TextField inputName = new TextField();
        Label inputNameLabel = new Label("Enter new Study Program Name:");
        inputNameLabel.getStyleClass().add("addCourseAndProgram_label");
        inputLayout.getChildren().add(inputNameLabel);
        inputLayout.getChildren().add(inputName);

        //create textfield for studyProgram abbreviation
        TextField inputAbbr = new TextField();
        Label inputAbbrLabel = new Label("Enter new Study Program Abbreviation:");
        inputAbbrLabel.getStyleClass().add("addCourseAndProgram_label");
        inputLayout.getChildren().add(inputAbbrLabel);
        inputLayout.getChildren().add(inputAbbr);

        //create confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> {
            String enteredName = inputName.getText();
            String enteredAbbr = inputAbbr.getText();
            SharedData.getNewStudyProgram().setProgram_name(enteredName);
            SharedData.getNewStudyProgram().setProgram_abbr(enteredAbbr);

            //check if name or abbreviation already exists in db
           ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll();
           boolean exists = false;
           for(StudyProgram studyProgram : studyPrograms) {
               if (studyProgram.getProgram_name().equals(enteredName)  || studyProgram.getProgram_abbr().equals(enteredAbbr)) {
                   exists = true;
                   break;
               }
           }
           //if not then create new study program entry in db
           if (!exists) {
               StudyProgram newProgram = new StudyProgram(enteredName,enteredAbbr);
               SQLiteDatabaseConnection.studyProgramRepository.add(newProgram);
           }
            //closes the windows
            inputStage.close();
           loadStudyPrograms();
        });
        inputLayout.getChildren().add(confirmButton);
        Scene inputScene = new Scene(inputLayout, 300, 100);
        inputScene.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());
        inputStage.setScene(inputScene);

        inputStage.show();
    }

    private void addCourse() {

        //create new stage
        Stage inputStage = new Stage();
        inputStage.setTitle("Add Course");

        //create layout
        VBox inputLayout = new VBox();
        inputLayout.getStyleClass().add("homeScreen_gridPane");


        //create texfield for course name
        TextField inputName = new TextField();
        Label inputNameLabel = new Label("Enter new Course Name:");
        inputNameLabel.getStyleClass().add("addCourseAndProgram_label");
        inputLayout.getChildren().add(inputNameLabel);
        inputLayout.getChildren().add(inputName);


        //create spinner for course number
        Spinner<Integer> numericSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        numericSpinner.setValueFactory(valueFactory);

        Label spinnerLabel = new Label("Enter new course number:");
        spinnerLabel.getStyleClass().add("addCourseAndProgram_label");

        // place spinner and label in hbox
        HBox hbox = new HBox(10); // 10 is the spacing between elements
        hbox.setPadding(new Insets(10));
        hbox.getChildren().addAll(spinnerLabel, numericSpinner);
        //add the hbox to layout
        inputLayout.getChildren().add(hbox);


        //add textfield for lecturer
        TextField inputLecturer = new TextField();
        Label inputLecturerLabel = new Label("Enter new Lecturer:");
        inputLecturerLabel.getStyleClass().add("addCourseAndProgram_label");
        inputLayout.getChildren().add(inputLecturerLabel);
        inputLayout.getChildren().add(inputLecturer);

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

            SharedData.getNewCourse().setCourse_name(enteredName);
            SharedData.getNewCourse().setCourse_number(enteredNumber);

            //check if course name or course number already exists in db
            ArrayList<Course> courses = SQLiteDatabaseConnection.courseRepository.getAll(SharedData.getSelectedStudyProgram().getProgram_id());
            boolean exists = false;
            for(Course course : courses) {
                if (course.getCourse_name().equals(enteredName)  || course.getCourse_number() == enteredNumber) {
                    exists = true;
                    break;
                }
            }
            //if not then create new study program entry in db
            if (!exists) {
                Course newCourse= new Course(enteredName,enteredNumber,enteredLecturer);
                SQLiteDatabaseConnection.courseRepository.add(newCourse);
                Course newlyAddedCOurse = SQLiteDatabaseConnection.courseRepository.get(newCourse.getCourse_name());
                SQLiteDatabaseConnection.courseRepository.addConnection(SharedData.getSelectedStudyProgram(), newlyAddedCOurse);
            }
            //closes the windows
            inputStage.close();
            resetCourseMenuButton();
            loadCourses();
        });
        inputLayout.getChildren().add(confirmButton);

        Scene inputScene = new Scene(inputLayout, 380, 150);
        inputScene.getStylesheets().add(getClass().getResource("/com/example/frontend/css/main.css").toExternalForm());
        inputStage.setScene(inputScene);

        inputStage.show();
    }



    //helper function resets menu button
    void resetStudyProgramMenuButton() {
        studyProgramMenuButton.getItems().clear();
        studyProgramMenuButton.setText("Courses");
        SharedData.setSelectedCourse(null);

    }

    //helper function resets menu button
    void resetCourseMenuButton() {
        coursesMenuButton.getItems().clear();
        coursesMenuButton.setText("Courses");
        SharedData.setSelectedCourse(null);
    }
}
