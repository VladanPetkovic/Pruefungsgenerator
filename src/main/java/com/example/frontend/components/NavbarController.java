package com.example.frontend.components;

import com.example.backend.app.SharedData;
import com.example.frontend.controller.ScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import com.example.backend.app.Screen;

import java.io.IOException;



public class NavbarController extends ScreenController {

    @FXML
    ImageView createTestAutomaticNavImageView;
    @FXML
    ImageView createTestManualNavImageView;
    @FXML
    ImageView createQuestionNavImageView;
    @FXML
    ImageView editQuestionNavImageView;
    @FXML
    ImageView settingsNavImageView;
    @FXML
    Label createTestAutomaticNavLabel;
    @FXML
    Label createTestManualNavLabel;
    @FXML
    Label createQuestionNavLabel;
    @FXML
    Label editQuestionNavLabel;
    @FXML
    Label settingsNavLabel;

    @FXML
    public void initialize(){
        switch(SharedData.getCurrentScreen()) {
            case CreateAutomatic:
                createAutomaticSelected();
                break;
            case CreateManual:
                createManualSelected();
                break;
            case QuestionCreate:
                createQuestionSelected();
                break;
            case QuestionEdit:
                editQuestionSelected();
                break;
            case Settings:
                settingsSelected();
                break;
            default:
        }
    }

    /**
     * handles click event for navigating to the create automatic test screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onCreateAutTestNavBtnClick(MouseEvent event) throws IOException {
        SharedData.setCurrentScreen(Screen.CreateAutomatic);
        switchScene(createTestAutomatic,true);
    }

    /**
     * handles click event for navigating to the create manual test screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onCreateManTestNavBtnClick(MouseEvent event) throws IOException {
        SharedData.setCurrentScreen(Screen.CreateManual);
        switchScene(createTestManual,true);
    }

    /**
     * handles click event for navigating to the question upload screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onUploadQuestionNavBtnClick(MouseEvent event) throws IOException
    {
        SharedData.setCurrentScreen(Screen.QuestionCreate);
        switchScene(questionCreate,true);
    }

    /**
     * handles click event for navigating to the question edit screen
     * @param event the mouse click event
     * @throws IOException if there is an error loading the screen
     */
    @FXML
    protected void onEditQuestionNavBtnClick(MouseEvent event) throws IOException
    {
        SharedData.setCurrentScreen(Screen.QuestionEdit);
        switchScene(questionEdit,true);
    }

    @FXML
    protected void onSettingsNavBtnClick(MouseEvent event) throws IOException
    {
        SharedData.setCurrentScreen(Screen.Settings);
        switchScene(settings,true);
    }

    private void createAutomaticSelected() {
        createTestAutomaticNavLabel.getStyleClass().add("navigation_item_label_selected");
        createTestAutomaticNavImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/frontend/icons/file_add_blue.png")));
    }

    private void createManualSelected() {
        createTestManualNavLabel.getStyleClass().add("navigation_item_label_selected");
        createTestManualNavImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/frontend/icons/file_add_blue.png")));
    }

    private void createQuestionSelected() {
        createQuestionNavLabel.getStyleClass().add("navigation_item_label_selected");
        createQuestionNavImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/frontend/icons/file_upload_blue.png")));
    }

    private void editQuestionSelected() {
        editQuestionNavLabel.getStyleClass().add("navigation_item_label_selected");
        editQuestionNavImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/frontend/icons/file_edit_blue.png")));
    }

    private void settingsSelected() {
        settingsNavLabel.getStyleClass().add("navigation_item_label_selected");
        settingsNavImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/frontend/icons/settings_blue.png")));
    }

}
