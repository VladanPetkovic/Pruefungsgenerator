package com.example.backend.db.services;

import com.example.backend.db.models.Answer;
import com.example.backend.db.repositories.AnswerRepository;
import com.example.backend.db.repositories.QuestionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    private static final Logger logger = LogManager.getLogger(AnswerService.class);
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }
}
