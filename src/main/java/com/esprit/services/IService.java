package com.esprit.services;

import com.esprit.models.Evenement;


import java.util.List;

public interface IService<T> {

    public void add(T t);
    public void update(T t);
    public void delete(T t);
    public List<T> show();
}
