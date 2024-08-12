package com.example.backend.db.services;

import com.example.backend.db.models.Keyword;
import com.example.backend.db.repositories.KeywordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeywordService {
    private static final Logger logger = LogManager.getLogger(KeywordService.class);
    private final KeywordRepository keywordRepository;

    @Autowired
    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    public Keyword add(Keyword keyword) {
        Keyword newKeyword = keywordRepository.save(keyword);
        logger.info("Keyword saved with ID: {}", newKeyword.getId());
        return newKeyword;
    }

    public Keyword getById(Long id) {
        Keyword keyword = keywordRepository.findById(id).orElse(null);
        if (keyword != null) {
            logger.info("Keyword found with ID: {}", id);
        } else {
            logger.warn("Keyword not found with ID: {}", id);
        }
        return keyword;
    }

    public List<Keyword> getAll() {
        List<Keyword> keywords = keywordRepository.findAll();
        logger.info("Retrieved all keywords, count: {}", keywords.size());
        return keywords;
    }

    public Keyword update(Keyword keyword) {
        Keyword existingKeyword = keywordRepository.findById(keyword.getId()).orElse(null);
        if (existingKeyword != null) {
            existingKeyword.setKeyword(keyword.getKeyword());

            Keyword updatedKeyword = keywordRepository.save(existingKeyword);
            logger.info("Keyword updated successfully for ID: {}", keyword.getId());
            return updatedKeyword;
        } else {
            logger.error("Failed to find keyword with ID: {}", keyword.getId());
            throw new RuntimeException("Keyword not found");
        }
    }

    public void remove(Long id) {
        try {
            keywordRepository.deleteById(id);
            logger.info("Keyword deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete keyword with ID: {}", id, e);
            throw e;
        }
    }
}
