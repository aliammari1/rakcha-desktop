package com.esprit.services;

import com.esprit.models.Users;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersService implements IService<Users> {

    Connection connection;

    public UsersService() {
        this.connection = DataSource.getInstance().getConnection();
    }
    @Override
    public void create(Users users) {

        String req = "INSERT into userss(id,nom,prenom,num_telephone,password,role,adresse,date_de_naissance,email,photo_de_profil) values (?, ?,?,?,?,?,?,?,?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, users.getFirstName());

            pst.setString(2, users.getLastName());
            pst.setInt(3,users.getPhoneNumber());
            pst.setString(4, users.getPassword());
            pst.setString(5, users.getRole());
            pst.setString(6, users.getAddress());
            pst.setDate(7, users.getBirthDate());
            pst.setString(8, users.getEmail());
            pst.setBlob(9, users.getPhoto_de_profil());
            pst.executeUpdate();
            System.out.println("Categorie ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Users> read() {
        List<Users> user = new ArrayList<>();

        String req = "SELECT * from users";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                user.add(new Users(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"), rs.getInt("num_telephone"), rs.getString("password"), rs.getString("role"), rs.getString("adresse"), rs.getDate("date_de_naissance"), rs.getString("email"), rs.getBlob("photo_de_profil")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return user;
    }

    @Override
    public void update(Users users) {

        try {
            PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE users SET  nom=?,prenom=?,num_telephone=?,password=?,role=?,adresse=?,date_de_naissance=?,email=?,photo_de_profil=? WHERE id=?");
            System.out.println(users);
            statement.setString(1, users.getFirstName());
            statement.setString(2, users.getLastName());
            statement.setInt(3, users.getPhoneNumber());
            statement.setString(4, users.getPassword());
            statement.setString(5, users.getRole());
            statement.setString(6, users.getAddress());
            statement.setDate(7, users.getBirthDate());
            statement.setString(8, users.getEmail());
            statement.setBlob(9, users.getPhoto_de_profil());
            statement.setInt(10, users.getId());
            statement.executeUpdate();
            System.out.println("user is updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Users users) {

        try {
            PreparedStatement statement = this.connection.prepareStatement("DELETE FROM users WHERE id = ?");
            statement.setInt(1, users.getId());
            statement.executeUpdate();
            System.out.println("user is deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    Users getUsers(int id) {
        Users user = null;

        String req = "SELECT * from Users where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            user = new Users(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"), rs.getInt("num_telephone"), rs.getString("password"), rs.getString("role"), rs.getString("adresse"), rs.getDate("date_de_naissance"), rs.getString("email"), rs.getBlob("photo_de_profil"));


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }
}
