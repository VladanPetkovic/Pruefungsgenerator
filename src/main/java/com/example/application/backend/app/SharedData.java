package com.example.application.backend.app;

import com.example.application.backend.db.models.Course;
import com.example.application.backend.db.models.Message;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.models.StudyProgram;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;

import static com.example.application.frontend.components.TitleBanner_ScreenController.resetOperationStatusAfterDelay;

@Getter
@Setter
@AllArgsConstructor
public class SharedData {
    @Getter
    private final static String filepath = "bin/crashFile.txt";

    // Default value for pageTitle
    private static StringProperty pageTitle = new SimpleStringProperty("");
    private static StringProperty operationStatus = new SimpleStringProperty("");
    private static BooleanProperty operationIsErrorType = new SimpleBooleanProperty(false);

    @Getter
    private static int currentLanguage = 0; // 0 for english; 1 for german

    @Getter
    //stores the users course selection from the Home Modal
    private static Course selectedCourse;

    @Getter
    //stores the users study program selection from the Home Modal
    private static StudyProgram selectedStudyProgram;

    @Getter
    private static Question filterQuestion = new Question();

    @Getter
    private static Question selectedEditQuestion = new Question();

    @Getter
    private static StudyProgram selectedEditStudyProgram = new StudyProgram();

    @Getter
    private static Course selectedEditCourse = new Course();

    @Getter
    private static Image resizeImage = null;

    @Getter
    @Setter
    private static ObservableList<Question> testQuestions = FXCollections.observableArrayList();
    @Getter
    @Setter
    private static ObservableList<Question> filteredQuestions = FXCollections.observableArrayList();

    @Getter
    @Setter
    private static String importTargetStudyProgram;

    @Getter
    @Setter
    private static String importTargetCourse;

    @Setter
    @Getter
    private static String modeOfImport;

    @Getter
    private static Screen currentScreen;


    //static block used for initialization ("static block will execute once when the class is loaded, ensuring that your change listeners are set up immediately.")
    static {
        initialize();
    }

    private static void initialize() {

        testQuestions.addListener((ListChangeListener<Question>) c -> {
            try {
                onAttributeChange();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        filteredQuestions.addListener((ListChangeListener<Question>) c -> {
            try {
                onAttributeChange();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void onAttributeChange() throws IOException {
        saveToFile();
    }


    //resets the SharedData class. Used with the HomeScreen Button (FHTW-LOGO)
    public static void resetAll() throws IOException {
        selectedCourse = null;
        selectedStudyProgram = null;
        filterQuestion = new Question();
        selectedEditQuestion = new Question();
        selectedEditStudyProgram = new StudyProgram();
        selectedEditCourse = new Course();
        testQuestions = FXCollections.observableArrayList();
        filteredQuestions = FXCollections.observableArrayList();
        saveToFile();
    }

    //reset the Questions stored from the Automatic Test Create (mainly created for use in CreateManual_ScreenController)
    public static void resetQuestions() throws IOException {
        filterQuestion = new Question();
        testQuestions = FXCollections.observableArrayList();
        saveToFile();
    }

    /**
     * This function reset objects, that have been selected for editing.
     * Currently, questions, courses, study-programs
     */
    public static void resetEditObjects() throws IOException {
        selectedEditQuestion = new Question();
        selectedEditStudyProgram = new StudyProgram();
        selectedEditCourse = new Course();
        saveToFile();
    }

    // Getter and setter for pageTitle
    public static String getPageTitle() {
        return pageTitle.get();
    }

    public static void setPageTitle(String pageTitle) throws IOException {
        SharedData.pageTitle.set(pageTitle);
        saveToFile();
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
        //saveToFile();
    }

    public static void setOperation(Message message) {
        operationStatus.set(message.getMessage());
        operationIsErrorType.set(message.isError());
        resetOperationStatusAfterDelay();
        //saveToFile();
    }

    // Property accessor for operationStatus
    public static StringProperty operationStatusProperty() {
        return operationStatus;
    }

    // Property accessor for operationIsErrorType
    public static BooleanProperty operationIsErrorTypeProperty() {
        return operationIsErrorType;
    }


    // ------------------------------------------------------------------
    // setter for non observables

    public static void setCurrentLanguage(int currentLanguage) throws IOException {
        SharedData.currentLanguage = currentLanguage;
        saveToFile();
    }

    public static void setSelectedCourse(Course selectedCourse) throws IOException {
        SharedData.selectedCourse = selectedCourse;
        saveToFile();
    }

    public static void setSelectedStudyProgram(StudyProgram selectedStudyProgram) throws IOException {
        SharedData.selectedStudyProgram = selectedStudyProgram;
        saveToFile();
    }

    public static void setFilterQuestion(Question filterQuestion) throws IOException {
        SharedData.filterQuestion = filterQuestion;
        saveToFile();
    }

    public static void setSelectedEditQuestion(Question selectedEditQuestion) throws IOException {
        SharedData.selectedEditQuestion = selectedEditQuestion;
        saveToFile();
    }

    public static void setSelectedEditStudyProgram(StudyProgram selectedEditStudyProgram) throws IOException {
        SharedData.selectedEditStudyProgram = selectedEditStudyProgram;
        saveToFile();
    }

    public static void setSelectedEditCourse(Course selectedEditCourse) throws IOException {
        SharedData.selectedEditCourse = selectedEditCourse;
        saveToFile();
    }

    public static void setResizeImage(Image resizeImage) throws IOException {
        SharedData.resizeImage = resizeImage;
        //saveToFile();
    }

    public static void setCurrentScreen(Screen currentScreen) throws IOException {
        SharedData.currentScreen = currentScreen;
        saveToFile();
    }

    // ------------------------------------------------------------------


    public static void saveToFile() throws IOException {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(pageTitle.get());
            oos.writeObject(operationStatus.get());
            oos.writeBoolean(operationIsErrorType.get());
            oos.writeInt(currentLanguage);
            oos.writeObject(selectedCourse);
            oos.writeObject(selectedStudyProgram);
            oos.writeObject(filterQuestion);
            oos.writeObject(selectedEditQuestion);
            oos.writeObject(selectedEditStudyProgram);
            oos.writeObject(selectedEditCourse);
            oos.writeObject(currentScreen);

            oos.writeObject(new ArrayList<>(testQuestions));

            //oos.writeObject(new ArrayList<>(filteredQuestions));
        }
    }

    public static void loadFromFile() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            pageTitle.set((String) ois.readObject());
            operationStatus.set((String) ois.readObject());
            operationIsErrorType.set(ois.readBoolean());
            currentLanguage = ois.readInt();
            selectedCourse = (Course) ois.readObject();
            selectedStudyProgram = (StudyProgram) ois.readObject();
            filterQuestion = (Question) ois.readObject();
            selectedEditQuestion = (Question) ois.readObject();
            selectedEditStudyProgram = (StudyProgram) ois.readObject();
            selectedEditCourse = (Course) ois.readObject();
            currentScreen = (Screen) ois.readObject();

            testQuestions.setAll((ArrayList<Question>) ois.readObject());
            //filteredQuestions.setAll((ArrayList<Question>) ois.readObject());
        }
    }
}