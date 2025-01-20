package com.example.application.frontend.controller;

import com.example.application.backend.app.ExportCSV;
import com.example.application.backend.app.ImportCSV;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Course;
import com.example.application.backend.db.models.Message;
import com.example.application.backend.db.models.StudyProgram;
import com.example.application.MainApp;
import com.example.application.backend.db.services.*;
import com.example.application.frontend.modals.ImportErrorScreenController;
import com.example.application.frontend.modals.ModalOpener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    // import related fxml items
    @FXML
    private MenuButton importModeMenuButton;
    @FXML
    public Label importModeHintLabel;
    @FXML
    public Label title_selectedStudyProgram;
    @FXML
    public MenuButton chooseStudyProgramMenuBtnImport;
    @FXML
    public Label studyProgramHintLabel;
    @FXML
    public Label title_selectedCourse;
    @FXML
    public MenuButton chooseCourseMenuButtonImport;
    @FXML
    public Label courseHintLabel;

    @FXML
    private Button selectCsvFileBtn;
    @FXML
    public Label label_selectedFile;
    @FXML
    public Label csvFileHintLabel;
    @FXML
    public Label alternativeCsvFileHintLabel;
    @FXML
    Button settingsImportBtn;

    // export related fxml items
    @FXML
    public MenuButton chooseQuestionsMenuButton;
    @FXML
    public Label exportModeHintLabel;
    @FXML
    public Label chooseQuestionsLabel;
    @FXML
    public Label chooseQuestionsHintLabel;

    @FXML
    public MenuButton chooseStudyProgramMenuBtnExport;
    @FXML
    public MenuButton chooseCourseMenuButtonExport;

    @FXML
    public Label label_selectedDirectory;
    @FXML
    private Button chooseDirectoryBtn;
    @FXML
    public Label directoryHintLabel;
    @FXML
    public Label alternativeDirectoryHintLabel;
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
        initializeMenuButton(chooseStudyProgramMenuBtnExport, studyPrograms, null);
        initializeMenuButton(chooseStudyProgramMenuBtnImport, studyPrograms, this::populateCourseMenuBtn);
        initializeMenuButton(chooseCourseMenuButtonExport, courses, null);

        // Import related buttons
        title_selectedStudyProgram.setVisible(false);
        chooseStudyProgramMenuBtnImport.setVisible(false);
        studyProgramHintLabel.setVisible(false);
        title_selectedCourse.setVisible(false);
        chooseCourseMenuButtonImport.setVisible(false);
        courseHintLabel.setVisible(false);
        selectCsvFileBtn.setVisible(false);
        csvFileHintLabel.setVisible(false);
        alternativeCsvFileHintLabel.setVisible(false);
        settingsImportBtn.setVisible(false);

        // listeners to monitor changes in the menu button text
        chooseStudyProgramMenuBtnImport.textProperty().addListener((observable, oldValue, newValue) -> checkMenuButtonText());
        chooseCourseMenuButtonImport.textProperty().addListener((observable, oldValue, newValue) -> checkMenuButtonText());

        // Export related buttons
        chooseQuestionsHintLabel.setVisible(false);
        chooseCourseMenuButtonExport.setVisible(false);
        chooseStudyProgramMenuBtnExport.setVisible(false);

        chooseDirectoryBtn.setVisible(false);
        directoryHintLabel.setVisible(false);
        alternativeDirectoryHintLabel.setVisible(false);

        settingsExportBtn.setVisible(false);

        // listeners to monitor changes in the menu button text
        chooseStudyProgramMenuBtnExport.textProperty().addListener((observable, oldValue, newValue) -> checkMenuButtonText());
        chooseCourseMenuButtonExport.textProperty().addListener((observable, oldValue, newValue) -> checkMenuButtonText());
    }

    private boolean isMenuButtonTextSet(MenuButton button, String defaultTextKey) {
        String defaultText = MainApp.resourceBundle.getString(defaultTextKey);
        String buttonText = button.getText();
        return !buttonText.equals(defaultText) && !buttonText.isEmpty();
    }

    private void checkMenuButtonText() {
        boolean studyProgramSelectedImport = isMenuButtonTextSet(chooseStudyProgramMenuBtnImport, "select_study_program");
        boolean courseSelectedImport = isMenuButtonTextSet(chooseCourseMenuButtonImport, "select_course");
        boolean studyProgramSelectedExport = isMenuButtonTextSet(chooseStudyProgramMenuBtnExport, "select_study_program");
        boolean courseSelectedExport = isMenuButtonTextSet(chooseCourseMenuButtonExport, "select_course");

        selectCsvFileBtn.setVisible(studyProgramSelectedImport && courseSelectedImport);
        csvFileHintLabel.setVisible(studyProgramSelectedImport && courseSelectedImport);
        chooseDirectoryBtn.setVisible(studyProgramSelectedExport || courseSelectedExport);
        directoryHintLabel.setVisible(studyProgramSelectedExport || courseSelectedExport);
    }

    private void initializeMenuButton(MenuButton menuButton, ArrayList<String> menuItems, Runnable onActionFunction) {
        menuButton.getItems().clear();

        for (String string : menuItems) {
            MenuItem menuItem = new MenuItem(string);
            menuItem.setOnAction(e -> {
                menuButton.setText(string);
                if (onActionFunction != null) {
                    onActionFunction.run();
                }
            });
            menuButton.getItems().add(menuItem);
        }
    }

    /* IMPORT RELATED FUNCTIONS - START */
    public void populateCourseMenuBtn() {
        StudyProgram studyProgram = studyProgramService.getByName(chooseStudyProgramMenuBtnImport.getText());
        List<Course> courses = courseService.getAllByStudyProgram(studyProgram.getId());
        chooseCourseMenuButtonImport.getItems().clear();
        chooseCourseMenuButtonImport.setText("");
        for (Course course : courses) {
            MenuItem menuItem = new MenuItem(course.getName());
            menuItem.setOnAction(e -> {
                chooseCourseMenuButtonImport.setText(course.getName());
            });
            chooseCourseMenuButtonImport.getItems().add(menuItem);
        }
    }

    @FXML
    private void onUpdateExistingQuestionsSelected(ActionEvent event) {
        importModeMenuButton.setText(MainApp.resourceBundle.getString("update_existing_questions"));
        title_selectedStudyProgram.setVisible(false);
        chooseStudyProgramMenuBtnImport.setVisible(false);
        studyProgramHintLabel.setVisible(false);
        title_selectedCourse.setVisible(false);
        chooseCourseMenuButtonImport.setVisible(false);
        courseHintLabel.setVisible(false);
        selectCsvFileBtn.setVisible(true);
        alternativeCsvFileHintLabel.setVisible(true);
        settingsImportBtn.setVisible(false);
        label_selectedFile.setText("");
    }

    @FXML
    private void onInsertNewQuestionsSelected(ActionEvent event) {
        importModeMenuButton.setText(MainApp.resourceBundle.getString("insert_new_questions"));
        title_selectedStudyProgram.setVisible(true);
        chooseStudyProgramMenuBtnImport.setText(MainApp.resourceBundle.getString("select_study_program"));
        chooseStudyProgramMenuBtnImport.setVisible(true);
        studyProgramHintLabel.setVisible(true);
        title_selectedCourse.setVisible(true);
        chooseCourseMenuButtonImport.setText(MainApp.resourceBundle.getString("select_course"));
        chooseCourseMenuButtonImport.setVisible(true);
        courseHintLabel.setVisible(true);
        selectCsvFileBtn.setVisible(false);
        csvFileHintLabel.setVisible(false);
        alternativeCsvFileHintLabel.setVisible(false);
        settingsImportBtn.setVisible(false);
        label_selectedFile.setText("");
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
        boolean isCreateMode = Objects.equals(importModeMenuButton.getText(), MainApp.resourceBundle.getString("insert_new_questions"));
        ImportCSV importCSV = new ImportCSV(
                this.label_selectedFile.getText(),
                studyProgramService, courseService,
                questionService, categoryService,
                answerService, keywordService,
                isCreateMode, chooseStudyProgramMenuBtnImport.getText(),
                chooseCourseMenuButtonImport.getText());

        // start the import process
        if (importCSV.importData()) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_DATA_IMPORTED);
        } else {
            openErrorModal(importCSV.getErrorMessage());
        }
    }

    private void openErrorModal(String errorString) {
        ModalOpener modalOpener = new ModalOpener();
        Stage errorModalStage = modalOpener.openModal(ModalOpener.IMPORT_ERROR);
        ImportErrorScreenController controller = (ImportErrorScreenController) modalOpener.getModal().controller;
        controller.errorLabel.setText(errorString);

        errorModalStage.setOnHidden(e -> {
            // TODO peter: add logic if necessary (maybe reset view, when the error-modal closes...)
        });
    }
    /* IMPORT RELATED FUNCTIONS - END */

    /* EXPORT RELATED FUNCTIONS - START */
    public void allQuestionsSelectedForExport(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("all_questions"));
        this.chooseStudyProgramMenuBtnExport.setVisible(false);
        this.chooseCourseMenuButtonExport.setVisible(false);
        this.chooseQuestionsLabel.setVisible(false);
        this.chooseQuestionsHintLabel.setVisible(false);
        this.chooseDirectoryBtn.setVisible(true);
        this.directoryHintLabel.setVisible(false);
        this.alternativeDirectoryHintLabel.setVisible(true);
        //this.label_selectedDirectory.setText("");
        this.settingsExportBtn.setVisible(false);
    }

    /**
     * SP ... StudyProgram
     */
    public void questionsOfSPselected(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("questions_of_study_program"));
        this.chooseStudyProgramMenuBtnExport.setVisible(true);
        this.chooseStudyProgramMenuBtnExport.setText(MainApp.resourceBundle.getString("select_study_program"));
        this.chooseCourseMenuButtonExport.setVisible(false);
        this.chooseQuestionsLabel.setText(MainApp.resourceBundle.getString("select_study_program"));
        this.chooseQuestionsLabel.setVisible(true);
        this.chooseQuestionsHintLabel.setVisible(true);
        this.chooseDirectoryBtn.setVisible(false);
        this.directoryHintLabel.setVisible(false);
        this.alternativeDirectoryHintLabel.setVisible(false);
        //this.label_selectedDirectory.setText("");
        this.settingsExportBtn.setVisible(false);
    }

    public void questionsOfCourseSelected(ActionEvent actionEvent) {
        this.chooseQuestionsMenuButton.setText(MainApp.resourceBundle.getString("questions_of_course"));
        this.chooseCourseMenuButtonExport.setVisible(true);
        this.chooseCourseMenuButtonExport.setText(MainApp.resourceBundle.getString("select_course"));
        this.chooseStudyProgramMenuBtnExport.setVisible(false);
        this.chooseQuestionsLabel.setText(MainApp.resourceBundle.getString("select_course"));
        this.chooseQuestionsLabel.setVisible(true);
        this.chooseQuestionsHintLabel.setVisible(true);
        this.chooseDirectoryBtn.setVisible(false);
        this.directoryHintLabel.setVisible(false);
        this.alternativeDirectoryHintLabel.setVisible(false);
        //this.label_selectedDirectory.setText("");
        this.settingsExportBtn.setVisible(false);
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
            exportCSV.initStudyProgram(chooseStudyProgramMenuBtnExport.getText());
        } else if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_course"))) {
            exportType = 2;     // only for course
            exportCSV.initCourse(chooseCourseMenuButtonExport.getText());
        }

        if (exportCSV.export(exportType)) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_QUESTIONS_EXPORTED);
        } else {
            SharedData.setOperation(Message.ERROR_MESSAGE_ERROR_OCCURRED);
        }
    }

    /* EXPORT RELATED FUNCTIONS - END */

    /**
     * Function that checks, if everything was put in properly.
     *
     * @return True, when user submitted everything, and false otherwise.
     */
    private boolean allFieldsSetProperly() {
        // check if everything was filled out
        if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_course"))) {
            if (Objects.equals(chooseCourseMenuButtonExport.getText(), "")) {
                SharedData.setOperation(Message.ERROR_MESSAGE_INPUT_ALL_FIELDS);
                return false;
            }
        }
        if (Objects.equals(chooseQuestionsMenuButton.getText(), MainApp.resourceBundle.getString("questions_of_study_program"))) {
            if (Objects.equals(chooseStudyProgramMenuBtnExport.getText(), "")) {
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
