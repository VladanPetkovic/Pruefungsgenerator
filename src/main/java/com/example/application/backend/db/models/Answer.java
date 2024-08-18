package com.example.application.backend.db.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

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
    @JoinColumn(name = "fk_question_id", nullable = false, updatable = false)
    private Question question = new Question();

    public Answer(String answer) {
        setAnswer(answer);
    }

    public Answer(Answer other) {
        setId(other.getId());
        setAnswer(other.getAnswer());
    }
}
