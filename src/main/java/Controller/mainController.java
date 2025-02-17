package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class mainController {
    @FXML
    private StackPane contentArea;
    public void showDemande(ActionEvent actionEvent) throws IOException {
        loadContent("/AjoutDemande.fxml");
    }

    public void showReclamation(ActionEvent actionEvent) {
    }
    private void loadContent(String fxmlPath) throws IOException {
        Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
        contentArea.getChildren().setAll(content);
    }


}
