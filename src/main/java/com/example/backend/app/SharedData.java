package com.example.backend.app;

import com.example.backend.db.models.*;
import com.example.backend.db.models.Question;
import com.example.frontend.controller.Home_ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

import static com.example.frontend.controller.TitleBanner_ScreenController.resetOperationStatusAfterDelay;

@Getter
@Setter
@AllArgsConstructor
public class SharedData {

    // Default value for pageTitle // TODO: use @Getter and @Setter if possible
    private static StringProperty pageTitle = new SimpleStringProperty("");
    private static StringProperty operationStatus = new SimpleStringProperty("");
    private static BooleanProperty operationIsErrorType = new SimpleBooleanProperty(false);

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
    private static ArrayList<String> suggestedCategories = new ArrayList<>();

    @Getter
    @Setter
    private static ArrayList<ArrayList<SearchObject<?>>> searchObjectsAutTestCreate = new ArrayList<>();

    @Getter
    @Setter
    private static Screen currentScreen = Screen.CreateAutomatic;

    //resets the SharedData class. Used with the HomeScreen Button (FHTW-LOGO)
    public static void resetAll() {
        selectedCourse = null;
        newCourse = new Course();
        selectedStudyProgram = null;
        newStudyProgram = new StudyProgram();
        filterQuestion = new Question();
        testQuestions = new ArrayList<>();
        filteredQuestions = FXCollections.observableArrayList();
        suggestedCategories = new ArrayList<>();
        searchObjectsAutTestCreate = new ArrayList<>();
    }

    //reset the Questions stored from the Automatic Test Create (mainly created for use in CreateManual_ScreenController)
    public static void resetQuestions() {
        filterQuestion = new Question();
        testQuestions = new ArrayList<>();
        searchObjectsAutTestCreate = new ArrayList<>();
    }

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

    // Getter and setter for operationStatus
    public static String getOperationStatus() {
        return operationStatus.get();
    }

    public static void setOperation(String messageString, boolean isErrorType) {
        operationStatus.set(messageString);
        operationIsErrorType.set(isErrorType);
        resetOperationStatusAfterDelay();
    }

    public static void setOperation(Message message) {
        operationStatus.set(message.getMessage());
        operationIsErrorType.set(message.isError());
        resetOperationStatusAfterDelay();
    }

    // Property accessor for operationStatus
    public static StringProperty operationStatusProperty() {
        return operationStatus;
    }

    // Getter and setter for operationIsErrorType
    public static boolean getOperationIsErrorType() {
        return operationIsErrorType.get();
    }

    public static void setOperationIsErrorType(boolean isErrorType) {
        operationIsErrorType.set(isErrorType);
    }

    // Property accessor for operationIsErrorType
    public static BooleanProperty operationIsErrorTypeProperty() {
        return operationIsErrorType;
    }

}
