package com.example.application.frontend.components;

import com.example.application.frontend.controller.ScreenController;
import com.example.application.frontend.controller.SwitchScene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.example.application.backend.app.SharedData;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.application.frontend.controller.SwitchScene.switchScene;

@Component
@Scope("prototype")
public class TitleBanner_ScreenController extends ScreenController {
    public Button helpBtn;
    public Tooltip helpTooltip;
    @FXML
    private Label titleLabel;
    @FXML
    private Label statusLabel;
    private static Timeline statusResetTimer;

    @FXML
    public void initialize() {
        // bind the text property of titleLabel to the pageTitleProperty in SharedData
        titleLabel.textProperty().bind(SharedData.pageTitleProperty());
        // bind the text property of statusLabel to the operationStatusProperty in SharedData
        statusLabel.textProperty().bind(SharedData.operationStatusProperty());
        helpTooltip.textProperty().bind(SharedData.helpTooltipProperty());

        // bind the textFill property of statusLabel to the error type in SharedData
        statusLabel.textFillProperty().bind(
                Bindings.when(SharedData.operationIsErrorTypeProperty())
                        .then(Color.RED)
                        .otherwise(Color.FORESTGREEN)
        );
    }

    /**
     * handles click event for navigating to the home screen and resetting shared data
     */
    public void onFHTWLogoClick() throws IOException {
        // navigate to the home screen and reset shared data
        switchScene(SwitchScene.HOME);
        SharedData.resetAll();
    }

    public static void resetOperationStatusAfterDelay() {
        // check if the statusResetTimer is not null
        // if it's not null, stop the timer
        if (statusResetTimer != null) {
            statusResetTimer.stop();
        }

        // create a new timeline with a keyframe that sets the operationStatus to an empty string after 5 seconds
        statusResetTimer = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            SharedData.setOperation("", false);
        }));
        statusResetTimer.play();
    }

    public void onMouseEntered(MouseEvent mouseEvent) {
        if (helpTooltip.isShowing()) {
            helpTooltip.hide();
        } else {
            helpTooltip.show(helpBtn, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    public void onMouseExited(MouseEvent mouseEvent) {
        helpTooltip.hide();
    }
}

