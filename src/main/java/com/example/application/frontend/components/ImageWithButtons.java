package com.example.application.frontend.components;

import com.example.application.backend.app.SharedData;
import com.example.application.frontend.modals.ModalOpener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class ImageWithButtons {
    public final String imageName;
    public final Image image;
    public ArrayList<ImageWithButtons> imageList;
    public HBox displayImagesHbox;
    private HTMLEditor editor;


    public ImageWithButtons(String imageName,
                            Image image,
                            ArrayList<ImageWithButtons> imageList,
                            HBox displayImagesHbox, HTMLEditor editor) {
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
            displayImagesHbox.getChildren().remove(stackPane);
            imageList.remove(this);
            removeTagFromEditor(getTag());
        });

        resizeButton.setOnAction(actionEvent -> {
            resizeImage(stackPane);
        });

        Tooltip.install(imageButton, new Tooltip(imageName));
        displayImagesHbox.getChildren().add(stackPane);
    }

    public void removeTagFromEditor(String tag) {
        editor.setHtmlText(editor.getHtmlText().replaceAll(tag, ""));
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
        String deleteImagePath = "src/main/resources/com/example/application/icons/delete_trash_can.png";
        return getButton(deleteImagePath);
    }

    /**
     * This function creates the scale-btn: a scale-icon.
     *
     * @return the button for scaling the image.
     */
    private Button addScaleBtn() {
        String resizeImagePath = "src/main/resources/com/example/application/icons/resize.png";
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
        ModalOpener modalOpener = new ModalOpener();
        Stage newStage = modalOpener.openModal(ModalOpener.IMAGE_RESIZER);

        newStage.setOnHidden(event -> {
            imageList.add(new ImageWithButtons(
                    this.imageName,
                    SharedData.getImageEditing(),
                    imageList,
                    displayImagesHbox,
                    editor
            ));
            displayImagesHbox.getChildren().remove(stackPane);
            imageList.remove(this);
            removeTagFromEditor(getTag());
        });
    }

    public String getTag() {
        return "<img name=\"" + imageName + "\"/>";
    }
}
