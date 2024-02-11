package com.example.frontend.controller;

import com.example.backend.app.Export;
import com.example.backend.app.SharedData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Objects;

public class pdfPreview_ScreenController extends ScreenController {
    @FXML
    private Slider questionCountSlider;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField destFolderTextField;

    @FXML
    private VBox vbox_previewPane;

    @FXML
    private Label label_selectedCourse;

    private Export export;

    @FXML
    private void initialize() {
        // create an Export object with
        this.export = new Export();

        // displays the selected course above the filter window
        label_selectedCourse.setText(SharedData.getSelectedCourse().getCourse_name());
    }

    public void applyExportBtnClicked(ActionEvent actionEvent) {
        // set the latest options
        this.export.setOptions(getTestHeader(), getQuestionCount(), getDestinationFolder());
        // export the test questions to PDF
        this.export.exportToPdf(SharedData.getTestQuestions());
        // reset the stored test questions
        SharedData.resetQuestions();
        // returning to the automatic-test-create-scene
        switchScene(createTestAutomatic, true);
    }

    public void applyFormattingBtnClicked(ActionEvent actionEvent) {
        // set the latest options
        this.export.setOptions(getTestHeader(), getQuestionCount(), getDestinationFolder());
        // get and insert the images (each page is one image) into the vbox
        showPreview(this.export.getPdfPreviewImages(SharedData.getTestQuestions()));
    }

    /**
     * Showing the preview of the test with the selected parameters.
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
            return "Test: " + SharedData.getSelectedCourse().getCourse_name();
        }
    }

    private int getQuestionCount() {
        return (int) questionCountSlider.getValue();
    }

    private String getDestinationFolder() {
        if (!Objects.equals(destFolderTextField.getText(), "")) {
            return destFolderTextField.getText();
        } else {
            return "";
        }
    }
}
