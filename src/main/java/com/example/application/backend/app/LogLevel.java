package com.example.application.backend.app;

public enum LogLevel {
    INFO,   // documents special events (entering password more than 3 times)
    DEBUG,  // used during development
    WARN,   // documents issues indicating upcoming errors
    ERROR   // documents errors that are unexpected but not causing applications a
            // FATAL is missing, but currently not needed
}
