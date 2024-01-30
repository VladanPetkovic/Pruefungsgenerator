package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.StudyProgram;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for interacting with StudyProgram entities in the database.
 */
public class StudyProgramDAO implements DAO<StudyProgram> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<StudyProgram> studyProgramCache;

    /**
     * Inserts a new study program into the database.
     *
     * @param program The study program to create.
     */
    @Override
    public void create(StudyProgram program) {
        String insertStmt = "INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation) VALUES (?, ?);";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setString(1, program.getProgram_name());
            preparedStatement.setString(2, program.getProgram_abbr());
            preparedStatement.executeUpdate();
            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all study programs from the database.
     *
     * @return ArrayList of all study programs.
     */
    @Override
    public ArrayList<StudyProgram> readAll() {
        ArrayList<StudyProgram> programs = new ArrayList<>();

        String selectStmt = "SELECT ProgramID, ProgramName, ProgramAbbreviation FROM StudyPrograms;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                StudyProgram newProgram = createModelFromResultSet(resultSet);
                programs.add(newProgram);
            }

            setStudyProgramCache(programs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return programs;
    }

    /**
     * Retrieves a study program by its ID from the database.
     *
     * @param id The ID of the study program to retrieve.
     * @return The StudyProgram object corresponding to the given ID.
     */
    @Override
    public StudyProgram read(int id) {
        StudyProgram program = null;

        String readStmt = "SELECT ProgramID, ProgramName, ProgramAbbreviation FROM StudyPrograms WHERE ProgramID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {

            preparedStatement.setInt(1, id);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    program = createModelFromResultSet(result);
                }
            }

            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return program;
    }

    /**
     * Retrieves a study program by its name from the database.
     *
     * @param name The name of the study program to retrieve.
     * @return The StudyProgram object corresponding to the given name.
     */
    public StudyProgram readByName(String name) {
        StudyProgram program = null;

        String readStmt = "SELECT ProgramID, ProgramName, ProgramAbbreviation FROM StudyPrograms WHERE ProgramName = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {

            preparedStatement.setString(1, name);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    program = createModelFromResultSet(result);
                }
            }

            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return program;
    }

    /**
     * Retrieves a study program by its abbreviation from the database.
     *
     * @param abbreviation The abbreviation of the study program to retrieve.
     * @return The StudyProgram object corresponding to the given abbreviation.
     */
    public StudyProgram readByAbbreviation(String abbreviation) {
        StudyProgram program = null;

        String readStmt = "SELECT ProgramID, ProgramName, ProgramAbbreviation FROM StudyPrograms WHERE ProgramAbbreviation = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(readStmt)) {

            preparedStatement.setString(1, abbreviation);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    program = createModelFromResultSet(result);
                }
            }

            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return program;
    }

    /**
     * Updates a study program in the database.
     *
     * @param program The study program to update.
     */
    @Override
    public void update(StudyProgram program) {
        String updateStmt = "UPDATE StudyPrograms SET ProgramName = ?, ProgramAbbreviation = ? WHERE ProgramID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {

            preparedStatement.setString(1, program.getProgram_name());
            preparedStatement.setString(2, program.getProgram_abbr());
            preparedStatement.setInt(3, program.getProgram_id());
            preparedStatement.executeUpdate();
            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a study program from the database.
     *
     * @param id The ID of the study program to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM StudyPrograms WHERE ProgramID = ?;";
        String deleteHasScStmt = "DELETE FROM hasSC WHERE ProgramID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPpStmt = connection.prepareStatement(deleteHasScStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            secondPpStmt.setInt(1, id);
            secondPpStmt.executeUpdate();

            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a StudyProgram object from the given ResultSet.
     *
     * @param resultSet The ResultSet containing study program data.
     * @return The StudyProgram object created from the ResultSet.
     * @throws SQLException If a SQL exception occurs.
     */
    @Override
    public StudyProgram createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new StudyProgram(
                resultSet.getInt("ProgramID"),
                resultSet.getString("ProgramName"),
                resultSet.getString("ProgramAbbreviation")
        );
    }
}
