package com.example.ewaste.repository;

import com.example.hellofx.Entities.Employee;
import com.example.hellofx.Interfaces.EntityCrud;

import java.util.List;

public class EmployeeRepository implements EntityCrud<Employee> {
    @Override
    public void addEntity(Employee employee) {

    }

    @Override
    public void updateEntity(Employee employee) {

    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public List<Employee> displayEntity() {
        return List.of();
    }

    @Override
    public Employee display(int id) {
        return null;
    }
}
