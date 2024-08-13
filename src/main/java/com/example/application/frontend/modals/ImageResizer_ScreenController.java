package com.example.application.frontend.modals;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.app.SharedData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.*;

import java.io.IOException;

public class ImageResizer_ScreenController extends ModalController {

    @FXML
    private Slider sliderWidth;
    @FXML
    private ImageView imageDisplay;
    private Image originalImage;
    public Image outputImage = null;

    public ImageResizer_ScreenController() {
    }

    @FXML
    public void initialize() {
        if (SharedData.getResizeImage() == null) {
            Logger.log(getClass().getName(), "The image is null", LogLevel.ERROR);
            return;
        }

        initImage(SharedData.getResizeImage());

        sliderWidth.valueProperty().addListener((observableValue, number, t1) -> {
            double scaleFactor = t1.doubleValue() / originalImage.getWidth();
            imageDisplay.setFitWidth(t1.doubleValue());
            imageDisplay.setFitHeight(originalImage.getHeight() * scaleFactor);
        });
    }

    public void initImage(Image image) {
        originalImage = image;
        imageDisplay.setImage(image);
        sliderWidth.setValue(image.getWidth());
    }

    @FXML
    private void onSaveBtnClick(ActionEvent actionEvent) throws IOException {
        if (originalImage != null) {
            double scaleFactor = sliderWidth.getValue() / originalImage.getWidth();
            int newWidth = (int) sliderWidth.getValue();
            int newHeight = (int) (originalImage.getHeight() * scaleFactor);
            outputImage = rescaleImage(originalImage, newWidth, newHeight);
            SharedData.setResizeImage(outputImage);
            closeStage(actionEvent);
        }
    }

    @FXML
    private void onCancelBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    private Image rescaleImage(Image image, int newWidth, int newHeight) {
        WritableImage writableImage = new WritableImage(newWidth, newHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();

        double scaleX = image.getWidth() / newWidth;
        double scaleY = image.getHeight() / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                pixelWriter.setArgb(x, y, pixelReader.getArgb((int) (x * scaleX), (int) (y * scaleY)));
            }
        }
        return writableImage;
    }
}
