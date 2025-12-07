package com.esprit.services.users;

import com.esprit.enums.UserRole;
import com.esprit.models.users.Admin;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import com.esprit.utils.UserMail;
import com.esprit.utils.UserPDF;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "first_name", "last_name", "phone_number", "role", "address", "birth_date", "email"
    };
    Connection con;

    /**
     * Constructs a new UserService instance.
     *
     */
    public UserService() {
        this.con = DataSource.getInstance().getConnection();
    }

    /**
     *
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
            log.error(e.getMessage(), e);
        }
        return user;
    }

    /**
     * @param email
     * @return User
     */
    public User getUserByEmail(final String email) {
        final String req = "select * from users where email = ?";
        User user = null;
        try {
            final PreparedStatement preparedStatement = this.con.prepareStatement(req);
            preparedStatement.setString(1, email);
            user = this.getUserRow(preparedStatement);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
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
                "INSERT INTO users (first_name, last_name, email, phone_number, password_hash, role, address, birth_date, profile_picture_url, is_verified, totp_secret, failed_login_attempts, is_locked, locked_until, is_active, deactivated_at, deactivation_reason) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhoneNumber());
            String hashedPassword = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());
            statement.setString(5, hashedPassword);
            statement.setString(6, user.getRole().name());
            statement.setString(7, user.getAddress());
            statement.setDate(8, user.getBirthDate());
            statement.setString(9, user.getProfilePictureUrl());
            statement.setBoolean(10, false);
            statement.setNull(11, java.sql.Types.VARCHAR);
            statement.setInt(12, 0);
            statement.setBoolean(13, false);
            statement.setNull(14, java.sql.Types.TIMESTAMP);
            statement.setBoolean(15, true);
            statement.setNull(16, java.sql.Types.TIMESTAMP);
            statement.setNull(17, java.sql.Types.VARCHAR);
            statement.executeUpdate();
            log.info("user was added");
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    /**
     * Retrieves users with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of users
     */
    public Page<User> read(PageRequest pageRequest) {
        final List<User> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM users";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
            !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.getSortBy());
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }

        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(con, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);
            final PreparedStatement statement = con.prepareStatement(paginatedQuery);

            getUsers(content, statement);

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated users: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }
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
                "UPDATE users SET first_name=?, last_name=?, email=?, phone_number=?, password_hash=?, role=?, address=?, birth_date=?, profile_picture_url=?, is_verified=?, totp_secret=?, failed_login_attempts=?, is_locked=?, locked_until=?, is_active=?, deactivated_at=?, deactivation_reason=? WHERE id=?");
            log.info(user.toString());
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhoneNumber());
            statement.setString(5, user.getPasswordHash());
            statement.setString(6, user.getRole().name());
            statement.setString(7, user.getAddress());
            statement.setDate(8, user.getBirthDate());
            statement.setString(9, user.getProfilePictureUrl());
            statement.setBoolean(10, user.isVerified());
            statement.setString(11, user.getTotpSecret());
            statement.setInt(12, user.getFailedLoginAttempts());
            statement.setBoolean(13, user.isLocked());
            if (user.getLockedUntil() != null) {
                statement.setTimestamp(14, user.getLockedUntil());
            } else {
                statement.setNull(14, java.sql.Types.TIMESTAMP);
            }
            statement.setBoolean(15, user.isActive());
            if (user.getDeactivatedAt() != null) {
                statement.setTimestamp(16, user.getDeactivatedAt());
            } else {
                statement.setNull(16, java.sql.Types.TIMESTAMP);
            }
            statement.setString(17, user.getDeactivationReason());
            statement.setLong(18, user.getId().longValue());
            statement.setString(17, user.getDeactivationReason());
            statement.setLong(18, user.getId().longValue());
            statement.executeUpdate();
            log.info("user is updated");
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
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
        if (user == null || user.getId() == null) {
            log.warn("Cannot delete null user or user without ID");
            return;
        }
        try {
            final PreparedStatement statement = con.prepareStatement("DELETE FROM users WHERE id = ?");
            statement.setLong(1, user.getId());
            statement.executeUpdate();
            log.info("user is deleted");
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
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
            final UserRole role = UserRole.valueOf(resultSet.getString("role"));
            User user = switch (role) {
                case UserRole.ADMIN:
                    yield new Admin(resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                        role, resultSet.getString("address"),
                        resultSet.getDate("birth_date"), resultSet.getString("email"),
                        resultSet.getString("profile_picture_url"));
                case UserRole.CLIENT:
                    yield new Client(resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                        role, resultSet.getString("address"),
                        resultSet.getDate("birth_date"), resultSet.getString("email"),
                        resultSet.getString("profile_picture_url"));
                case UserRole.CINEMA_MANAGER:
                    yield new CinemaManager(resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                        role, resultSet.getString("address"),
                        resultSet.getDate("birth_date"), resultSet.getString("email"),
                        resultSet.getString("profile_picture_url"));
                default:
                    yield null;
            };
            if (user != null) {
                user.setId(resultSet.getLong("id"));
                user.setVerified(resultSet.getBoolean("is_verified"));
                user.setActive(resultSet.getBoolean("is_active"));
                user.setFailedLoginAttempts(resultSet.getInt("failed_login_attempts"));
                user.setLocked(resultSet.getBoolean("is_locked"));
                user.setLockedUntil(resultSet.getTimestamp("locked_until") != null ?
                    resultSet.getTimestamp("locked_until") : null);
                user.setTotpSecret(resultSet.getString("totp_secret"));
                user.setDeactivationReason(resultSet.getString("deactivation_reason"));
                user.setDeactivatedAt(resultSet.getTimestamp("deactivated_at") != null ?
                    resultSet.getTimestamp("deactivated_at") : null);
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
            log.info("checkEmailFound: " + e.getMessage());
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
        if (email == null || email.isEmpty() || NewPassword == null || NewPassword.isEmpty()) {
            log.warn("Email and password cannot be null or empty");
            return;
        }
        try {
            String hashedPassword = BCrypt.hashpw(NewPassword, BCrypt.gensalt());
            final PreparedStatement statement = con.prepareStatement("UPDATE users SET password_hash=? where email=? ");
            statement.setString(1, hashedPassword);
            statement.setString(2, email);
            statement.executeUpdate();
            log.info("user password is updated");
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Handles forgotten password by sending a password reset email.
     *
     * @param email    the email address of the user who forgot their password
     * @param Password the password parameter (unused in current implementation)
     */
    public void forgetPassword(final String email, final String Password) {
        if (email == null || email.isEmpty()) {
            log.warn("Email cannot be null or empty");
            return;
        }
        final String query = "select * from users where email = ?";
        try {
            final PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, email);
            final User user = this.getUserRow(preparedStatement);
            if (null != user) {
                this.sendMail(user.getEmail(),
                    "Your password reset request has been received. Please check your email for instructions to reset your password.");
            } else {
                final Alert alert = new Alert(Alert.AlertType.ERROR,
                    "The user was not found. Please verify your email address.", ButtonType.CLOSE);
                alert.show();
            }
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
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
            final UserRole role = UserRole.valueOf(resultSet.getString("role"));
            log.info(role.name());
            User user = switch (role) {
                case UserRole.ADMIN -> new Admin(resultSet.getString("first_name"), resultSet.getString("last_name"),
                    resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                    role, resultSet.getString("address"),
                    resultSet.getDate("birth_date"), resultSet.getString("email"),
                    resultSet.getString("profile_picture_url"));
                case UserRole.CLIENT -> new Client(resultSet.getString("first_name"), resultSet.getString("last_name"),
                    resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                    role, resultSet.getString("address"),
                    resultSet.getDate("birth_date"), resultSet.getString("email"),
                    resultSet.getString("profile_picture_url"));
                case UserRole.CINEMA_MANAGER ->
                    new CinemaManager(resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                        role, resultSet.getString("address"),
                        resultSet.getDate("birth_date"), resultSet.getString("email"),
                        resultSet.getString("profile_picture_url"));
                default -> null;
            };
            if (user != null) {
                user.setId(resultSet.getLong("id"));
                user.setVerified(resultSet.getBoolean("is_verified"));
                user.setActive(resultSet.getBoolean("is_active"));
                user.setFailedLoginAttempts(resultSet.getInt("failed_login_attempts"));
                user.setLocked(resultSet.getBoolean("is_locked"));
                user.setLockedUntil(resultSet.getTimestamp("locked_until") != null ?
                    resultSet.getTimestamp("locked_until") : null);
                user.setTotpSecret(resultSet.getString("totp_secret"));
                user.setDeactivationReason(resultSet.getString("deactivation_reason"));
                user.setDeactivatedAt(resultSet.getTimestamp("deactivated_at") != null ?
                    resultSet.getTimestamp("deactivated_at") : null);
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
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            log.warn("Email and password cannot be null or empty");
            return null;
        }

        User user = this.getUserByEmail(email);
        if (null == user) {
            log.info("User not found with email: " + email);
            return null;
        }

        try {
            String storedHash = user.getPasswordHash();
            if (storedHash == null || storedHash.isEmpty()) {
                log.warn("Stored password hash is null or empty");
                return null;
            }

            if (BCrypt.checkpw(password, storedHash)) {
                log.info("Password matches for user: " + email);
                return user;
            } else {
                log.info("Password does not match for user: " + email);
                return null;
            }
        } catch (final Exception e) {
            log.error("Error during login", e);
            return null;
        }
    }

    /**
     * Changes the password for a user.
     *
     * @param userId          the ID of the user
     * @param newPassword     the new password
     * @param confirmPassword the confirmation password
     * @return true if password was changed successfully, false otherwise
     */
    public boolean changePassword(Long userId, String newPassword, String confirmPassword) {
        if (userId == null || userId <= 0) {
            log.warn("Invalid user ID for password change");
            return false;
        }

        if (newPassword == null || newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            log.warn("Passwords do not match or are empty");
            return false;
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String query = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, hashedPassword);
            stmt.setLong(2, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                log.info("Password changed successfully for user: " + userId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Error changing password for user " + userId, e);
        }

        return false;
    }

    /**
     * Deletes a user account after password verification.
     *
     * @param userId   the ID of the user
     * @param password the user's password for verification
     * @return true if account was deleted successfully, false otherwise
     */
    public boolean deleteAccount(Long userId, String password) {
        if (userId == null || userId <= 0 || password == null || password.isEmpty()) {
            log.warn("Invalid user ID or password for account deletion");
            return false;
        }

        // First verify the password
        User user = getUserById(userId);
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
            log.warn("Password verification failed for user deletion: " + userId);
            return false;
        }

        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setLong(1, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                log.info("User account deleted: " + userId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Error deleting user account " + userId, e);
        }

        return false;
    }

    /**
     * Counts the total number of users in the database.
     *
     * @return the total count of users
     */
    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM users";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting users", e);
        }
        return 0;
    }

    /**
     * Retrieves a user by its ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID, or null if not found
     */
    @Override
    public User getById(final Long id) {
        return getUserById(id);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users
     */
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = buildUserFromResultSet(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all users", e);
        }
        return users;
    }

    /**
     * Searches for users by name, email, or phone number.
     *
     * @param query the search query
     * @return a list of users matching the search query
     */
    @Override
    public List<User> search(final String query) {
        List<User> users = new ArrayList<>();
        final String req = "SELECT * FROM users WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone_number LIKE ? ORDER BY first_name";
        try (final PreparedStatement pst = this.con.prepareStatement(req)) {
            final String searchPattern = "%" + query + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);
            pst.setString(4, searchPattern);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                User user = buildUserFromResultSet(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (final SQLException e) {
            log.error("Error searching users", e);
        }
        return users;
    }

    /**
     * Checks if a user exists by its ID.
     *
     * @param id the ID of the user to check
     * @return true if the user exists, false otherwise
     */
    @Override
    public boolean exists(final Long id) {
        return getUserById(id) != null;
    }

    /**
     * Helper method to build a User from a ResultSet.
     *
     * @param resultSet the ResultSet containing user data (already positioned at a row)
     * @return the constructed User object
     * @throws SQLException if a database error occurs
     */
    private User buildUserFromResultSet(final ResultSet resultSet) throws SQLException {
        final UserRole role = UserRole.valueOf(resultSet.getString("role"));
        log.info(role.name());
        User user = switch (role) {
            case UserRole.ADMIN -> new Admin(resultSet.getString("first_name"), resultSet.getString("last_name"),
                resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                role, resultSet.getString("address"),
                resultSet.getDate("birth_date"), resultSet.getString("email"),
                resultSet.getString("profile_picture_url"));
            case UserRole.CLIENT -> new Client(resultSet.getString("first_name"), resultSet.getString("last_name"),
                resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                role, resultSet.getString("address"),
                resultSet.getDate("birth_date"), resultSet.getString("email"),
                resultSet.getString("profile_picture_url"));
            case UserRole.CINEMA_MANAGER ->
                new CinemaManager(resultSet.getString("first_name"), resultSet.getString("last_name"),
                    resultSet.getString("phone_number"), resultSet.getString("password_hash"),
                    role, resultSet.getString("address"),
                    resultSet.getDate("birth_date"), resultSet.getString("email"),
                    resultSet.getString("profile_picture_url"));
            default -> null;
        };
        if (user != null) {
            user.setId(resultSet.getLong("id"));
            user.setVerified(resultSet.getBoolean("is_verified"));
            user.setActive(resultSet.getBoolean("is_active"));
            user.setFailedLoginAttempts(resultSet.getInt("failed_login_attempts"));
            user.setLocked(resultSet.getBoolean("is_locked"));
            user.setLockedUntil(resultSet.getTimestamp("locked_until") != null ?
                resultSet.getTimestamp("locked_until") : null);
            user.setTotpSecret(resultSet.getString("totp_secret"));
            user.setDeactivationReason(resultSet.getString("deactivation_reason"));
            user.setDeactivatedAt(resultSet.getTimestamp("deactivated_at") != null ?
                resultSet.getTimestamp("deactivated_at") : null);
        }
        return user;
    }
}
