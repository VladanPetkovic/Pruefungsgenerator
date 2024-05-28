package com.example.frontend.controller;

import com.example.backend.app.SharedData;
import com.example.frontend.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class PicturePickerController{
    @FXML
    private Button uploadPicture;
    @FXML
    private HBox displayImages;
    private final FileChooser fileChooser;
    private static final FileChooser.ExtensionFilter EXTENSION_FILTER =
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
    private final ArrayList<ButtonAndImage> buttonAndImages;

    public ArrayList<com.example.backend.db.models.Image> getImages(){
        ArrayList<com.example.backend.db.models.Image> images = new ArrayList<>();
        for (ButtonAndImage bai : buttonAndImages){
            images.add(new com.example.backend.db.models.Image(bai.image,bai.imageName));
        }
        return images;
    }

    private TextArea textArea = null;

    public void setTextArea(TextArea textArea){
        this.textArea = textArea;
    }

    public boolean invalidSyntax(){
        for(ButtonAndImage bai : buttonAndImages){
            if(!textArea.getText().contains(bai.getTag()))return true;
        }
        return false;
    }

    public PicturePickerController() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(EXTENSION_FILTER);
        buttonAndImages = new ArrayList<>();
    }

    public void addPreExistingImages(ArrayList<com.example.backend.db.models.Image> images){
        for (com.example.backend.db.models.Image image : images){
            buttonAndImages.add(new ButtonAndImage(image.getName(),image.byteArrayToImage()));
        }
    }

    @FXML
    private void onActionUploadPicture() {
        File file = fileChooser.showOpenDialog(MainApp.stage);
        if (file != null) {
            String fileName = file.getName();
            if(isFileAlreadyUploaded(fileName)) return;
            Image image = new Image(file.toURI().toString());
            buttonAndImages.add(new ButtonAndImage(fileName, image));
        }
    }

    private boolean isFileAlreadyUploaded(String fileName){
        for(ButtonAndImage bai : buttonAndImages){
            if(bai.imageName.equals(fileName)){
                SharedData.setOperation("Can't upload picture with same name twice.",true);
                return true;
            }
        }
        return false;
    }

    public void removeTagFromTextArea(String tag){
        textArea.setText(textArea.getText().replaceAll(tag,""));
    }

    public class ButtonAndImage {
        public final String imageName;
        public final Image image;
        private final ImageView imageView;
        private final Button removeButton;
        private final StackPane stackPane;

        public ButtonAndImage(String imageName, Image image) {
            this.imageName = imageName;
            this.image = image;

            removeButton = new Button("X");
            removeButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");

            stackPane = new StackPane();

            imageView = new ImageView(image);
            imageView.setFitHeight(100); // Set a width for the image view
            imageView.setPreserveRatio(true); // Preserve aspect ratio

            stackPane.getChildren().addAll(imageView,removeButton);

            StackPane.setAlignment(removeButton, Pos.TOP_RIGHT);
            StackPane.setMargin(removeButton,new javafx.geometry.Insets(5));

            removeButton.setOnAction(actionEvent -> {
                displayImages.getChildren().remove(stackPane);
                buttonAndImages.remove(this);
                removeTagFromTextArea(getTag());
            });

            imageView.setOnMouseClicked(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(getTag());
                clipboard.setContent(content);
                SharedData.setOperation("Tag was copied to clipboard, paste into question text.",false);
            });

            Tooltip.install(imageView,new Tooltip(imageName));

            displayImages.getChildren().add(stackPane);
        }

        public String getTag(){
            return "<img name=\""+imageName+"\"/>";
        }
    }
}
