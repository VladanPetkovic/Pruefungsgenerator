package com.example.application.backend.app;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    public static void log(String className, String message, LogLevel level) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());

        String logLevelString;
        String color;

        switch (level) {
            case INFO:
                logLevelString = "INFO";
                color = ANSI_GREEN;
                break;
            case DEBUG:
                logLevelString = "DEBUG";
                color = ANSI_YELLOW;
                break;
            case ERROR:
                logLevelString = "ERROR";
                color = ANSI_RED;
                break;
            default:
                logLevelString = "UNKNOWN";
                color = ANSI_RESET;
        }

        String formattedMessage = String.format("[%s]: (%s%s%s) \"%s\": %s",
                timestamp, color, logLevelString, ANSI_RESET, className, message);

        System.out.println(formattedMessage);
    }
}