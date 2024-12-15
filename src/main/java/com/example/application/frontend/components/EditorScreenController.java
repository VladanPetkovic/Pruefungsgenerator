package com.example.application.frontend.components;

import com.example.application.MainApp;
import com.example.application.frontend.controller.FXMLDependencyInjection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope("prototype")
public class EditorScreenController {
    //    private VBox parentVbox; // TODO: this does not work: scrolling
    public HTMLEditor editor;
    public TextFlow questionPreview;
    public VBox picturePickerPlaceholder;
    public PicturePickerController picturePickerController;

    public void initialize() {
        editor.setHtmlText("");
        setScrollingBehaviour();

        try {
            FXMLLoader loader = FXMLDependencyInjection.getLoader("components/picture_picker.fxml", MainApp.resourceBundle);
            VBox picturePicker = loader.load();
            picturePickerController = loader.getController();
            picturePickerPlaceholder.getChildren().add(picturePicker);
//            picturePickerController.setTextArea(question);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setScrollingBehaviour() {
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
    }

    public void onAddLatexBtnClick(ActionEvent actionEvent) {
    }

    public void parseAndDisplayContent() {
        Pattern pattern = Pattern.compile("<img name=\"(.*?)\"/>");
        Matcher matcher = pattern.matcher(editor.getHtmlText());
        int lastIndex = 0;
        while (matcher.find()) {
            String textBeforeImage = editor.getHtmlText().substring(lastIndex, matcher.start());
            if (!textBeforeImage.isEmpty()) {
                questionPreview.getChildren().add(new Text(textBeforeImage));
            }
            String imageName = matcher.group(1);
            for (PicturePickerController.ButtonAndImage image : picturePickerController.buttonAndImages) {
                if (image.imageName.equals(imageName)) {
                    ImageView imageView = new ImageView(image.image);
                    questionPreview.getChildren().add(imageView);
                }
            }
            lastIndex = matcher.end();
        }
        String textAfterLastImage = editor.getHtmlText().substring(lastIndex);
        if (!textAfterLastImage.isEmpty()) {
            questionPreview.getChildren().add(new Text(textAfterLastImage));
        }
    }

    private boolean previewQuestionShouldBeVisible() {
        if (picturePickerController.invalidSyntax()) {
            return false;
        }
        if (picturePickerController.buttonAndImages.isEmpty()) {
            return false;
        }
        return true;


//        question.textProperty().addListener((observableValue, s, t1) -> {
//            previewQuestion.setVisible(previewQuestionShouldBeVisible());
//        });
    }
}
