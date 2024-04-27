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
    ERROR_MESSAGE_NOT_ALL_FIELDS_FILLED("Some required fields haven't been filled.", true),

    // error messages for the answer DAO:
    CREATE_ANSWER_ERROR_MESSAGE("Failed to insert answer into the database.", true),
    CREATE_ANSWER_SUCCESS_MESSAGE("Answer created successfully.", false),
    CREATE_ANSWERS_ERROR_MESSAGE("Failed to insert answers into the database.", true),
    CREATE_ANSWERS_SUCCESS_MESSAGE("Answers created successfully.", false),
    UPDATE_ANSWER_ERROR_MESSAGE("Failed to update answer in the database.", true),
    UPDATE_ANSWER_SUCCESS_MESSAGE("Answer updated successfully.", false),
    DELETE_ANSWER_ERROR_MESSAGE("Failed to delete answer from the database.", true),
    DELETE_ANSWER_SUCCESS_MESSAGE("Answer deleted successfully.", false),

    // error messages for the category DAO:
    CREATE_CATEGORY_ERROR_MESSAGE("Failed to insert category into the database.", true),
    CREATE_CATEGORY_SUCCESS_MESSAGE("Category created successfully.", false),
    UPDATE_CATEGORY_ERROR_MESSAGE("Failed to update category in the database.", true),
    UPDATE_CATEGORY_SUCCESS_MESSAGE("Category updated successfully.", false),
    DELETE_CATEGORY_ERROR_MESSAGE("Failed to delete category from the database.", true),
    DELETE_CATEGORY_SUCCESS_MESSAGE("Category deleted successfully.", false),
    CATEGORY_INVALID_CHARACTERS_ERROR_MESSAGE("Category contains invalid characters!", true),
    NO_CATEGORIES_FOR_SELECTED_COURSE("No categories found for the selected course - create a new category.", true),

    // error messages for the course DAO:
    CREATE_COURSE_ERROR_MESSAGE("Failed to insert course into the database.", true),
    CREATE_COURSE_SUCCESS_MESSAGE("Course created successfully.", false),
    UPDATE_COURSE_ERROR_MESSAGE("Failed to update course in the database.", true),
    UPDATE_COURSE_SUCCESS_MESSAGE("Course updated successfully.", false),
    DELETE_COURSE_ERROR_MESSAGE("Failed to delete course from the database.", true),
    DELETE_COURSE_SUCCESS_MESSAGE("Course deleted successfully.", false),

    // error messages for the image DAO:
    CREATE_IMAGE_ERROR_MESSAGE("Failed to insert image into the database.", true),
    CREATE_IMAGE_SUCCESS_MESSAGE("Image created successfully.", false),
    UPDATE_IMAGE_ERROR_MESSAGE("Failed to update image in the database.", true),
    UPDATE_IMAGE_SUCCESS_MESSAGE("Image updated successfully.", false),
    DELETE_IMAGE_ERROR_MESSAGE("Failed to delete image from the database.", true),
    DELETE_IMAGE_SUCCESS_MESSAGE("Image deleted successfully.", false),

    // error messages for the keyword DAO:
    CREATE_KEYWORD_ERROR_MESSAGE("Failed to insert keyword into the database.", true),
    CREATE_KEYWORD_SUCCESS_MESSAGE("Keyword created successfully.", false),
    UPDATE_KEYWORD_ERROR_MESSAGE("Failed to update keyword in the database.", true),
    UPDATE_KEYWORD_SUCCESS_MESSAGE("Keyword updated successfully.", false),
    DELETE_KEYWORD_ERROR_MESSAGE("Failed to delete keyword from the database.", true),
    DELETE_KEYWORD_SUCCESS_MESSAGE("Keyword deleted successfully.", false),
    KEYWORD_INVALID_CHARACTERS_ERROR_MESSAGE("Keyword contains invalid characters!", true),

    // error messages for the question DAO:
    CREATE_QUESTION_ERROR_MESSAGE("Failed to insert question into the database.", true),
    CREATE_QUESTION_SUCCESS_MESSAGE("Question created successfully.", false),
    UPDATE_QUESTION_ERROR_MESSAGE("Failed to update question in the database.", true),
    UPDATE_QUESTION_SUCCESS_MESSAGE("Question updated successfully.", false),
    DELETE_QUESTION_ERROR_MESSAGE("Failed to delete question from the database.", true),
    DELETE_QUESTION_SUCCESS_MESSAGE("Question deleted successfully.", false),

    // error messages for the question type DAO:
    CREATE_QUESTION_TYPE_ERROR_MESSAGE("Failed to insert question type into the database.", true),
    CREATE_QUESTION_TYPE_SUCCESS_MESSAGE("Question type created successfully.", false),
    UPDATE_QUESTION_TYPE_ERROR_MESSAGE("Failed to update question type in the database.", true),
    UPDATE_QUESTION_TYPE_SUCCESS_MESSAGE("Question type updated successfully.", false),
    DELETE_QUESTION_TYPE_ERROR_MESSAGE("Failed to delete question from the database.", true),
    DELETE_QUESTION_TYPE_SUCCESS_MESSAGE("Question deleted successfully.", false),

    // error messages for the study program DAO:
    CREATE_STUDYPROGRAM_ERROR_MESSAGE("Failed to insert study program into the database.", true),
    CREATE_STUDYPROGRAM_SUCCESS_MESSAGE("Study program created successfully.", false),
    UPDATE_STUDYPROGRAM_ERROR_MESSAGE("Failed to update study program in the database.", true),
    UPDATE_STUDYPROGRAM_SUCCESS_MESSAGE("Study program updated successfully.", false),
    DELETE_STUDYPROGRAM_ERROR_MESSAGE("Failed to delete study program from the database.", true),
    DELETE_STUDYPROGRAM_SUCCESS_MESSAGE("Study program deleted successfully.", false),

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

