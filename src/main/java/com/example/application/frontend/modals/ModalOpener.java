package com.example.application.frontend.modals;

import com.example.application.MainApp;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class ModalOpener {
    public static String ADD_CATEGORY = "modals/addCategory.fxml";
    public static String ADD_COURSE = "modals/add_Course.fxml";
    public static String ADD_KEYWORD = "modals/addKeyword.fxml";
    public static String ADD_STUDY_PROGRAM = "modals/add_StudyProgram.fxml";
    public static String CONFIRM_DELETION = "modals/confirm_deletion.fxml";
    public static String IMAGE_RESIZER = "modals/image_resizer.fxml";
    public static String LATEX = "modals/latex.fxml";
    public static String IMPORT_ERROR = "modals/import_error.fxml";

    private final Map<String, String> MODAL_TITLES = new HashMap<>();
    @Getter
    private Modal<?> modal;

    public ModalOpener() {
        loadTitles();
    }

    private void loadTitles() {
        MODAL_TITLES.clear();
        MODAL_TITLES.put(ADD_CATEGORY, MainApp.resourceBundle.getString("category_modal_title"));
        MODAL_TITLES.put(ADD_COURSE, MainApp.resourceBundle.getString("course"));
        MODAL_TITLES.put(ADD_KEYWORD, MainApp.resourceBundle.getString("keyword_modal_title"));
        MODAL_TITLES.put(ADD_STUDY_PROGRAM, MainApp.resourceBundle.getString("study_program"));
        MODAL_TITLES.put(CONFIRM_DELETION, MainApp.resourceBundle.getString("confirm_deletion"));
        MODAL_TITLES.put(IMAGE_RESIZER, MainApp.resourceBundle.getString("image_resize_modal_title"));
        MODAL_TITLES.put(LATEX, MainApp.resourceBundle.getString("latex_modal_title"));
        MODAL_TITLES.put(IMPORT_ERROR, MainApp.resourceBundle.getString("import_error"));
    }

    public Stage openModal(String path) {
        // update titles - it is possible, that the language was changed, so we need to update the titles
        loadTitles();

        Stage newStage = new Stage();
        setWindowSize(newStage);
        Modal<?> new_modal = new Modal<>(path);
        this.modal = new_modal;
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle(MODAL_TITLES.get(path));
        newStage.setScene(new_modal.scene);
        newStage.show();

        return newStage;
    }

    private void setWindowSize(Stage stage) {
        Stage mainStage = MainApp.stage;
        stage.setMinWidth(400);
        stage.setMinHeight(200);
        // modal on center of mainStage
        stage.setX(mainStage.getX() + (mainStage.getWidth() - stage.getMinWidth()) / 2);
        stage.setY(mainStage.getY() + (mainStage.getHeight() - stage.getMinHeight()) / 2);
    }
}
