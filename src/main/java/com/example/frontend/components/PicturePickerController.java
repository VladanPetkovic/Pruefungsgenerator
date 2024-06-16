package com.example.frontend.components;

import com.example.backend.app.SharedData;
import com.example.frontend.MainApp;
import com.example.frontend.modals.AddCourse_ScreenController;
import com.example.frontend.modals.Modal;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;

public class PicturePickerController {
    @FXML
    private HBox displayImages;
    private final FileChooser fileChooser;
    @Setter
    private TextArea textArea = null;
    public final ArrayList<ButtonAndImage> buttonAndImages;
    private static final FileChooser.ExtensionFilter EXTENSION_FILTER =
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");

    public ArrayList<com.example.backend.db.models.Image> getImages() {
        ArrayList<com.example.backend.db.models.Image> images = new ArrayList<>();
        for (ButtonAndImage bai : buttonAndImages) {
            images.add(new com.example.backend.db.models.Image(bai.image, bai.imageName));
        }
        return images;
    }

    public boolean invalidSyntax() {
        for (ButtonAndImage bai : buttonAndImages) {
            if (!textArea.getText().contains(bai.getTag())) {
                return true;
            }
        }
        return false;
    }

    public PicturePickerController() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(EXTENSION_FILTER);
        buttonAndImages = new ArrayList<>();
    }

    public void addPreExistingImages(ArrayList<com.example.backend.db.models.Image> images) {
        if (images == null) {
            return;
        }
        if (images.isEmpty()) {
            return;
        }

        for (com.example.backend.db.models.Image image : images) {
            buttonAndImages.add(new ButtonAndImage(image.getName(), image.byteArrayToImage()));
        }
    }

    @FXML
    private void onActionUploadPicture() {
        if (buttonAndImages.size() == 10) {
            SharedData.setOperation("Can't upload more than 10 pictures.", true);
            return;
        }
        File file = fileChooser.showOpenDialog(MainApp.stage);
        if (file != null) {
            String fileName = file.getName();
            if (isFileAlreadyUploaded(fileName)) {
                return;
            }
            Image image = new Image(file.toURI().toString());
            buttonAndImages.add(new ButtonAndImage(fileName, image));
            SharedData.setResizeImage(image);
//                // After the resizer stage is closed, check the output image
////                Image resizedImage = imageResizerScreenController.outputImage;
////                if (resizedImage != null) {
////                    buttonAndImages.add(new ButtonAndImage(fileName, resizedImage));
////                }
        }
    }

    private boolean isFileAlreadyUploaded(String fileName) {
        for (ButtonAndImage bai : buttonAndImages) {
            if (bai.imageName.equals(fileName)) {
                SharedData.setOperation("Can't upload picture with same name twice.", true);
                return true;
            }
        }
        return false;
    }

    public void removeTagFromTextArea(String tag) {
        textArea.setText(textArea.getText().replaceAll(tag, ""));
    }

    public class ButtonAndImage {
        public final String imageName;
        public final Image image;

        public ButtonAndImage(String imageName, Image image) {
            this.imageName = imageName;
            this.image = image;

            Button resizeButton = addScaleBtn();
            Button removeButton = addRemoveBtn();
            Button imageButton = getImageButton(image);
            StackPane stackPane = new StackPane();

            stackPane.getChildren().addAll(imageButton, removeButton, resizeButton);

            StackPane.setAlignment(resizeButton, Pos.TOP_LEFT);
            StackPane.setAlignment(removeButton, Pos.TOP_RIGHT);

            removeButton.setOnAction(actionEvent -> {
                displayImages.getChildren().remove(stackPane);
                buttonAndImages.remove(this);
                removeTagFromTextArea(getTag());
            });

            resizeButton.setOnAction(actionEvent -> {
                resizeImage(stackPane);
            });

            imageButton.setOnAction(actionEvent -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(getTag());
                clipboard.setContent(content);
                SharedData.setOperation("Tag was copied to clipboard, paste into question text.", false);
            });

            Tooltip.install(imageButton, new Tooltip(imageName));

            displayImages.getChildren().add(stackPane);
        }

        private Button getImageButton(Image image) {
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100); // Set a width for the image view
            imageView.setPreserveRatio(true); // Preserve aspect ratio
            Button button = new Button();
            button.setGraphic(imageView);
            button.getStyleClass().add("btn_image_icon");
            return button;
        }

        /**
         * This function creates the remove-btn: a trashcan.
         *
         * @return the button for deleting the image.
         */
        private Button addRemoveBtn() {
            String deleteImagePath = "src/main/resources/com/example/frontend/icons/delete_trash_can.png";
            return getButton(deleteImagePath);
        }

        /**
         * This function creates the scale-btn: a scale-icon.
         *
         * @return the button for scaling the image.
         */
        private Button addScaleBtn() {
            String resizeImagePath = "src/main/resources/com/example/frontend/icons/resize.png";
            return getButton(resizeImagePath);
        }

        private Button getButton(String resizeImagePath) {
            File file = new File(resizeImagePath);
            ImageView resizeImageView = new ImageView();
            resizeImageView.setImage(new Image(file.toURI().toString()));
            resizeImageView.setFitHeight(25);
            resizeImageView.setFitWidth(25);
            Button button = new Button();
            button.setGraphic(resizeImageView);
            button.getStyleClass().add("btn_add_icon");
            return button;
        }

        private void resizeImage(StackPane stackPane) {
            Stage newStage = new Stage();
            Modal<AddCourse_ScreenController> resize_image_modal = new Modal<>("modals/image_resizer.fxml");
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle(MainApp.resourceBundle.getString("image_resize_modal_title"));
            newStage.setScene(resize_image_modal.scene);

            //listener for when the stage is closed
            newStage.setOnHidden(event -> {
                buttonAndImages.add(new ButtonAndImage(this.imageName, SharedData.getResizeImage()));
                displayImages.getChildren().remove(stackPane);
                buttonAndImages.remove(this);
                removeTagFromTextArea(getTag());
            });
            newStage.show();
        }

        public String getTag() {
            return "<img name=\"" + imageName + "\"/>";
        }
    }
}
