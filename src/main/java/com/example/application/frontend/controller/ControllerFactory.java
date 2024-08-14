package com.example.application.frontend.controller;

import com.example.application.MainApp;
import com.example.application.backend.db.services.*;
import com.example.application.frontend.components.NavbarController;
import com.example.application.frontend.components.QuestionFilter_ScreenController;
import com.example.application.frontend.components.TitleBanner_ScreenController;
import org.springframework.context.ConfigurableApplicationContext;

public class ControllerFactory {
    private AnswerService answerService;
    private CategoryService categoryService;
    private final CourseService courseService;
    private ImageService imageService;
    private KeywordService keywordService;
    private QuestionService questionService;
    private QuestionTypeService questionTypeService;
    private final StudyProgramService studyProgramService;
    public ControllerFactory(ConfigurableApplicationContext springContext) {
        answerService = springContext.getBean(AnswerService.class);
        categoryService = springContext.getBean(CategoryService.class);
        courseService = springContext.getBean(CourseService.class);
        imageService = springContext.getBean(ImageService.class);
        keywordService = springContext.getBean(KeywordService.class);
        questionService = springContext.getBean(QuestionService.class);
        questionTypeService = springContext.getBean(QuestionTypeService.class);
        studyProgramService = springContext.getBean(StudyProgramService.class);
    }

    /*
     * Factory-Method Pattern
     */
    public Object create(Class<?> controllerClass) {
        return switch (controllerClass.getSimpleName()) {
            /* MAIN SCENES */
            case "CreateAutomatic_ScreenController" -> new CreateAutomatic_ScreenController();
            case "CreateManual_ScreenController" -> new CreateManual_ScreenController();
            case "Home_ScreenController" -> new Home_ScreenController(studyProgramService, courseService);
            case "PdfPreview_ScreenController" -> new PdfPreview_ScreenController();
            case "QuestionCreate_ScreenController" -> new QuestionCreate_ScreenController();
            case "QuestionEdit_ScreenController" -> new QuestionEdit_ScreenController();
            case "Settings_ScreenController" -> new Settings_ScreenController();
            /* COMPONENTS */
            case "NavbarController" -> new NavbarController();
            case "QuestionFilter_ScreenController" -> new QuestionFilter_ScreenController();
            case "TitleBanner_ScreenController" -> new TitleBanner_ScreenController();
            default -> throw new IllegalArgumentException("Unknown controller class: " + controllerClass);
        };
    }
}
