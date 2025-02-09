package com.example.ewaste.repository;

import com.example.hellofx.Entities.Citoyen;
import com.example.hellofx.Interfaces.EntityCrud;

import java.util.List;

public class CitoyenRepository implements EntityCrud<Citoyen> {
    @Override
    public void addEntity(Citoyen citoyen) {

    }

    @Override
    public void updateEntity(Citoyen citoyen) {

    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public List<Citoyen> displayEntity() {
        return List.of();
    }

    @Override
    public Citoyen display(int id) {
        return null;
    }
}
