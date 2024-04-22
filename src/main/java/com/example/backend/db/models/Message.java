package com.example.backend.db.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Message {
    SUCCESS_MESSAGE_OPERATION("Operation completed successfully.", false),
    SUCCESS_MESSAGE_DATA_SAVED("Data saved successfully.", false),

    ERROR_MESSAGE_ERROR_OCCURRED("An error occurred.", true),
    ERROR_MESSAGE_LOAD_DATA("Failed to load data.", true),
    ERROR_MESSAGE_DATA_CONTAINS_SPACES("String contains only spaces.", true),

    // error messages for the answer DAO:
    CREATE_ANSWER_ERROR_MESSAGE("Failed to insert answer into the database.", true),
    UPDATE_ANSWER_ERROR_MESSAGE("Failed to update answer in the database.", true),
    DELETE_ANSWER_ERROR_MESSAGE("Failed to delete answer from the database.", true),

    // category:
    CREATE_CATEGORY_ERROR_MESSAGE("Failed to insert category into the database.", true),
    CREATE_CATEGORY_SUCCESS_MESSAGE("Category created successfully.", false),
    UPDATE_CATEGORY_ERROR_MESSAGE("Failed to update category in the database.", true),
    UPDATE_CATEGORY_SUCCESS_MESSAGE("Category updated successfully.", false),
    DELETE_CATEGORY_ERROR_MESSAGE("Failed to delete category from the database.", true),
    DELETE_CATEGORY_SUCCESS_MESSAGE("Category deleted successfully.", false),
    CATEGORY_INVALID_CHARACTERS_ERROR_MESSAGE("Category contains invalid characters!", true),

    // error messages for the course DAO:
    CREATE_COURSE_ERROR_MESSAGE("Failed to insert course into the database.", true),
    CREATE_COURSE_SUCCESS_MESSAGE("Course created successfully.", false),
    UPDATE_COURSE_ERROR_MESSAGE("Failed to update course in the database.", true),
    UPDATE_COURSE_SUCCESS_MESSAGE("Course updated successfully.", false),
    DELETE_COURSE_ERROR_MESSAGE("Failed to delete course from the database.", true),
    DELETE_COURSE_SUCCESS_MESSAGE("Course deleted successfully.", false),

    // test:
    NO_QUESTIONS_PROVIDED_ERROR_MESSAGE("No questions provided.", true),
    NO_QUESTIONS_FOUND("No questions found.", true);


    private String message;
    private boolean isError;

    Message(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }
}

