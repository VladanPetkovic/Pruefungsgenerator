package com.example.frontend.controller;


import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.daos.QuestionDAO;
import com.example.backend.db.daos.StudyProgramDAO;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.StudyProgram;
import com.example.backend.db.repositories.QuestionRepository;
import com.example.backend.db.repositories.StudyProgramRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ArrayList;

public class Home_ScreenController extends ScreenController {
    @FXML
    public void onStudyProgramBtnClick(ActionEvent event) {
        // testing, if daos and repositories work correctly
                // when you click on the first option of studyprograms, you get all study programs

        ArrayList<StudyProgram> list = SQLiteDatabaseConnection.studyProgramRepository.getAll();
        for(StudyProgram studyProgram : list) {
            System.out.println("Studyprogram: " + studyProgram.getProgram_name());
        }

    }
}
