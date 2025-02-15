    package com.example.ewaste.controllers;

    import com.example.ewaste.utils.DataBase;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Node;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.ListView;
    import javafx.scene.control.TextField;

    import java.io.IOException;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.SQLException;
    import java.util.List;

    import com.example.ewaste.entities.Centre;
    import com.example.ewaste.repository.CentreRepository;
    import javafx.stage.Stage;

    public class AfficherCentreController {

        @FXML
        private ListView<Centre> affichage; // Remplace TableView par ListView

        @FXML
        private TextField TelephoneCentre;

        @FXML
        private TextField AdresseCentre;

        @FXML
        private TextField NomCentre;

        @FXML
        private TextField EmailCentre;

        private CentreRepository centreRepository = new CentreRepository();

        @FXML
        public void initialize() {
            // Charger les données dans la ListView
           /* loadData();

            // Ajouter un écouteur pour mettre à jour les TextFields lorsqu'un élément est sélectionné
            affichage.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    NomCentre.setText(newSelection.getNom());
                    AdresseCentre.setText(newSelection.getAdresse());
                    TelephoneCentre.setText(String.valueOf(newSelection.getTelephone())); // Conversion int → String
                    EmailCentre.setText(newSelection.getEmail());
                }
            });*/
            loadData();

            // Appliquer une ListCell personnalisée
            affichage.setCellFactory(param -> new CentreListCellController());

            // Ajouter un écouteur pour mettre à jour les TextFields lorsqu'un élément est sélectionné
            affichage.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    NomCentre.setText(newSelection.getNom());
                    AdresseCentre.setText(newSelection.getAdresse());
                    TelephoneCentre.setText(String.valueOf(newSelection.getTelephone())); // Conversion int → String
                    EmailCentre.setText(newSelection.getEmail());
                }
            });
        }

        @FXML
        void AfficherCentre(ActionEvent event) {
            // Charger les données quand l'utilisateur clique sur un bouton
            loadData();
        }

        private void loadData() {
            try {
                List<Centre> centreList = centreRepository.afficher();
                ObservableList<Centre> observableCentres = FXCollections.observableArrayList(centreList);
                affichage.setItems(observableCentres);
            } catch (SQLException e) {
                e.printStackTrace(); // Affiche l'erreur en console
            }
        }

        public void Supprimer(ActionEvent actionEvent) {
            Centre centreSelectionne = affichage.getSelectionModel().getSelectedItem();

            if (centreSelectionne != null) {
                try {
                    // Appeler la méthode supprimer en lui passant l'ID du centre sélectionné
                    centreRepository.supprimer(centreSelectionne.getId());

                    // Mettre à jour la ListView après la suppression
                    affichage.getItems().remove(centreSelectionne);

                } catch (SQLException e) {
                    e.printStackTrace(); // Gérer l'erreur si la suppression échoue
                }
            } else {
                // Gérer le cas où aucun centre n'est sélectionné
                System.out.println("Aucun centre sélectionné pour la suppression.");
            }
        }

        @FXML
        void ModifierCentre(ActionEvent event) {
            Centre selectedCentre = affichage.getSelectionModel().getSelectedItem();
            if (selectedCentre != null) {
                // Mettre à jour l'objet en mémoire
                selectedCentre.setNom(NomCentre.getText());
                selectedCentre.setAdresse(AdresseCentre.getText());
                selectedCentre.setTelephone(Integer.parseInt(TelephoneCentre.getText()));
                selectedCentre.setEmail(EmailCentre.getText());

                // Mise à jour de la base de données
                String sql = "UPDATE centre SET nom=?, adresse=?, telephone=?, email=? WHERE id=?";
                try (Connection conn = DataBase.getInstance().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, selectedCentre.getNom());
                    stmt.setString(2, selectedCentre.getAdresse());
                    stmt.setInt(3, selectedCentre.getTelephone());
                    stmt.setString(4, selectedCentre.getEmail());
                    stmt.setInt(5, selectedCentre.getId());

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Rafraîchir la ListView pour afficher les modifications
                loadData();
            }
        }

        @FXML
        void AjouterCentre(ActionEvent event) {
            // Récupérer les valeurs saisies dans les champs TextField
            String nom = NomCentre.getText();
            String adresse = AdresseCentre.getText();
            String email = EmailCentre.getText();
            String phoneText = TelephoneCentre.getText();

            // Vérifier si les champs sont vides
            if (nom.isEmpty() || adresse.isEmpty() || email.isEmpty() || phoneText.isEmpty()) {
                System.out.println("Tous les champs doivent être remplis !");
            } else {
                try {
                    // Convertir le téléphone en entier
                    int telephone = Integer.parseInt(phoneText);

                    // Créer un objet Centre avec les informations saisies
                    Centre centre = new Centre(nom, adresse, telephone, email);

                    // Appel de la fonction d'ajout dans le CentreRepository
                    centreRepository.ajouter(centre);

                    // Réinitialiser les champs après l'ajout
                    NomCentre.clear();
                    AdresseCentre.clear();
                    EmailCentre.clear();
                    TelephoneCentre.clear();

                    // Afficher un message de succès
                    System.out.println("Centre ajouté : " + centre);

                    // Rafraîchir la ListView
                    loadData();

                } catch (NumberFormatException | SQLException e) {
                    System.out.println("Veuillez entrer un numéro de téléphone valide.");
                }
            }
        }

        @FXML
        void GoToContrat(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Afficher_Contrat.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Récupérer la fenêtre actuelle
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
