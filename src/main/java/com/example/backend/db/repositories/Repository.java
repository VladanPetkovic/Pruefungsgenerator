package com.example.backend.db.repositories;

import java.io.IOException;
import java.util.ArrayList;

public interface Repository<T> {
    ArrayList<T> getAll();
    T get(int id);
    void add(T type) throws IOException;
    void update(T type);
    void remove(T type);
}