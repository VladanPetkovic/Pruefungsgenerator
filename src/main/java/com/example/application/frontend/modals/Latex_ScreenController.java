package com.example.application.frontend.modals;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.services.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
@Scope("prototype")
public class Latex_ScreenController extends ModalController {
    private final ImageService imageService;
    public TextArea inputTextArea;
    public ImageView resultImage;
    public Label messageLabel;
    public Label errorLabel;

    public Latex_ScreenController(ImageService imageService) {
        this.imageService = imageService;
    }

    @FXML
    private void initialize() {
        String emptyPromptText = "e^{iy} = 1 + iy + \\frac{(iy)^2}{2!} + \\frac{(iy)^3}{3!} + \\frac{(iy)^4}{4!} + \\dots \\\\" +
                "= \\left( 1 - \\frac{y^2}{2!} + \\frac{y^4}{4!} - \\dots \\right) + i \\cdot \\left( y - \\frac{y^3}{3!} + \\frac{y^5}{5!} - \\dots \\right) \\\\" +
                "= \\cos(y) + i \\cdot \\sin(y)";
        Image promptImage = getImageFromLatex(emptyPromptText);
        resultImage.setImage(promptImage);
    }

    public void updateLatexImage(KeyEvent keyEvent) {
        try {
            String latexInput = inputTextArea.getText();

            // handle input
            if (!latexInput.isEmpty()) {
                Image resultingImage = getImageFromLatex(latexInput);
                resultImage.setImage(resultingImage);
            } else {
                // no input --> show base-image
                resultImage.setImage(null);
            }
            errorLabel.setText(null);
        } catch (Exception e) {
            // show errors
            errorLabel.setText(e.getMessage());
            Logger.log(getClass().getName(), "Error rendering LaTeX: " + e.getMessage(), LogLevel.WARN);
            String imagePath = "src/main/resources/com/example/application/icons/broken_roboter.png";
            File file = new File(imagePath);
            Image errorImage = new Image(file.toURI().toString());
            resultImage.setImage(errorImage);
        }
    }

    public void onGoBackBtnClick(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    public void onCreateBtnClick(ActionEvent actionEvent) {
    }

    public void onUpdateBtnClick(ActionEvent actionEvent) {
    }

    public void onResetBtnClick(ActionEvent actionEvent) {
    }

    private Image getImageFromLatex(String latexInput) {
        TeXFormula formula = new TeXFormula(latexInput);

        TeXIcon icon = formula.createTeXIcon(TeXFormula.SERIF, 20); // Font size = 20
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Enable anti-aliasing for better rendering quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(Color.WHITE);   // background color set to white
        g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());

        // create the image
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        // convert to JavaFX Image
        return SwingFXUtils.toFXImage(image, null);
    }
}
