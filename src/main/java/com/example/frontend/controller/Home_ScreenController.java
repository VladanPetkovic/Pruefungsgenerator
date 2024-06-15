package com.example.frontend.controller;


import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.Screen;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.Message;
import com.example.backend.db.models.StudyProgram;
import com.example.backend.app.SharedData;
import com.example.frontend.MainApp;
import com.example.frontend.modals.AddCourse_ScreenController;
import com.example.frontend.modals.ConfirmDeletion_ScreenController;
import com.example.frontend.modals.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.example.frontend.controller.SwitchScene.HOME;
import static com.example.frontend.controller.SwitchScene.switchScene;

/**
 * controller class for the home screen
 * manages interaction and functionality of the home screen UI components
 */
public class Home_ScreenController extends ScreenController {
    @FXML
    public ImageView langImageView;
    @FXML
    private MenuButton studyProgramMenuButton;
    @FXML
    private MenuButton coursesMenuButton;
    private final int LANGUAGE_COUNT = 2;


    /**
     * initializes the home screen
     * resets and loads study programs and sets up initial UI state
     */
    @FXML
    private void initialize() {
        resetStudyProgramMenuButton();
        loadStudyPrograms();
        initLanguage();

        resetCourseMenuButton();
        MenuItem menuItem = new MenuItem(MainApp.resourceBundle.getString("menuitem_choose_study_program"));
        coursesMenuButton.getItems().add(menuItem);
    }

    // event handler for study program button click
    @FXML
    public void onStudyProgramBtnClick(ActionEvent event) {
        loadStudyPrograms();
    }

    // event handler for courses button click
    @FXML
    public void onCoursesBtnClick(ActionEvent event) {
        Logger.log(this.getClass().getName(), "Selected Study Program ID (loadCourses): " + SharedData.getSelectedStudyProgram().getId(), LogLevel.DEBUG);
        loadCourses();
    }

