package com.example.backend.app;

import com.example.backend.db.models.Question;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.rendering.PDFRenderer;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
public class Export {
    private float pageWidth;
    private float pageHeight;
    private float margin = 50;
    private int questionsPerSite = 5;
    private int questionNumber = 0;
    private String title = "";
    private String destinationFolder = "";
    private boolean setHeader = true;
    private boolean setPageNumber = true;

    /**
     * This functions exports a pdf with given testQuestions.
     * @param testQuestions ArrayList of testQuestions for making the pdf-File.
     * @return True, if export was successful and false, if something failed.
     */
    public boolean exportToPdf(ArrayList<Question> testQuestions) {
        boolean returnValue = false;

        try {
            PDDocument document = buildPdfDocument(testQuestions);
            if (document == null) {
                return false;
            }

            document.save(this.destinationFolder + "/" + createFileName());
            document.close();
            returnValue = true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return returnValue;    // needed later for confirming user the creation of the pdf document
    }

    /**
     * This functions builds the pdf-document. It is used for exporting and preview of the pdf-file.
     * @param testQuestions ArrayList of Questions needed for creating the pdf-document.
     * @return A document will be returned. Returns null, when something fails.
     */
    private PDDocument buildPdfDocument(ArrayList<Question> testQuestions) {
        this.questionNumber = 0;

        try {
            // Create a new PDF document
            PDDocument document = new PDDocument();
            PDPageContentStream contentStream = null;

            for (int i = 0; i < getNumberOfPages(testQuestions.size()); i++) {
                PDPage page = new PDPage();
                document.addPage(page);

                if (contentStream != null) {
                    contentStream.close(); // Close the previous content stream
                }

                contentStream = new PDPageContentStream(document, page);

                this.pageWidth = page.getMediaBox().getWidth();
                this.pageHeight = page.getMediaBox().getHeight();

                // setting our title and header only on the first page
                if (i == 0) {
                    setTitle(contentStream, this.title);
                    if (this.setHeader) {
                        setContentHeader(contentStream);
                    }
                }

                // set content with questions
                setContent(contentStream, testQuestions);
                // set pageNumber
                if (this.setPageNumber) {
                    setContentPageNumber(contentStream, i + 1);
                }
            }

            if (contentStream != null) {
                contentStream.close(); // Close the last content stream
            }

            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This functions makes an ArrayList of images (page of pdf-document) and returns it for the frontend-controller.
     * @param testQuestions ArrayList of questions needed for building the pdf-document.
     * @return ArrayList of images - these images are going to be used in the GUI.
     */
    public ArrayList<Image> getPdfPreviewImages(ArrayList<Question> testQuestions) {
        ArrayList<Image> images = new ArrayList<>();

        try {
            PDDocument document = buildPdfDocument(testQuestions);
            if (document == null) {
                return images;
            }

            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 300);

                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                images.add(image);
            }

            document.close();
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    /**
     * !!!Use this function before exportToPdf()!!!
     * Sets important parameters for the export-function.
     * @param testHeader The Title of the test
     * @param questionsPerSite The amount of questions on one page
     * @param destFolder The destination folder, where the user saves the test
     */
    public void setOptions(String testHeader, int questionsPerSite, String destFolder, boolean setHeader, boolean setPageNumber) {
        this.title = testHeader;
        this.questionsPerSite = questionsPerSite;
        this.destinationFolder = destFolder;
        this.setHeader = setHeader;
        this.setPageNumber = setPageNumber;
    }

    /**
     * Creates the fileName with the current DateTime.
     * @return String, for example: "test_2024-02-11_18-38-27.pdf"
     */
    public String createFileName() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        int year = currentDateTime.getYear();
        int month = currentDateTime.getMonthValue();
        int day = currentDateTime.getDayOfMonth();
        int hour = currentDateTime.getHour();
        int minute = currentDateTime.getMinute();
        int second = currentDateTime.getSecond();

        return String.format("test_%04d-%02d-%02d_%02d-%02d-%02d.pdf", year, month, day, hour, minute, second);
    }

    private int getNumberOfPages(int numberOfQuestions) {
        float numberOfPages = (float) numberOfQuestions /this.questionsPerSite;
        return (int) Math.ceil(numberOfPages);
    }

    private void setTitle(PDPageContentStream contentStream, String title) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
        float titleWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA).getStringWidth(title) / 1000f * 16;
        float titlePositionX = (this.pageWidth - titleWidth) / 2;
        contentStream.beginText();
        contentStream.newLineAtOffset(titlePositionX, this.pageHeight - this.margin - 20); // getting the title at correct height
        contentStream.showText(title);
        contentStream.endText();
    }

