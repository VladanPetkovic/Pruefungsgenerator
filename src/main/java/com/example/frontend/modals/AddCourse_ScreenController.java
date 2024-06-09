package com.example.frontend.modals;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;

import java.util.ArrayList;


public class AddCourse_ScreenController {
    @FXML
    private TextField inputName;

    @FXML
    private Spinner<Integer> numericSpinner;

    @FXML
    private TextField inputLecturer;

    @FXML
    private void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        numericSpinner.setValueFactory(valueFactory);
    }

    @FXML
    private void handleConfirm() {
        String enteredName = inputName.getText();
        int enteredNumber = numericSpinner.getValue();
        String enteredLecturer = inputLecturer.getText();

        if (!enteredName.isEmpty()  && !enteredLecturer.isEmpty() && enteredNumber != 0) {
            SharedData.getNewCourse().setName(enteredName);
            SharedData.getNewCourse().setNumber(enteredNumber);

            ArrayList<Course> courses = SQLiteDatabaseConnection.courseRepository.getAll(SharedData.getSelectedStudyProgram().getId());
            boolean exists = false;
            for (Course course : courses) {
                if (course.getName().equals(enteredName) || course.getNumber() == enteredNumber) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                Course newCourse = new Course(enteredName, enteredNumber, enteredLecturer);
                SQLiteDatabaseConnection.courseRepository.add(newCourse);
                Course newlyAddedCourse = SQLiteDatabaseConnection.courseRepository.get(newCourse.getName());
                SQLiteDatabaseConnection.courseRepository.addConnection(SharedData.getSelectedStudyProgram(), newlyAddedCourse);
            }

            Stage stage = (Stage) inputName.getScene().getWindow();
            stage.close();
        }
    }
}
