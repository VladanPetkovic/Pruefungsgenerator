package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.Image;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.repositories.ImageRepository;
import com.example.application.backend.db.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, QuestionRepository questionRepository) {
        this.imageRepository = imageRepository;
        this.questionRepository = questionRepository;
    }

    public HashSet<Image> addImages(Long questionId, Set<Image> images) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            Logger.log(this.getClass().getName(), "Question not found with ID: " + questionId, LogLevel.ERROR);
            return new RuntimeException("Question not found");
        });
        for (Image image : images) {
            image.setQuestion(question);
        }
        Logger.log(this.getClass().getName(), "Saving multiple Images", LogLevel.INFO);
        return new HashSet<>(imageRepository.saveAll(images));
    }

    public Image add(Image image) {
        Image newImage = imageRepository.save(image);
        Logger.log(this.getClass().getName(), "Image saved with ID: " + newImage.getId(), LogLevel.INFO);
        return newImage;
    }

    public Image getById(Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image != null) {
            Logger.log(this.getClass().getName(), "Image found with ID: " + id, LogLevel.INFO);
        } else {
            Logger.log(this.getClass().getName(), "Image not found with ID: " + id, LogLevel.WARN);
        }
        return image;
    }

    public List<Image> getAll() {
        List<Image> images = imageRepository.findAll();
        Logger.log(this.getClass().getName(), "Retrieved all images, count: " + images.size(), LogLevel.INFO);
        return images;
    }

    public Image update(Image image) {
        Image existingImage = imageRepository.findById(image.getId()).orElse(null);
        if (existingImage != null) {
            existingImage.setName(image.getName());
            existingImage.setPosition(image.getPosition());
            existingImage.setComment(image.getComment());
            existingImage.setImage(image.getImage());

            Image updatedImage = imageRepository.save(existingImage);
            Logger.log(this.getClass().getName(), "Image updated successfully for ID: " + image.getId(), LogLevel.INFO);
            return updatedImage;
        } else {
            Logger.log(this.getClass().getName(), "Failed to find image with ID: " + image.getId(), LogLevel.ERROR);
            throw new RuntimeException("Image not found");
        }
    }

    public void remove(Long id) {
        try {
            imageRepository.deleteById(id);
            Logger.log(this.getClass().getName(), "Image deleted successfully with ID: " + id, LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(this.getClass().getName(), "Failed to delete image with ID: " + id, LogLevel.ERROR);
            throw e;
        }
    }

    public void removeAllByQuestionId(Long questionId) {
        imageRepository.deleteByQuestionId(questionId);
        Logger.log(this.getClass().getName(), "Deleting images for question with id: " + questionId, LogLevel.INFO);
    }
}
