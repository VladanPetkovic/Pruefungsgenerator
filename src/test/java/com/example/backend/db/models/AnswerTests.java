package com.example.backend.db.models;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnswerTests {
    @BeforeAll
    void beforeAll() {
        System.out.println("Starting with Answers-tests");
    }
    @BeforeEach
    void beforeEach() {
        System.out.println("----------------------------------------------------------------------------");
    }

    @AfterEach
    void afterEach() {
        System.out.println("----------------------------------------------------------------------------");
    }

    @AfterAll
    void afterAll() {
        System.out.println("Answers-tests finished");
    }

    // USE BOTH TESTS AND DELETE MANUALLY THE DATA IN THE DB

//    /**
//     * this test is only for "testing" purpose - not reliable, because we use assert(true, true)...
//     */
//    @Test
//    void createAnswers_checkCreationOfOneAnswer() {
//        // arrange
//        Question newQuestion = new Question();
//        int newQuestionId = 4;
//        ArrayList<Answer> answers = new ArrayList<>();
//        answers.add(new Answer("Answer 1"));
//        newQuestion.setAnswers(answers);
//
//        // act
//        Answer.createAnswers(newQuestion, newQuestionId);
//
//        // assert
//        assertEquals(true, true);
//    }

//    @Test
//    void createAnswers_checkCreationOfThreeAnswers() {
//        // arrange
//        Question newQuestion = new Question();
//        int newQuestionId = 4;
//        ArrayList<Answer> answers = new ArrayList<>();
//        answers.add(new Answer("Answer 1"));
//        answers.add(new Answer("Answer 2"));
//        answers.add(new Answer("Answer 3"));
//        newQuestion.setAnswers(answers);
//
//        // act
//        Answer.createAnswers(newQuestion, newQuestionId);
//
//        // assert
//        assertEquals(true, true);
//    }
}
