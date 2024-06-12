package com.example.backend.app;

import com.example.backend.db.models.Answer;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.Type;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chunk;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;


import javax.swing.text.StyleConstants;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ExportPdf extends Export<Document> {
    private final float margin = 50;
    private PdfDocument pdfDocument;
    private int questionNumber = 0;

    public ExportPdf() {
        super();
    }

    @Override
    public boolean exportDocument(ArrayList<Question> testQuestions) {
        // Create a new PDF document
        File file = new File(this.destinationFolder + "\\" + createFileName(true));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        PdfWriter writer = new PdfWriter(fos);
        pdfDocument = new PdfDocument(writer);
        Document document = buildDocument(testQuestions);

        if (document == null) {
            return false;
        }

        document.close();
        Logger.log(getClass().getName(), "Pdf-file created successfully", LogLevel.INFO);

        return true;    // needed later for confirming user the creation of the pdf document
    }

    @Override
    protected Document buildDocument(ArrayList<Question> testQuestions) {
        setNumberOfPages(testQuestions.size());

        try {
            // create the document
            Document newDocument = new Document(pdfDocument);
            this.questionNumber = 0;

            for (int i = 1; i <= this.numberOfPages; i++) {
                // TITLE and HEADER (only on the first page)
                if (i == 1) {
                    setTitle(newDocument, this.title);
                    if (this.setHeader) {
                        setContentHeader(newDocument);
                    }
                }

                // CONTENT with questions
                setContent(newDocument, testQuestions);
                // PAGE-NUMBER
                if (this.setPageNumber) {
                    setContentPageNumber(newDocument, i);
                }

                // add another page, if no new page was created
                if (pdfDocument.getNumberOfPages() == i && i != this.numberOfPages) {
                    newDocument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }

            return newDocument;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<javafx.scene.image.Image> getPreviewImages(ArrayList<Question> testQuestions) {
        ArrayList<javafx.scene.image.Image> images = new ArrayList<>();

        createBinDirectory();
        // Create a temporary pdf document
        PdfWriter writer = null;
        try {
            writer = new PdfWriter("bin/temp.pdf");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        pdfDocument = new PdfDocument(writer);
        Document document = buildDocument(testQuestions);
        // Close the PDF document
        document.close();
        pdfDocument.close();

        // converting pdf to images
        File file = new File("bin/temp.pdf");
        try {
            PDDocument pdDocument = Loader.loadPDF(file);
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

            for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 300);

                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                images.add(image);
            }

            pdDocument.close();
            if (file.delete()) {
                Logger.log(getClass().getName(), "Temp-pdf-file deleted successfully.", LogLevel.INFO);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return images;
    }

    private void setTitle(Document document, String title) throws IOException {
        Paragraph titleHeader = new Paragraph(title)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(titleHeader);
    }

    private void setContentHeader(Document document) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Rectangle pageSize = getPageSize(document);
        float fontSize = 11;

        Paragraph dateParagraph = new Paragraph("Date:")
                .setFont(font)
                .setFontSize(fontSize)
                .setFixedLeading(fontSize)
                .setTextAlignment(TextAlignment.LEFT);
        Paragraph nameParagraph = new Paragraph("Name:")
                .setFont(font)
                .setFontSize(fontSize)
                .setFixedLeading(fontSize)
                .setTextAlignment(TextAlignment.CENTER);
        Paragraph uidParagraph = new Paragraph("UID:")
                .setFont(font)
                .setFontSize(fontSize)
                .setFixedLeading(fontSize)
                .setTextAlignment(TextAlignment.RIGHT);
        document.showTextAligned(dateParagraph, margin, pageSize.getHeight() - margin + 10, TextAlignment.LEFT);
        document.showTextAligned(nameParagraph, pageSize.getWidth()/2, pageSize.getHeight() - margin + 10, TextAlignment.CENTER);
        document.showTextAligned(uidParagraph, pageSize.getWidth() - 2 * margin, pageSize.getHeight() - margin + 10, TextAlignment.RIGHT);
    }

    private void setContentPageNumber(Document document, int pageNumber) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Rectangle pageSize = getPageSize(document);

        Paragraph pageNumParagraph = new Paragraph(String.format("%d", pageNumber));
        pageNumParagraph
                .setFont(font)
                .setFontSize(11);
        pageNumParagraph
                .setTextAlignment(TextAlignment.RIGHT);
        document.showTextAligned(pageNumParagraph, pageSize.getWidth() - margin, margin, TextAlignment.RIGHT);
    }

    private void setContent(Document document, ArrayList<Question> testQuestions) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        for (int i = questionsPerSite; i > 0; i--) {
            if (testQuestions.size() == this.questionNumber) {
                break;
            }
            Question question = testQuestions.get(this.questionNumber);

            // setting font to be bold for the questionString
            Paragraph questionParagraph = new Paragraph(questionNumber + 1 + ". " + question.getQuestion())
                    .setFont(fontBold)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(questionParagraph);

            // using normal font instead of bold
            if (question.getType().getType() == Type.OPEN) {
                Paragraph answerParagraph = new Paragraph("A: ")
                        .setFont(font)
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.LEFT);
                document.add(answerParagraph);
            } else if (question.getType().getType() == Type.MULTIPLE_CHOICE) {
                for (Answer answer : question.getAnswers()) {
                    document.add(setMcAnswer(" " + answer.getAnswer(), font));
                }
            } else if (question.getType().getType() == Type.TRUE_FALSE) {
                document.add(setMcAnswer(" True", font));
                document.add(setMcAnswer(" False", font));
            }
            this.questionNumber++;

            for (int j = 0; j < getParagraphCount(questionNumber) && questionNumber != testQuestions.size(); j++) {
                document.add(new Paragraph());
            }
        }
    }

    private Paragraph setMcAnswer(String answerText, PdfFont font) {
        try {
            Paragraph p = new Paragraph();
            PdfFont zapfdingbats = PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS);
            Text chunk = new Text("r").setFont(zapfdingbats).setFontSize(12);
            Text answerChunk = new Text(answerText).setFont(font).setFontSize(12);
            p.add(chunk);
            p.add(answerChunk);
            return p;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getParagraphCount(int questionNumber) {
        int paragraphsOnOnePage = 60 - this.questionsPerSite;

        // first page
        if (questionNumber < this.questionsPerSite) {
            // mind the title
            return (int) (paragraphsOnOnePage - 2)/this.questionsPerSite;
        }

        // following pages
        return (int) paragraphsOnOnePage/this.questionsPerSite;
    }

    private Rectangle getPageSize(Document document) {
        PdfDocument pdf = document.getPdfDocument();
        return pdf.getDefaultPageSize();
    }

    /**
     * Creates a bin-directory, if not already existing.
     */
    private void createBinDirectory() {
        File binDirectory = new File("bin");
        if (!binDirectory.exists()) {
            binDirectory.mkdir();
            Logger.log(getClass().getName(), "bin-directory created successfully!", LogLevel.INFO);
        }
    }
}
