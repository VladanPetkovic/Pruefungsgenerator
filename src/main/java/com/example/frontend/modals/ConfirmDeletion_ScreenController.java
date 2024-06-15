package com.example.frontend.modals;

import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Question;
import javafx.event.ActionEvent;

public class ConfirmDeletion_ScreenController extends ModalController {

    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) {
        // remove from other arrays
        int questionId = SharedData.getSelectedEditQuestion().getId();
        SharedData.getTestQuestions().removeIf(question -> question.getId() == questionId);
        SharedData.getFilteredQuestions().removeIf(question -> question.getId() == questionId);

        // delete in database
        SQLiteDatabaseConnection.questionRepository.remove(SharedData.getSelectedEditQuestion());

        // remove images, keywords and answers, that are not used
        SQLiteDatabaseConnection.keywordRepository.removeUnused();
        SQLiteDatabaseConnection.imageRepository.removeUnused();
        SQLiteDatabaseConnection.ANSWER_REPOSITORY.removeUnused();
        SQLiteDatabaseConnection.CategoryRepository.removeUnused();

        SharedData.setSelectedEditQuestion(new Question());
        closeStage(actionEvent);
    }
}
