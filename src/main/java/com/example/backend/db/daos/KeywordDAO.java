package com.example.backend.db.daos;

import com.example.backend.db.models.Keyword;
import com.example.backend.db.models.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KeywordDAO implements DAO<Keyword> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<Keyword> keywordCache;

    @Override
    public void create(Keyword keyword) {
        String insertStmt = "INSERT INTO Keywords (Keyword) VALUES (?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, keyword.getKeyword_text());
            preparedStatement.execute();
            getConnection().close();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Keyword> readAll() {
        ArrayList<Keyword> keywords = new ArrayList<>();

        String selectStmt = "SELECT KeywordID, Keyword FROM Keywords;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Keyword newKeyword = new Keyword(
                        resultSet.getInt(1),
                        resultSet.getString(2)
                );
                keywords.add(newKeyword);
            }
            setKeywordCache(keywords);
            getConnection().close();
            return keywords;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Keyword> readAllForOneQuestion(int question_id) {
        ArrayList<Keyword> keywords = new ArrayList<>();

        String selectStmt = "SELECT KeywordID, Keyword FROM Keywords;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, question_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Keyword newKeyword = new Keyword(
                        resultSet.getInt(1),
                        resultSet.getString(2)
                );
                keywords.add(newKeyword);
            }
            setKeywordCache(keywords);
            getConnection().close();
            return keywords;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Keyword read(int id) {
        Keyword keyword = null;

        String readStmt =
            "SELECT KeywordID, Keyword " +
            "FROM Keywords " +
            "WHERE KeywordID = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(readStmt);
            preparedStatement.setInt(1, id);

            ResultSet result = preparedStatement.executeQuery();

            if(result.next()) {
                keyword = new Keyword(
                        result.getInt(1),
                        result.getString(2)
                );
            }

            getConnection().close();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return keyword;
    }

    @Override
    public void update(Keyword keyword) {
        String updateStmt = "UPDATE Keywords SET Keyword = ? WHERE KeywordID = ?";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, keyword.getKeyword_text());
            preparedStatement.setInt(2, keyword.getKeyword_id());
            preparedStatement.execute();
            getConnection().close();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Keywords WHERE KeywordID = ?;";
        String deleteHasKQStmt = "DELETE FROM hasKQ WHERE KeywordID = ?;";
        try {
            // deleting from Keywords table
            PreparedStatement preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            // deleting from hasKQ table
            PreparedStatement secondPpStmt = getConnection().prepareStatement(deleteHasKQStmt);
            secondPpStmt.setInt(1, id);
            secondPpStmt.execute();

            getConnection().close();
            setKeywordCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
