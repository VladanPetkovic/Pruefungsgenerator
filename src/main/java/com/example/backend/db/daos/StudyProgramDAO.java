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
        String insertStmt = "INSERT into StudyPrograms (ProgramName, ProgramAbbreviation) VALUES (?, ?);";
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

        String insertStmt = "SELECT ProgramName, ProgramAbbreviation from StudyPrograms;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                StudyProgram newProgram = new StudyProgram(
                        resultSet.getString(1),
                        resultSet.getString(2));
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
        return null;
    }

    @Override
    public void update(StudyProgram object) {

    }

    @Override
    public void delete(int id) {

    }
}
