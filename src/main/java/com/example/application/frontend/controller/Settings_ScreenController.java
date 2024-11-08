package com.example.application.frontend.controller;

import com.example.application.backend.app.ExportCSV;
import com.example.application.backend.app.ImportCSV;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Course;
import com.example.application.backend.db.models.Message;
import com.example.application.backend.db.models.StudyProgram;
import com.example.application.MainApp;
import com.example.application.backend.db.services.*;
import com.example.application.frontend.modals.ModalOpener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class Settings_ScreenController extends ScreenController {
    // services
    private final StudyProgramService studyProgramService;
    private final CourseService courseService;
    private final QuestionService questionService;
    private final CategoryService categoryService;
    private final AnswerService answerService;
    private final KeywordService keywordService;
    private String modeOfImport = "";

    // import related fxml items
    @FXML
    private MenuButton importModeMenuButton;
    @FXML
    private Button chooseImportTargetBtn;
    @FXML
    private Button selectCsvFileBtn;
    @FXML
    public Label label_selectedFile;
    @FXML
    Button settingsImportBtn;

    // export related fxml items
    @FXML
    public MenuButton chooseQuestionsMenuButton;
    @FXML
    public Label chooseQuestionsLabel;
    @FXML
    public MenuButton chooseStudyProgramMenuBtn;
    @FXML
    public MenuButton chooseCourseMenuButton;
    @FXML
    public Label label_selectedDirectory;
    @FXML
    private Button chooseDirectoryBtn;
    @FXML
    private Button settingsExportBtn;

    public Settings_ScreenController(StudyProgramService studyProgramService,
                                     CourseService courseService,
                                     QuestionService questionService,
                                     CategoryService categoryService,
                                     AnswerService answerService,
                                     KeywordService keywordService) {
        super();
        this.studyProgramService = studyProgramService;
        this.courseService = courseService;
        this.questionService = questionService;
        this.categoryService = categoryService;
        this.answerService = answerService;
        this.keywordService = keywordService;
    }

    @FXML
    private void initialize() {
        ArrayList<String> studyPrograms = studyProgramService.getAll().stream()
                .map(StudyProgram::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> courses = courseService.getAllByStudyProgram(SharedData.getSelectedStudyProgram().getId())
                .stream()
                .map(Course::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        initializeMenuButton(chooseStudyProgramMenuBtn, studyPrograms);
        initializeMenuButton(chooseCourseMenuButton, courses);

        // Import related buttons
        chooseImportTargetBtn.setVisible(false);
        selectCsvFileBtn.setVisible(false);
        settingsImportBtn.setVisible(false);

        // Export related buttons
        chooseCourseMenuButton.setVisible(false);
        chooseStudyProgramMenuBtn.setVisible(false);
        chooseDirectoryBtn.setVisible(false);
        settingsExportBtn.setVisible(false);
    }

    private void initializeMenuButton(MenuButton menuButton, ArrayList<String> menuItems) {
        menuButton.getItems().clear();

        for (String string : menuItems) {
            MenuItem menuItem = new MenuItem(string);
            menuItem.setOnAction(e -> {
                menuButton.setText(string);
            });
            menuButton.getItems().add(menuItem);
        }
    }

    // import related functions
    @FXML
    private void onUpdateExistingQuestionsSelected(ActionEvent event) {
        modeOfImport = MainApp.resourceBundle.getString("update_existing_questions");
        SharedData.setModeOfImport(modeOfImport);
        importModeMenuButton.setText(modeOfImport);
        selectCsvFileBtn.setVisible(true);
    }

    @FXML
    private void onInsertNewQuestionsSelected(ActionEvent event) {
        modeOfImport = MainApp.resourceBundle.getString("insert_new_questions");
        SharedData.setModeOfImport(modeOfImport);
        importModeMenuButton.setText(modeOfImport);
        chooseImportTargetBtn.setVisible(true);
    }

    @FXML
    private void onChooseImportTargetBtnClick(ActionEvent event) {
        Stage newStage = ModalOpener.openModal(ModalOpener.TARGET_SELECTION);
        // listener for when the stage is closed
        newStage.setOnHidden(e -> {
            // check if ImportTargetStudyProgram and ImportTargetCourse were selected
            if (SharedData.getImportTargetStudyProgram() != null && SharedData.getImportTargetCourse() != null) {
                selectCsvFileBtn.setVisible(true);
            }
        });
    }

    public void onSelectCsvFileBtnClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(MainApp.resourceBundle.getString("select_csv_file"));

        // Set extension filter to only allow CSV files
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(MainApp.resourceBundle.getString("csv_files"), "*.csv")
        );

        File file = fileChooser.showOpenDialog(MainApp.stage);
        if (file != null) {
            label_selectedFile.setText(file.toString());
            settingsImportBtn.setVisible(true); // Show the import button when a file is selected
        }
    }

    @FXML
    public void onImportBtnClicked() {
        if (Objects.equals(this.label_selectedFile.getText(), "\"\"")) {
            SharedData.setOperation(Message.ERROR_MESSAGE_FILE_NOT_SELECTED);
            return;
        }
        ImportCSV importCSV = new ImportCSV(this.label_selectedFile.getText(), studyProgramService, courseService, questionService, categoryService, answerService, keywordService);

        // Start the import process
        boolean isSuccess = importCSV.importData();

        // Update SharedData with a success or error message
        if (isSuccess) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_DATA_IMPORTED);
        } else {
            System.out.println(importCSV.errorMessage);
            // write directly onto settings.fxml
            SharedData.setOperation(Message.ERROR_MESSAGE_IMPORT_FAILED);
        }
    }


    // export related functions
    public void allQuestionsSelectedForExport(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("all_questions"));
        this.chooseStudyProgramMenuBtn.setVisible(false);
        this.chooseCourseMenuButton.setVisible(false);
        this.chooseQuestionsLabel.setVisible(false);
        chooseDirectoryBtn.setVisible(true);
    }

    /**
     * SP ... StudyProgram
     */
    public void questionsOfSPselected(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("questions_of_study_program"));
        this.chooseStudyProgramMenuBtn.setVisible(true);
        this.chooseCourseMenuButton.setVisible(false);
        this.chooseQuestionsLabel.setText(MainApp.resourceBundle.getString("select_study_program"));
        this.chooseQuestionsLabel.setVisible(true);

        chooseDirectoryBtn.setVisible(true);
        /*
        if (!Objects.equals(chooseStudyProgramMenuBtn.getText(), "")) {
            chooseDirectoryBtn.setVisible(true);
        }

         */
    }

    public void questionsOfCourseSelected(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("questions_of_course"));
        this.chooseCourseMenuButton.setVisible(true);
        this.chooseStudyProgramMenuBtn.setVisible(false);
        this.chooseQuestionsLabel.setText(MainApp.resourceBundle.getString("select_course"));
        this.chooseQuestionsLabel.setVisible(true);

        chooseDirectoryBtn.setVisible(true);
        /*
        if (!Objects.equals(chooseCourseMenuButton.getText(), "")) {
            chooseDirectoryBtn.setVisible(true);
        }

         */
    }

    public void chooseDirectoryBtnClicked(ActionEvent actionEvent) {
        chooseDirectory(this.label_selectedDirectory);
        if (!Objects.equals(this.label_selectedDirectory.getText(), "\"\"")) {
            settingsExportBtn.setVisible(true); // Show the export button when a directory is selected
        }
    }

    public void applyExportBtnClicked(ActionEvent actionEvent) {
        if (!allFieldsSetProperly()) {
            return;
        }

        ExportCSV exportCSV = new ExportCSV(this.label_selectedDirectory.getText(), studyProgramService, courseService, questionService);
        int exportType = 0;     // all questions
        if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_study_program"))) {
            exportType = 1;     // only for studyProgram
            exportCSV.initStudyProgram(chooseStudyProgramMenuBtn.getText());
        } else if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_course"))) {
            exportType = 2;     // only for course
            exportCSV.initCourse(chooseCourseMenuButton.getText());
        }

        if (exportCSV.export(exportType)) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_QUESTIONS_EXPORTED);
        } else {
            SharedData.setOperation(Message.ERROR_MESSAGE_ERROR_OCCURRED);
        }
    }

    /**
     * Function that checks, if everything was inputted properly.
     *
     * @return True, when user submitted everything, and false otherwise.
     */
    private boolean allFieldsSetProperly() {
        // check if everything was filled out
        if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_course"))) {
            if (Objects.equals(chooseCourseMenuButton.getText(), "")) {
                SharedData.setOperation(Message.ERROR_MESSAGE_INPUT_ALL_FIELDS);
                return false;
            }
        }
        if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_study_program"))) {
            if (Objects.equals(chooseStudyProgramMenuBtn.getText(), "")) {
                SharedData.setOperation(Message.ERROR_MESSAGE_INPUT_ALL_FIELDS);
                return false;
            }
        }
        if (this.label_selectedDirectory.getText().equals("\"\"")) {
            SharedData.setOperation(Message.ERROR_MESSAGE_SELECT_A_FOLDER_SAVE_FILE);
            return false;
        }
        return true;
    }
}
