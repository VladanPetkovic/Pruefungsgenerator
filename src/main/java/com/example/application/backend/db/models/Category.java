package com.example.application.backend.db.models;

import com.example.application.backend.app.SharedData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
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
@Table(name = "categories")
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Question> questions = new HashSet<>();

    @ManyToMany
    private Set<Course> courses = new HashSet<>();

    public Category(Category other) {
        setId(other.getId());
        setName(other.getName());
    }

    public Category(String category) {
        setName(category);
    }

    /**
     * This function checks the provided category, no special chars permitted and no leading and ending space.
     *
     * @param newCategory The provided category
     * @return String - the error-message, returns null if everything is fine.
     */
    public static String checkNewCategory(String newCategory) throws IOException {
        if (newCategory == null) {
            return "No category provided!";
        }

        // Check for no leading and ending space
        if (newCategory.trim().isEmpty()) {
            SharedData.setOperation(Message.ERROR_MESSAGE_DATA_CONTAINS_SPACES);
            return "Category cannot be empty!";
        }

        // Check for no special characters except letters and digits
        if (!newCategory.matches("[a-zA-Z0-9öäüÖÄÜ\\s]+")) {
            SharedData.setOperation(Message.CATEGORY_INVALID_CHARACTERS_ERROR_MESSAGE);
            return "Category contains invalid characters!";
        }

        if (newCategory.length() > 30) {
            SharedData.setOperation("Category is too long - max 30 chars! Provided: " + newCategory.length(), true);
            return "Category is too long - max 30 chars! Provided: " + newCategory.length();
        }

        return null;
    }

//    public static Category createNewCategoryInDatabase(String category, Course course) {
//        // check for existence
//        Category newCategory = SQLiteDatabaseConnection.CATEGORY_REPOSITORY.get(category);
//
//        if (newCategory == null) {
//            Category addToDatabase = new Category(category);
//            SQLiteDatabaseConnection.CATEGORY_REPOSITORY.add(addToDatabase);
//            newCategory = SQLiteDatabaseConnection.CATEGORY_REPOSITORY.get(category);
//            SQLiteDatabaseConnection.CATEGORY_REPOSITORY.addConnection(course, newCategory);
//        } else {
//            SQLiteDatabaseConnection.CATEGORY_REPOSITORY.addConnection(course, newCategory);
//        }
//
//        return newCategory;
//    }
}
