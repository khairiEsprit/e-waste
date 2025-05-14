package com.example.ewaste.Repository;

import com.example.ewaste.Entities.capteur;
import com.example.ewaste.Entities.etat;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Entities.PlanificationTache;
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
    public List<PlanificationTache> afficher(int id_centre) throws SQLException {
        return List.of();
    }

    @Override
    public List<poubelle> afficher() throws SQLException {
        return recuperer();
    }
    // Méthode pour créer un centre par défaut si aucun centre n'existe
    private int getOrCreateDefaultCentre() throws SQLException {
        // Vérifier s'il existe déjà un centre par défaut
        String checkSql = "SELECT id FROM centre WHERE nom = 'Centre par défaut' LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        // Si aucun centre par défaut n'existe, en créer un
        String insertSql = "INSERT INTO centre (nom, longitude, altitude, telephone, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Centre par défaut");
            ps.setFloat(2, 0.0f);  // Longitude par défaut
            ps.setFloat(3, 0.0f);  // Latitude par défaut
            ps.setInt(4, 0);       // Téléphone par défaut
            ps.setString(5, "default@example.com"); // Email par défaut
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Impossible de créer un centre par défaut");
    }

    @Override
    public void ajouter(poubelle p) throws SQLException {
        System.out.println("Début de l'ajout de la poubelle dans la base de données");
        System.out.println("Centre ID: " + p.getId_centre());

        // Si l'ID du centre est 0 ou négatif, utiliser un centre par défaut
        if (p.getId_centre() <= 0) {
            int defaultCentreId = getOrCreateDefaultCentre();
            p.setId_centre(defaultCentreId);
            System.out.println("Utilisation du centre par défaut avec ID: " + defaultCentreId);
        } else {
            // Vérifier si le centre existe
            String checkCentreSql = "SELECT id FROM centre WHERE id = ?";
            PreparedStatement checkPs = connection.prepareStatement(checkCentreSql);
            checkPs.setInt(1, p.getId_centre());
            ResultSet checkRs = checkPs.executeQuery();

            if (!checkRs.next()) {
                throw new SQLException("Le centre avec l'ID " + p.getId_centre() + " n'existe pas dans la base de données.");
            }
        }

        String sql = "INSERT INTO poubelle (centre_id, adresse, niveau, etat, " +
                     "date_installation, hauteur_totale, latitude, longitude, revenu_genere) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        System.out.println("SQL: " + sql);

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, p.getId_centre());

        ps.setString(2, p.getAdresse());
        ps.setInt(3, p.getNiveau());
        ps.setString(4, p.getEtat().toString());
        ps.setDate(5, new java.sql.Date(p.getDate_installation().getTime()));
        ps.setInt(6, p.getHauteurTotale());
        ps.setDouble(7, p.getLatitude());
        ps.setDouble(8, p.getLongitude());
        ps.setDouble(9, p.getRevenu_genere());

        System.out.println("Exécution de la requête SQL...");
        ps.executeUpdate();
        System.out.println("Requête exécutée avec succès");

        // Récupérer l'ID généré pour la poubelle
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int idPoubelle = rs.getInt(1);
            System.out.println("ID de la poubelle générée: " + idPoubelle);

            // Ajouter un capteur par défaut pour cette poubelle
            ajouterCapteurParDefaut(idPoubelle, p.getHauteurTotale());
        } else {
            System.out.println("Aucun ID généré pour la poubelle");
        }
    }    @Override
    public void supprimer(int id) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // 1. Supprimer l'historique
            String deleteHistoriqueSql = "DELETE FROM historique WHERE poubelle_id = ?";
            PreparedStatement psHistorique = connection.prepareStatement(deleteHistoriqueSql);
            psHistorique.setInt(1, id);
            psHistorique.executeUpdate();

            // 2. Supprimer les capteurs de pression (capteurp)
            String deleteCapteursPressSql = "DELETE FROM capteurp WHERE poubelle_id = ?";
            PreparedStatement psCapteursPress = connection.prepareStatement(deleteCapteursPressSql);
            psCapteursPress.setInt(1, id);
            psCapteursPress.executeUpdate();

            // 3. Supprimer les capteurs normaux
            String deleteCapteursSql = "DELETE FROM capteur WHERE poubelle_id = ?";
            PreparedStatement psCapteurs = connection.prepareStatement(deleteCapteursSql);
            psCapteurs.setInt(1, id);
            psCapteurs.executeUpdate();

            // 4. Finalement, supprimer la poubelle
            String deletePoubelleSQL = "DELETE FROM poubelle WHERE id = ?";
            PreparedStatement psPoubelle = connection.prepareStatement(deletePoubelleSQL);
            psPoubelle.setInt(1, id);
            psPoubelle.executeUpdate();

            // Si tout s'est bien passé, on valide la transaction
            connection.commit();
        } catch (SQLException e) {
            // En cas d'erreur, on annule toutes les opérations
            connection.rollback();
            throw e;
        } finally {
            // On remet l'autocommit à true
            connection.setAutoCommit(true);
        }
    }    @Override
    public void modifier(poubelle p) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Si l'ID du centre est 0 ou négatif, utiliser un centre par défaut
            if (p.getId_centre() <= 0) {
                int defaultCentreId = getOrCreateDefaultCentre();
                p.setId_centre(defaultCentreId);
                System.out.println("Utilisation du centre par défaut avec ID: " + defaultCentreId);
            }

            String sql = "UPDATE poubelle SET centre_id = ?, adresse = ?, niveau = ?, etat = ?, date_installation = ?, " +
                        "hauteur_totale = ?, latitude = ?, longitude = ?, revenu_genere = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, p.getId_centre());
            ps.setString(2, p.getAdresse());
            ps.setInt(3, p.getNiveau());
            ps.setString(4, p.getEtat().toString());
            ps.setDate(5, new java.sql.Date(p.getDate_installation().getTime()));
            ps.setInt(6, p.getHauteurTotale());
            ps.setDouble(7, p.getLatitude());
            ps.setDouble(8, p.getLongitude());
            ps.setDouble(9, p.getRevenu_genere());
            ps.setInt(10, p.getId());

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

    // Méthode pour récupérer le nom du centre
    public String getNomCentre(int centreId) throws SQLException {
        String sql = "SELECT nom FROM centre WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, centreId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("nom");
        }
        return "Centre inconnu";
    }

    @Override
    public List<poubelle> recuperer() throws SQLException {
        List<poubelle> poubelles = new ArrayList<>();
        String sql = "SELECT p.*, c.nom as nom_centre FROM poubelle p LEFT JOIN centre c ON p.centre_id = c.id";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            poubelle p = new poubelle();
            p.setId(rs.getInt("id"));
            p.setId_centre(rs.getInt("centre_id")); // Changed from id_centre to centre_id
            p.setNomCentre(rs.getString("nom_centre")); // Nouveau champ pour le nom du centre
            p.setAdresse(rs.getString("adresse"));
            p.setNiveau(rs.getInt("niveau"));
            p.setEtat(etat.fromString(rs.getString("etat")));
            p.setDate_installation(rs.getTimestamp("date_installation"));
            p.setHauteurTotale(rs.getInt("hauteur_totale"));
            if (rs.getObject("latitude") != null) p.setLatitude(rs.getDouble("latitude"));
            if (rs.getObject("longitude") != null) p.setLongitude(rs.getDouble("longitude"));
            if (rs.getObject("revenu_genere") != null) p.setRevenu_genere(rs.getDouble("revenu_genere"));
            poubelles.add(p);
        }
        return poubelles;
    }

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
            return new poubelle(
                rs.getInt("id"),
                rs.getString("adresse"),
                rs.getInt("niveau"),
                etat.valueOf(rs.getString("etat")),
                rs.getDate("date_installation"),
                rs.getInt("hauteur_totale")  // Changed from hauteurTotale to hauteur_totale
            );
        }
        return null;
    }
    private void ajouterCapteurParDefaut(int idPoubelle, int hauteurTotale) throws SQLException {
        CapteurRepository capteurRepo = new CapteurRepository();
        capteur c = new capteur();
        c.setPoubelle_id(idPoubelle);
        c.setDistance_mesuree(hauteurTotale); // Par défaut, la distance mesurée est égale à la hauteur totale (poubelle vide)
        c.setPortee_maximale(150.0); // Valeur par défaut
        c.setPrecision_capteur(1.0); // Valeur par défaut
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
        String sql = "SELECT centre_id, SUM(quantite_dechets) AS total_dechets " +
                "FROM historique " +
                "GROUP BY centre_id";
        Map<Integer, Float> quantiteDechetsParCentre = new HashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quantiteDechetsParCentre.put(rs.getInt("centre_id"), rs.getFloat("total_dechets"));
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
}
