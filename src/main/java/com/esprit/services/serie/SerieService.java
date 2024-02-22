package com.esprit.services.serie;

import com.esprit.models.categorie;
import com.esprit.models.serie;
import com.esprit.services.IService;
import com.esprit.services.categorie.CategorieService;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SerieService implements IService<serie> {
    private Connection connection;
    private int serie;

    public SerieService() {
        connection = DataSource.getInstance().getConnection();
    }
    public void create(serie serie) {
        String req = "INSERT into serie(nom, resume, directeur,pays,image,idcategorie) values (?, ?,?,?,?,?)  ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            // Affichez les valeurs que vous essayez d'insérer (pour débogage)
            System.out.println("Nom: " + serie.getNom());
            System.out.println("Resume: " + serie.getResume());
            System.out.println("Directeur: " + serie.getDirecteur());
            System.out.println("Pays: " + serie.getPays());
            // ...
            pst.setString(1, serie.getNom());
            pst.setString(2, serie.getResume());
            pst.setString(3, serie.getDirecteur());
            pst.setString(4, serie.getPays());
            pst.setBlob(5, serie.getImage());
           // pst.setInt(6, serie.getCategorie().getIdcategorie());
            if (serie.getCategorie() != null) {
                pst.setInt(6, serie.getCategorie().getIdcategorie());
            } else {
                // Gérer le cas où la Categorie est null (peut-être lever une exception, afficher un message d'erreur, etc.)
                // Ici, je vais laisser le champ idcategorie comme null, assurez-vous que votre base de données peut gérer cela
                pst.setNull(6, Types.INTEGER);
            }
            pst.executeUpdate();
            System.out.println("Serie ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(serie serie) {
        String req = "UPDATE serie p " +
                "INNER JOIN categorie c ON p.idcategorie = c.idcategorie " +
                "SET p.idcategorie = ?, p.nom = ?, p.resume = ?, p.directeur = ?, p.pays = ?, p.image = ? " +
                "WHERE p.idserie = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(7, serie.getIdserie());
            pst.setString(2, serie.getNom());
            pst.setString(3, serie.getResume());
            pst.setString(4, serie.getDirecteur());
            pst.setString(5, serie.getPays());
            pst.setBlob(6, serie.getImage());
            pst.setInt(1, serie.getCategorie().getIdcategorie());

            pst.executeUpdate();
            System.out.println("Serie modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(serie serie) {
        String req = "DELETE from serie where idserie= ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, serie.getIdserie());
            pst.executeUpdate();
            System.out.println("Serie supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<serie> read() {
        List<serie> series = new ArrayList<>();

        String req = "SELECT  serie.* , categorie.nom from serie  JOIN categorie  ON serie.idcategorie = categorie.idcategorie";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CategorieService cs = new CategorieService();
            int i = 0;
            while (rs.next()) {
                series.add(new serie(rs.getInt("idserie"), rs.getString("nom"), rs.getString("resume"),rs.getString("directeur"),rs.getBlob("image"),rs.getString("pays"), (categorie) cs.getClass(rs.getInt("idcategorie"))));
                System.out.println(series.get(i));
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return series;
    }

    public int getSerie(int idserie) { return idserie;

    }
}



