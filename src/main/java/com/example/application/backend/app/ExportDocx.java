package com.example.application.backend.app;

import com.aspose.words.*;
import com.aspose.words.Document;
import com.example.application.MainApp;
import com.example.application.backend.db.models.Question;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.io.*;
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

    public boolean exportDocument(ArrayList<Question> testQuestions) {
        String htmlContent = buildHtmlContent(testQuestions, title, distancePerQuestion);
        String filePath = this.destinationFolder + "/" + createFileName(false);
        String tempFilePath = "bin/export_docx_temp.docx";
        try {
            Document doc = new Document();
            DocumentBuilder builder = new DocumentBuilder(doc);
            builder.insertHtml(htmlContent);

            // save document as word
            doc.save(new FileOutputStream(tempFilePath), SaveFormat.DOCX);
            processOptions(tempFilePath, filePath); // add header and/or pageNumbers
            Logger.log(getClass().getName(), "Docx-file created successfully", LogLevel.INFO);
        } catch (Exception e) {
            Logger.log(getClass().getName(), "Error exporting Docx-file: " + e.getMessage(), LogLevel.ERROR);
            return false;
        }
        return true;
    }

    private void processOptions(String oldFilePath, String newFilePath) throws Exception {
        try {
            XWPFDocument document = new XWPFDocument(new FileInputStream(oldFilePath));
            if (setPageNumber) {
                addPageNumbers(document);
            }
            if (setHeader) {
                addHeader(document);
            }
            removeEvaluationText(document);
            // deleting old file
            if (new File(oldFilePath).delete()) {
                Logger.log(getClass().getName(), oldFilePath + " deleted successfully.", LogLevel.DEBUG);
            }
            try (FileOutputStream out = new FileOutputStream(newFilePath)) {
                document.write(out);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    // TODO: setHeaderAllPages does not work
    private void addHeader(XWPFDocument document) {
        XWPFHeaderFooterPolicy headerFooterPolicy = document.createHeaderFooterPolicy();
        XWPFHeader header = headerFooterPolicy
                .createHeader(setHeaderAllPages ? XWPFHeaderFooterPolicy.DEFAULT : XWPFHeaderFooterPolicy.FIRST);

        XWPFParagraph headerParagraph = header.createParagraph();
        XWPFRun run = headerParagraph.createRun();
        String dateText = MainApp.resourceBundle.getString("date_pdf");
        String nameText = MainApp.resourceBundle.getString("name_pdf");
        String uidText = MainApp.resourceBundle.getString("uid_pdf");

        run.setText(dateText);
        for (int i = 0; i < 11; i++) {
            run.addTab();
        }
        run.setText(nameText);
        run.addCarriageReturn();
        for (int i = 0; i < 11; i++) {
            run.addTab();
        }
        run.setText(uidText);
    }

    private void addPageNumbers(XWPFDocument document) {
        for (XWPFFooter footer : document.getFooterList()) {
            footer.clearHeaderFooter();
        }
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

    // TODO: currently it does not work
    private void removeWaterMark(Document doc) {
        if (doc.getWatermark().getType() == WatermarkType.TEXT) {
            doc.getWatermark().remove();
        }
    }

    private void removeEvaluationText(XWPFDocument document) {
        String textToRemove = "Evaluation Only. Created with Aspose.Words. Copyright 2003-2022 Aspose Pty Ltd.";
        XWPFParagraph firstParagraph = document.getParagraphs().get(0);

        if (firstParagraph != null) {
            String paragraphText = firstParagraph.getText();

            if (paragraphText.contains(textToRemove)) {
                document.removeBodyElement(0);
            }
        }
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
}
