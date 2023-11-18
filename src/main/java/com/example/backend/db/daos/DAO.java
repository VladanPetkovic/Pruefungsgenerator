package com.example.backend.db.daos;

import java.util.ArrayList;

public interface DAO<T> {
    void create(T question);
    ArrayList<T> readAll();
    T read(int id);
    void update(T type);
    void delete(int id);
}
