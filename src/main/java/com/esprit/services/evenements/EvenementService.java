package com.esprit.services.evenements;

import com.esprit.models.evenements.Evenement;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.EventExcel;
import com.esprit.utils.EventPDF;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {

    private final Connection connection;

    public EvenementService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Evenement evenement) {
        String req = "INSERT into evenement(nom,dateDebut,dateFin,lieu,id_categorie,etat,description,affiche_event) values (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, evenement.getNom());
            pst.setDate(2, evenement.getDateDebut());
            pst.setDate(3, evenement.getDateFin());
            pst.setString(4, evenement.getLieu());
            pst.setInt(5, evenement.getCategorie().getId_categorie());
            pst.setString(6, evenement.getEtat());
            pst.setString(7, evenement.getDescription());
            pst.setBlob(8, evenement.getAffiche_event());
            pst.executeUpdate();
            System.out.println("Event Added !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Evenement evenement) {
        String req = "UPDATE evenement set nom = ?, dateDebut = ?, dateFin = ?, lieu = ?, id_categorie = ?, etat = ?, description = ?, affiche_event= ? where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(9, evenement.getId());
            pst.setString(1, evenement.getNom());
            pst.setDate(2, evenement.getDateDebut());
            pst.setDate(3, evenement.getDateFin());
            pst.setString(4, evenement.getLieu());
            pst.setInt(5, evenement.getCategorie().getId_categorie());
            pst.setString(6, evenement.getEtat());
            pst.setString(7, evenement.getDescription());
            pst.setBlob(8, evenement.getAffiche_event());
            pst.executeUpdate();
            System.out.println("Event Updated !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Evenement evenement) {
        String req = "DELETE from evenement where id = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, evenement.getId());
            pst.executeUpdate();
            System.out.println("Event Deleted !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Evenement> read() {
        List<Evenement> evenements = new ArrayList<>();

        String req = "SELECT evenement.* , categorie_evenement.nom_categorie from evenement JOIN categorie_evenement ON evenement.id_categorie = categorie_evenement.id";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CategorieService cs = new CategorieService();
            while (rs.next()) {
                evenements.add(new Evenement(rs.getInt("ID"), rs.getString("nom"), rs.getDate("dateDebut"), rs.getDate("dateFin"), rs.getString("lieu"), cs.getCategorie(rs.getInt("id_categorie")), rs.getString("etat"), rs.getString("description"), rs.getBlob("affiche_event")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            evenement = new Evenement((rs.getInt("ID")), rs.getString("nom"), rs.getDate("dateDebut"), rs.getDate("dateFin"), rs.getString("lieu"), cs.getCategorie(rs.getInt("id_categorie")), rs.getString("etat"), rs.getString("description"), rs.getBlob("affiche_event"));

        } catch (SQLException e) {
            e.printStackTrace();
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
            evenement = new Evenement((rs.getInt("ID")), rs.getString("nom"), rs.getDate("dateDebut"), rs.getDate("dateFin"), rs.getString("lieu"), cs.getCategorie(rs.getInt("id_categorie")), rs.getString("etat"), rs.getString("description"), rs.getBlob("affiche_event"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return evenement;
    }


    public Evenement getEvenementById(int eventId) {
        Evenement evenement = null;

        String req = "SELECT * from evenement WHERE id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                com.esprit.services.evenements.CategorieService cs = new com.esprit.services.evenements.CategorieService();

                evenement = new Evenement((rs.getInt("ID")), rs.getString("nom"), rs.getDate("dateDebut"), rs.getDate("dateFin"), rs.getString("lieu"), cs.getCategorie(rs.getInt("id_categorie")), rs.getString("etat"), rs.getString("description"), rs.getBlob("affiche_event"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
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
            return this.read();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
