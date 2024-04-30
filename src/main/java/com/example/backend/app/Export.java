package com.example.backend.app;

import com.example.backend.db.models.Question;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
public abstract class Export<T> {
    protected int questionsPerSite = 5;
    protected int numberOfPages;
    protected String title = "";
    protected String destinationFolder = "";
    protected boolean setHeader = true;
    protected boolean setPageNumber = true;

    /**
     * This functions exports a pdf/docx with given testQuestions.
     * @param testQuestions ArrayList of testQuestions for making the pdf-File.
     * @return True, if export was successful and false, if something failed.
     */
    public abstract boolean exportDocument(ArrayList<Question> testQuestions);

    /**
     * This functions builds the pdf/docx-document. It is used for exporting and preview of the pdf/docx-file.
     * @param testQuestions ArrayList of Questions needed for creating the pdf-document.
     * @return A document will be returned. Returns null, when something fails.
     */
    protected abstract T buildDocument(ArrayList<Question> testQuestions);

    /**
     * This functions makes an ArrayList of images (page of the document) and returns it for the frontend-controller.
     * @param testQuestions ArrayList of questions needed for building the pdf-document.
     * @return ArrayList of images - these images are going to be used in the GUI.
     */
    public abstract ArrayList<Image> getPreviewImages(ArrayList<Question> testQuestions);

    /**
     * Creates the fileName with the current DateTime.
     * @return String, for example: "test_2024-02-11_18-38-27.pdf" or "test_2024-02-11_18-38-27.docx"
     */
    public String createFileName(boolean isPdf) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        String extension = isPdf ? "pdf" : "docx";
        return String.format("test_%04d-%02d-%02d_%02d-%02d-%02d.%s",
                currentDateTime.getYear(),
                currentDateTime.getMonthValue(),
                currentDateTime.getDayOfMonth(),
                currentDateTime.getHour(),
                currentDateTime.getMinute(),
                currentDateTime.getSecond(),
                extension);
    }

    /**
     * !!!Use this function before exportDocument()!!!
     * Sets important parameters for the export-function.
     * @param testHeader The Title of the test
     * @param questionsPerSite The amount of questions on one page
     * @param destFolder The destination folder, where the user saves the test
     * @param setHeader Flag to set the header or not
     * @param setPageNumber Flag to set the pageNumber or not
     */
    public void setOptions(String testHeader, int questionsPerSite, String destFolder, boolean setHeader, boolean setPageNumber) {
        this.title = testHeader;
        this.questionsPerSite = questionsPerSite;
        this.destinationFolder = destFolder;
        this.setHeader = setHeader;
        this.setPageNumber = setPageNumber;
    }

    protected void setNumberOfPages(int numberOfQuestions) {
        float numberOfPages = (float) numberOfQuestions /this.questionsPerSite;
        this.numberOfPages = (int) Math.ceil(numberOfPages);
    }
}
