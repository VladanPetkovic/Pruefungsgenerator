package com.example.application.frontend.components;
import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import lombok.Getter;

import java.util.List;



@Getter
public class Editor {
    //private String text; //text of editor can be retrieved via .getDocument.getText()
    private String text;
    private final RichTextArea richTextArea = new RichTextArea();
    private Document document;
    public Editor() {
    }
    public void setText(String text) {
        this.text = text;
        updateDocument();
    }
    private void updateDocument() {
        TextDecoration textDecoration = TextDecoration.builder().presets()
                .fontFamily("Arial")
                .fontSize(20)
                .foreground("red")
                .build();
        ParagraphDecoration paragraphDecoration = ParagraphDecoration.builder().presets().build();
        DecorationModel decorationModel = new DecorationModel(0, this.text.length(), textDecoration, paragraphDecoration);
        this.document = new Document(this.text, List.of(decorationModel), this.text.length());
        richTextArea.getActionFactory().open(this.document).execute(new ActionEvent());
    }
}
