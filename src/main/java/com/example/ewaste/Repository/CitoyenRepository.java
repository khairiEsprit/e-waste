package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Citoyen;
import com.example.ewaste.Interfaces.EntityCrud;
import com.example.ewaste.Utils.DataBase;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CitoyenRepository implements EntityCrud<Citoyen> {

    private final Connection conn = DataBase.getInstance().getConnection();

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

    public int getCitoyenCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE role = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, "CITOYEN");

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1); // Retrieve the count value
        }
        return 0; // Return 0 if no records found
    }
}
