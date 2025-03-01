package com.example.ewaste.Controllers;



import com.example.ewaste.Entities.User;
import com.example.ewaste.Repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.List;

public class UserTableController {
    @FXML private Label roleLabel;

    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, Integer> col_employeeNum;
    @FXML private TableColumn<User, String> col_telephone;
    @FXML private TableColumn<User, String> col_firstName;
    @FXML private TableColumn<User, String> col_lastName;
    @FXML private TableColumn<User, String> col_email;
    @FXML private TableColumn<User, String> col_birthDate;
    @FXML private TableColumn<User, String> col_status;
    @FXML private TableColumn<User, String> col_photo;

    // A repository method that retrieves users by role. This can be a single repository that
    // has methods like getUsersByRole(String role)
    private UserRepository userRepo = new UserRepository();

    @FXML
    public void initialize() {
        // Set up the cell value factories (assuming the User model has properties with these names)
        col_employeeNum.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_telephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<>("nom"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_birthDate.setCellValueFactory(new PropertyValueFactory<>("DateNss"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        col_photo.setCellValueFactory(new PropertyValueFactory<>("photoUrl"));
    }

    /**
     * Loads the data into the TableView based on the role.
     *
     * @param role The role to filter users by (e.g., "EMPLOYEE" or "CITOYEN").
     */
    public void loadData(String role) {
        // Update the label based on role
        if (role.equalsIgnoreCase("EMPLOYEE")) {
            roleLabel.setText("Employee Details");
        } else if (role.equalsIgnoreCase("CITOYEN")) {
            roleLabel.setText("Citoyen Details");
        } else {
            roleLabel.setText("User Details");
        }

        try {
            List<User> userList = userRepo.getUsersByRole(role);
            ObservableList<User> observableList = FXCollections.observableArrayList(userList);
            userTableView.setItems(observableList);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}