    // event handler for continue button click
    @FXML
    public void onContinueBtnClick(ActionEvent event) throws IOException {
        SharedData.setCurrentScreen(Screen.CREATE_AUTOMATIC);
        if (SharedData.getSelectedCourse() != null && SharedData.getSelectedStudyProgram() != null) {
            Logger.log(getClass().getName(), "Selected Study Program: " + SharedData.getSelectedStudyProgram().getName(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected Study ProgramID: " + SharedData.getSelectedStudyProgram().getId(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected Course: " + SharedData.getSelectedCourse().getName(), LogLevel.INFO);
            Logger.log(getClass().getName(), "Selected CourseID: " + SharedData.getSelectedCourse().getId(), LogLevel.INFO);
            switchScene(SwitchScene.CREATE_TEST_AUTOMATIC);
        } else {
            SharedData.setOperation(Message.ERROR_COURSE_NOT_SELECTED);
        }
    }

    // loads available study programs into the menu
    private void loadStudyPrograms() {
        ArrayList<StudyProgram> studyPrograms = SQLiteDatabaseConnection.studyProgramRepository.getAll();
        studyProgramMenuButton.getItems().clear();

        for (StudyProgram studyProgram : studyPrograms) {
            addStudyProgramToMenuButton(studyProgram);
        }

        // add option to add a new study program
        Button customButton = new Button(MainApp.resourceBundle.getString("add_study_program"));
        //todo
        customButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        CustomMenuItem customMenuItem = new CustomMenuItem(customButton);
        customButton.setOnAction(e -> {
            studyProgramMenuButton.setText(MainApp.resourceBundle.getString("add_study_program"));
            try {
                SharedData.setSelectedEditStudyProgram(new StudyProgram());
                addStudyProgram();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        studyProgramMenuButton.getItems().add(customMenuItem);
    }

    private void addStudyProgramToMenuButton(StudyProgram studyProgram) {
        // the studyProgram-text
        Button textBtn = new Button(studyProgram.getName());
        textBtn.setStyle("-fx-background-color: none;-fx-text-fill: white");
        // the edit-btn
        Button editBtn = createEditBtn();

        HBox content = new HBox(editBtn, textBtn);
        content.setSpacing(5);
        CustomMenuItem customMenuItem = new CustomMenuItem(content);

        textBtn.setOnAction(e -> {
            studyProgramMenuButton.setText(studyProgram.getName());
            SharedData.setSelectedStudyProgram(studyProgram);
            // if the study program menu item is selected then the course menu and the course selection (variable)
            // is cleared/reset and the associated courses will get loaded in the course menu
            resetCourseMenuButton();
            loadCourses();
        });

        editBtn.setOnAction(e -> {
            try {
                SharedData.setSelectedEditStudyProgram(studyProgram);
                addStudyProgram();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        studyProgramMenuButton.getItems().add(customMenuItem);
    }

    // loads available courses into the menu
    private void loadCourses() {
        ArrayList<Course> courses = SQLiteDatabaseConnection.courseRepository.getAll(SharedData.getSelectedStudyProgram().getId());

        for (Course course : courses) {
            addCourseToMenuButton(course);
        }

        // add button to add a new course
        Button customButton = new Button(MainApp.resourceBundle.getString("add_course"));
        customButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        CustomMenuItem customMenuItem = new CustomMenuItem(customButton);
        customButton.setOnAction(e -> {
            coursesMenuButton.setText(MainApp.resourceBundle.getString("add_course"));
            try {
                SharedData.setSelectedEditCourse(new Course());
                addCourse();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        coursesMenuButton.getItems().add(customMenuItem);
    }

    private void addCourseToMenuButton(Course course) {
        // the course-text
        Button textBtn = new Button(course.getName());
        textBtn.setStyle("-fx-background-color: none;-fx-text-fill: white");
        // the edit-btn
        Button editBtn = createEditBtn();

        HBox content = new HBox(editBtn, textBtn);
        content.setSpacing(5);
        CustomMenuItem customMenuItem = new CustomMenuItem(content);

        textBtn.setOnAction(e -> {
            coursesMenuButton.setText(course.getName());
            SharedData.setSelectedCourse(course);
        });

        editBtn.setOnAction(e -> {
            try {
                SharedData.setSelectedEditCourse(course);
                addCourse();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        coursesMenuButton.getItems().add(customMenuItem);
    }

    private Button createEditBtn() {
        File file = new File("src/main/resources/com/example/frontend/icons/edit_white.png");
        Image editImage = new Image(file.toURI().toString());
        ImageView editImageView = new ImageView();
        editImageView.setImage(editImage);
        editImageView.setFitHeight(16);
        editImageView.setFitWidth(16);
        Button editBtn = new Button();
        editBtn.setGraphic(editImageView);
        editBtn.getStyleClass().add("btn_add_icon");

        return editBtn;
    }

    // method to add a new study program
    private void addStudyProgram() throws IOException {
        Stage newStage = new Stage();
        Modal<AddCourse_ScreenController> new_study_program_modal = new Modal<>("modals/add_StudyProgram.fxml");
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle(MainApp.resourceBundle.getString("study_program"));
        newStage.setScene(new_study_program_modal.scene);

        //listener for when the stage is closed
        newStage.setOnHidden(event -> {
            loadStudyPrograms();
        });
        newStage.show();
    }

    // method to add a new course
    private void addCourse() throws IOException {
        Stage newStage = new Stage();
        Modal<AddCourse_ScreenController> new_course_modal = new Modal<>("modals/add_Course.fxml");
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle(MainApp.resourceBundle.getString("course"));
        newStage.setScene(new_course_modal.scene);

        //listener for when the stage is closed
        newStage.setOnHidden(event -> {
            resetCourseMenuButton();
            loadCourses();
        });
        newStage.show();
    }


    // helper function to reset study program menu button
    void resetStudyProgramMenuButton() {
        studyProgramMenuButton.getItems().clear();
        studyProgramMenuButton.setText(MainApp.resourceBundle.getString("study_programs"));
        SharedData.setSelectedStudyProgram(null);
    }

    // helper function to reset course menu button
    void resetCourseMenuButton() {
        coursesMenuButton.getItems().clear();
        coursesMenuButton.setText(MainApp.resourceBundle.getString("courses"));
        SharedData.setSelectedCourse(null);
    }

    public void onLanguageBtnClick(ActionEvent actionEvent) {
        String imagePath = "src/main/resources/com/example/frontend/icons/";
        Locale locale = new Locale("en", "US");
        int temp = (SharedData.getCurrentLanguage() + 1) % LANGUAGE_COUNT;
        SharedData.setCurrentLanguage(temp);

        switch (temp) {
            case 0:                         // ENGLISH
                imagePath += "en.png";
                MainApp.resourceBundle = ResourceBundle.getBundle("common.en", locale);
                break;
            case 1:                         // GERMAN
                imagePath += "de.png";
                locale = new Locale("de", "AUT");
                MainApp.resourceBundle = ResourceBundle.getBundle("common.de", locale);
                break;
        }

        // setting the image
        File file = new File(imagePath);
        Image languageImage = new Image(file.toURI().toString());
        langImageView.setImage(languageImage);
        // reloading the scene
        switchScene(HOME);
    }

    private void initLanguage() {
        String imagePath = "src/main/resources/com/example/frontend/icons/";

        switch (SharedData.getCurrentLanguage()) {
            case 0:                         // ENGLISH
                imagePath += "en.png";
                break;
            case 1:                         // GERMAN
                imagePath += "de.png";
                break;
        }

        // setting the image
        File file = new File(imagePath);
        Image languageImage = new Image(file.toURI().toString());
        langImageView.setImage(languageImage);
    }
}
