package com.maxkavun.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {

    List<T> findAll();

    Optional<T> findById(K id);

    boolean deleteById(K id);

    void update(T model);

    T save(T model);
}

