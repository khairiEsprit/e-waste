package com.example.ewaste.repository;

import com.example.hellofx.Entities.Admin;
import com.example.hellofx.Interfaces.EntityCrud;

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
