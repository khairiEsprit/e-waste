package com.example.ewaste.Interfaces;

import com.example.ewaste.Entities.PlannificationTache;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {

    void ajouter(T t) throws SQLException;
    void modifier(T t)throws SQLException;

    List<PlannificationTache> recuperer() throws SQLException;

    void supprimer(int id)throws SQLException;
    List<T> afficher() throws SQLException;

    List<T> afficher(int id_centre) throws SQLException;
}
