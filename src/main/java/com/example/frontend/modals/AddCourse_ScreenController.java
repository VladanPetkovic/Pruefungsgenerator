package com.example.frontend.modals;

import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;

public class AddCourse_ScreenController extends ModalController {
    public Button deleteBtn;
    public Button saveBtn;
    @FXML
    private TextField inputName;
    @FXML
    private Spinner<Integer> numericSpinner;
    @FXML
    private TextField inputLecturer;

    @FXML
    private void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, Integer.MAX_VALUE, 0);
        numericSpinner.setValueFactory(valueFactory);

        // check, if a course was selected for editing
        Course toEdit = SharedData.getSelectedEditCourse();
        if (toEdit.getId() != 0) {
            initData(toEdit);
        } else {
            resetData();
        }
    }

    private void initData(Course course) {
        inputName.setText(course.getName());
        inputLecturer.setText(course.getLector());
        numericSpinner.getValueFactory().setValue(course.getNumber());
        deleteBtn.setVisible(true);
        saveBtn.setText(MainApp.resourceBundle.getString("save"));
    }

    private void resetData() {
        inputName.setText("");
        inputLecturer.setText("");
        numericSpinner.getValueFactory().setValue(0);
        deleteBtn.setVisible(false);
        saveBtn.setText(MainApp.resourceBundle.getString("create"));
    }

    @FXML
    private void handleConfirm() throws IOException {
        String enteredName = inputName.getText();
        int enteredNumber = numericSpinner.getValue();
        String enteredLecturer = inputLecturer.getText();

        if (!enteredName.isEmpty() && !enteredLecturer.isEmpty() && enteredNumber != 0) {
            if (SharedData.getSelectedEditCourse().getId() != 0) {
                updateCourse(enteredName, enteredNumber, enteredLecturer);
            } else {
                createCourse(enteredName, enteredNumber, enteredLecturer);
            }

            Stage stage = (Stage) inputName.getScene().getWindow();
            stage.close();
        }
    }

    private void createCourse(String enteredName, int enteredNumber, String enteredLecturer) throws IOException {
        ArrayList<Course> courses = SQLiteDatabaseConnection.COURSE_REPOSITORY.getAll(SharedData.getSelectedStudyProgram().getId());
        boolean exists = false;
        for (Course course : courses) {
            if (course.getName().equals(enteredName) || course.getNumber() == enteredNumber) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            Course newCourse = new Course(enteredName, enteredNumber, enteredLecturer);
            SQLiteDatabaseConnection.COURSE_REPOSITORY.add(newCourse);
            Course newlyAddedCourse = SQLiteDatabaseConnection.COURSE_REPOSITORY.get(newCourse.getName());
            SQLiteDatabaseConnection.COURSE_REPOSITORY.addConnection(SharedData.getSelectedStudyProgram(), newlyAddedCourse);
        }
    }

    private void updateCourse(String enteredName, int enteredNumber, String enteredLecturer) {
        int courseId = SharedData.getSelectedEditCourse().getId();
        Course toUpdate = new Course(courseId, enteredName, enteredNumber, enteredLecturer);
        SQLiteDatabaseConnection.COURSE_REPOSITORY.update(toUpdate);
    }

    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        Stage confirmStage = ModalOpener.openModal(ModalOpener.CONFIRM_DELETION);

        confirmStage.setOnHidden((WindowEvent event) -> {
            // question was deleted
            if (SharedData.getSelectedEditCourse().getId() == 0) {
                closeStage(actionEvent);
            }
        });
    }
}
