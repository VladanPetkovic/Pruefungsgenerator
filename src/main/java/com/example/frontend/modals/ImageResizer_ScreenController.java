package com.example.frontend.modals;

import com.example.frontend.components.PicturePickerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ImageResizer_ScreenController implements Initializable {

    @FXML
    private Slider sliderWidth;
    @FXML
    private ImageView imageDisplay;

    private PicturePickerController picturePickerController = null;

    private Image originalImage;
    public Image outputImage = null;

    public ImageResizer_ScreenController() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureSlider(sliderWidth);
        sliderWidth.valueProperty().addListener((observableValue, number, t1) -> {
            double scaleFactor = t1.doubleValue() / originalImage.getWidth();
            imageDisplay.setFitWidth(t1.doubleValue());
            imageDisplay.setFitHeight(originalImage.getHeight() * scaleFactor);
        });
    }

    private void configureSlider(Slider slider) {
        slider.setMin(100);
        slider.setMax(600);
        slider.setValue(500);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(100);
    }

    public void setPicturePickerController(PicturePickerController picturePickerController) {
        if (this.picturePickerController == null) {
            this.picturePickerController = picturePickerController;
        }
    }

    public void setImage(Image image) {
        originalImage = image;
        imageDisplay.setImage(image);
        sliderWidth.setValue(image.getWidth());
    }

    @FXML
    private void onActionSaveChanges(ActionEvent actionEvent) {
        if (originalImage != null) {
            double scaleFactor = sliderWidth.getValue() / originalImage.getWidth();
            int newWidth = (int) sliderWidth.getValue();
            int newHeight = (int) (originalImage.getHeight() * scaleFactor);
            outputImage = rescaleImage(originalImage, newWidth, newHeight);
            closeStage(actionEvent);
        }
    }

    @FXML
    private void onActionCancelChanges(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    private void closeStage(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    private Image rescaleImage(Image image, int newWidth, int newHeight) {
        WritableImage writableImage = new WritableImage(newWidth, newHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();

        double scaleX = image.getWidth() / newWidth;
        double scaleY = image.getHeight() / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                pixelWriter.setArgb(x, y, pixelReader.getArgb((int)(x * scaleX), (int)(y * scaleY)));
            }
        }
        return writableImage;
    }
}
