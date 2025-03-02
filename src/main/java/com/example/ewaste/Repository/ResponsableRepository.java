package com.example.ewaste.Repository;


import com.example.ewaste.Entities.Responsable;
import com.example.ewaste.Interfaces.EntityCrud;

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
