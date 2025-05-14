package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Centre;
import com.example.ewaste.Entities.Contrat;
import com.example.ewaste.Entities.PlannificationTache;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class ContratRepository implements IService<Contrat> {
    private Connection connection;

    public ContratRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Contrat contrat) throws SQLException {
        String sql = "INSERT INTO `contrat`(`centre_id`, `employe_id`, `date_debut`, `date_fin`, `signature_path`) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contrat.getCentreId());
            ps.setInt(2, contrat.getEmployeId());
            ps.setDate(3, Date.valueOf(contrat.getDateDebut()));
            ps.setDate(4, Date.valueOf(contrat.getDateFin()));
            ps.setString(5, contrat.getSignaturePath());
            ps.executeUpdate();
        }
    }

    @Override
    public void modifier(Contrat contrat) throws SQLException {
        String sql = "UPDATE `contrat` SET `centre_id`=?, `employe_id`=?, `date_debut`=?, `date_fin`=?, `signature_path`=? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contrat.getCentreId());
            ps.setInt(2, contrat.getEmployeId());
            ps.setDate(3, Date.valueOf(contrat.getDateDebut()));
            ps.setDate(4, Date.valueOf(contrat.getDateFin()));
            ps.setString(5, contrat.getSignaturePath());
            ps.setInt(6, contrat.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<PlannificationTache> recuperer() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `contrat` WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Contrat> afficher() throws SQLException {
        List<Contrat> contrats = new ArrayList<>();
        String sql = "SELECT * FROM `contrat`";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                contrats.add(new Contrat(
                        rs.getInt("id"),
                        rs.getInt("centre_id"),
                        rs.getInt("employe_id"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getString("signature_path")
                ));
            }
        }
        return contrats;
    }


    public boolean existeContrat(int centreId, int employeId, LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        String sql = "SELECT * FROM `contrat` WHERE `centre_id` = ? AND `employe_id` = ? AND `date_debut` = ? AND `date_fin` = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, centreId);
            ps.setInt(2, employeId);
            ps.setDate(3, Date.valueOf(dateDebut));
            ps.setDate(4, Date.valueOf(dateFin));
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public Integer getCentreIdByName(String centreNom) throws SQLException {
        String query = "SELECT id FROM centre WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, centreNom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return null;
        }
    }

    public List<Centre> getCentres() throws SQLException {
        List<Centre> centres = new ArrayList<>();
        String query = "SELECT id, nom FROM centre";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                centres.add(new Centre(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return centres;
    }

    public List<String> getEmployeNames() throws SQLException {
        List<String> employeNames = new ArrayList<>();
        String query = "SELECT first_name, last_name FROM user";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                employeNames.add(fullName);
            }
        }
        return employeNames;
    }

    public List<String> getEmployeNamesPren() throws SQLException {
        return getEmployeNames(); // Reuse getEmployeNames for consistency
    }

    public List<String> getEmployeNamess() throws SQLException {
        return getEmployeNames(); // Reuse getEmployeNames for consistency
    }

    public Integer getEmployeIdByName(String fullName) throws SQLException {
        String[] names = fullName.split(" ", 2);
        if (names.length < 2) return null;
        String firstName = names[0];
        String lastName = names[1];
        String query = "SELECT id FROM user WHERE first_name = ? AND last_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return null;
    }

    public String getCentreNameById(int centreId) throws SQLException {
        String query = "SELECT nom FROM centre WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, centreId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nom");
            }
        }
        return null;
    }

    public String getEmployeNameById(int employeId) throws SQLException {
        String query = "SELECT last_name FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, employeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("last_name");
            }
        }
        return null;
    }

    public String getEmployePrenameById(int employeId) throws SQLException {
        String query = "SELECT first_name FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, employeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("first_name");
            }
        }
        return null;
    }

    public LocalDate[] getContratDatesById(int contratId) throws SQLException {
        String query = "SELECT date_debut, date_fin FROM contrat WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, contratId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new LocalDate[]{
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate()
                };
            }
        }
        return null;
    }

    public int getLastInsertedContratId() throws SQLException {
        String sql = "SELECT MAX(id) FROM contrat";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void updateSignaturePath(int contratId, String signaturePath) throws SQLException {
        String sql = "UPDATE contrat SET signature_path = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, signaturePath);
            stmt.setInt(2, contratId);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Mise à jour du path : " + rowsUpdated + " ligne(s) affectée(s)");
        }
    }

    public String getEmployeEmailById(int idEmploye) throws SQLException {
        String query = "SELECT email FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idEmploye);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");
                System.out.println(email != null ? "Email trouvé : " + email : "Aucun email trouvé pour l'employé avec l'ID : " + idEmploye);
                return email;
            }
            System.out.println("Aucun employé trouvé avec l'ID : " + idEmploye);
        }
        return null;
    }

    public String getEmployTelephoneById(int idEmploye) throws SQLException {
        String query = "SELECT phone FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idEmploye);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String phone = rs.getString("phone");
                System.out.println(phone != null ? "Téléphone trouvé : " + phone : "Aucun téléphone trouvé pour l'employé avec l'ID : " + idEmploye);
                return phone;
            }
            System.out.println("Aucun employé trouvé avec l'ID : " + idEmploye);
        }
        return null;
    }

    public Contrat getContratById(int id) throws SQLException {
        String query = "SELECT * FROM contrat WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Contrat contrat = new Contrat();
                contrat.setId(rs.getInt("id"));
                contrat.setCentreId(rs.getInt("centre_id"));
                contrat.setEmployeId(rs.getInt("employe_id"));
                contrat.setDateDebut(rs.getDate("date_debut").toLocalDate());
                contrat.setDateFin(rs.getDate("date_fin").toLocalDate());
                contrat.setSignaturePath(rs.getString("signature_path"));
                return contrat;
            }
        }
        return null;
    }
}