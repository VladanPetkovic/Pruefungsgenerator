package com.example.application.backend.db.models;

import com.example.application.MainApp;
import lombok.Getter;

import java.io.Serializable;

@Getter
public enum Message implements Serializable {
    SUCCESS_MESSAGE_OPERATION(MainApp.resourceBundle.getString("success_message_operation"), false),
    SUCCESS_MESSAGE_DATA_SAVED(MainApp.resourceBundle.getString("success_message_data_saved"), false),
    SUCCESS_MESSAGE_QUESTIONS_EXPORTED(MainApp.resourceBundle.getString("success_message_questions_exported"), false),
    SUCCESS_MESSAGE_FILE_SAVED(MainApp.resourceBundle.getString("success_message_file_saved"), false),

    ERROR_MESSAGE_ERROR_OCCURRED(MainApp.resourceBundle.getString("error_message_error_occurred"), true),
    ERROR_MESSAGE_LOAD_DATA(MainApp.resourceBundle.getString("error_message_load_data"), true),
    ERROR_MESSAGE_DATA_CONTAINS_SPACES(MainApp.resourceBundle.getString("error_message_data_contains_spaces"), true),
    ERROR_MESSAGE_INPUT_ALL_FIELDS(MainApp.resourceBundle.getString("error_message_input_all_fields"), true),
    ERROR_MESSAGE_SELECT_A_FOLDER_SAVE_FILE(MainApp.resourceBundle.getString("error_message_select_a_folder_save_file"), true),
    ERROR_MESSAGE_FILE_NOT_SELECTED(MainApp.resourceBundle.getString("error_message_file_not_selected"), true),
    ERROR_COURSE_AND_SP_NOT_SELECTED(MainApp.resourceBundle.getString("error_course_and_sp_not_selected"), true),

