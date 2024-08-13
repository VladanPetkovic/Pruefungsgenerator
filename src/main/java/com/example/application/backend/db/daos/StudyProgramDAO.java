//package com.example.backend.db.daos;
//
//import com.example.backend.app.LogLevel;
//import com.example.backend.app.Logger;
//import com.example.backend.app.SharedData;
//import com.example.backend.db.SQLiteDatabaseConnection;
//import com.example.backend.db.models.Message;
//import com.example.backend.db.models.StudyProgram;
//import lombok.AccessLevel;
//import lombok.Setter;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//
///**
// * Data Access Object (DAO) class for interacting with StudyProgram entities in the database.
// */
//public class StudyProgramDAO implements DAO<StudyProgram> {
//    @Setter(AccessLevel.PRIVATE)
//    ArrayList<StudyProgram> studyProgramCache;
//
//    /**
//     * Inserts a new study program into the database.
//     *
//     * @param program The study program to create.
//     */
//    @Override
//    public void create(StudyProgram program) {
//        String insertStmt = "INSERT INTO study_programs (name, abbreviation) VALUES (?, ?);";
//        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);
//
//        try (Connection connection = SQLiteDatabaseConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {
//
//            preparedStatement.setString(1, program.getName());
//            preparedStatement.setString(2, program.getAbbreviation());
//            preparedStatement.executeUpdate();
//            setStudyProgramCache(null);
//
//            SharedData.setOperation(Message.CREATE_STUDYPROGRAM_SUCCESS_MESSAGE);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            SharedData.setOperation(Message.CREATE_STUDYPROGRAM_ERROR_MESSAGE);
//        }
//    }
//
//    /**
//     * Retrieves all study programs from the database.
//     *
//     * @return ArrayList of all study programs.
//     */
//    @Override
//    public ArrayList<StudyProgram> readAll() {
//        ArrayList<StudyProgram> programs = new ArrayList<>();
//        String selectStmt = "SELECT * FROM study_programs;";
//        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);
//
//        try (Connection connection = SQLiteDatabaseConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
//             ResultSet resultSet = preparedStatement.executeQuery()) {
//
//            while (resultSet.next()) {
//                StudyProgram newProgram = createModelFromResultSet(resultSet);
//                programs.add(newProgram);
//            }
//            setStudyProgramCache(programs);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return programs;
//    }
//
//    /**
//     * Retrieves a study program by its ID from the database.
//     *
//     * @param id The ID of the study program to retrieve.
//     * @return The StudyProgram object corresponding to the given ID.
//     */
//    @Override
//    public StudyProgram read(int id) {
//        StudyProgram program = null;
//        String readStmt = "SELECT * FROM study_programs WHERE id = ?;";
//        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);
//
//        try (Connection connection = SQLiteDatabaseConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
//
//            preparedStatement.setInt(1, id);
//            try (ResultSet result = preparedStatement.executeQuery()) {
//                if (result.next()) {
//                    program = createModelFromResultSet(result);
//                }
//            }
//            setStudyProgramCache(null);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return program;
//    }
//
//    /**
//     * Retrieves a study program by its name from the database.
//     *
//     * @param name The name of the study program to retrieve.
//     * @return The StudyProgram object corresponding to the given name.
//     */
//    public StudyProgram read(String name) {
//        StudyProgram program = null;
//        String readStmt = "SELECT * FROM study_programs WHERE name = ?;";
//        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);
//
//        try (Connection connection = SQLiteDatabaseConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
//
//            preparedStatement.setString(1, name);
//            try (ResultSet result = preparedStatement.executeQuery()) {
//                if (result.next()) {
//                    program = createModelFromResultSet(result);
//                }
//            }
//            setStudyProgramCache(null);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return program;
//    }
//
//    public boolean hasCourses(int studyProgramId) {
//        boolean hasCourses = false;
//
//        String readStmt = "SELECT CASE " +
//                "WHEN COUNT(*) > 0 THEN 1 " +
//                "ELSE 0 " +
//                "END AS has_courses " +
//                "FROM has_sc " +
//                "WHERE fk_program_id = ?;";
//        Logger.log(getClass().getName(), readStmt, LogLevel.DEBUG);
//        try (Connection connection = SQLiteDatabaseConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {
//            preparedStatement.setInt(1, studyProgramId);
//            try (ResultSet result = preparedStatement.executeQuery()) {
//                if (result.next()) {
//                    hasCourses = result.getBoolean("has_courses");
//                }
//                setStudyProgramCache(null);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return hasCourses;
//    }
//
//    /**
//     * Updates a study program in the database.
//     *
//     * @param program The study program to update.
//     */
//    @Override
//    public void update(StudyProgram program) {
//        String updateStmt = "UPDATE study_programs SET name = ?, abbreviation = ? WHERE id = ?;";
//        Logger.log(getClass().getName(), updateStmt, LogLevel.DEBUG);
//
//        try (Connection connection = SQLiteDatabaseConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {
//
//            preparedStatement.setString(1, program.getName());
//            preparedStatement.setString(2, program.getAbbreviation());
//            preparedStatement.setInt(3, program.getId());
//            preparedStatement.executeUpdate();
//            setStudyProgramCache(null);
//
//            SharedData.setOperation(Message.UPDATE_STUDYPROGRAM_SUCCESS_MESSAGE);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            SharedData.setOperation(Message.UPDATE_STUDYPROGRAM_ERROR_MESSAGE);
//        }
//    }
//
//    /**
//     * Deletes a study program from the database.
//     *
//     * @param id The ID of the study program to delete.
//     */
//    @Override
//    public void delete(int id) {
//        String deleteStmt = "DELETE FROM study_programs WHERE id = ?;";
//        String deleteHasScStmt = "DELETE FROM has_sc WHERE fk_program_id = ?;";
//        Logger.log(getClass().getName(), deleteStmt, LogLevel.DEBUG);
//        Logger.log(getClass().getName(), deleteHasScStmt, LogLevel.DEBUG);
//
//        try (Connection connection = SQLiteDatabaseConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
//             PreparedStatement secondPpStmt = connection.prepareStatement(deleteHasScStmt)) {
//
//            preparedStatement.setInt(1, id);
//            preparedStatement.executeUpdate();
//
//            secondPpStmt.setInt(1, id);
//            secondPpStmt.executeUpdate();
//
//            setStudyProgramCache(null);
//
//            SharedData.setOperation(Message.DELETE_STUDYPROGRAM_SUCCESS_MESSAGE);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            SharedData.setOperation(Message.DELETE_STUDYPROGRAM_ERROR_MESSAGE);
//        }
//    }
//
//    /**
//     * Creates a StudyProgram object from the given ResultSet.
//     *
//     * @param resultSet The ResultSet containing study program data.
//     * @return The StudyProgram object created from the ResultSet.
//     * @throws SQLException If a SQL exception occurs.
//     */
//    @Override
//    public StudyProgram createModelFromResultSet(ResultSet resultSet) throws SQLException {
//        return new StudyProgram(
//                resultSet.getInt("id"),
//                resultSet.getString("name"),
//                resultSet.getString("abbreviation")
//        );
//    }
//}
