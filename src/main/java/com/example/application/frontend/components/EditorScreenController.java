package com.example.application.frontend.components;

import com.example.application.MainApp;
import com.example.application.backend.app.SharedData;
import com.example.application.backend.db.models.Message;
import com.example.application.frontend.modals.ModalOpener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Component
@Scope("prototype")
public class EditorScreenController {
    public HTMLEditor editor;
    //public TextFlow questionPreview;
    @FXML
    private WebView questionPreview;
    public HBox displayImagesHbox;
    public final ArrayList<ImageWithButtons> imageList = new ArrayList<>();

    public void initialize() {
        editor.setHtmlText("");
        setScrollingBehaviour();
    }

    private void setScrollingBehaviour() {
        // TODO: fix scrolling
//        // otherwise we are not able to scroll, when the mouse has entered the editor
//        editor.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, event -> {
//            if (event.getDeltaY() != 0) {
//                parentVbox.fireEvent(event);
//            }
//        });

        editor.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                // Consume the event to prevent scrolling
                event.consume();
            }
        });
    }

    public void onActionUploadPicture(ActionEvent actionEvent) {
        if (imageList.size() == 10) {
            SharedData.setOperation(Message.MAX_PICTURES_UPLOADED);
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(MainApp.stage);
        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            if (!isFileAlreadyUploaded(imagePath)) {
                Image image = new Image(selectedFile.toURI().toString());
                imageList.add(new ImageWithButtons(imagePath, image, imageList, displayImagesHbox, editor));
                SharedData.setImageEditing(image);
            }
            // create an <img> HTML tag
            String imgTag = "<img src='" + imagePath + "' alt='Image' style='max-width:100%;height:auto;' />";
            editor.setHtmlText(editor.getHtmlText() + imgTag);
        }
    }

    private boolean isFileAlreadyUploaded(String fileName) {
        for (ImageWithButtons bai : imageList) {
            if (bai.imageName.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public void onAddLatexBtnClick(ActionEvent actionEvent) {
        ModalOpener modalOpener = new ModalOpener();
        Stage newStage = modalOpener.openModal(ModalOpener.LATEX);
        // listener for when the stage is closed

        newStage.setOnHidden(event -> {
            String latexCode = SharedData.getLatexCode();

            if (latexCode != null && !latexCode.isEmpty()) {

                String latexTag = "<latex>" + latexCode + "</latex>";
                insertTextWithJsoup(latexTag);
                System.out.println("Updated HTML Content: " + editor.getHtmlText());
            }
        });
    }

    public Set<com.example.application.backend.db.models.Image> getImages() {
        Set<com.example.application.backend.db.models.Image> images = new HashSet<>();
        for (ImageWithButtons bai : imageList) {
            images.add(new com.example.application.backend.db.models.Image(bai.image, bai.imageName));
        }
        return images;
    }

    public boolean invalidSyntax() {
        for (ImageWithButtons bai : imageList) {
            if (!editor.getHtmlText().contains(bai.getTag())) {
                return true;
            }
        }
        return false;
    }

    public void addPreExistingImages(Set<com.example.application.backend.db.models.Image> images) {
        if (images == null) {
            return;
        }
        if (images.isEmpty()) {
            return;
        }

        for (com.example.application.backend.db.models.Image image : images) {
            imageList.add(new ImageWithButtons(image.getName(), image.byteArrayToImage(), imageList, displayImagesHbox, editor));
        }
    }


    public void onPreviewTabClick(Event event) {

        //clear webview content
        questionPreview.getEngine().loadContent("");
        System.out.println("previewContentBefore: "+editor.getHtmlText());

        //decode html content
        String htmlText = editor.getHtmlText().replace("&lt;", "<").replace("&gt;", ">");

        //regex for latex tags
        //(.*?) = non greedy pattern match
        String regex = "(.*?)<latex>(.*?)</latex>|(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL); //.DOTALL includes \n and \r
        // the matcher is used to find occurrences of the regex pattern in the htmlText
        Matcher matcher = pattern.matcher(htmlText);

        StringBuilder htmlBuilder = new StringBuilder("<html><body>");

        while (matcher.find()) {
            // add plain text before the latex
            if (matcher.group(1) != null) {
                htmlBuilder.append(matcher.group(1));
            }

            //process latex
            if (matcher.group(2) != null) {
                try {
                    String latexContent = matcher.group(2).trim();
                    Image image = getImageFromLatex(latexContent);

                    //save javaFx image as temp file
                    File tempFile = File.createTempFile("latex_image", ".png"); // <-- location depends on OS
                    tempFile.deleteOnExit(); //<--deletes after use

                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(bufferedImage, "png", tempFile);

                    //image location added to html
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

        htmlBuilder.append("</body></html>");

        System.out.println("previewContentAfter: "+ htmlBuilder.toString());
        //load to webview
        questionPreview.getEngine().loadContent(htmlBuilder.toString());
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

    private void insertTextWithJsoup(String newText) {

        String currentHtml = editor.getHtmlText();
        //parse html with jsoup
        Document doc = Jsoup.parse(currentHtml);

        // clean nested html or body tags
        Element body = doc.body();

        //wrap latex code in paragraph and add it to the body
        body.appendElement("p").text(newText);

        //add new body to html
        editor.setHtmlText(doc.body().html());
    }



}
