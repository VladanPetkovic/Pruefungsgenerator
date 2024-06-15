package com.example.backend.db.models;

import com.example.backend.db.SQLiteDatabaseConnection;
import javafx.embed.swing.SwingFXUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
@Setter
@AllArgsConstructor
public class Image {
    private int id;
    private byte[] image;
    private String name;
    private int position;
    private String comment;

    public Image(javafx.scene.image.Image image, String name) {
        this.image = imageToByteArray(image, getFileExtension(name));
        this.name = name;
    }

    public static void createImages(Question question, int newQuestionId) {
        if (question == null) {
            return;
        }
        if (question.getImages() == null) {
            return;
        }
        SQLiteDatabaseConnection.IMAGE_REPOSITORY.add(question.getImages(), newQuestionId);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        // Split the filename by the dot
        String[] parts = fileName.split("\\.");

        // Check if there is an extension part
        if (parts.length < 2) {
            return "";
        }

        // The extension is the last part
        return parts[parts.length - 1].toLowerCase();
    }

    private byte[] imageToByteArray(javafx.scene.image.Image image, String format) {
        try {
            // Convert JavaFX Image to BufferedImage
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

            // Convert BufferedImage to Byte Array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, format, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Convert byte array to JavaFX Image
    public javafx.scene.image.Image byteArrayToImage() {
        try {
            // Convert byte array to BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(image);
            BufferedImage bufferedImage = ImageIO.read(bais);

            // Convert BufferedImage to JavaFX Image
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
