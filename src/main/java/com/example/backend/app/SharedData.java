package com.example.backend.app;

import com.example.backend.db.models.*;
import com.example.backend.db.models.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

import static com.example.frontend.components.TitleBanner_ScreenController.resetOperationStatusAfterDelay;

@Getter
@Setter
@AllArgsConstructor
public class SharedData {

    // Default value for pageTitle
    private static StringProperty pageTitle = new SimpleStringProperty("");
    private static StringProperty operationStatus = new SimpleStringProperty("");
    private static BooleanProperty operationIsErrorType = new SimpleBooleanProperty(false);

    @Getter
    @Setter
    private static int currentLanguage = 0; // 0 for english; 1 for german

    @Getter
    @Setter
    //stores the users course selection from the Home Modal
    private static Course selectedCourse;

    @Getter
    @Setter
    //stores the users study program selection from the Home Modal
    private static StudyProgram selectedStudyProgram;

    @Getter
    @Setter
    private static Question filterQuestion = new Question();

    @Getter
    @Setter
    private static Question selectedEditQuestion = new Question();

    @Getter
    @Setter
    private static StudyProgram selectedEditStudyProgram = new StudyProgram();

    @Getter
    @Setter
    private static Course selectedEditCourse = new Course();

    @Getter
    @Setter
    private static Image resizeImage = null;

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
    private static Screen currentScreen = Screen.CREATE_AUTOMATIC;

    //resets the SharedData class. Used with the HomeScreen Button (FHTW-LOGO)
    public static void resetAll() {
        selectedCourse = null;
        selectedStudyProgram = null;
        filterQuestion = new Question();
        selectedEditQuestion = new Question();
        selectedEditStudyProgram = new StudyProgram();
        selectedEditCourse = new Course();
        testQuestions = new ArrayList<>();
        filteredQuestions = FXCollections.observableArrayList();
        suggestedCategories = new ArrayList<>();
    }

    //reset the Questions stored from the Automatic Test Create (mainly created for use in CreateManual_ScreenController)
    public static void resetQuestions() {
        filterQuestion = new Question();
        testQuestions = new ArrayList<>();
    }

    /**
     * This function reset objects, that have been selected for editing.
     * Currently, questions, courses, study-programs
     */
    public static void resetEditObjects() {
        selectedEditQuestion = new Question();
        selectedEditStudyProgram = new StudyProgram();
        selectedEditCourse = new Course();
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

    // Property accessor for operationIsErrorType
    public static BooleanProperty operationIsErrorTypeProperty() {
        return operationIsErrorType;
    }

}
