package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;

public class CourseDAO implements DAO<Course> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Course> courseCache;

    @Override
    public void create(Course course) {
        String insertStmt = "INSERT INTO Courses (CourseName, CourseNumber, Lector) VALUES (?, ?, ?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, course.getCourse_name());
            preparedStatement.setInt(2, course.getCourse_number());
            preparedStatement.setString(3, course.getLector());
            preparedStatement.execute();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Course> readAll() {
        String selectStmt = "SELECT CourseID, CourseName, CourseNumber, Lector FROM Courses;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ArrayList<Course> courses = new ArrayList<>();
            while (resultSet.next()) {
                Course newCourse = createModelFromResultSet(resultSet);
                courses.add(newCourse);
            }

            setCourseCache(courses);
            return courses;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Course> readAllForOneProgram(int studyProgramId) {
        String selectStmt =
                "SELECT Courses.CourseID, CourseName, CourseNumber, Lector " +
                        "FROM Courses " +
                        "JOIN hasSC ON Courses.CourseID = hasSC.CourseID " +
                        "JOIN StudyPrograms ON hasSC.ProgramID = StudyPrograms.ProgramID " +
                        "WHERE hasSC.ProgramID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {
            preparedStatement.setInt(1, studyProgramId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ArrayList<Course> courses = new ArrayList<>();
                while (resultSet.next()) {
                    Course newCourse = createModelFromResultSet(resultSet);
                    courses.add(newCourse);
                }
                setCourseCache(courses);
                return courses;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Course read(int id) {
        Course course = null;

        String readStmt = "SELECT CourseID, CourseName, CourseNumber, Lector FROM Courses WHERE CourseID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    course = createModelFromResultSet(result);
                }
                setCourseCache(null);
            }
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
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
            preparedStatement.setString(1, course.getCourse_name());
            preparedStatement.setInt(2, course.getCourse_number());
            preparedStatement.setString(3, course.getLector());
            preparedStatement.setInt(4, course.getCourse_id());
            preparedStatement.execute();
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
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasCTStmt);
             PreparedStatement thirdPreparedStatement = connection.prepareStatement(deleteHasSCStmt)) {
            // deleting from Courses table
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            // deleting from hasCT table
            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.execute();
            // deleting from hasSC table
            thirdPreparedStatement.setInt(1, id);
            thirdPreparedStatement.execute();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSCConnection(int programId, int courseId) {
        String insertStmt = "INSERT INTO hasSC (ProgramID, CourseID) VALUES (?, ?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setInt(1, programId);
            preparedStatement.setInt(2, courseId);
            preparedStatement.execute();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Course createModelFromResultSet (ResultSet resultSet) throws SQLException {
        return new Course(
                resultSet.getInt("CourseID"),
                resultSet.getString("CourseName"),
                resultSet.getInt("CourseNumber"),
                resultSet.getString("Lector")
        );
    }
}
