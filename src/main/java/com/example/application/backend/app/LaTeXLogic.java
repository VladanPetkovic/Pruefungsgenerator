package com.example.application.backend.app;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LaTeXLogic {
    public String transformLatexTags(String htmlText, boolean isSingleQuestion) {

        // decode HTML content
        htmlText = htmlText.replace("&lt;", "<").replace("&gt;", ">");

        // regex for LaTeX tags
        //(.*?) = non-greedy pattern match
        String regex = "(.*?)<latex>(.*?)</latex>|(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL); // .DOTALL includes \n and \r
        // the matcher is used to find occurrences of the regex pattern in the htmlText
        Matcher matcher = pattern.matcher(htmlText);

        StringBuilder htmlBuilder = isSingleQuestion ? new StringBuilder("<html><body>") : new StringBuilder();

        while (matcher.find()) {
            // add plain text before the LaTeX
            if (matcher.group(1) != null) {
                htmlBuilder.append(matcher.group(1));
            }

            // process LaTeX
            if (matcher.group(2) != null) {
                try {
                    String latexContent = matcher.group(2).trim();
                    Image image = getImageFromLatex(latexContent);

                    // save JavaFX image as a temp file
                    File tempFile = File.createTempFile("bin/latex_image", ".png"); // <-- location depends on OS
                    tempFile.deleteOnExit(); // <-- deletes after use

                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(bufferedImage, "png", tempFile);

                    // image location added to HTML
                    String fileUri = tempFile.toURI().toString();
                    htmlBuilder.append("<img src='").append(fileUri).append("' />");

                } catch (Exception e) {
                    htmlBuilder.append("<span>Error rendering LaTeX</span>");
                }
            }

            // add the remaining text
            if (matcher.group(3) != null) {
                htmlBuilder.append(matcher.group(3));
            }
        }

        // when processing multiple questions (a test) body and html are already set
        if (isSingleQuestion) {
            htmlBuilder.append("</body></html>");
        }
        return htmlBuilder.toString();
    }

    public Image getImageFromLatex(String latexInput) {
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
