package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) for managing Course objects in the database.
 */
public class CourseDAO implements DAO<Course> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Course> courseCache;

    /**
     * Creates a new course record in the database.
     *
     * @param course The Course object to create.
     */
    @Override
    public void create(Course course) {
        String insertStmt = "INSERT INTO Courses (CourseName, CourseNumber, Lector) VALUES (?, ?, ?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, course.getCourse_name());
            preparedStatement.setInt(2, course.getCourse_number());
            preparedStatement.setString(3, course.getLector());
            preparedStatement.executeUpdate();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all courses from the database.
     *
     * @return An ArrayList containing all courses.
     */
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

    /**
     * Retrieves all courses associated with a specific study program.
     *
     * @param studyProgramId The ID of the study program.
     * @return An ArrayList containing courses associated with the specified study program.
     */
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

    /**
     * Retrieves a course by its ID from the database.
     *
     * @param id The ID of the course to retrieve.
     * @return The Course object corresponding to the given ID.
     */
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

    /**
     * Retrieves a course by its name from the database.
     *
     * @param course_name The name of the course to retrieve.
     * @return The Course object corresponding to the given name.
     */
    public Course read(String course_name) {
        Course course = null;

        String readStmt = "SELECT * FROM Courses WHERE CourseName = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setString(1, course_name);
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

    /**
     * Updates an existing course record in the database.
     *
     * @param course The Course object with updated information.
     */
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
            preparedStatement.executeUpdate();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a course record from the database by its ID.
     *
     * @param id The ID of the course to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Courses WHERE CourseID = ?;";
        String deleteHasCCStmt = "DELETE FROM hasCC WHERE CourseID = ?;";
        String deleteHasSCStmt = "DELETE FROM hasSC WHERE CourseID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasCCStmt);
             PreparedStatement thirdPreparedStatement = connection.prepareStatement(deleteHasSCStmt)) {
            // deleting from Courses table
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            // deleting from hasCC table
            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.executeUpdate();
            // deleting from hasSC table
            thirdPreparedStatement.setInt(1, id);
            thirdPreparedStatement.executeUpdate();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a connection between a study program and a course in the database.
     *
     * @param programId The ID of the study program.
     * @param courseId  The ID of the course.
     */
    public void addSCConnection(int programId, int courseId) {
        String insertStmt = "INSERT INTO hasSC (ProgramID, CourseID) VALUES (?, ?);";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setInt(1, programId);
            preparedStatement.setInt(2, courseId);
            preparedStatement.executeUpdate();
            setCourseCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Course object from a ResultSet obtained from the database.
     *
     * @param resultSet The ResultSet containing course data.
     * @return A Course object created from the ResultSet.
     * @throws SQLException If a database access error occurs or the column labels are not valid.
     */
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
