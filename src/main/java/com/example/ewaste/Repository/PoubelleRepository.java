package com.example.ewaste.Repository;

import com.example.ewaste.Entities.*;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoubelleRepository implements IService<poubelle>
{

    private Connection connection;

    public PoubelleRepository (){
        connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void ajouter(poubelle p) throws SQLException {
        String sql = "INSERT INTO poubelle (id_centre, adresse, niveau, etat, date_installation, hauteur_totale) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, p.getId_centre());
        ps.setString(2, p.getAdresse());
        ps.setInt(3, p.getNiveau());
        ps.setString(4, p.getEtat().toString());
        ps.setDate(5, new java.sql.Date(p.getDate_installation().getTime()));
        ps.setInt(6, p.getHauteurTotale());
        ps.executeUpdate();

        // Récupérer l'ID généré pour la poubelle
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int idPoubelle = rs.getInt(1);

            // Ajouter un capteur par défaut pour cette poubelle
            ajouterCapteurParDefaut(idPoubelle, p.getHauteurTotale());
        }
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM poubelle WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<poubelle> afficher() throws SQLException {
        return List.of();
    }

    @Override
    public List<PlanificationTache> afficher(int id_centre) throws SQLException {
        return List.of();
    }

    @Override
    public void modifier(poubelle p) throws SQLException {
        try {
            String sql = "UPDATE poubelle SET id_centre = ?, adresse = ?, niveau = ?, etat = ?, date_installation = ? ,hauteur_totale=?  WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, p.getId_centre());
            ps.setString(2, p.getAdresse());
            ps.setInt(3, p.getNiveau());
            ps.setString(4, p.getEtat().toString());
            ps.setDate(5, new java.sql.Date(p.getDate_installation().getTime()));
            ps.setInt(6,p.getHauteurTotale());
            ps.setInt(7, p.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("La poubelle a été modifiée avec succès.");
            } else {
                System.out.println("Aucune poubelle trouvée avec l'ID spécifié.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la poubelle : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<PlanificationTache> recuperer() throws SQLException {
        return List.of();
    }


    public List<poubelle> recupererr() throws SQLException {
        String sql = "SELECT * FROM poubelle";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<poubelle> poubelles = new ArrayList<>();
        while (rs.next()) {
            poubelle p = new poubelle(
                    rs.getInt("id_centre"),
                    rs.getString("adresse"),
                    rs.getInt("niveau"),
                    etat.valueOf(rs.getString("etat")),
                    rs.getDate("date_installation"),
                    rs.getInt("hauteur_totale")
            );
            p.setId(rs.getInt("id"));
            poubelles.add(p);

        }
        return poubelles;
    }
//    public void viderPoubelle(int poubelleId) throws SQLException {
//        CapteurRepository capteurRepo = new CapteurRepository();
//        PoubelleRepository poubelleRepo = new PoubelleRepository();
//
//        // Récupérer la hauteur totale
//        poubelle p = poubelleRepo.getById(poubelleId);
//
//        // Créer une mesure correspondant à une poubelle vide
//        capteur mesureVide = new capteur(173, 42.0f, Timestamp.valueOf(LocalDateTime.now()));        mesureVide.setId_poubelle(poubelleId);
//        mesureVide.setDistance_mesuree(p.getHauteurTotale());
//        mesureVide.setDate_mesure( Timestamp.valueOf(LocalDateTime.now()));
//
//        capteurRepo.ajouter(mesureVide);
//    }
public int recupererDernierIdPoubelle() throws SQLException {
    String sql = "SELECT MAX(id) FROM poubelle";
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            throw new SQLException("Aucune poubelle trouvée.");
        }
    }
}
    public poubelle getById(int id) throws SQLException {
        String sql = "SELECT * FROM poubelle WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            poubelle p = new poubelle(
                    rs.getInt("id_centre"),
                    rs.getString("adresse"),
                    rs.getInt("niveau"),
                    etat.valueOf(rs.getString("etat")),
                    rs.getDate("date_installation"),
                    rs.getInt("hauteur_totale")
            );
            p.setId(rs.getInt("id"));
            return p;
        }
        return null;
    }


    private void ajouterCapteurParDefaut(int idPoubelle, int hauteurTotale) throws SQLException {
        CapteurRepository capteurRepo = new CapteurRepository();
        capteur c = new capteur();
        c.setId_poubelle(idPoubelle);
        c.setDistance_mesuree(hauteurTotale); // Par défaut, la distance mesurée est égale à la hauteur totale (poubelle vide)
        c.setPorteeMaximale(150.0f); // Valeur par défaut
        c.setPrecision(1.0f); // Valeur par défaut
        capteurRepo.ajouter(c);
    }
    public Map<String, Integer> recupererCentres() throws SQLException {
        String sql = "SELECT id, nom FROM centre"; // Supposons que la table s'appelle "centre"
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        Map<String, Integer> centres = new HashMap<>();
        while (rs.next()) {
            centres.put(rs.getString("nom"), rs.getInt("id"));
        }
        return centres;
    }
    public int getTotalPoubelles() throws SQLException {
        String sql = "SELECT COUNT(*) FROM poubelle";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getNbPoubellesParEtat(etat etat) throws SQLException {
        String sql = "SELECT COUNT(*) FROM poubelle WHERE etat = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, etat.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public Map<Integer, Float> getQuantiteDechetsParCentre() throws SQLException {
        String sql = "SELECT id_centre, SUM(quantite_dechets) AS total_dechets " +
                "FROM historique " +
                "GROUP BY id_centre";
        Map<Integer, Float> quantiteDechetsParCentre = new HashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quantiteDechetsParCentre.put(rs.getInt("id_centre"), rs.getFloat("total_dechets"));
            }
        }
        return quantiteDechetsParCentre;
    }
    public float getQuantiteDechetsParPoubelle(int idPoubelle) throws SQLException {
        String query = "SELECT quantite_dechets FROM poubelle WHERE id = ?";
             PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idPoubelle);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getFloat("quantite_dechets");
                } else {
                    throw new SQLException("Poubelle non trouvée avec l'ID: " + idPoubelle);
                }
            }

    }





    private final Connection conn = DataBase.getInstance().getConnection();

    public List<poubelleK> getPoubellesByCenter(int centerId) {
        List<poubelleK> trashBins = new ArrayList<>();
        String sql = "SELECT id, latitude, longitude, fillLevel, isWorking, centerId FROM poubelleK WHERE centerId = ?";

        try (conn;
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, centerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                poubelleK trash = new poubelleK(
                        rs.getInt("id"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("fillLevel"),
                        rs.getBoolean("isWorking"),
                        rs.getInt("centerId")
                );
                trashBins.add(trash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trashBins;
    }

    public String getLastTenPoubellesForOpenAI(int centerId) {
        List<poubelleK> trashBins = new ArrayList<>();
        String sql = "SELECT id, latitude, longitude, fillLevel, isWorking, centerId " +
                "FROM poubelleK " +
                "WHERE centerId = ? " +
                "ORDER BY id DESC LIMIT 10"; // Ensure we get the last 10 records

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, centerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                poubelleK trash = new poubelleK(
                        rs.getInt("id"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("fillLevel"),
                        rs.getBoolean("isWorking"),
                        rs.getInt("centerId")
                );
                trashBins.add(trash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Format the data in a clear structure acceptable for OpenAI
        StringBuilder sb = new StringBuilder();
        for (poubelleK p : trashBins) {
            sb.append(String.format("Bin ID %d: fillLevel=%d%%, isWorking=%s, position=(%.6f, %.6f)%n",
                    p.getId(),
                    p.getFillLevel(),
                    p.isWorking() ? "true" : "false",
                    p.getLatitude(),
                    p.getLongitude()));
        }
        return sb.toString();
    }
}
