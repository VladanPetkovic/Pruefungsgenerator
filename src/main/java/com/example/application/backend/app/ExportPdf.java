package com.example.application.backend.app;

import com.example.application.backend.db.models.Answer;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.models.Type;
import com.itextpdf.html2pdf.HtmlConverter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ExportPdf extends Export {
    private final LaTeXLogic laTeXLogic = new LaTeXLogic();
    protected int questionsPerSite;
    protected String title;
    protected String destinationFolder;
    protected boolean setHeader;
    protected boolean setPageNumber;

    public ExportPdf(String testHeader,
                     int questionsPerSite,
                     String destFolder,
                     boolean setHeader,
                     boolean setPageNumber) {
        this.title = testHeader;
        this.questionsPerSite = questionsPerSite;
        this.destinationFolder = destFolder;
        this.setHeader = setHeader;
        this.setPageNumber = setPageNumber;
    }

    public boolean exportDocument(ArrayList<Question> testQuestions) {
        String htmlContent = buildHtmlContent(testQuestions);

        String filePath = this.destinationFolder + "/" + createFileName(true);

        try {
            toPdf(htmlContent, filePath);
            Logger.log(getClass().getName(), "Pdf-file created successfully", LogLevel.INFO);
            return true;
        } catch (IOException e) {
            Logger.log(getClass().getName(), "Error creating PDF: " + e.getMessage(), LogLevel.ERROR);
            return false;
        }
    }

    public ArrayList<Image> getPreviewImages(ArrayList<Question> testQuestions) {
        ArrayList<Image> previewImages = new ArrayList<>();

        String htmlContent = buildHtmlContent(testQuestions);
        String filePath = "bin/temp.pdf";

        try {
            toPdf(htmlContent, filePath);
            File pdfFile = new File(filePath);
            previewImages = convertPdfToImages(pdfFile);
            if (pdfFile.delete()) {
                Logger.log(getClass().getName(), "Temp-pdf-file deleted successfully.", LogLevel.INFO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return previewImages;
    }

    private ArrayList<Image> convertPdfToImages(File pdfFile) {
        ArrayList<Image> images = new ArrayList<>();

        try {
            PDDocument pdDocument = Loader.loadPDF(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

            for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 300);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                images.add(image);
            }

            pdDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    private String buildHtmlContent(ArrayList<Question> testQuestions) {
        StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder.append("<html><head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; }")
                .append("h1 { text-align: center; font-size: 20px; }")
                .append(".question { margin: 15px 0; }")
                .append(".answers { margin-left: 20px; }")
                .append("</style>")
                .append("</head><body>");

        // title
        htmlBuilder.append("<h1>").append(this.title).append("</h1>");

        // questions
        int questionNumber = 1;
        for (Question question : testQuestions) {
            htmlBuilder.append("<div class='question'>").append(questionNumber).append(". ")
                    .append(extractHtmlContent(question.getQuestion())).append("</div>");

            htmlBuilder.append("<div class='answers'>");
            if (Type.isOpen(question.getType())) {
                htmlBuilder.append("<p>A: ___________</p>");
            } else if (Type.isMultipleChoice(question.getType())) {
                for (Answer answer : question.getAnswers()) {
                    htmlBuilder.append("<p>□ ").append(answer.getAnswer()).append("</p>");
                }
            } else if (Type.isTrueFalse(question.getType())) {
                htmlBuilder.append("<p>□ True</p>").append("<p>□ False</p>");
            }
            htmlBuilder.append("</div>");

            questionNumber++;
        }

        htmlBuilder.append("</body></html>");
        return laTeXLogic.transformLatexTags(htmlBuilder.toString(), false);
    }

    /**
     * TODO:
     * - set content header
     * - set content page number
     * - set questionsPerSite
     * - set images
     */

    private String extractHtmlContent(String questionHtml) {
        if (questionHtml.contains("<body")) {
            int bodyStart = questionHtml.indexOf("<body");
            int bodyEnd = questionHtml.indexOf("</body>") + 7;
            return questionHtml.substring(bodyStart, bodyEnd);
        }
        return questionHtml;
    }

    private void toPdf(String htmlContent, String filePath) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            HtmlConverter.convertToPdf(htmlContent, outputStream);
        }
    }
}
