package com.example.backend.db.daos;

import java.util.ArrayList;

public interface DAO<T> {
    void create(T question);
    ArrayList<T> readAll(String subject);
    T read(int id);
    void update(T question);
    void delete(int id);
}
