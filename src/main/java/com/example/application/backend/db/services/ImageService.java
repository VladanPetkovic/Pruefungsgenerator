package com.example.application.backend.db.services;

import com.example.application.backend.app.LogLevel;
import com.example.application.backend.app.Logger;
import com.example.application.backend.db.models.Image;
import com.example.application.backend.db.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
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
}
