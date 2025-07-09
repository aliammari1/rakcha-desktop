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
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.TableCreator;
import com.esprit.utils.UserMail;
import com.esprit.utils.UserPDF;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserService implements IService<User> {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    Connection con;

    /**
     * Constructs a new UserService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public UserService() {
        con = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        TableCreator tableCreator = new TableCreator(con);
        tableCreator.createTableIfNotExists("users", """
                    CREATE TABLE users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nom VARCHAR(50) NOT NULL,
                        prenom VARCHAR(50) NOT NULL,
                        num_telephone INT,
                        password VARCHAR(180) NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        adresse VARCHAR(50),
                        date_de_naissance DATE,
                        email VARCHAR(180) NOT NULL UNIQUE,
                        photo_de_profil VARCHAR(255),
                        is_verified BOOLEAN NOT NULL DEFAULT TRUE,
                        roles TEXT NOT NULL,
                        totp_secret VARCHAR(255)
                    )
                """);
    }

    /**
     * @param id
     * @return User
     */
    public User getUserById(final Long id) {
        final String req = "select * from users where id = ?";
        User user = null;
        try {
            final PreparedStatement preparedStatement = this.con.prepareStatement(req);
            preparedStatement.setLong(1, id);
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
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final User user) {
        try {
            final PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO users (nom,prenom,num_telephone,password,role,adresse,date_de_naissance,email,photo_de_profil,is_verified,roles) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPhoneNumber());
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            hashedPassword = hashedPassword.replaceFirst("\\$2a\\$", "\\$2y\\$");
            statement.setString(4, hashedPassword);
            statement.setString(5, user.getRole());
            statement.setString(6, user.getAddress());
            statement.setDate(7, user.getBirthDate());
            statement.setString(8, user.getEmail());
            statement.setString(9, user.getPhotoDeProfil());
            statement.setBoolean(10, true);
            if ("admin".equals(user.getRole())) {
                statement.setString(11, "[\"ROLE_ADMIN\"]");
            } else if ("client".equals(user.getRole())) {
                statement.setString(11, "[\"ROLE_CLIENT\"]");
            } else if ("responsable de cinema".equals(user.getRole())) {
                statement.setString(11, "[\"ROLE_CINEMAMANAGER\"]");
            }
            statement.executeUpdate();
            UserService.LOGGER.info("user was added");
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
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
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final User user) {
        try {
            final PreparedStatement statement = con.prepareStatement(
                    "UPDATE users SET  nom=?,prenom=?,num_telephone=?,password=?,role=?,adresse=?,date_de_naissance=?,email=?,photo_de_profil=? WHERE id=?");
            UserService.LOGGER.info(user.toString());
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPhoneNumber());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole());
            statement.setString(6, user.getAddress());
            statement.setDate(7, user.getBirthDate());
            statement.setString(8, user.getEmail());
            statement.setString(9, user.getPhotoDeProfil());
            statement.setLong(10, user.getId().longValue());
            statement.executeUpdate();
            UserService.LOGGER.info("user is updated");
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final User user) {
        try {
            final PreparedStatement statement = con.prepareStatement("DELETE FROM users WHERE id = ?");
            statement.setLong(1, user.getId().intValue());
            statement.executeUpdate();
            UserService.LOGGER.info("user is deleted");
        } catch (final SQLException e) {
            UserService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Sends an email to the specified recipient.
     *
     * @param Recipient     the email address of the recipient
     * @param messageToSend the message content to send
     */
    public void sendMail(final String Recipient, final String messageToSend) {
        UserMail.send(Recipient, messageToSend);
    }

    /**
     * Generates a PDF report of all users sorted by role.
     */
    public void generateUserPDF() {
        final UserPDF userPDF = new UserPDF();
        userPDF.generate(sort("role"));
    }

    /**
     * Performs sort operation.
     *
     * @return the result of the operation
     */
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

    /**
     * @param userList
     * @param statement
     * @return List<User>
     * @throws SQLException
     */
    private List<User> getUsers(final List<User> userList, final PreparedStatement statement) throws SQLException {
        final ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            final String role = resultSet.getString("role");
            User user = switch (role) {
                case "admin":
                    yield new Admin(resultSet.getString("nom"), resultSet.getString("prenom"),
                            resultSet.getString("num_telephone"), resultSet.getString("password"),
                            resultSet.getString("role"), resultSet.getString("adresse"),
                            resultSet.getDate("date_de_naissance"), resultSet.getString("email"),
                            resultSet.getString("photo_de_profil"));
                case "client":
                    yield new Client(resultSet.getString("nom"), resultSet.getString("prenom"),
                            resultSet.getString("num_telephone"), resultSet.getString("password"),
                            resultSet.getString("role"), resultSet.getString("adresse"),
                            resultSet.getDate("date_de_naissance"), resultSet.getString("email"),
                            resultSet.getString("photo_de_profil"));
                case "responsable de cinema":
                    yield new CinemaManager(resultSet.getString("nom"), resultSet.getString("prenom"),
                            resultSet.getString("num_telephone"), resultSet.getString("password"),
                            resultSet.getString("role"), resultSet.getString("adresse"),
                            resultSet.getDate("date_de_naissance"), resultSet.getString("email"),
                            resultSet.getString("photo_de_profil"));
                default:
                    yield null;
            };
            if (user != null) {
                user.setId(resultSet.getLong("id"));
                userList.add(user);
            }
        }
        return userList;
    }

    /**
     * Performs checkEmailFound operation.
     *
     * @return the result of the operation
     */
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

    /**
     * Updates the password for a user with the specified email.
     *
     * @param email       the email of the user whose password to update
     * @param NewPassword the new password to set
     */
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

    /**
     * Handles forgotten password by sending a password reset email.
     *
     * @param email    the email address of the user who forgot their password
     * @param Password the password parameter (unused in current implementation)
     */
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

    /**
     * @param preparedStatement
     * @return User
     * @throws SQLException
     */
    private User getUserRow(final PreparedStatement preparedStatement) throws SQLException {
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            final String role = resultSet.getString("role");
            UserService.LOGGER.info(role);
            User user = switch (role.trim()) {
                case "admin":
                    yield new Admin(resultSet.getString("nom"), resultSet.getString("prenom"),
                            resultSet.getString("num_telephone"), resultSet.getString("password"),
                            resultSet.getString("role"), resultSet.getString("adresse"),
                            resultSet.getDate("date_de_naissance"), resultSet.getString("email"),
                            resultSet.getString("photo_de_profil"));
                case "client":
                    yield new Client(resultSet.getString("nom"), resultSet.getString("prenom"),
                            resultSet.getString("num_telephone"), resultSet.getString("password"),
                            resultSet.getString("role"), resultSet.getString("adresse"),
                            resultSet.getDate("date_de_naissance"), resultSet.getString("email"),
                            resultSet.getString("photo_de_profil"));
                case "responsable de cinema":
                    yield new CinemaManager(resultSet.getString("nom"), resultSet.getString("prenom"),
                            resultSet.getString("num_telephone"), resultSet.getString("password"),
                            resultSet.getString("role"), resultSet.getString("adresse"),
                            resultSet.getDate("date_de_naissance"), resultSet.getString("email"),
                            resultSet.getString("photo_de_profil"));
                default:
                    yield null;
            };
            if (user != null) {
                user.setId(resultSet.getLong("id"));
            }
            return user;
        }
        return null;
    }

    /**
     * Performs login operation.
     *
     * @return the result of the operation
     */
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
