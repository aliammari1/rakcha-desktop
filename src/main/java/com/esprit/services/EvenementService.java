package com.esprit.services;

import com.esprit.models.Evenement;

import com.esprit.utils.DataSource;

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
}
