package com.example.backend.app;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Question;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExportTests {
    Export export = new Export();

    @BeforeAll
    void beforeAll() {
        System.out.println("Starting with Export-tests");
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
        System.out.println("Export-tests finished");
    }

    @Test
    void exportToPdf_createPdf() {
        System.out.println("Check: creation of the pdf document");
        // arrange
        Question testQuestion = new Question();
        String testHeader = "Test: Datenmanagement";

        // act
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll(testQuestion, "Datenmanagement", false);
        boolean pdfWasCreated = export.exportToPdf(questions, testHeader);

        // assert
        assertTrue(pdfWasCreated);
    }

    @Test
    void exportToPdf_createPdfWithAllQuestions() {
        System.out.println("Check: creation of the pdf document");
        // arrange
        String testHeader = "Test: all questions";

        // act
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll();
        boolean pdfWasCreated = export.exportToPdf(questions, testHeader);

        // assert
        assertTrue(pdfWasCreated);
    }
}
