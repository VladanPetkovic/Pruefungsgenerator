package com.example.backend.db.daos;

import com.example.backend.db.models.StudyProgram;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;

public class StudyProgramDAO implements DAO<StudyProgram> {
    // TODO: change the location of the connectionString --> make a class for databaseaccess
    private String connectionString = "jdbc:sqlite:C:/Users/vlada/Documents/FH_Vladan_Petkovic/3.Semester/Innovation_project/Pruefungsgenerator/sqlite/project_database.db";
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<StudyProgram> studyProgramCache;

    public StudyProgramDAO() {
        try {
            this.connection = DriverManager.getConnection(connectionString);
            setConnection(connection);
            System.out.println("success");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }

    @Override
    public void create(StudyProgram program) {
        String insertStmt =
                "INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation) VALUES (?, ?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, program.getProgram_name());
            preparedStatement.setString(2, program.getProgram_abbr());
            preparedStatement.execute();
            getConnection().close();
            setStudyProgramCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<StudyProgram> readAll() {
        ArrayList<StudyProgram> programs = new ArrayList<>();

        String insertStmt =
                "SELECT ProgramID, ProgramName, ProgramAbbreviation " +
                "FROM StudyPrograms;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                StudyProgram newProgram = new StudyProgram(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3));
                programs.add(newProgram);
            }
            setStudyProgramCache(programs);
            getConnection().close();
            return programs;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public StudyProgram read(int id) {
        StudyProgram program = null;

        String readStmt =
                "SELECT ProgramID, ProgramName, ProgramAbbreviation " +
                "FROM StudyPrograms " +
                "WHERE ProgramID = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(readStmt);
            preparedStatement.setInt(1, id);

            ResultSet result = preparedStatement.executeQuery();

            if(result.next()) {
                program = new StudyProgram(
                    result.getInt(1),
                    result.getString(2),
                    result.getString(3)
                );
            }

            getConnection().close();
            setStudyProgramCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return program;
    }

    @Override
    public void update(StudyProgram program) {
        String updateStmt =
                "UPDATE StudyPrograms " +
                "SET ProgramName = ?, ProgramAbbreviation = ? " +
                "WHERE ProgramID = ?";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, program.getProgram_name());
            preparedStatement.setString(2, program.getProgram_abbr());
            preparedStatement.setInt(3, program.getProgram_id());
            preparedStatement.execute();
            getConnection().close();
            setStudyProgramCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM StudyPrograms WHERE ProgramID = ?;";
        String deleteHasScStmt = "DELETE FROM hasSC WHERE ProgramID = ?;";
        try {
            // deleting from StudyPrograms table
            PreparedStatement preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            // deleting from hasSC table
            PreparedStatement secondPpStmt = getConnection().prepareStatement(deleteHasScStmt);
            secondPpStmt.setInt(1, id);
            secondPpStmt.execute();

            getConnection().close();
            setStudyProgramCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
