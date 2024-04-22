package com.example.backend.db.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Message {
    SUCCESS_MESSAGE_1("Operation completed successfully."),
    SUCCESS_MESSAGE_2("Data saved successfully."),

    ERROR_MESSAGE_1("An error occurred."),
    ERROR_MESSAGE_2("Failed to load data."),
    ERROR_MESSAGE_4("Failed to retrieve a course by its name from the database."),
    ERROR_MESSAGE_5("Failed to retrieve questions based on search options and a course from the database."),

    // error messages for the answer DAO:
    CREATE_ANSWER_ERROR_MESSAGE("Failed to insert answer into the database."),
    CREATE_ANSWERS_ERROR_MESSAGE("Failed to insert answer(s) into the database."),
    CREATE_HASAQ_CONNECTION_ERROR_MESSAGE("Failed to insert hasAQ connection(s) into the database."),
    READALL_ANSWERS_ERROR_MESSAGE("Failed to retrieve all answers from the database."),
    READ_ANSWER_BY_ID_ERROR_MESSAGE("Failed to retrieve answer by ID from the database."),
    READ_ANSWER_BY_NAME_ERROR_MESSAGE("Failed to retrieve answer by name from the database."),
    UPDATE_ANSWER_ERROR_MESSAGE("Failed to update answer in the database."),
    DELETE_ANSWER_ERROR_MESSAGE("Failed to delete answer from the database."),

    // error messages for the category DAO:
    CREATE_CATEGORY_ERROR_MESSAGE("Failed to insert category into the database."),
    READALL_CATEGORIES_ERROR_MESSAGE("Failed to retrieve all categories from the database."),
    READALL_CATEGORIES_FOR_ONE_COURSE_ERROR_MESSAGE("Failed to retrieve all categories for one course from the database."),
    READ_CATEGORY_BY_ID_ERROR_MESSAGE("Failed to retrieve category by ID from the database."),
    READ_CATEGORY_FOR_ONE_QUESTION_ERROR_MESSAGE("Failed to retrieve category for one question from the database."),
    READ_CATEGORY_BY_NAME_ERROR_MESSAGE("Failed to retrieve category by its name from the database."),
    UPDATE_CATEGORY_ERROR_MESSAGE("Failed to update category in the database."),
    DELETE_CATEGORY_ERROR_MESSAGE("Failed to delete category from the database."),
    CREATE_HASCC_CONNECTION_ERROR_MESSAGE("Failed to insert hasCC connection into the database."),
    DELETE_HASCC_CONNECTION_ERROR_MESSAGE("Failed to delete hasCC connection from the database."),

    // error messages for the course DAO:
    CREATE_COURSE_ERROR_MESSAGE("Failed to insert course into the database.");



    @Setter
    private final String message;

    Message(String message) {
        this.message = message;
    }

}

