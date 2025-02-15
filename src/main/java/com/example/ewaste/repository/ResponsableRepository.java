package com.example.ewaste.repository;


import com.example.ewaste.entities.Responsable;
import com.example.ewaste.interfaces.EntityCrud;

import java.util.List;

public class ResponsableRepository implements EntityCrud<Responsable> {
    @Override
    public void addEntity(Responsable responsable) {

    }

    @Override
    public void updateEntity(Responsable responsable) {

    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public List<Responsable> displayEntity() {
        return List.of();
    }

    @Override
    public Responsable display(int id) {
        return null;
    }
}
