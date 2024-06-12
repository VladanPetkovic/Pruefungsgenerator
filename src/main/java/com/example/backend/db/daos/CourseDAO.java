package com.example.backend.db.daos;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.app.SharedData;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.Message;
import lombok.AccessLevel;
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
        String insertStmt = "INSERT INTO courses (name, number, lector) VALUES (?, ?, ?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
            preparedStatement.setString(1, course.getName());
            preparedStatement.setInt(2, course.getNumber());
            preparedStatement.setString(3, course.getLector());
            preparedStatement.executeUpdate();
            setCourseCache(null);

            SharedData.setOperation(Message.CREATE_COURSE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.CREATE_COURSE_ERROR_MESSAGE);
        }
    }

    /**
     * Retrieves all courses from the database.
     *
     * @return An ArrayList containing all courses.
     */
    @Override
    public ArrayList<Course> readAll() {
        String selectStmt = "SELECT * FROM courses;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

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
                "SELECT courses.* " +
                "FROM courses " +
                "JOIN has_sc ON courses.id = has_sc.fk_course_id " +
                "JOIN study_programs ON has_sc.fk_program_id = study_programs.id " +
                "WHERE has_sc.fk_program_id = ?;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

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

        String readStmt = "SELECT * FROM courses WHERE id = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);
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

        String readStmt = "SELECT * FROM courses WHERE name = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);
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
     * Retrieves a course by its course number from the database.
     *
     * @param courseNumber The course number of the course to retrieve.
     * @return The Course object corresponding to the given course number.
     */
    public Course readByCourseNumber(int courseNumber) {
        Course course = null;

        String readStmt = "SELECT * FROM courses WHERE number = ?;";
        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
            preparedStatement.setInt(1, courseNumber);
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
                "UPDATE courses " +
                "SET name = ?, number = ?, lector = ? " +
                "WHERE id = ?";
        Logger.log(getClass().getName(), updateStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
            preparedStatement.setString(1, course.getName());
            preparedStatement.setInt(2, course.getNumber());
            preparedStatement.setString(3, course.getLector());
            preparedStatement.setInt(4, course.getId());
            preparedStatement.executeUpdate();
            setCourseCache(null);

            SharedData.setOperation(Message.UPDATE_COURSE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.UPDATE_COURSE_ERROR_MESSAGE);
        }
    }

    /**
     * Deletes a course record from the database by its ID.
     *
     * @param id The ID of the course to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM courses WHERE id = ?;";
        String deleteHasCCStmt = "DELETE FROM has_cc WHERE fk_course_id = ?;";
        String deleteHasSCStmt = "DELETE FROM has_sc WHERE fk_course_id = ?;";
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

            SharedData.setOperation(Message.DELETE_COURSE_SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            SharedData.setOperation(Message.DELETE_COURSE_ERROR_MESSAGE);
        }
    }

    /**
     * Adds a connection between a study program and a course in the database.
     *
     * @param programId The ID of the study program.
     * @param courseId  The ID of the course.
     */
    public void addSCConnection(int programId, int courseId) {
        // SQL statement to check if the relationship already exists
        String checkStmt = "SELECT COUNT(*) FROM has_sc WHERE fk_program_id = ? AND fk_course_id = ?;";
        // SQL statement to insert a new relationship
        String insertStmt = "INSERT INTO has_sc (fk_program_id, fk_course_id) VALUES (?, ?);";

        Logger.log(getClass().getName(), "Checking existence: " + checkStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement checkStatement = connection.prepareStatement(checkStmt);
             PreparedStatement insertStatement = connection.prepareStatement(insertStmt)) {

            // Set parameters for the check statement
            checkStatement.setInt(1, programId);
            checkStatement.setInt(2, courseId);

            // Execute the check query
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Relationship already exists, log and return
                    Logger.log(getClass().getName(),
                            "The relationship between program ID " + programId + " and course ID " + courseId + " already exists.",
                            LogLevel.INFO);
                    return;
                }
            }

            // Relationship does not exist, proceed with the insertion
            Logger.log(getClass().getName(), "Inserting new relationship: " + insertStmt, LogLevel.DEBUG);

            insertStatement.setInt(1, programId);
            insertStatement.setInt(2, courseId);
            insertStatement.executeUpdate();

            // Optionally clear any cache or perform additional actions
            setCourseCache(null);

            Logger.log(getClass().getName(),
                    "Successfully inserted relationship between program ID " + programId + " and course ID " + courseId,
                    LogLevel.INFO);
        } catch (SQLException e) {
            Logger.log(getClass().getName(), "SQL error: " + e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Removes a connection between a study program and a course from the database.
     *
     * @param programId The ID of the study program.
     * @param courseId  The ID of the course.
     */
    public void removeSCConnection(int programId, int courseId) {
        String deleteStmt = "DELETE FROM has_sc WHERE fk_program_id = ? AND fk_course_id = ?";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt)) {
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
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("number"),
                resultSet.getString("lector")
        );
    }
}
