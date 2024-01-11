package com.example.frontend.controller;


import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.daos.QuestionDAO;
import com.example.backend.db.daos.StudyProgramDAO;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.StudyProgram;
import com.example.backend.db.models.Topic;
import com.example.backend.db.repositories.QuestionRepository;
import com.example.backend.db.repositories.StudyProgramRepository;
import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.ArrayList;

public class Home_ScreenController extends ScreenController {


    @FXML
    public void onContinueBtnClick(ActionEvent event) throws IOException {
        System.out.println("continue Button Click");
        switchScene(createTestAutomatic,true);
    }
    @FXML
    public void onStudyProgramBtnClick(ActionEvent event) {
        // testing, if daos and repositories work correctly
        // when you click on the first option of studyprograms, you get all study programs

        ArrayList<StudyProgram> list = SQLiteDatabaseConnection.studyProgramRepository.getAll();
        for(StudyProgram studyProgram : list) {
            System.out.println("Studyprogram: " + studyProgram.getProgram_name());
        }


        // set random Question-values to get all questions for a new test
        Question testQuestion = new Question();
        testQuestion.setLanguage("Deutsch");

        ArrayList<Question> list2 = SQLiteDatabaseConnection.questionRepository.getAll(testQuestion);

        for(Question question : list2) {
            System.out.println("_----------------------------------_");
            System.out.println(question.getQuestion_id());
            System.out.println(question.getQuestionString());
            System.out.println(question.getAnswers());
            System.out.println(question.getTopic().getTopic());
        }

        // set random Question-values to get all questions for a new test
        Question testQuestion2 = new Question();
        testQuestion.setMultipleChoice(0);

        ArrayList<Question> list3 = SQLiteDatabaseConnection.questionRepository.getAll(testQuestion);

        for(Question question : list3) {
            System.out.println("_----------------------------------_");
            System.out.println(question.getQuestion_id());
            System.out.println(question.getQuestionString());
            System.out.println(question.getAnswers());
            System.out.println(question.getTopic().getTopic());
        }
    }
}
