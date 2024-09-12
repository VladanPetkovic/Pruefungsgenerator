package com.example.application.frontend.modals;

import com.example.application.frontend.components.ControlsInitializer;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class ModalController extends ControlsInitializer {
    protected void closeStage(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
