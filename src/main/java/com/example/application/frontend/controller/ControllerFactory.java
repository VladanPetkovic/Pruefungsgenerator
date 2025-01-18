package com.example.application.frontend.controller;

import com.example.application.backend.db.services.*;
import com.example.application.frontend.components.*;
import com.example.application.frontend.modals.*;
import org.springframework.context.ConfigurableApplicationContext;

public class ControllerFactory {
    private final AnswerService answerService;
    private final CategoryService categoryService;
    private final CourseService courseService;
    private final ImageService imageService;
    private final KeywordService keywordService;
    private final QuestionService questionService;
    private final SettingService settingService;
    private final StudyProgramService studyProgramService;

    public ControllerFactory(ConfigurableApplicationContext springContext) {
        answerService = springContext.getBean(AnswerService.class);
        categoryService = springContext.getBean(CategoryService.class);
        courseService = springContext.getBean(CourseService.class);
        imageService = springContext.getBean(ImageService.class);
        keywordService = springContext.getBean(KeywordService.class);
        questionService = springContext.getBean(QuestionService.class);
        settingService = springContext.getBean(SettingService.class);
        studyProgramService = springContext.getBean(StudyProgramService.class);
    }

    /*
     * Factory-Method Pattern
     */
    public Object create(Class<?> controllerClass) {
        return switch (controllerClass.getSimpleName()) {
            /* MAIN SCENES */
            case "CreateAutomatic_ScreenController" ->
                    new CreateAutomatic_ScreenController(questionService, categoryService);
            case "CreateManual_ScreenController" -> new CreateManual_ScreenController();
            case "Home_ScreenController" ->
                    new Home_ScreenController(studyProgramService, courseService, settingService);
            case "PdfPreview_ScreenController" -> new PdfPreview_ScreenController();
            case "QuestionCreate_ScreenController" ->
                    new QuestionCreate_ScreenController(keywordService, categoryService, questionService, answerService, imageService);
            case "QuestionEdit_ScreenController" ->
                    new QuestionEdit_ScreenController(keywordService, categoryService, answerService, questionService, imageService);
            case "Settings_ScreenController" ->
                    new Settings_ScreenController(studyProgramService, courseService, questionService, categoryService, answerService, keywordService);
            /* COMPONENTS */
            case "CreateTestOptions_ScreenController" -> new CreateTestOptions_ScreenController(categoryService);
            case "EditorScreenController" -> new EditorScreenController();
            case "EditorSmallScreenController" -> new EditorSmallScreenController();
            case "NavbarController" -> new NavbarController();
            case "QuestionFilter_ScreenController" ->
                    new QuestionFilter_ScreenController(questionService, keywordService, categoryService);
            case "TitleBanner_ScreenController" -> new TitleBanner_ScreenController();
            /* MODALS */
            case "AddCategory_ScreenController" -> new AddCategory_ScreenController(categoryService);
            case "AddCourse_ScreenController" -> new AddCourse_ScreenController(courseService);
            case "AddKeyword_ScreenController" -> new AddKeyword_ScreenController(keywordService);
            case "AddStudyProgram_ScreenController" -> new AddStudyProgram_ScreenController(studyProgramService);
            case "ConfirmDeletion_ScreenController" ->
                    new ConfirmDeletion_ScreenController(studyProgramService, courseService, questionService);
            case "ImageResizer_ScreenController" -> new ImageResizer_ScreenController();
            case "ImportErrorScreenController" -> new ImportErrorScreenController();
            case "Latex_ScreenController" -> new Latex_ScreenController();
            default -> throw new IllegalArgumentException("Unknown controller class: " + controllerClass);
        };
    }
}
