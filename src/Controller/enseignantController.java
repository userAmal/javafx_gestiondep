package Controller;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import Models.Matiere;
import application.connexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class enseignantController implements Initializable {
    @FXML
    private ComboBox<Integer> cbEnseignants;
    @FXML
    private TableColumn<Matiere, String> colnomMat;
    @FXML
    private TableColumn<Matiere, Integer> colNiveauMat;
    @FXML
    private TableColumn<Matiere, String> colDescMat;
    @FXML
    private TableView<Matiere> tableV;
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfGrade;
    @FXML
    private TextField tfChargeH;
    @FXML
    private TextField tfNewGrade;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnRecherche;
    @FXML
    private Button btnSupp;
    ObservableList<Matiere> listM = FXCollections.observableArrayList();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        try {
            Connection con = connexion.getCn();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM enseignant");
            while (rs.next()) {
                list.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObservableList<Integer> listeE = FXCollections.observableArrayList(list);
        cbEnseignants.setItems(listeE);

        colnomMat.setCellValueFactory(new PropertyValueFactory<Matiere, String>("nom"));
        colNiveauMat.setCellValueFactory(new PropertyValueFactory<Matiere, Integer>("niveau"));
        colDescMat.setCellValueFactory(new PropertyValueFactory<Matiere, String>("description"));
 

        tableV.setItems(listM);
    
    }
    @FXML
    public void getEnseignant(ActionEvent event) {
        Integer selectedE = cbEnseignants.getValue();
        try {
            Connection con = connexion.getCn();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select nom, grade from enseignant where id = " + selectedE);
            if (rs.next()) {
                String nom = rs.getString("nom");
                String grade = rs.getString("grade");
                tfNom.setText(nom);
                tfGrade.setText(grade);
            }
            ResultSet rs2 = stmt.executeQuery("select nom, niveau, description, nbreHeure from matiere where idE = " + selectedE);
            listM.clear();
            int totalH = 0;
            while (rs2.next()) {
                String nomM = rs2.getString("nom");
                int niveauM = rs2.getInt("niveau");
                String descriptionM = rs2.getString("description");
                int heuresM = rs2.getInt("nbreHeure");

                listM.add(new Matiere(nomM, niveauM, descriptionM, heuresM));
                totalH += heuresM;
            }

            tfChargeH.setText(String.valueOf(totalH));
            tableV.setItems(listM);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
	protected void deleteMatiere(ActionEvent event)
	{

		Matiere selected=tableV.getSelectionModel().getSelectedItem();
		Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()&&result.get()==ButtonType.OK) {	
        	tableV.getItems().remove(selected);
        }
        try {
        Connection con = connexion.getCn();
	 	String sql = "delete from matiere where nom=?";
     	PreparedStatement pstmt = con.prepareStatement(sql);
     	pstmt.setString(1, selected.getNom());
     	pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
 
	
}

	@FXML
	void updateGrade(ActionEvent event) {
	    Integer selectedE = cbEnseignants.getValue();
	    String ng = tfNewGrade.getText();
	    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
	    confirmAlert.setTitle("Confirmation de la mise à jour");
	    confirmAlert.setContentText("Êtes-vous sûr de vouloir mettre à jour le grade?");
	    Optional<ButtonType> result = confirmAlert.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
	        try {
	            Connection con = connexion.getCn();
	            Statement stmt = con.createStatement();
	            String updateQuery = "update enseignant set grade = '" + ng + "' where id = " + selectedE;
	            stmt.executeUpdate(updateQuery);
	            tfGrade.setText(ng);
	            FileWriter writer = new FileWriter("GradesEnseignants.txt", true); 
	            writer.write("id enseignant: " + selectedE + " nouveau grade: " + ng);
	            writer.close();
	            Alert successAlert = new Alert(AlertType.INFORMATION);
	            successAlert.setContentText("Le grade a été mis à jour avec succès.");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

}
