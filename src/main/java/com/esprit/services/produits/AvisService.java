package com.esprit.services.produits;


import com.esprit.models.produits.Avis;
import com.esprit.models.produits.Client;
import com.esprit.models.produits.Users;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.services.produits.ProduitService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvisService  implements IService<Avis> {

    private Connection connection;

    public AvisService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Avis avis) {
        String req = "INSERT into avis(idusers,note,id_produit ,avis) values (?,?, ?,?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, avis.getUser().getId() );
            pst.setInt(2, avis.getNote() );
            pst.setInt(3,avis.getProduit().getId_produit());

            pst.setString(4,avis.getAvis());

            pst.executeUpdate();
            System.out.println("avis ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Avis> read() {
        return null;
    }

   /* public Avis getmy(int id) throws SQLException {
        Avis avis=new Avis();
        UsersService usersService=new UsersService();
        String req = "SELECT * FROM `avis`  where iduser = ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            avis.setUser(usersService.getUserById(rs.getInt("idusers")));
            avis.setNote(rs.getInt("note"));
            avis.setAvis(rs.getString("avis"));

        }
        return avis;
    }*/


    /*public List<Avis> gettall(int id) throws SQLException {
        List<Avis> avisList=new ArrayList<>();
        UsersService usersService=new UsersService();
        String req = "SELECT * FROM `avis`  where iduser != ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Avis avis=new Avis();
            avis.setUser(usersService.getUserById(rs.getInt("idusers")));
            avis.setNote(rs.getInt("note"));
            avis.setAvis(rs.getString("avi"));
            avisList.add(avis);
        }
        return avisList;
    }*/



    @Override
    public void update(Avis avis) {

        String req = "UPDATE avis set note = ?,  avis = ? where idavis=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, avis.getNote());
            pst.setString(2, avis.getAvis());


            pst.executeUpdate();
            System.out.println("avis modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Avis avis) {

        String req = "DELETE from avis where id_client = ? and id_produit=?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, avis.getUser().getId());
            pst.setInt(2,avis.getProduit().getId_produit());
            pst.executeUpdate();
            System.out.println("categorie supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public double getavergerating(int id_film) {
        String req = "SELECT AVG(rate) AS averageRate FROM ratingfilm WHERE id_film =? ";
        double aver = 0.0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, id_film);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            aver = resultSet.getDouble("averageRate");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return aver;
    }

    public List<Avis> getavergeratingSorted() {
        String req = "SELECT id_film, AVG(rate) AS averageRate FROM ratingfilm GROUP BY id_film  ORDER BY averageRate DESC ";
        List<Avis> aver = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                aver.add(new Avis( null, (int) resultSet.getDouble("averageRate"),null,new ProduitService().getProduitById(resultSet.getInt("id_ptoduit"))));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return aver;
    }

    public Avis ratingExiste(int id_film, int id_user) {
        String req = "SELECT *  FROM ratingfilm WHERE id_film =? AND id_user=? ";
        Avis rate = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, id_film);
            preparedStatement.setInt(2, id_user);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                rate=new Avis((Client) new UsersService().getUserById(id_user), (int) resultSet.getDouble("averageRate"),null,new ProduitService().getProduitById(resultSet.getInt("id_ptoduit")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return rate;
    }

}

