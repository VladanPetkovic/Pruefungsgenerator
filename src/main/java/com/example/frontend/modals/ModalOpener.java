package com.example.frontend.modals;

import com.example.frontend.MainApp;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ModalOpener {
    public static String ADD_COURSE = "modals/add_Course.fxml";
    public static String ADD_STUDY_PROGRAM = "modals/add_StudyProgram.fxml";
    public static String CONFIRM_DELETION = "modals/confirm_deletion.fxml";
    public static String IMAGE_RESIZER = "modals/image_resizer.fxml";

    public static String TARGET_SELECTION = "modals/import_target_selection.fxml";

    private static final Map<String, String> MODAL_TITLES = new HashMap<>();

    static {
        loadTitles();
    }

    private static void loadTitles() {
        MODAL_TITLES.clear();
        MODAL_TITLES.put(ADD_COURSE, MainApp.resourceBundle.getString("course"));
        MODAL_TITLES.put(ADD_STUDY_PROGRAM, MainApp.resourceBundle.getString("study_program"));
        MODAL_TITLES.put(CONFIRM_DELETION, MainApp.resourceBundle.getString("confirm_deletion"));
        MODAL_TITLES.put(IMAGE_RESIZER, MainApp.resourceBundle.getString("image_resize_modal_title"));
        MODAL_TITLES.put(TARGET_SELECTION, MainApp.resourceBundle.getString("import_target_selection"));
    }

    public static Stage openModal(String path) {
        // update titles - it is possible, that the language was changed, so we need to update the titles
        loadTitles();

        Stage newStage = new Stage();
        Modal<AddCourse_ScreenController> new_modal = new Modal<>(path);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle(MODAL_TITLES.get(path));
        newStage.setScene(new_modal.scene);
        newStage.show();

        return newStage;
    }
}
