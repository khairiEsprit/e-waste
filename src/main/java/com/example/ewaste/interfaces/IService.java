package com.example.ewaste.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    void ajouter(T t) throws SQLException;
    void modifier(T t)throws SQLException;
    void supprimer(int id)throws SQLException;
    List<T> afficher(int idCentre) throws SQLException;
    //List<T> afficherParEmploye(int idCentre, int idEmploye) throws SQLException;

}