    private void setContentHeader(PDPageContentStream contentStream) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
        contentStream.beginText();
        contentStream.newLineAtOffset(this.margin, this.pageHeight - this.margin + 15);
        contentStream.showText("Date:________________");
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(this.pageWidth - 4 * this.margin, this.pageHeight - this.margin + 15);
        contentStream.showText("Name:________________");
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(this.pageWidth - 4 * this.margin, this.pageHeight - this.margin);
        contentStream.showText("UID:________________");
        contentStream.endText();
    }

    private void setContentPageNumber(PDPageContentStream contentStream, int pageNumber) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
        contentStream.beginText();
        contentStream.newLineAtOffset(this.pageWidth - 40, 40);
        contentStream.showText(String.valueOf(pageNumber));
        contentStream.endText();
    }

    private void setContent(PDPageContentStream contentStream, ArrayList<Question> testQuestions) throws IOException {
        float contentCoordinateY;
        if (questionNumber == 0) {
            contentCoordinateY = 680;
        } else {
            contentCoordinateY = (this.pageHeight - this.margin - 10);
        }

        int counter = 0;

        for (int i = 0; i < this.questionsPerSite; i ++) {
            counter++;
            // stop when all questions have been printed or the max-amount of questions-per-size hast been printed
            if (questionNumber == testQuestions.size() || (counter % this.questionsPerSite + 1) == 0) {
                break;
            }

            // setting font to be bold for the questionString
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(this.margin, contentCoordinateY); // Set Y-coordinate for Line 1
            contentStream.showText((questionNumber + 1) + ". ");
            contentStream.endText();

            for (String line : splitLongQuestion(testQuestions.get(questionNumber).getQuestionString())) {
                contentStream.beginText();
                contentStream.newLineAtOffset(this.margin + 20, contentCoordinateY); // Set Y-coordinate for Line 1
                contentStream.showText(line);
                contentStream.endText();
                contentCoordinateY -= 15;
            }

            // using normal font instead of bold
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(this.margin + 10, contentCoordinateY);
            contentStream.showText("A:");
            contentStream.endText();

            contentCoordinateY -= getParagraphDistance();
            questionNumber++;
        }
    }

    private float getParagraphDistance() {
        float pageSize = this.pageHeight - (2 * this.margin) - 70;
        return pageSize/this.questionsPerSite;
    }

    private ArrayList<String> splitLongQuestion(String originalQuestion) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder question = new StringBuilder(originalQuestion);

        // get length of our question
        float questionWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA).
                getStringWidth("1. " + question) / 1000f * 12;

        // while the question is too long, we will split it
        while (questionWidth > this.pageWidth - 2 * this.margin) {
            int index = findSpaceOrHyphen(question, 90);    // guessing that 90 chars can be in one sentence
            if (index == -1) {
                break;
            } else {
                lines.add(question.substring(0, index));
                question.delete(0, index);
                questionWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA).
                        getStringWidth(String.valueOf(question)) / 1000f * 12;
            }
        }

        // if question was short enough
        if (lines.isEmpty()) {
            lines.add(originalQuestion);
        }

        return lines;
    }

    private static int findSpaceOrHyphen(StringBuilder input, int maxLength) {
        // Ensure that maxLength is not greater than the length of the string
        int length = Math.min(input.length(), maxLength);

        for (int i = length - 1; i > 0; i--) {
            char currentChar = input.charAt(i);
            if (currentChar == ' ' || currentChar == '-') {
                return i;
            }
        }

        return -1; // Return -1 if no space or hyphen is found within the specified length
    }

//    private String getOutPutPath() {
//        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/example/backend/app/output_config.txt"))) {
//            return reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
