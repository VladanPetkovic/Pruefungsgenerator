package com.example.backend.db.daos;

import com.example.backend.db.models.Course;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CourseDAO implements DAO<Course> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Course> CourseCache;

    @Override
    public void create(Course course) {
        String insertStmt =
                "INSERT INTO Courses (CourseName, CourseNumber, Lector) " +
                "VALUES (?, ?, ?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, course.getCourse_name());
            preparedStatement.setInt(2, course.getCourse_number());
            preparedStatement.setString(3, course.getLector());
            preparedStatement.execute();
            getConnection().close();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Course> readAll() {
        ArrayList<Course> courses = new ArrayList<>();

        String selectStmt =
                "SELECT CourseID, CourseName, CourseNumber, Lector " +
                "FROM Courses;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Course newCourse = new Course(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getString(4));
                courses.add(newCourse);
            }
            setCourseCache(courses);
            getConnection().close();
            return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Course> readAllForOneProgram(int studyProgram_id) {
        ArrayList<Course> courses = new ArrayList<>();

        String selectStmt =
            "Select Courses.CourseID, CourseName, CourseNumber, Lector " +
            "FROM Courses " +
            "JOIN hasSC ON Courses.CourseID = hasSC.CourseID " +
            "JOIN StudyPrograms ON hasSC.ProgramID = StudyPrograms.ProgramID " +
            "WHERE hasSC.ProgramID = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, studyProgram_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Course newCourse = new Course(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getString(4));
                courses.add(newCourse);
            }
            setCourseCache(courses);
            getConnection().close();
            return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Course read(int id) {
        Course course = null;

        String readStmt =
                "SELECT CourseID, CourseName, CourseNumber, Lector " +
                        "FROM Courses " +
                        "WHERE CourseID = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(readStmt);
            preparedStatement.setInt(1, id);

            ResultSet result = preparedStatement.executeQuery();

            if(result.next()) {
                course = new Course(
                        result.getInt(1),
                        result.getString(2),
                        result.getInt(3),
                        result.getString(4)
                );
            }

            getConnection().close();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return course;
    }

    @Override
    public void update(Course course) {
        String updateStmt =
                "UPDATE Courses " +
                "SET CourseName = ?, CourseNumber = ?, Lector = ? " +
                "WHERE CourseID = ?";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, course.getCourse_name());
            preparedStatement.setInt(2, course.getCourse_number());
            preparedStatement.setString(3, course.getLector());
            preparedStatement.execute();
            getConnection().close();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Courses WHERE CourseID = ?;";
        String deleteHasCTStmt = "DELETE FROM hasCT WHERE CourseID = ?;";
        String deleteHasSCStmt = "DELETE FROM hasSC WHERE CourseID = ?;";
        try {
            // deleting from Courses table
            PreparedStatement preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            // deleting from hasCT table
            PreparedStatement secondPpStmt = getConnection().prepareStatement(deleteHasCTStmt);
            secondPpStmt.setInt(1, id);
            secondPpStmt.execute();
            // deleting from hasSC table
            PreparedStatement thirdPpStmt = getConnection().prepareStatement(deleteHasSCStmt);
            thirdPpStmt.setInt(1, id);
            thirdPpStmt.execute();

            getConnection().close();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSCConnection(int program_id, int course_id) {
        String insertStmt = "INSERT INTO hasSC (ProgramID, CourseID) VALUES (?, ?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setInt(1, program_id);
            preparedStatement.setInt(2, course_id);
            preparedStatement.execute();
            getConnection().close();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
