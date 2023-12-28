package com.example.frontend.controller;


import com.example.backend.db.daos.StudyProgramDAO;
import com.example.backend.db.models.StudyProgram;
import com.example.backend.db.repositories.StudyProgramRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

public class Home_ScreenController extends ScreenController {
    @FXML
    public void onStudyProgramBtnClick(ActionEvent event) {
        // testing, if daos and repositories work correctly
                // when you click on the first option of studyprograms, you get the name of the first study program

        /*System.out.println("MenuButton clicked");
        StudyProgramDAO sdao = new StudyProgramDAO();
        StudyProgramRepository repo = new StudyProgramRepository(sdao);
        ArrayList data = repo.getAll();
        StudyProgram temp = (StudyProgram) data.get(0);
        System.out.println(temp.getProgram_name());
        System.out.println(temp.getProgram_abbr());*/
    }
}
