package com.example.backend.app;

import com.example.backend.db.models.*;
import com.example.backend.db.models.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class SharedData {

    // Default value for pageTitle
    private static final String DEFAULT_PAGE_TITLE = "default page title";

    private static StringProperty pageTitle = new SimpleStringProperty(DEFAULT_PAGE_TITLE);

    // Getter and setter for pageTitle
    public static String getPageTitle() {
        return pageTitle.get();
    }

    public static void setPageTitle(String pageTitle) {
        SharedData.pageTitle.set(pageTitle);
    }

    // Property accessor for pageTitle
    public static StringProperty pageTitleProperty() {
        return pageTitle;
    }



    // Default value for operationStatus
    private static final String DEFAULT_STATUS_MESSAGE = "default message";

    private static StringProperty operationStatus = new SimpleStringProperty(DEFAULT_STATUS_MESSAGE);

    // Getter and setter for operationStatus
    public static String getOperationStatus() {
        return operationStatus.get();
    }

    public static void setOperationStatus(String operationStatus) {
        SharedData.operationStatus.set(operationStatus);
    }

    // Property accessor for operationStatus
    public static StringProperty operationStatusProperty() {
        return operationStatus;
    }



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
    private static Question filterQuestion = new Question();

    @Getter
    @Setter
    private static ArrayList<Question> testQuestions = new ArrayList<>();

    @Getter
    @Setter
    private static ObservableList<Question> filteredQuestions = FXCollections.observableArrayList();
    @Getter
    @Setter
    private static ObservableList<String> suggestedCategories = FXCollections.observableArrayList();

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
        suggestedCategories = FXCollections.observableArrayList();
        searchObjectsAutTestCreate = new ArrayList<>();
    }

    //reset the Questions stored from the Automatic Test Create (mainly created for use in CreateManual_ScreenController)
    public static void resetQuestions() {
        filterQuestion = new Question();
        testQuestions = new ArrayList<>();
        searchObjectsAutTestCreate = new ArrayList<>();
    }

}
