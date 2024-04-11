package com.example.backend.app;

import com.example.backend.db.models.Question;
import javafx.scene.image.Image;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ExportDocx extends Export<XWPFDocument> {
    public ExportDocx() {
        super();
    }

    @Override
    public boolean exportDocument(ArrayList<Question> testQuestions) {
        try {
            // Create a new Word document
            XWPFDocument document = buildDocument(testQuestions);
            if (document == null) {
                return false;
            }

            // Save the document to a file
            FileOutputStream out = new FileOutputStream(new File(this.destinationFolder + "/" + createFileName()));
            document.write(out);
            out.close();
            Logger.log(getClass().getName(), "Docx-file created successfully", LogLevel.INFO);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected XWPFDocument buildDocument(ArrayList<Question> testQuestions) {
        this.questionNumber = 0;
        setNumberOfPages(testQuestions.size());

        try {
            XWPFDocument document = new XWPFDocument();

            // TITLE and HEADER --> is set once for the whole document
            setTitle(document, this.title);
            if (this.setHeader) {
                setContentHeader(document);
            }

            // PAGE-NUMBER --> is set once for the whole document
            if (this.setPageNumber) {
                setContentPageNumber(document);
            }

            for (int i = 0; i < this.numberOfPages; i++) {
                // CONTENT with questions
                setContent(document, testQuestions);

                // adds a page-break for a document with multiple pages
                if (i + 1 < this.numberOfPages) {
                    addPageBreak(document);
                }
            }

            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Image> getPreviewImages(ArrayList<Question> testQuestions) {
        return null;
    }

    @Override
    public String createFileName() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        return String.format("test_%04d-%02d-%02d_%02d-%02d-%02d.docx",
                currentDateTime.getYear(),
                currentDateTime.getMonthValue(),
                currentDateTime.getDayOfMonth(),
                currentDateTime.getHour(),
                currentDateTime.getMinute(),
                currentDateTime.getSecond());
    }

    private void setTitle(XWPFDocument document, String title) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(18);
        titleRun.setText(title);
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
        run.setText("Page ");
        paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
        run = paragraph.createRun();
        run.setText(" of ");
        paragraph.getCTP().addNewFldSimple().setInstr("NUMPAGES \\* MERGEFORMAT");
    }

    /**
     * visit stackoverflow to view more:
     * <a href="https://stackoverflow.com/questions/72343953/add-dynamic-footer-in-ms-word-using-apache-poi-java">stackoverflow</a>
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

        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop  cTTabStop = cTTabs.addNewTab();
        cTTabStop.setPos(BigInteger.valueOf(pos));

        switch(type) {
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
        int counter = 0;

        for (int i = 0; i < this.questionsPerSite; i++) {
            counter++;
            // stop when all questions have been printed or the max-amount of questions-per-size hast been printed
            if (questionNumber == testQuestions.size() || (counter % this.questionsPerSite + 1) == 0) {
                break;
            }

            // questionString
            XWPFParagraph questionParagraph = document.createParagraph();
            XWPFRun questionRun = questionParagraph.createRun();
            questionRun.setBold(true);
            questionRun.setText((questionNumber + 1) + ". " + testQuestions.get(questionNumber).getQuestion());
            questionRun.addCarriageReturn();

            // answer
            XWPFRun answerRun = questionParagraph.createRun();
            answerRun.setBold(false);
            answerRun.setText("A:");

            // adding paragraphs for the answer-block
            if (counter % this.questionsPerSite != 0) {
                // not adding paragraphs for the last question-block
                for (int j = 0; j < getParagraphCount(); j++) {
                    XWPFParagraph answerBlockParagraph = document.createParagraph();
                }
            }

            questionNumber++;
        }
    }

    private void addPageBreak(XWPFDocument document) {
        // Create a paragraph with a run containing a break for a new page
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);
    }

    /**
     * Calculation the number of paragraphs between questions.
     * So that setting of 4, 5,... questions per site looks ok.
     * @return the number of paragraphs between questions (the answer-block)
     */
    private int getParagraphCount() {
        int paragraphsOnOnePage = 29 - this.questionsPerSite;

        // first page
        if (this.questionNumber < this.questionsPerSite) {
            // mind the title
            return (int) (paragraphsOnOnePage - 2)/this.questionsPerSite;
        }

        // following pages
        return (int) paragraphsOnOnePage/this.questionsPerSite;
    }

    private void convertWordToPdf(XWPFDocument wordDocument) {

    }
}
