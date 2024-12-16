package com.example.application.backend.app;

public class Export {
    public String createFileName(boolean isPdf) {
        String extension = isPdf ? "pdf" : "docx";
        return String.format("generated_exam.%s", extension);
    }

    protected int getNumberOfPages(int numberOfQuestions, int questionsPerSite) {
        float numberOfPages = (float) numberOfQuestions / questionsPerSite;
        return (int) Math.ceil(numberOfPages);
    }
}
