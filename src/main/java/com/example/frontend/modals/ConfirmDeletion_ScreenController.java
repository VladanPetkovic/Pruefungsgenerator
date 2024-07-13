package com.example.frontend.modals;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class ConfirmDeletion_ScreenController extends ModalController {

    public Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) throws IOException {
        if (SharedData.getSelectedEditQuestion().getId() != 0) {
            deleteQuestion(actionEvent);
        } else if (SharedData.getSelectedEditCourse().getId() != 0) {
            deleteCourse(actionEvent);
        } else if (SharedData.getSelectedEditStudyProgram().getId() != 0) {
            deleteStudyProgram(actionEvent);
        }
    }

    private void deleteQuestion(ActionEvent actionEvent) throws IOException {
        // remove from other arrays
        int questionId = SharedData.getSelectedEditQuestion().getId();
        SharedData.getTestQuestions().removeIf(question -> question.getId() == questionId);
        SharedData.getFilteredQuestions().removeIf(question -> question.getId() == questionId);

        // delete in database
        SQLiteDatabaseConnection.QUESTION_REPOSITORY.remove(SharedData.getSelectedEditQuestion());

        // remove images, keywords and answers, that are not used
        SQLiteDatabaseConnection.KEYWORD_REPOSITORY.removeUnused();
        SQLiteDatabaseConnection.IMAGE_REPOSITORY.removeUnused();
        SQLiteDatabaseConnection.ANSWER_REPOSITORY.removeUnused();
        SQLiteDatabaseConnection.CATEGORY_REPOSITORY.removeUnused();

        SharedData.resetEditObjects();
        closeStage(actionEvent);
    }

    private void deleteCourse(ActionEvent actionEvent) throws IOException {
        int courseId = SharedData.getSelectedEditCourse().getId();
        boolean hasCategories = SQLiteDatabaseConnection.COURSE_REPOSITORY.hasCategories(courseId);

        if (hasCategories) {
            errorLabel.setText(MainApp.resourceBundle.getString("error_course_has_categories"));
        } else {
            SQLiteDatabaseConnection.COURSE_REPOSITORY.remove(SharedData.getSelectedEditCourse());
            SharedData.resetEditObjects();
            closeStage(actionEvent);
        }
    }

    private void deleteStudyProgram(ActionEvent actionEvent) throws IOException {
        int id = SharedData.getSelectedEditStudyProgram().getId();
        boolean hasCourses = SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.hasCourses(id);

        if (hasCourses) {
            errorLabel.setText(MainApp.resourceBundle.getString("error_study_program_has_courses"));
        } else {
            SQLiteDatabaseConnection.STUDY_PROGRAM_REPOSITORY.remove(SharedData.getSelectedEditStudyProgram());
            SharedData.resetEditObjects();
            closeStage(actionEvent);
        }
    }
}
