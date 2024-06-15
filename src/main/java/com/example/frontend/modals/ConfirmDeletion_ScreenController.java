package com.example.frontend.modals;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Question;
import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ConfirmDeletion_ScreenController extends ModalController {

    public Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        if (SharedData.getSelectedEditQuestion().getId() != 0) {
            deleteQuestion(actionEvent);
        } else if (SharedData.getSelectedEditCourse().getId() != 0) {
            deleteCourse(actionEvent);
        } else if (SharedData.getSelectedEditStudyProgram().getId() != 0) {
            deleteStudyProgram(actionEvent);
        }
    }

    private void deleteQuestion(ActionEvent actionEvent) {
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

        SharedData.setSelectedEditQuestion(new Question());
        closeStage(actionEvent);
    }

    private void deleteCourse(ActionEvent actionEvent) {
        int courseId = SharedData.getSelectedEditCourse().getId();
        boolean hasCategories = SQLiteDatabaseConnection.COURSE_REPOSITORY.hasCategories(courseId);

        if (hasCategories) {
            errorLabel.setText(MainApp.resourceBundle.getString("error_course_has_categories"));
        } else {
            SQLiteDatabaseConnection.COURSE_REPOSITORY.remove(SharedData.getSelectedEditCourse());
            closeStage(actionEvent);
        }
    }

    private void deleteStudyProgram(ActionEvent actionEvent) {
        // TODO
    }
}
