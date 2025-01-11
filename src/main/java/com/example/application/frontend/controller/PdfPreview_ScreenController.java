package com.example.application.frontend.controller;

import com.example.application.MainApp;
import com.example.application.backend.app.*;
import com.example.application.backend.db.models.Message;
import com.example.application.backend.db.models.Question;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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
    public SplitPane splitPane;
    @FXML
    public ScrollPane previewScrollPane;
    @FXML
    private Slider distanceBetweenQuestionsSlider;
    @FXML
    private TextField titleTextField;
    @FXML
    private VBox vbox_previewPane;
    @FXML
    private Label label_selectedCourse;
    @FXML
    private CheckBox checkbox_applyHeader;
    @FXML
    public GridPane headerSpecificGridPane;
    @FXML
    public CheckBox checkbox_applyHeaderAllPages;
    @FXML
    private CheckBox checkbox_showPageNumber;
    public Label label_selectedDirectory;

    @FXML
    private void initialize() {
        label_selectedCourse.setText(MessageFormat.format(
                MainApp.resourceBundle.getString("question_filter_selected_course"),
                SharedData.getSelectedCourse().getName())
        );

        // wait 0,5 seconds after moving the divider to prevent calling the function too often
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(event -> onDividerMovedDone());

        splitPane.getDividers().forEach(divider -> {
            divider.positionProperty().addListener((observable, oldValue, newValue) -> {
                // restart timer after every change
                pause.playFromStart();
            });
        });
    }

    public void applyExportBtnClicked(ActionEvent actionEvent) {
        if (this.label_selectedDirectory.getText().equals("\"\"")) {
            SharedData.setOperation(Message.ERROR_MESSAGE_SELECT_A_FOLDER_SAVE_FILE);
            return;
        }

        ExportPdf exportPdf = new ExportPdf(getTestHeader(),
                getQuestionDistance(),
                this.label_selectedDirectory.getText(),
                this.checkbox_applyHeader.isSelected(),
                this.checkbox_applyHeaderAllPages.isSelected(),
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
                getQuestionDistance(),
                this.label_selectedDirectory.getText(),
                this.checkbox_applyHeader.isSelected(),
                this.checkbox_applyHeaderAllPages.isSelected(),
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
                getQuestionDistance(),
                this.label_selectedDirectory.getText(),
                this.checkbox_applyHeader.isSelected(),
                this.checkbox_applyHeaderAllPages.isSelected(),
                this.checkbox_showPageNumber.isSelected());

        ObservableList<Question> questions = SharedData.getTestQuestions();
        ArrayList<Question> questionList = new ArrayList<>(questions);
        showPreview(exportPdf.getPreviewImages(questionList));
    }

    private void onDividerMovedDone() {
        applyFormattingBtnClicked(null);
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
        // get updated width of vbox-parent (the scroll-pane)
        double targetWidth = previewScrollPane.getViewportBounds().getWidth();

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

    private int getQuestionDistance() {
        return (int) distanceBetweenQuestionsSlider.getValue();
    }

    public void onGoBackBtnClick(ActionEvent mouseEvent) throws IOException {
        SharedData.setCurrentScreen(Screen.CREATE_MANUAL);
        switchScene(SwitchScene.CREATE_TEST_MANUAL);
    }

    public void onApplyHeaderClick(ActionEvent actionEvent) {
        headerSpecificGridPane.setDisable(!checkbox_applyHeader.isSelected());
    }
}
