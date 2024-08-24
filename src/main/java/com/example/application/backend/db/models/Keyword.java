package com.example.application.backend.db.models;

import com.example.application.backend.app.SharedData;
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
@Table(name = "keywords")
public class Keyword implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @ManyToOne
    @JoinColumn(name = "fk_course_id")
    private Course course;

    @ManyToMany(mappedBy = "keywords", fetch = FetchType.LAZY)
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
    public static String checkNewKeyword(String newKeyword) {
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

    /**
     * This function is used for filtering questions.
     * We can pass a Set of Strings and not Objects to the database.
     *
     * @param keywords the objects we want to "transform"
     * @return Set of Strings
     */
    public static Set<String> getKeywordsAsString(Set<Keyword> keywords) {
        if (keywords.isEmpty()) {
            return null;
        }

        Set<String> keywordStrings = new HashSet<>();
        for (Keyword keyword : keywords) {
            if (keyword != null && keyword.getKeyword() != null) {
                keywordStrings.add(keyword.getKeyword());
            }
        }
        return keywordStrings;
    }

    public static Set<Keyword> getKeywordsAsSet(String[] keywords) {
        Set<Keyword> newKeywords = new HashSet<>();

        for (String keywordText : keywords) {
            String trimmedKeyword = keywordText.trim();
            if (!trimmedKeyword.isEmpty()) {
                newKeywords.add(new Keyword(trimmedKeyword));
            }
        }

        return newKeywords;
    }
}
