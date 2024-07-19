package com.example.backend.db.services;

import java.util.List;

public interface Service<T> {
    T add(T type);
    T getById(Long id);
    List<T> getAll();
    T update(T type);
    void remove(T type);
}
