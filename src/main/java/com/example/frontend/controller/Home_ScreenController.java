package com.example.frontend.controller;


import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.StudyProgram;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.util.ArrayList;

public class Home_ScreenController extends ScreenController {

    @FXML
    private MenuButton studyProgramMenuButton;

    @FXML
    private MenuButton coursesMenuButton;

    @FXML
    private void initialize() {
        loadStudyPrograms();
        // You can also load courses here if needed
        // loadCourses();
    }

    @FXML
    public void onContinueBtnClick(ActionEvent event) throws IOException {
        System.out.println("continue Button Click");
        switchScene(createTestAutomatic, true);
    }

    @FXML
    public void onStudyProgramBtnClick(ActionEvent event) {
        loadStudyPrograms();
        Question testQuestion = new Question();
        testQuestion.setLanguage("Deutsch");

        ArrayList<Question> list2 = SQLiteDatabaseConnection.questionRepository.getAll(testQuestion, "MACS1");

        for(Question question : list2) {
            System.out.println("_----------------------------------_");
            System.out.println(question.getQuestion_id());
            System.out.println(question.getQuestionString());
            System.out.println(question.getAnswers());
            System.out.println(question.getCategory().getTopic());
        }

        // set random Question-values to get all questions for a new test
        Question testQuestion2 = new Question();
        testQuestion.setMultipleChoice(0);

        ArrayList<Question> list3 = SQLiteDatabaseConnection.questionRepository.getAll(testQuestion, "MACS1");

        for(Question question : list3) {
            System.out.println("_----------------------------------_");
            System.out.println(question.getQuestion_id());
            System.out.println(question.getQuestionString());
            System.out.println(question.getAnswers());
            System.out.println(question.getCategory().getTopic());
        }
    }

    @FXML
    public void onCoursesBtnClick(ActionEvent event) {
        // Implement loading courses here if needed
        // loadCourses();
    }

    private void loadStudyPrograms() {
        ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll();
        studyProgramMenuButton.getItems().clear(); // Clear existing items

        for (StudyProgram studyProgram : studyPrograms) {
            MenuItem menuItem = new MenuItem(studyProgram.getProgram_name());
            menuItem.setOnAction(e -> {
                // Handle the selection of a study program if needed
                // For example: setSelectedStudyProgram(studyProgram);
            });
            studyProgramMenuButton.getItems().add(menuItem);
        }
    }
}
