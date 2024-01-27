package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.frontend.MainApp;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public abstract class ScreenController {

    public static Screen<CreateAutomatic_ScreenController> createTestAutomatic = new Screen<>("sites/create_automatic.fxml");
    public static Screen<CreateManual_ScreenController> createTestManual = new Screen<>("sites/create_manual.fxml");
    public static Screen<QuestionCreate_ScreenController> questionUpload = new Screen<>("sites/question_create.fxml");
    public static Screen<QuestionEdit_ScreenController> questionEdit = new Screen<>("sites/question_edit.fxml");
    public static Screen<QuestionEdit_ScreenController> home = new Screen<>("sites/home.fxml");
    public static Screen<QuestionEdit_ScreenController> addStudyProgram = new Screen<>("sites/add_studyProgram.fxml");


    public static void switchScene(Screen screen, boolean refresh){
        if(refresh){
            screen.loadComponents();
        }
        MainApp.stage.setScene(screen.scene);
        MainApp.stage.show();
    }

    @FXML
    protected void onCreateAutTestNavBtnClick(MouseEvent event) throws IOException {
        switchScene(createTestAutomatic,true);
    }
    @FXML
    protected void onCreateManTestNavBtnClick(MouseEvent event) throws IOException {
        switchScene(createTestManual,true);
    }
    @FXML
    protected void onUploadQuestionNavBtnClick(MouseEvent event) throws IOException
    {
        System.out.println("question Upload Button Clicked");
        switchScene(questionUpload,true);
    }
    @FXML
    protected void onEditQuestionNavBtnClick(MouseEvent event) throws IOException
    {
        switchScene(questionEdit,true);
    }

    public void onFHTWLogoClick() {
        switchScene(home,true);
        SharedData.reset();
    }
}
