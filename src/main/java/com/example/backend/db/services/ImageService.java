package com.example.backend.db.services;

import com.example.backend.db.models.Image;
import com.example.backend.db.repositories.ImageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    private static final Logger logger = LogManager.getLogger(ImageService.class);
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image add(Image image) {
        Image newImage = imageRepository.save(image);
        logger.info("Image saved with ID: {}", newImage.getId());
        return newImage;
    }

    public Image getById(Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image != null) {
            logger.info("Image found with ID: {}", id);
        } else {
            logger.warn("Image not found with ID: {}", id);
        }
        return image;
    }

    public List<Image> getAll() {
        List<Image> images = imageRepository.findAll();
        logger.info("Retrieved all images, count: {}", images.size());
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
            logger.info("Image updated successfully for ID: {}", image.getId());
            return updatedImage;
        } else {
            logger.error("Failed to find image with ID: {}", image.getId());
            throw new RuntimeException("Image not found");
        }
    }

    public void remove(Long id) {
        try {
            imageRepository.deleteById(id);
            logger.info("Image deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete image with ID: {}", id, e);
            throw e;
        }
    }
}
