package com.example.backend.app;

import com.example.backend.db.models.*;
import com.example.backend.db.models.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class SharedData {
    @Getter
    @Setter
    //stores the users course selection from the Home Screen
    private static Course selectedCourse;

    @Getter
    @Setter
    //stores the course the user wants to add
    private static Course newCourse = new Course();

    @Getter
    @Setter
    //stores the users study program selection from the Home Screen
    private static StudyProgram selectedStudyProgram;

    @Getter
    @Setter
    //stores the study program the user wants to add
    private static StudyProgram newStudyProgram = new StudyProgram();

    @Getter
    @Setter
    private static com.example.backend.db.models.Question filterQuestion = new Question();

    @Getter
    @Setter
    private static ArrayList<Question> testQuestions = new ArrayList<>();

    @Getter
    @Setter
    private static ObservableList<Question> filteredQuestions = FXCollections.observableArrayList();

    @Getter
    @Setter
    private static ArrayList<ArrayList<SearchObject<?>>> searchObjectsAutTestCreate = new ArrayList<>();


    //resets the SharedData class. Used with the HomeScreen Button (FHTW-LOGO)
    public static void resetAll() {
        selectedCourse = null;
        newCourse = new Course();
        selectedStudyProgram = null;
        newStudyProgram = new StudyProgram();
        filterQuestion = new Question();
        testQuestions = new ArrayList<>();
        filteredQuestions = FXCollections.observableArrayList();
        searchObjectsAutTestCreate = new ArrayList<>();
    }

    //reset the Questions stored from the Automatic Test Create (mainly created for use in CreateManual_ScreenController)
    public static void resetQuestions() {
        filterQuestion = new Question();
        testQuestions = new ArrayList<>();
        searchObjectsAutTestCreate = new ArrayList<>();
    }

}
