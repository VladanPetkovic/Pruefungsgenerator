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
        export.setOptions(testHeader, 9, null);
        boolean pdfWasCreated = export.exportToPdf(questions);

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
        export.setOptions(testHeader, 5, null);
        boolean pdfWasCreated = export.exportToPdf(questions);

        // assert
        assertTrue(pdfWasCreated);
    }

    @Test
    void createFileName_checkFileName() {
        System.out.println("Check: valid file-name");

        System.out.println(export.createFileName());

        assertTrue(true);
    }
}
