package com.example.backend.db.models;

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

//    /**
//     * This method creates answers and their connection for a new question.
//     * If same answers exists, only the connection is made.
//     *
//     * @param newQuestion A new question with possible answers (possible Multiple-choice)
//     */
//    public static void createAnswers(Question newQuestion, int newQuestionId) {
//        if (newQuestion == null || newQuestion.getAnswers() == null) {
//            return;
//        }
//
//        // add one or multiple answers and the connection in the join table (has_aq)
//        SQLiteDatabaseConnection.ANSWER_REPOSITORY.add(newQuestion.getAnswers(), newQuestionId);
//    }
}
