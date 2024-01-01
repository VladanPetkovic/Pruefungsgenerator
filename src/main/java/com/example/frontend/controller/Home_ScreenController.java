package com.example.frontend.controller;


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
                // when you click on the first option of studyprograms, you get the name of the first study program

        System.out.println("MenuButton clicked");
        StudyProgramDAO sdao = new StudyProgramDAO();
        StudyProgramRepository repo = new StudyProgramRepository(sdao);
        StudyProgram program = repo.get(1); // id = 1
        System.out.println(program.getProgram_name());
        System.out.println(program.getProgram_abbr());
    }
}
