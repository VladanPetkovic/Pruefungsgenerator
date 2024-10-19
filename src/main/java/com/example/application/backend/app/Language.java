package com.example.application.backend.app;

public enum Language {
    ENGLISH,
    GERMAN;

    public static String getAbbreviation(int language) {
        if (language == 1) {
            return "de";
        }
        return "en";
    }
}
