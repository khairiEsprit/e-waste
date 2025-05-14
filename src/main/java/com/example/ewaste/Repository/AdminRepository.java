package com.example.ewaste.Repository;


import com.example.ewaste.Entities.Admin;
import com.example.ewaste.Interfaces.EntityCrud;

import java.util.List;

public class AdminRepository implements EntityCrud<Admin> {
    @Override
    public void addEntity(Admin admin) {

    }

    @Override
    public void updateEntity(Admin admin) {

    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public List<Admin> displayEntity() {
        return List.of();
    }

    @Override
    public Admin display(int id) {
        return null;
    }
}
