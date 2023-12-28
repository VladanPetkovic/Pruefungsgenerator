package com.example.backend.db.daos;

import java.util.ArrayList;

public interface DAO<T> {
    void create(T object);
    ArrayList<T> readAll();
    T read(int id);
    void update(T object);
    void delete(int id);
}
