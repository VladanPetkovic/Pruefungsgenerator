package com.example.application.frontend.modals;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ImportErrorScreenController extends ModalController {
    public Label errorLabel;

    public void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }
}
