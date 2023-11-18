package com.example.backend.db.repository;

import java.util.ArrayList;

public interface Repository<T> {
    ArrayList<T> getAll();
    T get(int id);
    void add(T type);
    void update(T type);
    void remove(T type);
}