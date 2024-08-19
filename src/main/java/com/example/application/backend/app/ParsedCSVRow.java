package com.example.application.backend.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParsedCSVRow {
    private Long questionId;
    private String questionText;
    private String categoryName;
    private Integer difficulty;
    private Float points;
    private String type;
    private String remark;
    private String answersText;
    private String keywordsText;
    private String courseName;
    private Integer courseNumber;
    private String studyProgramName;
}