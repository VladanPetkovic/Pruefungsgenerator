package com.example.application.backend.app;

import com.example.application.backend.db.models.Question;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExportTests {
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
        ArrayList<Question> questions = new ArrayList<>();
        ExportPdf export = new ExportPdf(testHeader,
                2,
                "C:\\Users\\vlada\\Downloads",
                true,
                true);
        boolean pdfWasCreated = export.exportDocument(questions);

        // assert
        assertTrue(pdfWasCreated);
    }

    @Test
    void exportToPdf_createPdfWithAllQuestions() {
        System.out.println("Check: creation of the pdf document");
        // arrange
        String testHeader = "Test: all questions";

        // act
        ArrayList<Question> questions = new ArrayList<>();
        ExportPdf export = new ExportPdf(testHeader,
                2,
                "C:\\Users\\vlada\\Downloads",
                true,
                true);
        boolean pdfWasCreated = export.exportDocument(questions);

        // assert
        assertTrue(pdfWasCreated);
    }

    @Test
    void exportToDocx_createDocxWithAllQuestions() {
        System.out.println("Check: creation of the docx document");
        // arrange
        String testHeader = "Test: all questions";

        // act
        ArrayList<Question> questions = new ArrayList<>();
        ExportDocx exportDocx = new ExportDocx(testHeader,
                6,
                "C:\\Users\\vlada\\Downloads",
                true,
                true);
        boolean docxWasCreated = exportDocx.exportDocument(questions);

        // assert
        assertTrue(docxWasCreated);
    }
}
