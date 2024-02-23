package com.esprit.services;

import com.esprit.models.Admin;
import com.esprit.models.Client;
import com.esprit.models.Responsable_de_cinema;
import com.esprit.models.User;
import com.esprit.utils.DataSource;
import com.esprit.utils.UserMail;
import com.esprit.utils.UserPDF;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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
    public void create(User user) {
        try {
            PreparedStatement statement = this.con.prepareStatement(
                    "INSERT INTO users (nom,prenom,num_telephone,password,role,adresse,date_de_naissance,email,photo_de_profil) VALUES (?,?,?,?,?,?,?,?,?)");
            updateAndAddStatementSetter(user, statement);
            statement.executeUpdate();
            System.out.println("user was added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public List<User> read() {
        try {
            List<User> userList = new ArrayList<>();
            String query = "SELECT * FROM users";
            PreparedStatement statement = this.con.prepareStatement(query);
            return this.getUsers(userList, statement);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void update(User user) {
        try {
            PreparedStatement statement = this.con.prepareStatement(
                    "UPDATE users SET  nom=?,prenom=?,num_telephone=?,password=?,role=?,adresse=?,date_de_naissance=?,email=?,photo_de_profil=? WHERE id=?");
            updateAndAddStatementSetter(user, statement);
            statement.setInt(10, user.getId());
            statement.executeUpdate();
            System.out.println("user is updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(User user) {
        try {
            PreparedStatement statement = this.con.prepareStatement("DELETE FROM users WHERE id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();
            System.out.println("user is deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateAndAddStatementSetter(User user, PreparedStatement statement) throws SQLException {
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

    public void sendMail(String Recipient, String messageToSend) {
        UserMail.send(Recipient, messageToSend);
    }

    public void generateUserPDF() {
        UserPDF userPDF = new UserPDF();
        userPDF.generate(this.sort("role"));
    }


    public List<User> sort(String Option) {
        try {
            List<User> userList = new ArrayList<>();
            String query = "SELECT * FROM users ORDER BY " + Option;
            PreparedStatement statement = this.con.prepareStatement(query);
            return this.getUsers(userList, statement);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private List<User> getUsers(List<User> userList, PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String role = resultSet.getString("role");
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
                        default:
                            yield null;
                    });
        }
        return userList;
    }

    public void updatePassword(String email, String NewPassword) {
        try {
            PreparedStatement statement = this.con.prepareStatement("UPDATE users SET password=? where email=? ");
            statement.setString(1, NewPassword);
            statement.setString(2, email);
            statement.executeUpdate();
            System.out.println("user password is updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void forgetPassword(String email, String Password) {
        String query = "select * from users where email = ?";
        try {
            PreparedStatement preparedStatement = this.con.prepareStatement(query);
            preparedStatement.setString(1, email);
            User user = getUserRow(preparedStatement);
            if (user != null)
                sendMail(user.getEmail(), "you forget your password dumbhead hhhh");
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "the user was not found", ButtonType.CLOSE);
                alert.show();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private User getUserRow(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String role = resultSet.getString("role");
        return switch (role) {
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
            default:
                yield null;
        };
    }

    public void login(String email, String password) {
        String query = "select * from users where (email LIKE ?) AND (password LIKE ?)";
        try {
            PreparedStatement statement = this.con.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            User user = getUserRow(statement);
            if (user != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "the user was found", ButtonType.CLOSE);
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "the user was not found", ButtonType.CLOSE);
                alert.show();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
