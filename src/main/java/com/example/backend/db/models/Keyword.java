package com.example.backend.db.models;

import com.example.backend.db.SQLiteDatabaseConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Keyword {
    private int id;
    private String keyword;

    public Keyword(String keyword) {
        setKeyword(keyword);
    }

    /**
     * This function checks the provided keyword, no special chars permitted and no leading and ending space.
     * @param newKeyword The provided keyword
     * @return String - the error-message, returns null if everything is fine.
     */
    public static String checkNewKeyword(String newKeyword) {
        if (newKeyword == null) {
            return "No keyword provided!";
        }

        // Check for no leading and ending space
        if (newKeyword.trim().isEmpty()) {
            return "Keyword cannot be empty!";
        }

        // Check for no special characters except letters and digits
        if (!newKeyword.matches("[a-zA-Z0-9öäüÖÄÜ\\s]+")) {
            return "Keyword contains invalid characters!";
        }

        if (newKeyword.length() > 30) {
            return "Keyword too long - max 30 chars! You entered: " + newKeyword.length();
        }

        return null;
    }

    public static void createNewKeywordInDatabase(String keyword) {
        Keyword newKeyword = new Keyword(keyword);

        // check for existing keywords
        Keyword keywordInDatabase = SQLiteDatabaseConnection.keywordRepository.get(keyword);

        if (keywordInDatabase == null) {
            SQLiteDatabaseConnection.keywordRepository.add(newKeyword);
        }

        // TODO: add the connection to the question
        // keywords can only be created when creating/editing a question
    }
}
