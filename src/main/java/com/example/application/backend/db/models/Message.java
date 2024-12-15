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
    ERROR_COURSE_AND_SP_NOT_SELECTED(MainApp.resourceBundle.getString("error_message_course_and_sp_not_selected"), true),

    CATEGORY_INVALID_CHARACTERS_ERROR_MESSAGE(MainApp.resourceBundle.getString("category_invalid_characters_error_message"), true),

    // error messages for the images:
    MAX_PICTURES_UPLOADED(MainApp.resourceBundle.getString("max_pictures_uploaded"), true),

    // error messages for the keywords:
    KEYWORD_INVALID_CHARACTERS_ERROR_MESSAGE(MainApp.resourceBundle.getString("keyword_invalid_characters_error_message"), true),

    // error messages for the questions:
    CREATE_QUESTION_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_question_error_message"), true),
    CREATE_QUESTION_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_question_success_message"), false),

    // error messages for the study program:
    CREATE_STUDYPROGRAM_ERROR_MESSAGE(MainApp.resourceBundle.getString("create_studyprogram_error_message"), true),
    CREATE_STUDYPROGRAM_SUCCESS_MESSAGE(MainApp.resourceBundle.getString("create_studyprogram_success_message"), false),

    // test:
    NO_QUESTIONS_PROVIDED_ERROR_MESSAGE(MainApp.resourceBundle.getString("no_questions_provided_error_message"), true),
    NO_QUESTIONS_FOUND(MainApp.resourceBundle.getString("no_questions_found"), true),

    // import functionality:
    SUCCESS_MESSAGE_DATA_IMPORTED(MainApp.resourceBundle.getString("success_message_data_imported"), false);


    private String message;
    private boolean isError;

    Message(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }
}

