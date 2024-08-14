package com.example.application.backend.db.models;

import com.example.application.backend.app.SharedData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "keywords")
public class Keyword implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @ManyToMany
    private Set<Question> questions = new HashSet<>();

    public Keyword(String keyword) {
        setKeyword(keyword);
    }

    /**
     * This function checks the provided keyword, no special chars permitted and no leading and ending space.
     *
     * @param newKeyword The provided keyword
     * @return String - the error-message, returns null if everything is fine.
     */
    public static String checkNewKeyword(String newKeyword) throws IOException {
        if (newKeyword == null) {
            return "No keyword provided!";
        }

        // Check for no leading and ending space
        if (newKeyword.trim().isEmpty()) {
            SharedData.setOperation(Message.ERROR_MESSAGE_DATA_CONTAINS_SPACES);
            return "Keyword cannot be empty!";
        }

        // Check for no special characters except letters and digits
        if (!newKeyword.matches("[a-zA-Z0-9öäüÖÄÜ\\s]+")) {
            SharedData.setOperation(Message.KEYWORD_INVALID_CHARACTERS_ERROR_MESSAGE);
            return "Keyword contains invalid characters!";
        }

        if (newKeyword.length() > 30) {
            SharedData.setOperation("Keyword too long - max 30 chars! You entered: " + newKeyword.length(), true);
            return "Keyword too long - max 30 chars! You entered: " + newKeyword.length();
        }

        return null;
    }

//    public static Keyword createNewKeywordInDatabase(String keyword) {
//        Keyword newKeyword = SQLiteDatabaseConnection.KEYWORD_REPOSITORY.get(keyword);
//
//        // check for existing keywords
//        if (newKeyword == null) {
//            Keyword addToDatabase = new Keyword(keyword);
//            SQLiteDatabaseConnection.KEYWORD_REPOSITORY.add(addToDatabase);
//            newKeyword = SQLiteDatabaseConnection.KEYWORD_REPOSITORY.get(keyword);
//        }
//        return newKeyword;
//    }

//    /**
//     * This method creates keywords and their connection for a new question.
//     * If same keywords exists, only the connection is made.
//     *
//     * @param newQuestion A new question with keywords
//     */
//    public static void createKeywords(Question newQuestion, int newQuestionId) {
//        if (newQuestion == null || newQuestion.getKeywords() == null) {
//            return;
//        }
//
//        // add one or multiple keywords and the connection in the join table (has_kq)
//        SQLiteDatabaseConnection.KEYWORD_REPOSITORY.add(newQuestion.getKeywords(), newQuestionId);
//    }
}