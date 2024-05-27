package com.example.frontend.controller;

import com.example.frontend.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
            Image image = new Image(file.toURI().toString());
            buttonAndImages.add(new ButtonAndImage(fileName, image));
        }
    }

    public class ButtonAndImage {
        public final String imageName;
        public final Image image;
        private final Button imageButton;
        private final ImageView imageView;
        private final Button removeButton;
        private final HBox hBox;

        public ButtonAndImage(String imageName, Image image) {
            this.imageName = imageName;
            this.image = image;

            removeButton = new Button("X");
            hBox = new HBox();


            imageView = new ImageView(image);
            imageView.setFitWidth(100); // Set a width for the image view
            imageView.setPreserveRatio(true); // Preserve aspect ratio

            imageButton = new Button(imageName);

            hBox.getChildren().add(imageButton);
            hBox.getChildren().add(removeButton);


            imageButton.setOnMouseEntered(event -> showImage());
            imageButton.setOnMouseExited(event -> hideImage());
            removeButton.setOnAction(actionEvent -> {
                displayImages.getChildren().remove(hBox);
                buttonAndImages.remove(this);
            });
            imageButton.setOnAction(actionEvent -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString("<img name=\""+imageName+"\"/>");
                clipboard.setContent(content);
            });

            displayImages.getChildren().add(hBox);
        }

        private void showImage() {
            if (!hBox.getChildren().contains(imageView)) {
                hBox.getChildren().add(imageView);
            }
        }

        private void hideImage() {
            hBox.getChildren().remove(imageView);
        }
    }
}
