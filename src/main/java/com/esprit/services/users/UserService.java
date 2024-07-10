package com.esprit.services.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import com.esprit.models.users.Admin;
import com.esprit.models.users.Client;
import com.esprit.models.users.Responsable_de_cinema;
import com.esprit.models.users.User;
import com.esprit.services.IService;
import com.esprit.services.produits.AvisService;
import com.esprit.utils.DataSource;
import com.esprit.utils.UserMail;
import com.esprit.utils.UserPDF;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class UserService implements IService<User> {
    Connection con;
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    public UserService() {
        this.con = DataSource.getInstance().getConnection();
    }

    /**
     * @param id
     * @return User
     */
    public User getUserById(int id) {
        String req = "select * from users where id = ?";
        User user = null;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(req);
            preparedStatement.setInt(1, id);
            user = getUserRow(preparedStatement);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }

    /**
     * @param email
     * @return User
     */
    private User getUserByEmail(String email) {
        String req = "select * from users where email = ?";
        User user = null;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(req);
            preparedStatement.setString(1, email);
            user = getUserRow(preparedStatement);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }

    @Override
    public void create(User user) {
        try {
            PreparedStatement statement = this.con.prepareStatement(
                    "INSERT INTO users (nom,prenom,num_telephone,password,role,adresse,date_de_naissance,email,photo_de_profil,is_verified,roles) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getPhoneNumber());
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            hashedPassword = hashedPassword.replaceFirst("\\$2a\\$", "\\$2y\\$");
            statement.setString(4, hashedPassword);
            statement.setString(5, user.getRole());
            statement.setString(6, user.getAddress());
            statement.setDate(7, user.getBirthDate());
            statement.setString(8, user.getEmail());
            statement.setString(9, user.getPhoto_de_profil());
            statement.setBoolean(10, true);
            if (user.getRole().equals("admin")) {
                statement.setString(11, "[\"ROLE_ADMIN\"]");
            } else if (user.getRole().equals("client")) {
                statement.setString(11, "[\"ROLE_CLIENT\"]");
            } else if (user.getRole().equals("responsable de cinema")) {
                statement.setString(11, "[\"ROLE_RESPONSABLE_DE_CINEMA\"]");
            }
            statement.executeUpdate();
            LOGGER.info("user was added");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void update(User user) {
        try {
            PreparedStatement statement = this.con.prepareStatement(
                    "UPDATE users SET  nom=?,prenom=?,num_telephone=?,password=?,role=?,adresse=?,date_de_naissance=?,email=?,photo_de_profil=? WHERE id=?");
            LOGGER.info(user.toString());
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getPhoneNumber());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole());
            statement.setString(6, user.getAddress());
            statement.setDate(7, user.getBirthDate());
            statement.setString(8, user.getEmail());
            statement.setString(9, user.getPhoto_de_profil());
            statement.setInt(10, user.getId());
            statement.executeUpdate();
            LOGGER.info("user is updated");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(User user) {
        try {
            PreparedStatement statement = this.con.prepareStatement("DELETE FROM users WHERE id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();
            LOGGER.info("user is deleted");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
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
            String query = String.format("SELECT * FROM users ORDER BY %s", Option);
            PreparedStatement statement = this.con.prepareStatement(query);
            return this.getUsers(userList, statement);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
                                    resultSet.getString("photo_de_profil"));
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
                                    resultSet.getString("photo_de_profil"));
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
                                    resultSet.getString("photo_de_profil"));
                        default:
                            yield null;
                    });
        }
        return userList;
    }

    public boolean checkEmailFound(String email) {
        String req = "select email from users where email LIKE ?";
        boolean check = false;
        try {
            PreparedStatement statement = con.prepareStatement(req);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            check = resultSet.next();
        } catch (Exception e) {
            LOGGER.info("checkEmailFound: " + e.getMessage());
        }
        return check;
    }

    public void updatePassword(String email, String NewPassword) {
        try {
            PreparedStatement statement = this.con.prepareStatement("UPDATE users SET password=? where email=? ");
            statement.setString(1, NewPassword);
            statement.setString(2, email);
            statement.executeUpdate();
            LOGGER.info("user password is updated");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void forgetPassword(String email, String Password) {
        String query = "select * from users where email LIKE ?";
        try {
            PreparedStatement preparedStatement = this.con.prepareStatement(query);
            preparedStatement.setString(1, email);
            User user = getUserRow(preparedStatement);
            if (user != null) {
                sendMail(user.getEmail(), "you forget your password dumbhead hhhh");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "the user was not found", ButtonType.CLOSE);
                alert.show();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private User getUserRow(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String role = resultSet.getString("role");
            LOGGER.info(role);
            return switch (role.trim()) {
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
                            resultSet.getString("photo_de_profil"));
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
                            resultSet.getString("photo_de_profil"));
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
                            resultSet.getString("photo_de_profil"));
                default:
                    yield null;
            };
        }
        return null;
    }

    public User login(String email, String password) {
        User user = getUserByEmail(email);
        if (user == null) {
            return null;
        }
        try {
            String query = "select * from users where (email LIKE ?) and (password LIKE ?)";
            PreparedStatement statement = this.con.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, user.getPassword());
            user = getUserRow(statement);
            String storedHash = user.getPassword();
            storedHash = storedHash.replaceFirst("\\$2y\\$", "\\$2a\\$");
            if (storedHash == null || !storedHash.startsWith("$2a$")) {
                LOGGER.info("Invalid stored hash");
                return null;
            }
            if (BCrypt.checkpw(password, storedHash)) {
                LOGGER.info("Password matches");
            } else {
                LOGGER.info("Password does not match");
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }
}
