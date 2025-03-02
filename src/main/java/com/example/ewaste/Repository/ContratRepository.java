package com.example.ewaste.Repository;



import com.example.ewaste.Entities.Centre;
import com.example.ewaste.Entities.Contrat;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContratRepository implements IService<Contrat> {
    private Connection connection;

    public ContratRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Contrat contrat) throws SQLException {
        String sql = "INSERT INTO `contrat`(`id_centre`, `id_employe`, `date_debut`, `date_fin`, `signaturePath`) VALUES ('" + contrat.getIdCentre() + "','" + contrat.getIdEmploye() + "','" + contrat.getDateDebut() + "','" + contrat.getDateFin() + "','" + contrat.getSignaturePath() + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    @Override
    public void modifier(Contrat contrat) throws SQLException {
        String sql = "UPDATE `contrat` SET `id_centre`=?,`id_employe`=?,`date_debut`=?,`date_fin`=?,`signaturePath`=?   WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, contrat.getIdCentre());
        ps.setInt(2, contrat.getIdEmploye());
        ps.setDate(3, Date.valueOf(contrat.getDateDebut()));
        ps.setDate(4, Date.valueOf(contrat.getDateFin()));
        ps.setString(5, contrat.getSignaturePath());
        ps.setInt(6, contrat.getId());

        ps.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `contrat` WHERE id =?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();

    }

    @Override
    public List<Contrat> afficher() throws SQLException {
        List<Contrat> contrats = new ArrayList<>();
        String sql = " SELECT * FROM `contrat`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            contrats.add(new Contrat(rs.getInt("id"), rs.getInt("id_centre"), rs.getInt("id_employe"), rs.getDate("date_debut").toLocalDate(), rs.getDate("date_fin").toLocalDate(), rs.getString("signaturePath")));
        }
        return contrats;
    }


    public boolean existeContrat(int idCentre, int idEmploye, LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        String sql = "SELECT * FROM `contrat` WHERE `id_centre` = ? AND `id_employe` = ? AND `date_debut` = ? AND `date_fin` = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idCentre);
        ps.setInt(2, idEmploye);
        ps.setDate(3, Date.valueOf(dateDebut));
        ps.setDate(4, Date.valueOf(dateFin));

        ResultSet rs = ps.executeQuery();

        return rs.next();
    }

    public Integer getCentreIdByName(String centreNom) throws SQLException {
        // Créez votre requête SQL pour récupérer l'ID en fonction du nom du centre
        String query = "SELECT id FROM centre WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, centreNom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return null; // Si aucun centre n'est trouvé, retournez null
            }
        }
    }

    public List<Centre> getCentres() throws SQLException {
        List<Centre> centres = new ArrayList<>();
        String query = "SELECT id, nom FROM centre"; // Requête pour récupérer l'id et le nom des centres
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                centres.add(new Centre(id, nom)); // Créez un objet Centre et ajoutez-le à la liste
            }
        }
        return centres; // Retourne la liste des centres
    }
    public List<String> getEmployeNames() throws SQLException {
        List<String> employeNames = new ArrayList<>();
        String query = "SELECT nom FROM utilisateur WHERE role = 'EMPLOYE'"; // Récupère tous les noms des utilisateurs

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employeNames.add(rs.getString("nom"));
            }
        }
        return employeNames;
    }
    public List<String> getEmployeNamess() throws SQLException {
        List<String> employeNames = new ArrayList<>();
        String query = "SELECT nom, prenom FROM utilisateur WHERE role = 'EMPLOYE'"; // Récupère nom et prénom des employés

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Récupérer nom et prénom de l'employé
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");

                // Ajouter "Prénom Nom" à la liste
                employeNames.add(prenom + " " + nom);
            }
        }
        return employeNames;
    }


    public Integer getEmployeIdByName(String nom) throws SQLException {
        String query = "SELECT id FROM utilisateur WHERE nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nom);
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
        String query = "SELECT nom FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, employeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nom");
            }
        }
        return null;
    }
    public String getEmployePrenameById(int employeId) throws SQLException {
        String query = "SELECT prenom FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, employeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("prenom");
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
                Date dateDebut = rs.getDate("date_debut");
                Date dateFin = rs.getDate("date_fin");

                // Retourne les dates sous forme de tableau LocalDate
                return new LocalDate[]{dateDebut.toLocalDate(), dateFin.toLocalDate()};
            }
        }
        return null;
    }



    public int getLastInsertedContratId() throws SQLException {
        String sql = "SELECT MAX(id) FROM contrat"; // Récupérer le dernier ID inséré (alternative)

        try (Connection conn = DataBase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1); // Retourne l'ID du dernier contrat inséré
            }
        }

        return -1; // Retourne -1 si aucun ID trouvé

    }

    public void updateSignaturePath(int contratId, String signaturePath) throws SQLException {
        String sql = "UPDATE contrat SET signaturePath = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, signaturePath);
            stmt.setInt(2, contratId);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Mise à jour du path : " + rowsUpdated + " ligne(s) affectée(s)"); // 🔹 Debug
        }
    }

    public String getEmployeEmailById(int idEmploye) throws SQLException {
        String email = null;
        String query = "SELECT email FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idEmploye);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Now you only call rs.next() once, and it will return true if a row exists
                    email = rs.getString("email");
                    if (email != null) {
                        System.out.println("Email trouvé : " + email);
                    } else {
                        System.out.println("Aucun email trouvé pour l'employé avec l'ID : " + idEmploye);
                    }
                } else {
                    System.out.println("Aucun employé trouvé avec l'ID : " + idEmploye);
                }
            }
        }
        return email;
    }
    public String getEmployTelephoneById(int idEmploye) throws SQLException {
        String telephone = null;
        String query = "SELECT telephone FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idEmploye);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Now you only call rs.next() once, and it will return true if a row exists
                    telephone = rs.getString("telephone");
                    if (telephone != null) {
                        System.out.println("telephone trouvé : " + telephone);
                    } else {
                        System.out.println("Aucun telephone trouvé pour l'employé avec l'ID : " + idEmploye);
                    }
                } else {
                    System.out.println("Aucun employé trouvé avec l'ID : " + idEmploye);
                }
            }
        }
        return telephone;
    }

    public Contrat getContratById(int id) throws SQLException {
        String query = "SELECT * FROM Contrat WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Contrat contrat = new Contrat();
                contrat.setId(resultSet.getInt("id"));
                contrat.setIdCentre(resultSet.getInt("id_centre"));
                contrat.setIdEmploye(resultSet.getInt("id_employe"));
                contrat.setDateDebut(resultSet.getDate("date_debut").toLocalDate());
                contrat.setDateFin(resultSet.getDate("date_fin").toLocalDate());
                contrat.setSignaturePath(resultSet.getString("signature_path")); // Récupérer le chemin de la signature
                return contrat;
            }
        }
        return null;
    }








}

