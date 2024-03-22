package com.esprit.services;

import java.util.List;

public interface IService<T> {

    void create(T t);

    List<T> read();

    void update(T t);

    void delete(T t);
}
