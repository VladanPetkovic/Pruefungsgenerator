package com.example.application.frontend.modals;

import com.example.application.backend.db.services.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

@Component
@Scope("prototype")
public class Latex_ScreenController extends ModalController {
    private final ImageService imageService;
    public TextArea inputTextArea;
    public ImageView resultImage;
    public Label messageLabel;
    public TableView categoryTable;
    public TableColumn latexTableColumn;
    public TableColumn imageTableColumn;

    public Latex_ScreenController(ImageService imageService) {
        this.imageService = imageService;
    }

    @FXML
    private void initialize() {

    }

    public void updateLatexImage(KeyEvent keyEvent) {
        try {
            // Get the LaTeX code from the inputTextArea
            String latexInput = inputTextArea.getText();

            // If the input is not empty, try to render the LaTeX
            if (!latexInput.isEmpty()) {
                // Create a TeXFormula object from the LaTeX string
                TeXFormula formula = new TeXFormula(latexInput);

                // Create a TeXIcon to render the formula
                TeXIcon icon = formula.createTeXIcon(TeXFormula.SERIF, 20); // Font size = 20

                // Create a BufferedImage to render the LaTeX formula
                BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

                // Get the Graphics2D object from the image
                Graphics2D g2 = image.createGraphics();

                // Enable anti-aliasing for better rendering quality
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Optionally set background color to white (if needed)
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());

                // Draw the LaTeX icon on the BufferedImage
                icon.paintIcon(null, g2, 0, 0);
                g2.dispose();

                // Convert BufferedImage to JavaFX Image
                Image fxImage = SwingFXUtils.toFXImage(image, null);

                // Set the generated image into the ImageView
                resultImage.setImage(fxImage);
            } else {
                // Clear the ImageView if the input is empty
                resultImage.setImage(null);
            }
        } catch (Exception e) {
            // Handle any errors in rendering LaTeX (e.g., invalid LaTeX syntax)
            System.err.println("Error rendering LaTeX: " + e.getMessage());
            resultImage.setImage(null); // Clear the ImageView on error
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
}
