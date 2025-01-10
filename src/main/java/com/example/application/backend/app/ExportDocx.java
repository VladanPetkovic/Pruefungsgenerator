package com.example.application.backend.app;

import com.example.application.backend.db.models.Answer;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.models.Type;
import javafx.scene.image.Image;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ExportDocx extends Export {
    private final LaTeXLogic laTeXLogic = new LaTeXLogic();
    protected int questionsPerSite;
    protected int numberOfPages;
    protected String title;
    protected String destinationFolder;
    protected boolean setHeader;
    protected boolean setPageNumber;

    public ExportDocx(String testHeader,
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

    /** Using Aspose */
    public boolean exportDocument(ArrayList<Question> testQuestions) {
        String htmlContent = buildHtmlContent(testQuestions);
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
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /** Using Docx4J */
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



    protected XWPFDocument buildDocument(ArrayList<Question> testQuestions) {
        numberOfPages = getNumberOfPages(testQuestions.size(), questionsPerSite);

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

            // CONTENT with questions
            setContent(document, testQuestions);

            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Image> getPreviewImages(ArrayList<Question> testQuestions) {
        return null;
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
        int paragraphsOnOnePage = 25 - this.questionsPerSite;

        // first page
        if (questionNumber < this.questionsPerSite) {
            // mind the title
            return (int) (paragraphsOnOnePage - 2) / this.questionsPerSite;
        }

        // following pages
        return (int) paragraphsOnOnePage / this.questionsPerSite;
    }
}
