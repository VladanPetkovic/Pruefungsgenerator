package com.example.application.backend.app;

import com.example.application.MainApp;
import com.example.application.backend.db.models.Answer;
import com.example.application.backend.db.models.Question;
import com.example.application.backend.db.models.Type;

import java.util.ArrayList;
import java.util.UUID;

public class Export {
    private final LaTeXLogic laTeXLogic = new LaTeXLogic();

    public String createFileName(boolean isPdf) {
        String title = "generated_exam_" + UUID.randomUUID().toString().substring(0, 8);
        String extension = isPdf ? "pdf" : "docx";
        return String.format("%s.%s", title, extension);
    }

    protected String buildHtmlContent(ArrayList<Question> testQuestions, String title, int distancePerQuestion) {
        StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder.append("<html><head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; }")
                .append("h1 { text-align: center; font-size: 20px; }")
                .append(".question { margin: 15px 0; }")
                .append(".answers { margin-left: 20px; margin-bottom: ")
                .append(distancePerQuestion * 20)    // making space between questions
                .append("px; }")
                .append("</style>")
                .append("</head><body>");

        // title
        htmlBuilder.append("<h1>").append(title).append("</h1>");

        // questions
        int questionNumber = 1;
        for (Question question : testQuestions) {
            htmlBuilder.append("<div class='question'>").append(questionNumber).append(". ")
                    .append(extractHtmlContent(question.getQuestion())).append("</div>");

            htmlBuilder.append("<div class='answers'>");
            if (Type.isOpen(question.getType())) {
                htmlBuilder.append("<p>A:</p>");
            } else if (Type.isMultipleChoice(question.getType())) {
                for (Answer answer : question.getAnswers()) {
                    htmlBuilder.append("<p>□ ").append(answer.getAnswer()).append("</p>");
                }
            } else if (Type.isTrueFalse(question.getType())) {
                htmlBuilder.append("<p>□ " + MainApp.resourceBundle.getString("true_pdf") + "</p>")
                        .append("<p>□ " + MainApp.resourceBundle.getString("false_pdf") + "</p>");
            }
            htmlBuilder.append("</div>");

            questionNumber++;
        }

        htmlBuilder.append("</body></html>");
        return laTeXLogic.transformLatexTags(htmlBuilder.toString(), false);
    }

    private String extractHtmlContent(String questionHtml) {
        if (questionHtml.contains("<body")) {
            int bodyStart = questionHtml.indexOf("<body");
            int bodyEnd = questionHtml.indexOf("</body>") + 7;
            return questionHtml.substring(bodyStart, bodyEnd);
        }
        return questionHtml;
    }
}
