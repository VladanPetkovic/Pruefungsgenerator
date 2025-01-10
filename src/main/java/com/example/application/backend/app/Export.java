package com.example.application.backend.app;

import java.util.UUID;

public class Export {
    public String createFileName(boolean isPdf) {
        String title = "generated_exam_" + UUID.randomUUID().toString().substring(0, 8);
        String extension = isPdf ? "pdf" : "docx";
        return String.format("%s.%s", title, extension);
    }

    protected int getNumberOfPages(int numberOfQuestions, int questionsPerSite) {
        float numberOfPages = (float) numberOfQuestions / questionsPerSite;
        return (int) Math.ceil(numberOfPages);
    }
}