    // error messages for the answer DAO:
    CREATE_ANSWER_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_answer_error_message"), true),
    CREATE_ANSWER_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_answer_success_message"), false),
    CREATE_ANSWERS_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_answers_error_message"), true),
    CREATE_ANSWERS_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_answers_success_message"), false),
    UPDATE_ANSWER_ERROR_MESSAGE(MainApp.resourceBundle.getString("update_answer_error_message"), true),
    UPDATE_ANSWER_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("update_answer_success_message"), false),
    DELETE_ANSWER_ERROR_MESSAGE(MainApp.resourceBundle.getString("delete_answer_error_message"), true),
    DELETE_ANSWER_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("delete_answer_success_message"), false),

    // error messages for the category DAO:
    CREATE_CATEGORY_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_category_error_message"), true),
    CREATE_CATEGORY_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_category_success_message"), false),
    UPDATE_CATEGORY_ERROR_MESSAGE(MainApp.resourceBundle.getString("update_category_error_message"), true),
    UPDATE_CATEGORY_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("update_category_success_message"), false),
    DELETE_CATEGORY_ERROR_MESSAGE(MainApp.resourceBundle.getString("delete_category_error_message"), true),
    DELETE_CATEGORY_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("delete_category_success_message"), false),
    CATEGORY_INVALID_CHARACTERS_ERROR_MESSAGE(MainApp.resourceBundle.getString("category_invalid_characters_error_message"), true),
    NO_CATEGORIES_FOR_SELECTED_COURSE(MainApp.resourceBundle.getString("no_categories_for_selected_course"), true),


    // error messages for the course DAO:
    CREATE_COURSE_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_course_error_message"), true),
    CREATE_COURSE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_course_success_message"), false),
    UPDATE_COURSE_ERROR_MESSAGE(MainApp.resourceBundle.getString("update_course_error_message"), true),
    UPDATE_COURSE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("update_course_success_message"), false),
    DELETE_COURSE_ERROR_MESSAGE(MainApp.resourceBundle.getString("delete_course_error_message"), true),
    DELETE_COURSE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("delete_course_success_message"), false),

    // error messages for the image DAO:
    CREATE_IMAGE_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_image_error_message"), true),
    CREATE_IMAGES_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_images_error_message"), true),
    CREATE_IMAGE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_image_success_message"), false),
    UPDATE_IMAGE_ERROR_MESSAGE(MainApp.resourceBundle.getString("update_image_error_message"), true),
    UPDATE_IMAGE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("update_image_success_message"), false),
    DELETE_IMAGE_ERROR_MESSAGE(MainApp.resourceBundle.getString("delete_image_error_message"), true),
    DELETE_IMAGE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("delete_image_success_message"), false),

    // error messages for the keyword DAO:
    CREATE_KEYWORD_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_keyword_error_message"), true),
    CREATE_KEYWORD_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_keyword_success_message"), false),
    UPDATE_KEYWORD_ERROR_MESSAGE(MainApp.resourceBundle.getString("update_keyword_error_message"), true),
    UPDATE_KEYWORD_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("update_keyword_success_message"), false),
    DELETE_KEYWORD_ERROR_MESSAGE(MainApp.resourceBundle.getString("delete_keyword_error_message"), true),
    DELETE_KEYWORD_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("delete_keyword_success_message"), false),
    KEYWORD_INVALID_CHARACTERS_ERROR_MESSAGE(MainApp.resourceBundle.getString("keyword_invalid_characters_error_message"), true),


    // error messages for the question DAO:
    CREATE_QUESTION_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_question_error_message"), true),
    CREATE_QUESTION_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_question_success_message"), false),
    UPDATE_QUESTION_ERROR_MESSAGE(MainApp.resourceBundle.getString("update_question_error_message"), true),
    UPDATE_QUESTION_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("update_question_success_message"), false),
    DELETE_QUESTION_ERROR_MESSAGE(MainApp.resourceBundle.getString("delete_question_error_message"), true),
    DELETE_QUESTION_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("delete_question_success_message"), false),

    // error messages for the question type DAO:
    CREATE_QUESTION_TYPE_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_question_type_error_message"), true),
    CREATE_QUESTION_TYPE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_question_type_success_message"), false),
    UPDATE_QUESTION_TYPE_ERROR_MESSAGE(MainApp.resourceBundle.getString("update_question_type_error_message"), true),
    UPDATE_QUESTION_TYPE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("update_question_type_success_message"), false),
    DELETE_QUESTION_TYPE_ERROR_MESSAGE(MainApp.resourceBundle.getString("delete_question_type_error_message"), true),
    DELETE_QUESTION_TYPE_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("delete_question_type_success_message"), false),

    // error messages for the study program DAO:
    CREATE_STUDYPROGRAM_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_studyprogram_error_message"), true),
    CREATE_STUDYPROGRAM_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_studyprogram_success_message"), false),
    UPDATE_STUDYPROGRAM_ERROR_MESSAGE(MainApp.resourceBundle.getString("update_studyprogram_error_message"), true),
    UPDATE_STUDYPROGRAM_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("update_studyprogram_success_message"), false),
    DELETE_STUDYPROGRAM_ERROR_MESSAGE(MainApp.resourceBundle.getString("delete_studyprogram_error_message"), true),
    DELETE_STUDYPROGRAM_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("delete_studyprogram_success_message"), false),


    // test:
    NO_QUESTIONS_PROVIDED_ERROR_MESSAGE(MainApp.resourceBundle.getString("no_questions_provided_error_message"), true),
    NO_QUESTIONS_FOUND(MainApp.resourceBundle.getString("no_questions_found"), true),

    // import functionality:
    SUCCESS_MESSAGE_DATA_IMPORTED(MainApp.resourceBundle.getString("success_message_data_imported"), false),
    ERROR_MESSAGE_IMPORT_FAILED(MainApp.resourceBundle.getString("error_message_import_failed"), true);


    private String message;
    private boolean isError;

    Message(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }
}

