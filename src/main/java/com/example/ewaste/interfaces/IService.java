package com.example.ewaste.interfaces;

import com.example.ewaste.Models.Historique_Poubelle;

import java.sql.SQLException;
import java.util.List;

        public interface IService<T> {
        void ajouter (T m) throws SQLException;
        void supprimer (int id) throws SQLException;
        void modifier (T m) throws SQLException;
        List<T> recuperer() throws SQLException;
    }

