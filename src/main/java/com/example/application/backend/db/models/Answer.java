package com.example.application.backend.db.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "answers")
public class Answer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String answer;

    @ManyToOne
    @JoinColumn(name = "fk_question_id", nullable = false)
    private Question question = new Question();

    public Answer(String answer) {
        setAnswer(answer);
    }

    public Answer(Answer other) {
        setId(other.getId());
        setAnswer(other.getAnswer());
    }

    public static Set<Answer> createAnswers(String[] answers) {
        Set<Answer> newAnswers = new HashSet<>();

        for (String answerText : answers) {
            String trimmedAnswer = answerText.trim();
            if (!trimmedAnswer.isEmpty()) {
                newAnswers.add(new Answer(trimmedAnswer));
            }
        }

        return newAnswers;
    }
}
