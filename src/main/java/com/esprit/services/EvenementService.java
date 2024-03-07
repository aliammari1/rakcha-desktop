package com.esprit.services;

import com.esprit.models.Categorie_evenement;
import com.esprit.models.Evenement;

import com.esprit.models.Feedback;
import com.esprit.utils.DataSource;
import com.esprit.utils.EventExcel;
import com.esprit.utils.EventPDF;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {

    private Connection connection;

    public EvenementService() {
        connection = DataSource.getInstance().getConnection();
    }
    @Override
    public void add(Evenement evenement) {
        String req = "INSERT into evenement(nom,dateDebut,dateFin,lieu,id_categorie,etat,description) values (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, evenement.getNom());
            pst.setDate(2, evenement.getDateDebut());
            pst.setDate(3, evenement.getDateFin());
            pst.setString(4, evenement.getLieu());
            pst.setInt(5, evenement.getCategorie().getId_categorie());
            pst.setString(6, evenement.getEtat());
            pst.setString(7, evenement.getDescription());
            pst.executeUpdate();
            System.out.println("Evenement ajouté !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Evenement evenement) {
        String req = "UPDATE evenement set nom = ?, dateDebut = ?, dateFin = ?, lieu = ?, id_categorie = ?, etat = ?, description = ? where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(8, evenement.getId());
            pst.setString(1, evenement.getNom());
            pst.setDate(2, evenement.getDateDebut());
            pst.setDate(3, evenement.getDateFin());
            pst.setString(4, evenement.getLieu());
            pst.setInt(5, evenement.getCategorie().getId_categorie());
            pst.setString(6, evenement.getEtat());
            pst.setString(7, evenement.getDescription());
            pst.executeUpdate();
            System.out.println("Evenement modifié !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Evenement evenement) {
        String req = "DELETE from evenement where id = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, evenement.getId());
            pst.executeUpdate();
            System.out.println("Evenement supprimé !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Evenement> show() {
        List<Evenement> evenements = new ArrayList<>();

        String req = "SELECT evenement.* , categorie_evenement.nom_categorie from evenement JOIN categorie_evenement ON evenement.id_categorie = categorie_evenement.id";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CategorieService cs = new CategorieService();
            while (rs.next()) {
                evenements.add(new Evenement(rs.getInt("ID"), rs.getString("nom"), rs.getDate("dateDebut"), rs.getDate("dateFin"), rs.getString("lieu"), cs.getCategorie(rs.getInt("id_categorie")), rs.getString("etat"), rs.getString("description")));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return evenements;
    }

    public Evenement getEvenement(int evenement_id) {

        Evenement evenement = null;

        String req = "SELECT * from evenement where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, evenement_id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            CategorieService cs = new CategorieService();
            evenement = new Evenement((rs.getInt("ID")), rs.getString("nom"), rs.getDate("dateDebut"), rs.getDate("dateFin"), rs.getString("lieu"), cs.getCategorie(rs.getInt("id_categorie")), rs.getString("etat"), rs.getString("description")) ;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return evenement;
    }

        public Evenement getEvenementByNom(String evenement_nom) {

        Evenement evenement = null;

        String req = "SELECT * from evenement where nom = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, evenement_nom);
            ResultSet rs = pst.executeQuery();
            rs.next();
            CategorieService cs = new CategorieService();
            evenement = new Evenement((rs.getInt("ID")), rs.getString("nom"), rs.getDate("dateDebut"), rs.getDate("dateFin"), rs.getString("lieu"), cs.getCategorie(rs.getInt("id_categorie")), rs.getString("etat"), rs.getString("description")) ;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return evenement;
    }

    public void generateEventPDF() {
        EventPDF eventPDF = new EventPDF();
        eventPDF.generate(this.sort("id"));
    }

    public void generateEventExcel() {
        EventExcel eventExcel = new EventExcel();
        eventExcel.generate(this.sort("id"));
    }

    public List<Evenement> sort(String Option) {
        try {
            List<Evenement> eventList = new ArrayList<>();
            String query = "SELECT * FROM evenement ORDER BY " + Option;
            PreparedStatement statement = this.connection.prepareStatement(query);
            return this.show();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


}
