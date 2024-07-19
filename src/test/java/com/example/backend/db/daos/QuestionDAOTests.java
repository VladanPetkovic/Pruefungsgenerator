//package com.example.backend.db.daos;
//
//import com.example.backend.db.SQLiteDatabaseConnection;
//import com.example.backend.db.models.*;
//import org.junit.jupiter.api.*;
//
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class QuestionDAOTests {
//    @BeforeAll
//    void beforeAll() {
//        System.out.println("Starting with QuestionDAO-tests");
//    }
//    @BeforeEach
//    void beforeEach() {
//        System.out.println("----------------------------------------------------------------------------");
//    }
//
//    @AfterEach
//    void afterEach() {
//        System.out.println("----------------------------------------------------------------------------");
//    }
//
//    @AfterAll
//    void afterAll() {
//        System.out.println("QuestionDAO-tests finished");
//    }
//
//    @Test
//    void read_getOneQuestion() {
//        System.out.println("Check: Get question with id = 1");
//        // REMINDER: question with this id can be deleted or changed, so tests will fail in the future, when db changes
//
//        // arrange
//        Question question = SQLiteDatabaseConnection.QUESTION_REPOSITORY.get(1);
//        ArrayList<Question> questions = new ArrayList<>();
//        questions.add(question);
//
//        // show results
//        printQuestions(questions);
//
//        // act
//        // done in arrange
//
//        // assert
//        assertNotNull(question);
//    }
//
//    @Test
//    void readAll_forOneCategory() {
//        System.out.println("Check: Count of Questions for one category");
//        // REMINDER: expectedLength changes overtime, when new questions have been added
//
//        // arrange
//        Category category = new Category(4, "Algebra");
//        ArrayList<Question> questions = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getAll(category);
//        int expectedLength = 6;
//
//        // show results
//        printQuestions(questions);
//
//        // act
//        // done in arrange
//
//        // assert
//        assertEquals(expectedLength, questions.size());
//    }
//
//    @Test
//    void readAll_dynamicForOneCourse() {
//        System.out.println("Check: return all Questions for one Course, because nothing is set");
//
//        // arrange
//        Question testQuestion = new Question();
//        int expectedResult = 11;
//        // all other field are not set
//
//        // act
//        ArrayList<Question> questions = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getAll(testQuestion, "Datenmanagement");
//        // show results
//        printQuestions(questions);
//
//        // assert
//        assertEquals(expectedResult, questions.size());
//    }
//
//    @Test
//    void readAll_dynamicLangMC() {
//        System.out.println("Check: return all Questions, were only question_type = open is set");
//
//        // arrange
//        Question testQuestion = new Question();
//        QuestionType type = SQLiteDatabaseConnection.QUESTION_TYPE_REPOSITORY.get("open");
//        testQuestion.setType(type);
//        int expectedResult = 13;
//        // all other field are not set
//
//        // act
//        ArrayList<Question> questions = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getAll(testQuestion, "MACS1");
//        // show results
//        printQuestions(questions);
//
//        // assert
//        assertEquals(expectedResult, questions.size());
//    }
//
//    @Test
//    void readAll_dynamicKeywordThreeTimes() {
//        System.out.println("Check: return all Questions, these three keywords are set");
//
//        // arrange
//        Question testQuestion = new Question();
//        ArrayList<Keyword> keywords = new ArrayList<Keyword>();
//        keywords.add(new Keyword("Vektoren"));
//        keywords.add(new Keyword("Heine-Matrix"));
//        keywords.add(new Keyword("Eigenwert"));
//        testQuestion.setKeywords(keywords);
//        int expectedResult = 3;
//        // all other field are not set
//
//        // act
//        ArrayList<Question> questions = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getAll(testQuestion, "MACS1");
//        // show results
//        printQuestions(questions);
//
//        // assert
//        assertEquals(expectedResult, questions.size());
//    }
//
//    @Test
//    void readAll_dynamicMCTrue() {
//        System.out.println("Check: return all Questions, were question_type = multiple-choice is set");
//
//        // arrange
//        Question testQuestion = new Question();
//        testQuestion.setType(new QuestionType(Type.MULTIPLE_CHOICE));
//        int expectedResult = 0;
//        // all other field are not set
//
//        // act
//        ArrayList<Question> questions = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getAll(testQuestion, "MACS1");
//        // show results
//        printQuestions(questions);
//
//        // assert
//        assertEquals(expectedResult, questions.size());
//    }
//
//    @Test
//    void readAll_dynamicCategoryPointsDiff() {
//        System.out.println("Check: search Questions based on input criteria");
//
//        // arrange
//        Question question1 = new Question();
//        question1.setCategory(new Category(2, "Analysis"));
//        question1.setPoints(7);
//        question1.setDifficulty(8);
//        int expectedQuestionsCount = 2;
//
//        // act
//        ArrayList<Question> actualQuestions = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getAll(question1, "MACS1");
//
//        // show results
//        printQuestions(actualQuestions);
//
//        // assert
//        assertEquals(expectedQuestionsCount, actualQuestions.size());
//    }
//
//    @Test
//    void readAll_newPerformance() {
//        System.out.println("Check: performance of getting all questions with keywords, ...");
//
//        // arrange
//        int expectedResult = 24;
//
//        // act
//        long startTime = System.currentTimeMillis();
//        ArrayList<Question> questions = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getAll();
//        long stopTime = System.currentTimeMillis();
//
//        // printQuestions(questions);
//
//        // assert
//        System.out.println("Time spent: " + (stopTime - startTime));
//        assertEquals(expectedResult, questions.size());
//    }
//
//    void printQuestions(ArrayList<Question> questions) {
//        if(questions.isEmpty()) {
//            System.out.println("No questions found");
//            return;
//        }
//
//        for(Question question : questions) {
//            System.out.println("_----------------------------------_");
//            System.out.println("ID: " + question.getId());
//            System.out.println("Category: " + question.getCategory().getName());
//            System.out.println("Difficulty: " + question.getDifficulty());
//            System.out.println("Points: " + question.getPoints());
//            System.out.println("QuestionString: " + question.getQuestion());
//            System.out.println("Type: " + question.getType().getName());
//            System.out.println("Remark: " + question.getRemark());
//            System.out.println("created_at: " + question.getCreated_at());
//            System.out.println("updated_at: " + question.getUpdated_at());
//            System.out.println("Answer: " + question.getAnswersAsString());
//            System.out.print("Keywords: ");
//            for(Keyword keyword : question.getKeywords()) {
//                System.out.print(keyword.getKeyword() + " ");
//            }
//            System.out.println();
//        }
//    }
//}
