package com.example.application.frontend.controller;

import com.example.application.backend.db.services.*;
import com.example.application.frontend.components.NavbarController;
import com.example.application.frontend.components.QuestionFilter_ScreenController;
import com.example.application.frontend.components.TitleBanner_ScreenController;
import com.example.application.frontend.modals.*;
import org.springframework.context.ConfigurableApplicationContext;

public class ControllerFactory {
    private final AnswerService answerService;
    private final CategoryService categoryService;
    private final CourseService courseService;
    private final ImageService imageService;
    private final KeywordService keywordService;
    private final QuestionService questionService;
    private final StudyProgramService studyProgramService;

    public ControllerFactory(ConfigurableApplicationContext springContext) {
        answerService = springContext.getBean(AnswerService.class);
        categoryService = springContext.getBean(CategoryService.class);
        courseService = springContext.getBean(CourseService.class);
        imageService = springContext.getBean(ImageService.class);
        keywordService = springContext.getBean(KeywordService.class);
        questionService = springContext.getBean(QuestionService.class);
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
            case "Home_ScreenController" -> new Home_ScreenController(studyProgramService, courseService);
            case "PdfPreview_ScreenController" -> new PdfPreview_ScreenController();
            case "QuestionCreate_ScreenController" ->
                    new QuestionCreate_ScreenController(keywordService, categoryService, questionService, answerService, imageService);
            case "QuestionEdit_ScreenController" ->
                    new QuestionEdit_ScreenController(keywordService, categoryService, answerService, questionService, imageService);
            case "Settings_ScreenController" ->
                    new Settings_ScreenController(studyProgramService, courseService, questionService);
            /* COMPONENTS */
            case "NavbarController" -> new NavbarController();
            case "QuestionFilter_ScreenController" ->
                    new QuestionFilter_ScreenController(questionService, keywordService, categoryService);
            case "TitleBanner_ScreenController" -> new TitleBanner_ScreenController();
            /* MODALS */
            case "AddCourse_ScreenController" -> new AddCourse_ScreenController(courseService);
            case "AddStudyProgram_ScreenController" -> new AddStudyProgram_ScreenController(studyProgramService);
            case "ConfirmDeletion_ScreenController" ->
                    new ConfirmDeletion_ScreenController(studyProgramService, courseService, questionService);
            case "ImageResizer_ScreenController" -> new ImageResizer_ScreenController();
            case "TargetSelectionController" -> new TargetSelectionController(studyProgramService, courseService);
            default -> throw new IllegalArgumentException("Unknown controller class: " + controllerClass);
        };
    }
}
