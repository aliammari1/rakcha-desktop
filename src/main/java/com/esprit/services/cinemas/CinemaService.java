package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.User;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CinemaService implements IService<Cinema> {

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id", "name", "address", "manager_id", "logo_url", "status"
    };
    private final Connection connection;
    private final UserService userService;

    /**
     * Initialize the service by obtaining a database connection, creating a
     * UserService, and ensuring the cinema table exists.
     *
     * <p>
     * Establishes the JDBC connection used by the service, instantiates
     * dependencies, and creates the `cinema` table
     * if it does not already exist.
     * </p>
     */
    public CinemaService() {
        this.connection = DataSource.getInstance().getConnection();
        this.userService = new UserService();
    }

    /**
     * Insert a new Cinema record into the database.
     * <p>
     * If the cinema's status is null, the status is set to "Pending" before
     * insertion.
     *
     * @param cinema the Cinema to persist; must have a non-null manager with a
     *               non-null id
     * @throws IllegalArgumentException if the cinema has no manager or the manager
     *                                  has no id
     * @throws RuntimeException         if a database error prevents creating the
     *                                  cinema
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(Cinema cinema) {
        String query = "INSERT INTO cinemas (name, address, manager_id, logo_url, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cinema.getName());
            stmt.setString(2, cinema.getAddress());

            // Handle null manager case
            if (cinema.getManager() != null && cinema.getManager().getId() != null) {
                stmt.setLong(3, cinema.getManager().getId());
            } else {
                log.warn("Cinema manager is null for cinema: " + cinema.getName());
                throw new IllegalArgumentException("Cinema must have a valid manager with an ID");
            }

            stmt.setString(4, cinema.getLogoUrl());
            stmt.setString(5, cinema.getStatus() != null ? cinema.getStatus() : "Pending");
            stmt.executeUpdate();
            log.info("Cinema created successfully");
        } catch (SQLException e) {
            log.error("Error creating cinema", e);
            throw new RuntimeException("Failed to create cinema", e);
        }

    }

    /**
     * Update the database record identified by the given cinema's id.
     * <p>
     * Updates the record's name, address, logoPath, and status to match the
     * provided Cinema object.
     *
     * @param cinema the Cinema whose id identifies the record to update; its name,
     *               address, logoPath, and status will be saved
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(Cinema cinema) {
        String query = "UPDATE cinemas SET name = ?, address = ?, logo_url = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cinema.getName());
            stmt.setString(2, cinema.getAddress());
            stmt.setString(3, cinema.getLogoUrl());
            stmt.setString(4, cinema.getStatus());
            stmt.setLong(5, cinema.getId());
            stmt.executeUpdate();
            log.info("Cinema updated successfully");
        } catch (SQLException e) {
            log.error("Error updating cinema", e);
        }

    }

    /**
     * Deletes the specified cinema from the database using its id.
     *
     * @param cinema the Cinema whose `id` identifies the record to remove
     */
    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(Cinema cinema) {
        String query = "DELETE FROM cinemas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinema.getId());
            stmt.executeUpdate();
            log.info("Cinema deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting cinema", e);
        }

    }

    @Override
    /**
     * Retrieves cinemas with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of cinemas
     */
    public Page<Cinema> read(PageRequest pageRequest) {
        final List<Cinema> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM cinemas";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
                !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.getSortBy());
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }

        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (PreparedStatement stmt = connection.prepareStatement(paginatedQuery);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cinema cinema = buildCinema(rs);
                    if (cinema != null) {
                        content.add(cinema);
                    }

                }

            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated cinemas: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }

    /**
     * Retrieve a page of cinemas sorted by the specified column.
     * <p>
     * Validates the requested sort column against the allowed set; if invalid, the
     * result
     * uses the service's default sorting. The returned Page contains the cinemas
     * for the
     * requested page and page size.
     *
     * @param pageRequest pagination information (page index and page size)
     * @param orderBy     column name to sort by (must be one of the allowed sort
     *                    columns)
     * @return a Page of cinemas sorted by the requested column for the given page;
     *         if the
     *         sort column is invalid, the page is returned using the default sort.
     *         In case
     *         of a query error the page contains whatever rows were retrieved (may
     *         be empty)
     *         and its total count equals the number of returned items.
     */
    public Page<Cinema> sort(PageRequest pageRequest, String orderBy) {
        List<Cinema> cinemas = new ArrayList<>();

        // Validate sort column to prevent SQL injection
        if (!PaginationQueryBuilder.isValidSortColumn(orderBy, ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting by id.", orderBy);
            return read(pageRequest); // Return default sorted results
        }

        String query = "SELECT * FROM cinemas ORDER BY " + orderBy + " LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, pageRequest.getSize());
            stmt.setInt(2, pageRequest.getPage() * pageRequest.getSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cinema cinema = buildCinema(rs);
                    if (cinema != null) {
                        cinemas.add(cinema);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error sorting cinemas", e);
        }

        return new Page<>(cinemas, pageRequest.getPage(), pageRequest.getSize(), cinemas.size());
    }

    /**
     * Retrieves a cinema by its ID.
     *
     * @param cinemaId the ID of the cinema to retrieve
     * @return the cinema with the specified ID, or null if not found
     */
    public Cinema getCinemaById(Long cinemaId) {
        String query = "SELECT * FROM cinemas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildCinema(rs);
            }

        } catch (SQLException e) {
            log.error("Error getting cinema by id: " + cinemaId, e);
        }

        return null;
    }

    /**
     * Retrieves a cinema by its name.
     *
     * @param name the name of the cinema to retrieve
     * @return the cinema with the specified name, or null if not found
     */
    public Cinema getCinemaByName(String name) {
        String query = "SELECT * FROM cinemas WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildCinema(rs);
            }

        } catch (SQLException e) {
            log.error("Error getting cinema by name: " + name, e);
        }

        return null;
    }

    /**
     * Construct a Cinema object from the current row of the given ResultSet.
     *
     * @param rs the ResultSet positioned at the row to map; must contain columns:
     *           id, name, address, manager_id, logo_path, status
     * @return the mapped Cinema, or `null` if the referenced manager is not found
     *         or a SQL error occurs
     */
    private Cinema buildCinema(ResultSet rs) {
        try {
            User user = userService.getUserById(rs.getLong("manager_id"));
            CinemaManager manager = null;
            if (user instanceof CinemaManager) {
                manager = (CinemaManager) user;
            } else if (user != null) {
                log.warn("User with id " + rs.getLong("manager_id") + " is associated with cinema " + rs.getLong("id")
                        + " but is not a CinemaManager (Role: " + user.getRole() + ")");
                // Manager remains null to avoid ClassCastException in model
            }

            if (manager == null && user == null) {
                log.warn("Cinema manager not found for cinema id: " + rs.getLong("id"));
                // We choose to return null for the cinema or return cinema with null manager?
                // Returning null cinema hides it. Returning cinema with null manager shows it
                // but empty manager field.
                // User said "i dont see all cinemas", so we should probably return the cinema
                // even if manager is invalid.
            }
            // Continuing to build cinema, manager might be null
            return Cinema.builder().id(rs.getLong("id")).name(rs.getString("name")).address(rs.getString("address"))
                    .manager(manager).logoUrl(rs.getString("logo_url")).status(rs.getString("status")).build();
        } catch (SQLException e) {
            log.error("Error building cinema from ResultSet", e);
            return null;
        }

    }

    /**
     * Checks if a cinema with the specified ID exists in the database.
     *
     * @param id the ID of the cinema to check
     * @return true if the cinema exists, false otherwise
     */
    @Override
    public boolean exists(Long id) {
        String query = "SELECT COUNT(*) FROM cinemas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            log.error("Error checking if cinema exists with id: " + id, e);
        }
        return false;
    }

    /**
     * Retrieves a cinema by its ID.
     *
     * @param id the ID of the cinema to retrieve
     * @return the cinema with the specified ID, or null if not found
     */
    @Override
    public Cinema getById(Long id) {
        return getCinemaById(id);
    }

    /**
     * Retrieves all cinemas from the database.
     *
     * @return a list of all cinemas
     */
    @Override
    public List<Cinema> getAll() {
        List<Cinema> cinemas = new ArrayList<>();
        String query = "SELECT * FROM cinemas";
        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cinema cinema = buildCinema(rs);
                if (cinema != null) {
                    cinemas.add(cinema);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all cinemas", e);
        }
        return cinemas;
    }

    /**
     * Get all cinemas (alias for compatibility).
     *
     * @return list of all cinemas
     */
    public List<Cinema> getAllCinemas() {
        return getAll();
    }

    /**
     * Returns the total number of cinemas in the database.
     *
     * @return the count of cinemas
     */
    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM cinemas";
        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting cinemas", e);
        }
        return 0;
    }

    /**
     * Searches for cinemas by name.
     *
     * @param keyword the search keyword to match against cinema names
     * @return a list of cinemas matching the search keyword
     */
    @Override
    public List<Cinema> search(String keyword) {
        List<Cinema> cinemas = new ArrayList<>();
        String query = "SELECT * FROM cinemas WHERE name LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cinema cinema = buildCinema(rs);
                    if (cinema != null) {
                        cinemas.add(cinema);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error searching cinemas with keyword: " + keyword, e);
        }
        return cinemas;
    }

}
