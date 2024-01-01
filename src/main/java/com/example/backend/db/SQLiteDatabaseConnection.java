package com.example.backend.db;

import com.example.backend.db.daos.*;
import com.example.backend.db.models.Topic;
import com.example.backend.db.repositories.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabaseConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:mydatabase.db";

    public static final CourseRepository courseRepository = new CourseRepository(new CourseDAO());
    public static final ImageRepository imageRepository =  new ImageRepository(new ImageDAO());
    public static final KeywordRepository keywordRepository = new KeywordRepository(new KeywordDAO());
    public static final QuestionRepository questionRepository = new QuestionRepository(new QuestionDAO());
    public static final StudyProgramRepository studyProgramRepository = new StudyProgramRepository(new StudyProgramDAO());
    public static final TopicRepository TopicRepository = new TopicRepository(new TopicDAO());
    public static Connection connect(){
        try{
            return DriverManager.getConnection(DATABASE_URL);
        }catch (SQLException e){
            System.out.println(e.getErrorCode());
        }
        return null;
    }



}
