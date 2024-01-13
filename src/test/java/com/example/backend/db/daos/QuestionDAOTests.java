package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Question;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestionDAOTests {
    @BeforeAll
    void beforeAll() {
        System.out.println("Starting with QuestionDAO-tests");
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
        System.out.println("QuestionDAO-tests finished");
    }

    @Test
    void readAll_checkResult() {
        System.out.println("Check: Count of Questions for one course");
        // REMINDER: expectedLength changes overtime, when new questions have been added

        // arrange
        Course course = SQLiteDatabaseConnection.courseRepository.get("MACS1");
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll(course);
        int expectedLength = 2;

        // show results
        printQuestions(questions);

        // act
        // done in arrange

        // assert
        assertEquals(expectedLength, questions.size());
    }

    @Test
    void readAll_dynamicLangMC() {
        System.out.println("Check: return all Questions, were only language and multiplechoice = false is set");

        // arrange
        Question testQuestion = new Question();
        testQuestion.setLanguage("Deutsch");
        testQuestion.setMultipleChoice(0);
        int expectedResult = 3;
        // all other field are not set

        // act
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll(testQuestion, "MACS1", true);
        // show results
        printQuestions(questions);

        // assert
        assertEquals(expectedResult, questions.size());
    }

    @Test
    void readAll_dynamicLangKeyword() {
        System.out.println("Check: return all Questions, were only language and one keyword is set");

        // arrange
        Question testQuestion = new Question();
        ArrayList<Keyword> keywords = new ArrayList<Keyword>();
        keywords.add(new Keyword(7, "Vektoren"));
        testQuestion.setLanguage("Deutsch");
        testQuestion.setKeywords(keywords);
        int expectedResult = 1;
        // all other field are not set

        // act
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll(testQuestion, "MACS1", false);
        // show results
        printQuestions(questions);

        // assert
        assertEquals(expectedResult, questions.size());
    }

    @Test
    void readAll_dynamicMCTrue() {
        System.out.println("Check: return all Questions, were multiplechoice = true is set");

        // arrange
        Question testQuestion = new Question();
        testQuestion.setMultipleChoice(1);
        int expectedResult = 0;
        // all other field are not set

        // act
        ArrayList<Question> questions = SQLiteDatabaseConnection.questionRepository.getAll(testQuestion, "MACS1", true);
        // show results
        printQuestions(questions);

        // assert
        assertEquals(expectedResult, questions.size());
    }

    void printQuestions(ArrayList<Question> questions) {
        if(questions.isEmpty()) {
            System.out.println("No questions found");
            return;
        }

        for(Question question : questions) {
            System.out.println("_----------------------------------_");
            System.out.println("ID: " + question.getQuestion_id());
            System.out.println("QuestionString: " + question.getQuestionString());
            System.out.println("Answer: " + question.getAnswers());
            System.out.println("Category: " + question.getCategory().getCategory());
            System.out.println("Language: " + question.getLanguage());
            System.out.println("Difficulty: " + question.getDifficulty());
            System.out.println("Points: " + question.getPoints());
        }
    }
}
