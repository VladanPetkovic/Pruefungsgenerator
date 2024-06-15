package com.example.frontend.controller;

import com.example.backend.app.*;
import com.example.backend.app.Screen;
import com.example.backend.db.models.Message;
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

import java.util.ArrayList;
import java.util.Objects;

import static com.example.frontend.controller.SwitchScene.switchScene;

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

    private ExportPdf exportPdf;
    private ExportDocx exportDocx;
    public Label label_selectedDirectory;

    @FXML
    private void initialize() {
        // create an Export object with
        exportPdf = new ExportPdf();
        exportDocx = new ExportDocx();

        // displays the selected course above the filter window
        label_selectedCourse.setText(SharedData.getSelectedCourse().getName());
    }

    public void applyExportBtnClicked(ActionEvent actionEvent) {
        exportFile(this.exportPdf);
    }

    public void onExportDocxBtnClick(ActionEvent actionEvent) {
        exportFile(this.exportDocx);
    }

    /**
     * This functions only exports to pdf or docx, if a folder was selected to save the file.
     *
     * @param export the base class - we are passing either exportDocx or exportPdf.
     */
    private void exportFile(Export export) {
        if (!this.label_selectedDirectory.getText().equals("\"\"")) {
            // set the latest options
            export.setOptions(getTestHeader(),
                    getQuestionCount(),
                    this.label_selectedDirectory.getText(),
                    this.checkbox_applyHeader.isSelected(),
                    this.checkbox_showPageNumber.isSelected());
            // export the test questions to PDF/Docx
            export.exportDocument(SharedData.getTestQuestions());
            // reset the stored test questions
            SharedData.resetQuestions();
            // returning to the automatic-test-create-scene
            switchScene(SwitchScene.CREATE_TEST_AUTOMATIC);
        } else {
            SharedData.setOperation(Message.ERROR_MESSAGE_SELECT_A_FOLDER_SAVE_FILE);
        }
    }

    public void applyFormattingBtnClicked(ActionEvent actionEvent) {
        // set the latest options
        this.exportPdf.setOptions(getTestHeader(),
                getQuestionCount(),
                this.label_selectedDirectory.getText(),
                this.checkbox_applyHeader.isSelected(),
                this.checkbox_showPageNumber.isSelected());
        // get and insert the images (each page is one image) into the vbox
        showPreview(this.exportPdf.getPreviewImages(SharedData.getTestQuestions()));
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

    public void onGoBackBtnClick(ActionEvent mouseEvent) {
        SharedData.setCurrentScreen(Screen.CREATE_MANUAL);
        switchScene(SwitchScene.CREATE_TEST_MANUAL);
    }
}
