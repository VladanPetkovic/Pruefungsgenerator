package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Category;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryDAOTests {

    @BeforeAll
    void beforeAll() {
        System.out.println("Starting with CategoryDAO-tests");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("----------------------------------------------------------------------------");
    }

    @AfterEach
    void afterEach() {
        System.out.println("----------------------------------------------------------------------------");
    }

    @AfterAll
    void afterAll() {
        System.out.println("CategoryDAO-tests finished");
    }

    @Test
    void create_insertCategory() {
        System.out.println("Check: Insert a new category into the database");

        // Arrange
        Category category = new Category(0, "TestCategory");

        // Act
        SQLiteDatabaseConnection.CATEGORY_REPOSITORY.add(category);

        // Assert
        Category retrievedCategory = SQLiteDatabaseConnection.CATEGORY_REPOSITORY.get(category.getName());
        assertNotNull(retrievedCategory);
        assertEquals(category.getName(), retrievedCategory.getName());
    }

    @Test
    void readAll_getAllCategories() {
        System.out.println("Check: Retrieve all categories from the database");

        // Arrange
        // Act
        ArrayList<Category> categories = SQLiteDatabaseConnection.CATEGORY_REPOSITORY.getAll();

        // Assert
        assertNotNull(categories);
        assertTrue(categories.size() > 0);
    }

    @Test
    void readAllForOneCourse_getCategoriesForCourse() {
        System.out.println("Check: Retrieve categories associated with a specific course");

        // Arrange
        int courseId = 1;

        // Act
        ArrayList<Category> categories = SQLiteDatabaseConnection.CATEGORY_REPOSITORY.getAll(courseId);

        // Assert
        assertNotNull(categories);
        assertTrue(categories.size() > 0);
    }

    @Test
    void read_getCategoryById() {
        System.out.println("Check: Retrieve a category by its ID");

        // Arrange
        int categoryId = 1; // Update with existing category ID

        // Act
        Category category = SQLiteDatabaseConnection.CATEGORY_REPOSITORY.get(categoryId);

        // Assert
        assertNotNull(category);
        assertEquals(categoryId, category.getId());
    }

//    @Test --> THIS WILL CHANGE OUR DB --> MAYBE REWRITE TO MOCK A DB-CALL
//    void update_modifyCategory() {
//        System.out.println("Check: Update a category in the database");
//
//        // Arrange
//        int categoryId = 1;
//        Category category = SQLiteDatabaseConnection.CategoryRepository.get(categoryId);
//        String updatedCategoryName = "UpdatedCategory";
//
//        // Act
//        category.setName(updatedCategoryName);
//        SQLiteDatabaseConnection.CategoryRepository.update(category);
//
//        // Assert
//        Category updatedCategory = SQLiteDatabaseConnection.CategoryRepository.get(categoryId);
//        assertNotNull(updatedCategory);
//        assertEquals(updatedCategoryName, updatedCategory.getName());
//    }

//    @Test --> THIS WILL NOT WORK, BECAUS ID CANNOT BE SET, IT IS INCREMENTAL
//    void delete_removeCategory() {
//        System.out.println("Check: Delete a category from the database");
//
//        // Arrange
//        Category category = new Category(10, "Category to delete");
//        SQLiteDatabaseConnection.CategoryRepository.add(category);
//
//        // Act
//        SQLiteDatabaseConnection.CategoryRepository.remove(category);
//
//        // Assert
//        Category deletedCategory = SQLiteDatabaseConnection.CategoryRepository.get(category.getId());
//        assertNull(deletedCategory);
//    }
}
