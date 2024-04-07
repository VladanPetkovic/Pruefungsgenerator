package com.example.backend.db;

import com.example.backend.db.daos.*;
import com.example.backend.db.repositories.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabaseConnection {
    private static final String DATABASE_URL = getDatabaseUrl();

    public static final CourseRepository courseRepository = new CourseRepository(new CourseDAO());
    public static final ImageRepository imageRepository =  new ImageRepository(new ImageDAO());
    public static final KeywordRepository keywordRepository = new KeywordRepository(new KeywordDAO());
    public static final QuestionRepository questionRepository = new QuestionRepository(new QuestionDAO());
    public static final StudyProgramRepository studyProgramRepository = new StudyProgramRepository(new StudyProgramDAO());
    public static final CategoryRepository CategoryRepository = new CategoryRepository(new CategoryDAO());
    public static final QuestionTypeRepository QUESTION_TYPE_REPOSITORY = new QuestionTypeRepository(new QuestionTypeDAO());
    public static final AnswerRepository ANSWER_REPOSITORY = new AnswerRepository(new AnswerDAO());
    public static Connection connect() {
        try{
            return DriverManager.getConnection(DATABASE_URL);
        }catch (SQLException e){
            System.out.println(e.getErrorCode());
        }
        return null;
    }

    public static String getDatabaseUrl() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/example/backend/db/db_config.txt"))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
