package com.example.application.backend.app;

import com.example.application.MainApp;
import com.example.application.backend.db.models.Answer;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.models.Type;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.SaveFormat;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class ExportDocx extends Export {
    private final int distancePerQuestion;
    private final String title;
    private final String destinationFolder;
    private final boolean setHeader;
    private final boolean setHeaderAllPages;
    private final boolean setPageNumber;

    public ExportDocx(String testHeader,
                      int distancePerQuestion,
                      String destFolder,
                      boolean setHeader,
                      boolean setHeaderAllPages,
                      boolean setPageNumber) {
        this.title = testHeader;
        this.distancePerQuestion = distancePerQuestion;
        this.destinationFolder = destFolder;
        this.setHeader = setHeader;
        this.setHeaderAllPages = setHeaderAllPages;
        this.setPageNumber = setPageNumber;
    }

    /**
     * Using Aspose
     */
    public boolean exportDocument(ArrayList<Question> testQuestions) {
        String htmlContent = buildHtmlContent(testQuestions, title, distancePerQuestion);
        String filePath = this.destinationFolder + "/" + createFileName(false);
        try {
            Document doc = new Document();

            // insert html-string
            DocumentBuilder builder = new DocumentBuilder(doc);
            builder.insertHtml(htmlContent);

            // save document as word
            doc.save(new FileOutputStream(filePath), SaveFormat.DOCX);
            Logger.log(getClass().getName(), "Docx-file created successfully", LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(getClass().getName(), "Error exporting Docx-file: " + e.getMessage(), LogLevel.ERROR);
            return false;
        }
        return true;
    }

    /**
     * Using Docx4J
     */
//    public boolean exportDocument(ArrayList<Question> testQuestions) {
//        String htmlContent = buildHtmlContent(testQuestions);
//        String filePath = this.destinationFolder + "/" + createFileName(false, title);
//        try {
//            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
//
//            // import html into word
//            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
//            List<Object> elements = xhtmlImporter.convert(htmlContent, null);
//            wordMLPackage.getMainDocumentPart().getContent().addAll(elements);
//
//            // save document
//            wordMLPackage.save(new File(filePath));
//            Logger.log(getClass().getName(), "Docx-file created successfully", LogLevel.INFO);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
    protected XWPFDocument buildDocument(ArrayList<Question> testQuestions) {
        try {
            XWPFDocument document = new XWPFDocument();

            if (setHeader) {
                setContentHeader(document);
            }

            if (setPageNumber) {
                setContentPageNumber(document);
            }

            // CONTENT with questions
            setContent(document, testQuestions);

            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setContentHeader(XWPFDocument document) {
        XWPFHeaderFooterPolicy headerFooterPolicy = document.createHeaderFooterPolicy();
        XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFParagraph headerParagraph = header.createParagraph();
        XWPFRun run = headerParagraph.createRun();

        // DATE, NAME and UID
        run.setText("Date:");
        for (int i = 0; i < 11; i++) {
            run.addTab();
        }
        run.setText("Name:");
        run.addCarriageReturn();
        for (int i = 0; i < 11; i++) {
            run.addTab();
        }
        run.setText("UID:");
    }

    private void setContentPageNumber(XWPFDocument document) {
        XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

        XWPFParagraph paragraph = footer.getParagraphArray(0);
        if (paragraph == null) paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        addTabStop(paragraph, "CENTER", 3.25);

        XWPFRun run = paragraph.createRun();
        run.setText(MainApp.resourceBundle.getString("page") + " ");
        paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
        run = paragraph.createRun();
        run.setText(" " + MainApp.resourceBundle.getString("of") + " ");
        paragraph.getCTP().addNewFldSimple().setInstr("NUMPAGES \\* MERGEFORMAT");
    }

    /**
     * visit stackoverflow to view more:
     * <a href="https://stackoverflow.com/questions/72343953/add-dynamic-footer-in-ms-word-using-apache-poi-java">stackoverflow</a>
     *
     * @param paragraph
     * @param type
     * @param posInches
     */
    private void addTabStop(XWPFParagraph paragraph, String type, double posInches) {
        int twipsPerInch = 1440; //measurement unit for tab stop pos is twips (twentieth of an inch point)
        long pos = Math.round(posInches * twipsPerInch);

        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP cTP = paragraph.getCTP();
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr cTPPr = cTP.getPPr();
        if (cTPPr == null) cTPPr = cTP.addNewPPr();
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs cTTabs = cTPPr.getTabs();
        if (cTTabs == null) cTTabs = cTPPr.addNewTabs();

        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop cTTabStop = cTTabs.addNewTab();
        cTTabStop.setPos(BigInteger.valueOf(pos));

        switch (type) {
            case "LEFT":
                cTTabStop.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc.LEFT);
                break;
            case "CENTER":
                cTTabStop.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc.CENTER);
                break;
            case "RIGHT":
                cTTabStop.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc.RIGHT);
                break;
            case "DECIMAL":
                cTTabStop.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc.DECIMAL);
                break;
            case "BAR":
                cTTabStop.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc.BAR);
                break;
            default:
                // simply do nothing
        }

    }

    private void setContent(XWPFDocument document, ArrayList<Question> testQuestions) {
        int questionNumber = 0;

        for (Question question : testQuestions) {
            questionNumber++;

            // questionString
            XWPFParagraph questionParagraph = document.createParagraph();
            XWPFRun questionRun = questionParagraph.createRun();
            questionRun.setBold(true);
            questionRun.setText(questionNumber + ". " + question.getQuestion());

            // answer
            XWPFRun answerRun = questionParagraph.createRun();
            answerRun.setBold(false);
            // different questionTypes
            if (Type.isOpen(question.getType())) {
                answerRun.addCarriageReturn();
                answerRun.setText("A:");
            } else if (Type.isMultipleChoice(question.getType())) {
                for (Answer answer : question.getAnswers()) {
                    setMcAnswer(answer.getAnswer(), answerRun);
                }
            } else if (Type.isTrueFalse(question.getType())) {
                setMcAnswer("True", answerRun);
                setMcAnswer("False", answerRun);
            }


            for (int j = 0; j < getParagraphCount(questionNumber) && questionNumber != testQuestions.size(); j++) {
                document.createParagraph();
            }
        }
    }

    private void setMcAnswer(String answerText, XWPFRun answerRun) {
        try {
            answerRun.addCarriageReturn();
            answerRun.addTab();
            answerRun.addPicture(new FileInputStream("src/main/resources/com/example/application/icons/checkbox.png"), XWPFDocument.PICTURE_TYPE_PNG, "unchecked_checkbox.png", Units.toEMU(15), Units.toEMU(15));
            answerRun.setText(answerText);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculation the number of paragraphs between questions.
     * So that setting of 4, 5,... questions per site looks ok.
     *
     * @return the number of paragraphs between questions (the answer-block)
     */
    private int getParagraphCount(int questionNumber) {
        int paragraphsOnOnePage = 25 - this.distancePerQuestion;

        // first page
        if (questionNumber < this.distancePerQuestion) {
            // mind the title
            return (int) (paragraphsOnOnePage - 2) / this.distancePerQuestion;
        }

        // following pages
        return (int) paragraphsOnOnePage / this.distancePerQuestion;
    }
}
