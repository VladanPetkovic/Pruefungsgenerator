package com.example.application.backend.db.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer difficulty;

    @Column(nullable = false)
    private Float points;

    @Column(nullable = false)
    private String question;

    @ManyToOne
    @JoinColumn(name = "fk_question_type_id", nullable = false)
    private QuestionType type;

    private String remark;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<Answer> answers = new HashSet<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<>();

    @ManyToMany(mappedBy = "questions")
    private Set<Keyword> keywords = new HashSet<>();

    public Question(Category category, int difficulty, float points, String question, QuestionType type, String remark, LocalDateTime created_at, LocalDateTime updated_at, Set<Answer> answers, Set<Keyword> keywords, Set<Image> images) {
        setCategory(category);
        setDifficulty(difficulty);
        setPoints(points);
        setQuestion(question);
        setType(type);
        setRemark(remark);
        setCreatedAt(created_at);
        setUpdatedAt(updated_at);
        setAnswers(answers);
        setKeywords(keywords);
        setImages(images);
    }

    public String getAnswersAsString() {
        StringBuilder answersCombined = new StringBuilder();

        // return the first answer of answers, if only one is available
        if (answers.size() == 1) {
            return answers.iterator().next().getAnswer();
        }

        for (Answer answer : answers) {
            if (!Objects.equals(answer.getAnswer(), "")) {
                answersCombined.append(answer.getAnswer());
                answersCombined.append("\n");
            }
        }

        return String.valueOf(answersCombined);
    }

    /**
     * This function returns the formatted string of a timestamp (created_at, updated_at)
     *
     * @param time the time to format
     * @return A String, which we can use in the frontend
     */
    public String getTimeStampFormatted(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return time.format(formatter);
    }
}