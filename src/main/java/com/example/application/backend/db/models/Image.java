package com.example.application.backend.db.models;

import jakarta.persistence.*;
import javafx.embed.swing.SwingFXUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    private String name;

    @Column(nullable = false)
    private Integer position;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "fk_question_id", nullable = false, updatable = false)
    private Question question = new Question();

    public Image(javafx.scene.image.Image image, String name) {
        this.image = imageToByteArray(image, getFileExtension(name));
        this.name = name;
    }

//    public static void createImages(Question question, int newQuestionId) {
//        if (question == null) {
//            return;
//        }
//        if (question.getImages() == null) {
//            return;
//        }
//        SQLiteDatabaseConnection.IMAGE_REPOSITORY.add(question.getImages(), newQuestionId);
//    }

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
