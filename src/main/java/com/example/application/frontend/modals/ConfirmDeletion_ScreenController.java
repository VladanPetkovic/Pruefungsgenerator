package com.example.application.frontend.modals;

import com.example.application.backend.app.SharedData;
import com.example.application.MainApp;
import com.example.application.backend.db.services.CourseService;
import com.example.application.backend.db.services.QuestionService;
import com.example.application.backend.db.services.StudyProgramService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope("prototype")
public class ConfirmDeletion_ScreenController extends ModalController {
    private final StudyProgramService studyProgramService;
    private final CourseService courseService;
    private final QuestionService questionService;
    public Label errorLabel;

    public ConfirmDeletion_ScreenController(StudyProgramService studyProgramService, CourseService courseService, QuestionService questionService) {
        super();
        this.studyProgramService = studyProgramService;
        this.courseService = courseService;
        this.questionService = questionService;
    }

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onDeleteBtnClick(ActionEvent actionEvent) throws IOException {
        if (SharedData.getSelectedEditQuestion().getId() != null) {
            deleteQuestion(actionEvent);
        } else if (SharedData.getSelectedEditCourse().getId() != null) {
            deleteCourse(actionEvent);
        } else if (SharedData.getSelectedEditStudyProgram().getId() != null) {
            deleteStudyProgram(actionEvent);
        }
    }

    private void deleteQuestion(ActionEvent actionEvent) throws IOException {
        // remove from other arrays
//        int questionId = SharedData.getSelectedEditQuestion().getId();
//        SharedData.getTestQuestions().removeIf(question -> question.getId() == questionId);
//        SharedData.getFilteredQuestions().removeIf(question -> question.getId() == questionId);
//
//        // delete in database
//        SQLiteDatabaseConnection.QUESTION_REPOSITORY.remove(SharedData.getSelectedEditQuestion());
//
//        // remove images, keywords and answers, that are not used
//        SQLiteDatabaseConnection.KEYWORD_REPOSITORY.removeUnused();
//        SQLiteDatabaseConnection.IMAGE_REPOSITORY.removeUnused();
//        SQLiteDatabaseConnection.ANSWER_REPOSITORY.removeUnused();
//        SQLiteDatabaseConnection.CATEGORY_REPOSITORY.removeUnused();
//
//        SharedData.resetEditObjects();
//        closeStage(actionEvent);
    }

    private void deleteCourse(ActionEvent actionEvent) throws IOException {
        Long courseId = SharedData.getSelectedEditCourse().getId();
        boolean hasCategories = courseService.hasCategories(courseId);

        if (hasCategories) {
            errorLabel.setText(MainApp.resourceBundle.getString("error_course_has_categories"));
        } else {
            courseService.remove(courseId);
            SharedData.resetEditObjects();
            closeStage(actionEvent);
        }
    }

    private void deleteStudyProgram(ActionEvent actionEvent) throws IOException {
        Long id = SharedData.getSelectedEditStudyProgram().getId();
        boolean hasCourses = studyProgramService.hasCourses(id);

        if (hasCourses) {
            errorLabel.setText(MainApp.resourceBundle.getString("error_study_program_has_courses"));
        } else {
            studyProgramService.remove(id);
            SharedData.resetEditObjects();
            closeStage(actionEvent);
        }
    }
}
