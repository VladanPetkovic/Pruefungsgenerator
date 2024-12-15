package com.example.application.frontend.components;

import com.example.application.backend.app.SharedData;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class EditorSmallScreenController {
    public HTMLEditor smallHtmlEditor;

    @Getter
    @Setter
    private String htmlText;
    @Setter
    private int numberOfQuestion;
    @Setter
    private VBox parentVbox;

    public void initialize() {
        smallHtmlEditor.setHtmlText(getHtmlText());
        setScrollingBehaviour();
    }

    private void setScrollingBehaviour() {
        // otherwise we are not able to scroll, when the mouse has entered the editor
        smallHtmlEditor.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() != 0) {
                parentVbox.fireEvent(event);
                event.consume();
            }
        });
    }

    /**
     * listener that saves the changes from user input for the question text in the sharedData.testquestions array
     * (used for pdf export)
     */
    public void onKeyReleased(KeyEvent keyEvent) {
        String newValue = smallHtmlEditor.getHtmlText();
        SharedData.getTestQuestions().get(numberOfQuestion).setQuestion(newValue);
    }
}
