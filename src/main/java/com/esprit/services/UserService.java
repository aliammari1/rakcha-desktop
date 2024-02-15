package com.esprit.services;

import com.esprit.models.*;
import com.esprit.utils.DataSource;
import com.esprit.utils.UserMail;
import com.esprit.utils.UserPDF;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    Connection con;

    public UserService() {
        this.con = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(final User user) {
        try {
            final PreparedStatement statement = this.con.prepareStatement(
                    "INSERT INTO users (nom,prenom,num_telephone,password,role,adresse,date_de_naissance,email,photo_de_profil) VALUES (?,?,?,?,?,?,?,?,?)");
            this.updateAndAddStatementSetter(user, statement);
            statement.executeUpdate();
            System.out.println("user was added");
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<User> read() {
        try {
            final List<User> userList = new ArrayList<>();
            final String query = "SELECT * FROM users";
            final PreparedStatement statement = this.con.prepareStatement(query);
            return this.getUsers(userList, statement);
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void update(final User user) {
        try {
            final PreparedStatement statement = this.con.prepareStatement(
                    "UPDATE users SET  nom=?,prenom=?,num_telephone=?,password=?,role=?,adresse=?,date_de_naissance=?,email=?,photo_de_profil=? WHERE id=?");
            this.updateAndAddStatementSetter(user, statement);
            statement.setInt(10, user.getId());
            statement.executeUpdate();
            System.out.println("user is updated");
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateAndAddStatementSetter(final User user, final PreparedStatement statement) throws SQLException {
        statement.setString(1, user.getNom());
        statement.setString(2, user.getPrenom());
        statement.setInt(3, user.getNum_telephone());
        statement.setString(4, user.getPassword());
        statement.setString(5, user.getRole());
        statement.setString(6, user.getAdresse());
        statement.setDate(7, user.getDate_de_naissance());
        statement.setString(8, user.getEmail());
        statement.setBlob(9, user.getPhoto_de_profil());
    }

    @Override
    public void delete(final User user) {
        try {
            final PreparedStatement statement = this.con.prepareStatement("DELETE FROM users WHERE id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();
            System.out.println("user is deleted");
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMail(final String Recipient, final String messageToSend) {
        UserMail.send(Recipient, messageToSend);
    }

    public void generateUserPDF() {
        final UserPDF userPDF = new UserPDF();
        userPDF.generate(this.sort("role"));
    }


    public List<User> sort(final String Option) {
        try {
            final List<User> userList = new ArrayList<>();
            String query = "SELECT * FROM users ORDER BY " + Option;
            final PreparedStatement statement = this.con.prepareStatement(query);
            return this.getUsers(userList, statement);
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private List<User> getUsers(final List<User> userList, final PreparedStatement statement) throws SQLException {
        final ResultSet resultSet = statement.executeQuery();
        int i = 0;
        while (resultSet.next()) {
            final String role = resultSet.getString("role");
            userList.add(
                    switch (role) {
                        case "admin":
                            yield new Admin(
                                    resultSet.getInt("id"),
                                    resultSet.getString("nom"),
                                    resultSet.getString("prenom"),
                                    resultSet.getInt("num_telephone"),
                                    resultSet.getString("password"),
                                    resultSet.getString("role"),
                                    resultSet.getString("adresse"),
                                    resultSet.getDate("date_de_naissance"),
                                    resultSet.getString("email"),
                                    resultSet.getBlob("photo_de_profil"));
                        case "client":
                            yield new Client(
                                    resultSet.getInt("id"),
                                    resultSet.getString("nom"),
                                    resultSet.getString("prenom"),
                                    resultSet.getInt("num_telephone"),
                                    resultSet.getString("password"),
                                    resultSet.getString("role"),
                                    resultSet.getString("adresse"),
                                    resultSet.getDate("date_de_naissance"),
                                    resultSet.getString("email"),
                                    resultSet.getBlob("photo_de_profil"));
                        case "responsable de cinema":
                            yield new Responsable_de_cinema(
                                    resultSet.getInt("id"),
                                    resultSet.getString("nom"),
                                    resultSet.getString("prenom"),
                                    resultSet.getInt("num_telephone"),
                                    resultSet.getString("password"),
                                    resultSet.getString("role"),
                                    resultSet.getString("adresse"),
                                    resultSet.getDate("date_de_naissance"),
                                    resultSet.getString("email"),
                                    resultSet.getBlob("photo_de_profil"));
                        case "sponsor":
                            yield new Sponsor(
                                    resultSet.getInt("id"),
                                    resultSet.getString("nom"),
                                    resultSet.getString("prenom"),
                                    resultSet.getInt("num_telephone"),
                                    resultSet.getString("password"),
                                    resultSet.getString("role"),
                                    resultSet.getString("adresse"),
                                    resultSet.getDate("date_de_naissance"),
                                    resultSet.getString("email"),
                                    resultSet.getBlob("photo_de_profil"));
                        default:
                            throw new IllegalStateException("Unexpected role value: " + role);
                    });
            System.out.println(userList.get(i));
            i++;

        }
        return userList;
    }

}
