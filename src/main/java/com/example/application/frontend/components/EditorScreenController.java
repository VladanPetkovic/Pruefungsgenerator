package com.example.application.frontend.components;

import com.example.application.MainApp;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Message;
import com.example.application.frontend.modals.ModalOpener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
@Scope("prototype")
public class EditorScreenController {
    public HTMLEditor editor;
    public TextFlow questionPreview;
    public HBox displayImagesHbox;
    public final ArrayList<ImageWithButtons> imageList = new ArrayList<>();

    public void initialize() {
        editor.setHtmlText("");
        setScrollingBehaviour();
    }

    private void setScrollingBehaviour() {
        // TODO: fix scrolling
//        // otherwise we are not able to scroll, when the mouse has entered the editor
//        editor.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, event -> {
//            if (event.getDeltaY() != 0) {
//                parentVbox.fireEvent(event);
//            }
//        });

        editor.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                // Consume the event to prevent scrolling
                event.consume();
            }
        });
    }

    public void onActionUploadPicture(ActionEvent actionEvent) {
        if (imageList.size() == 10) {
            SharedData.setOperation(Message.MAX_PICTURES_UPLOADED);
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(MainApp.stage);
        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            if (!isFileAlreadyUploaded(imagePath)) {
                Image image = new Image(selectedFile.toURI().toString());
                imageList.add(new ImageWithButtons(imagePath, image, imageList, displayImagesHbox, editor));
                SharedData.setImageEditing(image);
            }
            // create an <img> HTML tag
            String imgTag = "<img src='" + imagePath + "' alt='Image' style='max-width:100%;height:auto;' />";
            editor.setHtmlText(editor.getHtmlText() + imgTag);
        }
    }

    private boolean isFileAlreadyUploaded(String fileName) {
        for (ImageWithButtons bai : imageList) {
            if (bai.imageName.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public void onAddLatexBtnClick(ActionEvent actionEvent) {
        ModalOpener modalOpener = new ModalOpener();
        Stage newStage = modalOpener.openModal(ModalOpener.LATEX);

        // listener for when the stage is closed
        newStage.setOnHidden(event -> {
            // TODO
        });
    }

    public Set<com.example.application.backend.db.models.Image> getImages() {
        Set<com.example.application.backend.db.models.Image> images = new HashSet<>();
        for (ImageWithButtons bai : imageList) {
            images.add(new com.example.application.backend.db.models.Image(bai.image, bai.imageName));
        }
        return images;
    }

    public boolean invalidSyntax() {
        for (ImageWithButtons bai : imageList) {
            if (!editor.getHtmlText().contains(bai.getTag())) {
                return true;
            }
        }
        return false;
    }

    public void addPreExistingImages(Set<com.example.application.backend.db.models.Image> images) {
        if (images == null) {
            return;
        }
        if (images.isEmpty()) {
            return;
        }

        for (com.example.application.backend.db.models.Image image : images) {
            imageList.add(new ImageWithButtons(image.getName(), image.byteArrayToImage(), imageList, displayImagesHbox, editor));
        }
    }

    public void onPreviewTabClick(Event event) {
        // TODO: add here the parse logic to LaTeX
    }
}
