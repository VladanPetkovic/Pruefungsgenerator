package com.example.backend.app;

import com.example.backend.db.models.Question;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Getter
@Setter
public class Export {
    private float pageWidth;
    private float pageHeight;
    private float margin = 50;
    private final int questionsPerSite = 5;
    private int questionNumber = 0;

    public boolean exportToPdf(ArrayList<Question> testQuestions, String testHeader) {
        try {
            // Create a new PDF document
            PDDocument document = new PDDocument();
            String outputPath = getOutPutPath();
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

                // setting our title only on the first page
                if(i == 0) {
                    setTitle(contentStream, testHeader);
                }

                // set content with questions
                setContent(contentStream, testQuestions);
            }

            if (contentStream != null) {
                contentStream.close(); // Close the last content stream
            }

            document.save(outputPath + "test.pdf");
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;    // needed later for confirming user the creation of the pdf document
    }

    private int getNumberOfPages(int numberOfQuestions) {
        float numberOfPages = (float) numberOfQuestions /this.questionsPerSite;
        return (int) Math.ceil(numberOfPages);
    }

    private void setTitle(PDPageContentStream contentStream, String title) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 16);
        float titleWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA).getStringWidth(title) / 1000f * 16;
        float titlePositionX = (this.pageWidth - titleWidth) / 2;
        contentStream.beginText();
        contentStream.newLineAtOffset(titlePositionX, this.pageHeight - this.margin - 10); // getting the title at correct height
        contentStream.showText(title);
        contentStream.endText();
    }

    private void setContent(PDPageContentStream contentStream, ArrayList<Question> testQuestions) throws IOException {
        float contentCoordinateY = 680;
        int counter = 0;

        for (int i = 0; i < this.questionsPerSite; i ++) {
            counter++;
            // stop when all questions have been printed or the max-amount of questions-per-size hast been printed
            if(questionNumber == testQuestions.size() || (counter % this.questionsPerSite + 1) == 0) {
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
        float pageSize = this.pageHeight - 2 * this.margin - 70;
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

    private String getOutPutPath() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/example/backend/app/output_config.txt"))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
