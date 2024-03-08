package com.esprit.services;

import java.util.List;

public interface IService<T> {

    public void create(T t);
    public List<T> read();
    public void update(T t);
    public void delete(T t);
}
