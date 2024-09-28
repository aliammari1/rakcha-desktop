package com.esprit.services.users;

import com.esprit.models.users.Admin;
import com.esprit.models.users.Client;
import com.esprit.models.users.Responsable_de_cinema;
import com.esprit.models.users.User;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.UserMail;
import com.esprit.utils.UserPDF;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService implements IService<User> {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    Connection con;

    public UserService() {
        con = DataSource.getInstance().getConnection();
    }

    /**
     * @param id
     * @return User
     */
    public User getUserById(final int id) {
        final String req = "select * from users where id = ?";
        User user = null;
        try {
            final PreparedStatement preparedStatement = this.con.prepareStatement(req);
            preparedStatement.setInt(1, id);
            user = this.getUserRow(preparedStatement);
        } catch (final Exception e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }

    /**
     * @param email
     * @return User
     */
    private User getUserByEmail(final String email) {
        final String req = "select * from users where email = ?";
        User user = null;
        try {
            final PreparedStatement preparedStatement = this.con.prepareStatement(req);
            preparedStatement.setString(1, email);
            user = this.getUserRow(preparedStatement);
        } catch (final Exception e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }

    @Override
    public void create(final User user) {
        try {
            final PreparedStatement statement = con.prepareStatement(
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
            if ("admin".equals(user.getRole())) {
                statement.setString(11, "[\"ROLE_ADMIN\"]");
            } else if ("client".equals(user.getRole())) {
                statement.setString(11, "[\"ROLE_CLIENT\"]");
            } else if ("responsable de cinema".equals(user.getRole())) {
                statement.setString(11, "[\"ROLE_RESPONSABLE_DE_CINEMA\"]");
            }
            statement.executeUpdate();
            UserService.LOGGER.info("user was added");
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<User> read() {
        try {
            final List<User> userList = new ArrayList<>();
            final String query = "SELECT * FROM users";
            final PreparedStatement statement = con.prepareStatement(query);
            return getUsers(userList, statement);
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void update(final User user) {
        try {
            final PreparedStatement statement = con.prepareStatement(
                    "UPDATE users SET  nom=?,prenom=?,num_telephone=?,password=?,role=?,adresse=?,date_de_naissance=?,email=?,photo_de_profil=? WHERE id=?");
            UserService.LOGGER.info(user.toString());
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
            UserService.LOGGER.info("user is updated");
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final User user) {
        try {
            final PreparedStatement statement = con.prepareStatement("DELETE FROM users WHERE id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();
            UserService.LOGGER.info("user is deleted");
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void sendMail(final String Recipient, final String messageToSend) {
        UserMail.send(Recipient, messageToSend);
    }

    public void generateUserPDF() {
        final UserPDF userPDF = new UserPDF();
        userPDF.generate(sort("role"));
    }

    public List<User> sort(final String Option) {
        try {
            final List<User> userList = new ArrayList<>();
            final String query = "SELECT * FROM users ORDER BY %s".formatted(Option);
            final PreparedStatement statement = con.prepareStatement(query);
            return getUsers(userList, statement);
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    private List<User> getUsers(final List<User> userList, final PreparedStatement statement) throws SQLException {
        final ResultSet resultSet = statement.executeQuery();
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

    public boolean checkEmailFound(final String email) {
        final String req = "select email from users where email LIKE ?";
        boolean check = false;
        try {
            final PreparedStatement statement = this.con.prepareStatement(req);
            statement.setString(1, email);
            final ResultSet resultSet = statement.executeQuery();
            check = resultSet.next();
        } catch (final Exception e) {
            UserService.LOGGER.info("checkEmailFound: " + e.getMessage());
        }
        return check;
    }

    public void updatePassword(final String email, final String NewPassword) {
        try {
            final PreparedStatement statement = con.prepareStatement("UPDATE users SET password=? where email=? ");
            statement.setString(1, NewPassword);
            statement.setString(2, email);
            statement.executeUpdate();
            UserService.LOGGER.info("user password is updated");
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void forgetPassword(final String email, final String Password) {
        final String query = "select * from users where email LIKE ?";
        try {
            final PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, email);
            final User user = this.getUserRow(preparedStatement);
            if (null != user) {
                this.sendMail(user.getEmail(), "you forget your password dumbhead hhhh");
            } else {
                final Alert alert = new Alert(Alert.AlertType.ERROR, "the user was not found", ButtonType.CLOSE);
                alert.show();
            }
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private User getUserRow(final PreparedStatement preparedStatement) throws SQLException {
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            final String role = resultSet.getString("role");
            UserService.LOGGER.info(role);
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

    public User login(final String email, final String password) {
        User user = this.getUserByEmail(email);
        if (null == user) {
            return null;
        }
        try {
            final String query = "select * from users where (email LIKE ?) and (password LIKE ?)";
            final PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, user.getPassword());
            user = this.getUserRow(statement);
            String storedHash = user.getPassword();
            storedHash = storedHash.replaceFirst("\\$2y\\$", "\\$2a\\$");
            if (null == storedHash || !storedHash.startsWith("$2a$")) {
                UserService.LOGGER.info("Invalid stored hash");
                return null;
            }
            if (BCrypt.checkpw(password, storedHash)) {
                UserService.LOGGER.info("Password matches");
            } else {
                UserService.LOGGER.info("Password does not match");
                return null;
            }
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }
}
