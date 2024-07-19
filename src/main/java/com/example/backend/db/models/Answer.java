package com.example.backend.db.models;

import com.example.backend.db.SQLiteDatabaseConnection;
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
@Table(name = "logs")
public class Answer implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "answer", nullable = false)
    private String answer;

    public Answer(String answer) {
        setAnswer(answer);
    }

    public Answer(Answer other) {
        setId(other.getId());
        setAnswer(other.getAnswer());
    }

    /**
     * This method creates answers and their connection for a new question.
     * If same answers exists, only the connection is made.
     *
     * @param newQuestion A new question with possible answers (possible Multiple-choice)
     */
    public static void createAnswers(Question newQuestion, int newQuestionId) {
        if (newQuestion == null || newQuestion.getAnswers() == null) {
            return;
        }

        // add one or multiple answers and the connection in the join table (has_aq)
        SQLiteDatabaseConnection.ANSWER_REPOSITORY.add(newQuestion.getAnswers(), newQuestionId);
    }
}
