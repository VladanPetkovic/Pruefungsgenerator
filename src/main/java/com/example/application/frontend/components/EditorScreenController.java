package com.example.application.frontend.components;

import com.example.application.MainApp;
import com.example.application.backend.app.LaTeXLogic;
import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Message;
import com.example.application.frontend.modals.Latex_ScreenController;
import com.example.application.frontend.modals.ModalOpener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Component
@Scope("prototype")
public class EditorScreenController {
    public TabPane tabPane;
    public Tab previewTab;
    public Tab editorTab;
    @Setter
    private int numberOfQuestion;
    @Setter
    private VBox parentVbox;
    @Getter
    public HTMLEditor editor;
    @FXML
    private WebView questionPreview;
    @FXML
    private HBox displayImagesHbox;
    public final ArrayList<ImageWithButtons> imageList = new ArrayList<>();

    public void initialize() {
        setScrollingBehaviour();
        initTabPaneListener();
        // make the webView not editable
        questionPreview.addEventFilter(KeyEvent.ANY, Event::consume);
        questionPreview.addEventFilter(MouseEvent.ANY, Event::consume);
    }

    private void initTabPaneListener() {
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab oldTab, Tab newTab) {
                if (newTab == previewTab) {
                    onPreviewTabClick();
                }
            }
        });
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
        Latex_ScreenController controller = (Latex_ScreenController) modalOpener.getModal().controller;

        // listener for when the latex modal is closed
        newStage.setOnHidden(event -> {
            String latexCode = controller.getLatexCode();

            if (latexCode != null && !latexCode.isEmpty() && controller.isInsertLatex()) {
                String latexTag = "<latex>" + latexCode + "</latex>";
                insertTextWithJsoup(latexTag);
            }
            // simulate user presses space (otherwise the binding listener does recognize a change and the up/down buttons dont work as intended)
            editor.fireEvent(new KeyEvent(
                    KeyEvent.KEY_PRESSED, " ", " ", KeyCode.SPACE, false, false, false, false
            ));
            editor.fireEvent(new KeyEvent(
                    KeyEvent.KEY_RELEASED, " ", " ", KeyCode.SPACE, false, false, false, false
            ));
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

    public void onPreviewTabClick() {
        LaTeXLogic laTeXLogic = new LaTeXLogic();

        Logger.log(getClass().getName(), "previewContentBefore: " + editor.getHtmlText(), LogLevel.INFO);
        String previewHtml = laTeXLogic.transformLatexTags(editor.getHtmlText(), true);

        //clear webview content
        questionPreview.getEngine().loadContent("");

        Logger.log(getClass().getName(), "previewContentAfter: " + previewHtml, LogLevel.INFO);
        //load to webview
        questionPreview.getEngine().loadContent(previewHtml);
    }

    private void insertTextWithJsoup(String newText) {
        String currentHtml = editor.getHtmlText();
        //parse html with jsoup
        Document doc = Jsoup.parse(currentHtml);

        // clean nested html or body tags
        Element body = doc.body();

        //wrap latex code in paragraph and add it to the body
        body.appendElement("p").text(newText);

        //add new body to html
        editor.setHtmlText(doc.body().html());
    }

    /**
     * listener that saves the changes from user input for the question text in the sharedData.testquestions array
     * (used for pdf export)
     */
    public void onKeyReleased(KeyEvent keyEvent) {
        String newValue = editor.getHtmlText();
        SharedData.getTestQuestions().get(numberOfQuestion).setQuestion(newValue);
    }
}
