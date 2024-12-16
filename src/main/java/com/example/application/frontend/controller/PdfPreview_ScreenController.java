package com.example.application.frontend.controller;

import com.example.application.MainApp;
import com.example.application.backend.app.*;
import com.example.application.backend.db.models.Message;
import com.example.application.backend.db.models.Question;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.application.frontend.controller.SwitchScene.switchScene;

@Component
@Scope("prototype")
public class PdfPreview_ScreenController extends ScreenController {
    @FXML
    private Slider questionCountSlider;
    @FXML
    private TextField titleTextField;
    @FXML
    private VBox vbox_previewPane;
    @FXML
    private Label label_selectedCourse;
    @FXML
    private CheckBox checkbox_applyHeader;
    @FXML
    private CheckBox checkbox_showPageNumber;
    public Label label_selectedDirectory;

    @FXML
    private void initialize() {
        label_selectedCourse.setText(MessageFormat.format(
                MainApp.resourceBundle.getString("question_filter_selected_course"),
                SharedData.getSelectedCourse().getName())
        );
    }

    public void applyExportBtnClicked(ActionEvent actionEvent) {
        if (this.label_selectedDirectory.getText().equals("\"\"")) {
            SharedData.setOperation(Message.ERROR_MESSAGE_SELECT_A_FOLDER_SAVE_FILE);
            return;
        }

        ExportPdf exportPdf = new ExportPdf(getTestHeader(),
                getQuestionCount(),
                this.label_selectedDirectory.getText(),
                this.checkbox_applyHeader.isSelected(),
                this.checkbox_showPageNumber.isSelected());
        ObservableList<Question> observableQuestions = SharedData.getTestQuestions();
        ArrayList<Question> questionsList = new ArrayList<>(observableQuestions);

        if (exportPdf.exportDocument(questionsList)) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_FILE_SAVED);
        }
    }

    public void onExportDocxBtnClick(ActionEvent actionEvent) {
        if (this.label_selectedDirectory.getText().equals("\"\"")) {
            SharedData.setOperation(Message.ERROR_MESSAGE_SELECT_A_FOLDER_SAVE_FILE);
            return;
        }

        ExportDocx exportDocx = new ExportDocx(getTestHeader(),
                getQuestionCount(),
                this.label_selectedDirectory.getText(),
                this.checkbox_applyHeader.isSelected(),
                this.checkbox_showPageNumber.isSelected());

        ObservableList<Question> observableQuestions = SharedData.getTestQuestions();
        ArrayList<Question> questionsList = new ArrayList<>(observableQuestions);

        if (exportDocx.exportDocument(questionsList)) {
            SharedData.setOperation(Message.SUCCESS_MESSAGE_FILE_SAVED);
        }
    }

    public void applyFormattingBtnClicked(ActionEvent actionEvent) {
        // set the latest options
        ExportPdf exportPdf = new ExportPdf(getTestHeader(),
                getQuestionCount(),
                this.label_selectedDirectory.getText(),
                this.checkbox_applyHeader.isSelected(),
                this.checkbox_showPageNumber.isSelected());
        // get and insert the images (each page is one image) into the vbox

        ObservableList<Question> questions = SharedData.getTestQuestions();
        ArrayList<Question> questionList = new ArrayList<>(questions);
        exportPdf.getPreviewImages(questionList);

        showPreview(exportPdf.getPreviewImages(questionList));
    }

    public void chooseDirectoryBtnClicked(ActionEvent actionEvent) {
        chooseDirectory(this.label_selectedDirectory);
    }

    /**
     * Showing the preview of the test with the selected parameters.
     *
     * @param images ArrayList of java.lang.image or so --> every image is one page
     */
    public void showPreview(ArrayList<Image> images) {
        // removing previous images
        this.vbox_previewPane.getChildren().clear();

        double targetWidth = this.vbox_previewPane.getWidth();

        for (int i = 0; i < images.size(); i++) {
            ImageView imageView = new ImageView(images.get(i));

            // Set the fit width and preserve the aspect ratio
            imageView.setFitWidth(targetWidth);
            imageView.setPreserveRatio(true);

            this.vbox_previewPane.getChildren().add(imageView);

            // adding a black line between pages
            if (i < images.size() - 1) {
                this.vbox_previewPane.getChildren().add(createBlackSeparator());
            }
        }
    }

    private Region createBlackSeparator() {
        // creating a black separator
        Region separator = new Region();
        separator.setMinHeight(5);
        separator.setBackground(javafx.scene.layout.Background.EMPTY);
        separator.setStyle("-fx-background-color: black;");
        return separator;
    }

    private String getTestHeader() {
        if (!Objects.equals(titleTextField.getText(), "")) {
            return titleTextField.getText();
        } else {
            return "Test: " + SharedData.getSelectedCourse().getName();
        }
    }

    private int getQuestionCount() {
        return (int) questionCountSlider.getValue();
    }

    public void onGoBackBtnClick(ActionEvent mouseEvent) throws IOException {
        SharedData.setCurrentScreen(Screen.CREATE_MANUAL);
        switchScene(SwitchScene.CREATE_TEST_MANUAL);
    }
}
