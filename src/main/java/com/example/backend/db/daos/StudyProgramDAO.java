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

public class StudyProgramDAO implements DAO<StudyProgram> {

    @Setter(AccessLevel.PRIVATE)
    ArrayList<StudyProgram> studyProgramCache;

    @Override
    public void create(StudyProgram program) {
        String insertStmt = "INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation) VALUES (?, ?);";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setString(1, program.getProgram_name());
            preparedStatement.setString(2, program.getProgram_abbr());
            preparedStatement.execute();
            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public void update(StudyProgram program) {
        String updateStmt = "UPDATE StudyPrograms SET ProgramName = ?, ProgramAbbreviation = ? WHERE ProgramID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {

            preparedStatement.setString(1, program.getProgram_name());
            preparedStatement.setString(2, program.getProgram_abbr());
            preparedStatement.setInt(3, program.getProgram_id());
            preparedStatement.execute();
            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM StudyPrograms WHERE ProgramID = ?;";
        String deleteHasScStmt = "DELETE FROM hasSC WHERE ProgramID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPpStmt = connection.prepareStatement(deleteHasScStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.execute();

            secondPpStmt.setInt(1, id);
            secondPpStmt.execute();

            setStudyProgramCache(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public StudyProgram createModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new StudyProgram(
                resultSet.getInt("ProgramID"),
                resultSet.getString("ProgramName"),
                resultSet.getString("ProgramAbbreviation")
        );
    }
}
