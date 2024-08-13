package com.example.application.backend.db.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DAO<T> {
    void create(T object);
    ArrayList<T> readAll();
    T read(int id);
    void update(T object);
    void delete(int id);
    T createModelFromResultSet(ResultSet resultSet) throws SQLException;
}
