package com.example.application.backend.app;

import com.example.application.MainApp;
import com.example.application.backend.db.models.Question;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ExportPdf extends Export {
    private int distancePerQuestion;
    private final String title;
    private final String destinationFolder;
    private final boolean setHeader;
    private final boolean setHeaderAllPages;
    private final boolean setPageNumber;

    public ExportPdf(String testHeader, int distancePerQuestion, String destFolder, boolean setHeader,
                     boolean setHeaderAllPages, boolean setPageNumber) {
        this.title = testHeader;
        this.distancePerQuestion = distancePerQuestion;
        this.destinationFolder = destFolder;
        this.setHeader = setHeader;
        this.setHeaderAllPages = setHeaderAllPages;
        this.setPageNumber = setPageNumber;
    }

    public boolean exportDocument(ArrayList<Question> testQuestions) {
        String htmlContent = buildHtmlContent(testQuestions, title, distancePerQuestion);
        String filePath = this.destinationFolder + "/" + createFileName(true);
        String tempFilePath = "bin/export_temp.pdf";

        try {
            generateTempPdf(htmlContent, tempFilePath);
            processOptions(tempFilePath, filePath, true); // add header and/or pageNumbers
            Logger.log(getClass().getName(), "Pdf-file created successfully", LogLevel.INFO);
            return true;
        } catch (IOException e) {
            Logger.log(getClass().getName(), "Error creating PDF: " + e.getMessage(), LogLevel.ERROR);
            return false;
        }
    }

    public ArrayList<Image> getPreviewImages(ArrayList<Question> testQuestions) {
        ArrayList<Image> previewImages = new ArrayList<>();

        String htmlContent = buildHtmlContent(testQuestions, title, distancePerQuestion);
        String tempFilePath = "bin/temp.pdf";

        try {
            generateTempPdf(htmlContent, tempFilePath);
            String processedFilePath = processOptions(tempFilePath, "bin/new_temp.pdf", false); // add header and/or pageNumbers
            File pdfFile = new File(processedFilePath);
            previewImages = convertPdfToImages(pdfFile);
            if (pdfFile.delete()) {
                Logger.log(getClass().getName(), processedFilePath + " deleted successfully.", LogLevel.DEBUG);
            }
        } catch (IOException e) {
            Logger.log(getClass().getName(), "Error getting preview images: " + e.getMessage(), LogLevel.ERROR);
        }

        return previewImages;
    }

    private String processOptions(String oldFilePath, String newFilePath, boolean isForExport) throws IOException {
        if (!setHeader && !setPageNumber && !isForExport) {
            return oldFilePath;
        }

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(oldFilePath), new PdfWriter(newFilePath))) {
            if (setPageNumber) {
                addPageNumbersToDocument(pdfDocument);
            }
            if (setHeader) {
                addHeadersToDocument(pdfDocument);
            }
        }
        // deleting old file
        if (new File(oldFilePath).delete()) {
            Logger.log(getClass().getName(), oldFilePath + " deleted successfully.", LogLevel.DEBUG);
        }
        return newFilePath; // on success return newFile
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
            Logger.log(getClass().getName(), "Error converting PDF to images: " + e.getMessage(), LogLevel.ERROR);
        }

        return images;
    }

    private void generateTempPdf(String htmlContent, String filePath) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            HtmlConverter.convertToPdf(htmlContent, outputStream);
        }
    }

    private void addPageNumbersToDocument(PdfDocument pdfDocument) throws IOException {
        int numberOfPages = pdfDocument.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            PdfPage page = pdfDocument.getPage(i);
            float pageWidth = page.getPageSize().getWidth();

            PdfCanvas pdfCanvas = new PdfCanvas(page);
            pdfCanvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(), 10)
                    .moveText(pageWidth - 70, 20)
                    .showText(MainApp.resourceBundle.getString("page") +
                            " " + i + " " +
                            MainApp.resourceBundle.getString("of") +
                            " " + numberOfPages).endText();
        }
    }

    private void addHeadersToDocument(PdfDocument pdfDocument) throws IOException {
        int numberOfPages = pdfDocument.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            PdfPage page = pdfDocument.getPage(i);
            float pageHeight = page.getPageSize().getHeight();

            PdfCanvas pdfCanvas = new PdfCanvas(page);
            String dateText = MainApp.resourceBundle.getString("date_pdf");
            String nameText = MainApp.resourceBundle.getString("name_pdf");
            String uidText = MainApp.resourceBundle.getString("uid_pdf");

            PdfFont font = PdfFontFactory.createFont();
            float fontSize = 12;
            float yPosition = pageHeight - 40;

            String fullHeaderText = String.format("%-60s%-50s%-20s", dateText, nameText, uidText);
            pdfCanvas.beginText().setFontAndSize(font, fontSize);
            pdfCanvas.moveText(70, yPosition).showText(fullHeaderText);

            pdfCanvas.endText();
            if (!setHeaderAllPages) {
                break;
            }
        }
    }
}
