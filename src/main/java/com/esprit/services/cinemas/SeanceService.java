package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.films.Filmcinema;
import com.esprit.services.IService;
import com.esprit.services.films.FilmService;
import com.esprit.services.films.FilmcinemaService; // Add this import
import com.esprit.utils.DataSource;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeanceService implements IService<Seance> {
    private static final Logger LOGGER = Logger.getLogger(SeanceService.class.getName());
    private final Connection connection;

    public SeanceService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param seance
     */
    @Override
    public void create(final Seance seance) {
        final String req = "INSERT into seance(id_film, id_salle, HD, HF, date, id_cinema, prix) values (?, ?, ?, ?, ?, ?, ?);";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, seance.getFilmcinema().getId_film().getId());
            pst.setInt(2, seance.getSalle().getId_salle());
            pst.setTime(3, seance.getHD());
            pst.setTime(4, seance.getHF());
            pst.setDate(5, seance.getDate());
            pst.setInt(6, seance.getFilmcinema().getId_cinema().getId_cinema());
            pst.setDouble(7, seance.getPrix());
            pst.executeUpdate();
            SeanceService.LOGGER.info("Seance ajoutée !");
        } catch (final SQLException e) {
            SeanceService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param seance
     */
    @Override
    public void update(final Seance seance) {
        final String req = """
                UPDATE seance \
                JOIN cinema ON seance.id_cinema = cinema.id_cinema \
                JOIN film ON seance.id_film = film.id \
                JOIN salle ON seance.id_salle = salle.id_salle \
                SET seance.id_film = ?, seance.id_salle = ?, seance.HD = ?, seance.HF = ?, seance.date = ?, \
                seance.id_cinema = ?, seance.prix = ? \
                WHERE seance.id_seance = ?;\
                """;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, seance.getFilmcinema().getId_film().getId());
            pst.setInt(2, seance.getSalle().getId_salle());
            pst.setTime(3, seance.getHD());
            pst.setTime(4, seance.getHF());
            pst.setDate(5, seance.getDate());
            pst.setInt(6, seance.getFilmcinema().getId_cinema().getId_cinema());
            pst.setDouble(7, seance.getPrix());
            pst.setInt(8, seance.getId_seance());
            pst.executeUpdate();
            SeanceService.LOGGER.info("Seance modifiée !");
        } catch (final SQLException e) {
            SeanceService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final Seance seance) {
        final String req = "DELETE from seance where id_seance= ?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, seance.getId_seance());
            pst.executeUpdate();
            SeanceService.LOGGER.info("Seance supprmiée !");
        } catch (final SQLException e) {
            SeanceService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public List<Seance> readLoujain(final int id_film, final int id_cinema) {
        final List<Seance> seances = new ArrayList<>();
        final String req = "SELECT * FROM seance where id_film = ? AND id_cinema = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, id_film);
            pst.setInt(2, id_cinema);
            final ResultSet rs = pst.executeQuery();
            final CinemaService cs = new CinemaService();
            final SalleService ss = new SalleService();
            final FilmService fs = new FilmService();
            final int i = 0;
            while (rs.next()) {
                seances.add(new Seance(rs.getInt("id_seance"), ss.getSalle(rs.getInt("id_salle")), rs.getTime("HD"),
                        rs.getTime("HF"), rs.getDate("date"), rs.getInt("prix"),
                        new Filmcinema(fs.getFilm(rs.getInt("id_film")), cs.getCinema(rs.getInt("id_cinema")))));
            }
        } catch (final SQLException e) {
            SeanceService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return seances;
    }

    @Override
    public List<Seance> read() {
        final List<Seance> seances = new ArrayList<>();
        final String req = """
                SELECT seance.*, cinema.*, film.*, salle.* \
                FROM seance \
                JOIN cinema ON seance.id_cinema = cinema.id_cinema \
                JOIN film ON seance.id_film = film.id \
                JOIN salle ON seance.id_salle = salle.id_salle\
                """;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final CinemaService cs = new CinemaService();
            final SalleService ss = new SalleService();
            final FilmService fs = new FilmService();
            final int i = 0;
            while (rs.next()) {
                seances.add(new Seance(rs.getInt("id_seance"), ss.getSalle(rs.getInt("id_salle")), rs.getTime("HD"),
                        rs.getTime("HF"), rs.getDate("date"), rs.getInt("prix"),
                        new Filmcinema(fs.getFilm(rs.getInt("id_film")), cs.getCinema(rs.getInt("id_cinema")))));
            }
        } catch (final SQLException e) {
            SeanceService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return seances;
    }

    // Méthode pour récupérer les séances dans une plage de dates spécifiée
    public Map<LocalDate, List<Seance>> getSeancesByDateRangeAndCinema(final LocalDate startDate,
            final LocalDate endDate,
            final Cinema cinema) {
        final Map<LocalDate, List<Seance>> seancesByDate = new HashMap<>();
        // Vérifier si cinema est null
        if (null == cinema) {
            SeanceService.LOGGER.info("Erreur : cinema est null");
            return seancesByDate;
        }
        try {
            // Créer la requête SQL pour récupérer les séances dans la plage de dates
            // spécifiée et pour le cinéma donné
            final String query = "SELECT * FROM seance WHERE date BETWEEN ? AND ? AND id_cinema = ?";
            final PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setDate(1, Date.valueOf(startDate)); // Utilisez java.sql.Date.valueOf
            statement.setDate(2, Date.valueOf(endDate)); // Utilisez java.sql.Date.valueOf
            statement.setInt(3, cinema.getId_cinema()); // Supposons que getId() renvoie l'identifiant du cinéma
            // Exécuter la requête
            final ResultSet rs = statement.executeQuery();
            final CinemaService cs = new CinemaService();
            final SalleService ss = new SalleService();
            final FilmService fs = new FilmService();
            // Parcourir les résultats de la requête et créer des objets Seance
            while (rs.next()) {
                final LocalDate seanceDate = rs.getDate("date").toLocalDate(); // Convertir java.sql.Date en
                // java.time.LocalDate
                final List<Seance> seancesForDate = seancesByDate.getOrDefault(seanceDate, new ArrayList<>());
                seancesForDate.add(new Seance(rs.getInt("id_seance"), ss.getSalle(rs.getInt("id_salle")),
                        rs.getTime("HD"), rs.getTime("HF"), seanceDate, rs.getInt("prix"),
                        new Filmcinema(fs.getFilm(rs.getInt("id_film")), cs.getCinema(rs.getInt("id_cinema")))));
                seancesByDate.put(seanceDate, seancesForDate);
            }
        } catch (final SQLException e) {
            SeanceService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return seancesByDate;
    }

    public List<Seance> getSeancesByDate(final LocalDate date) {
        // Exemple de liste de séances fictive
        final List<Seance> seances = new ArrayList<>();
        // Supposons que seanceList soit une liste de toutes les séances disponibles
        // Vous devez définir seanceList en fonction de votre logique d'accès aux
        // données
        final List<Seance> seanceList = null; // Logique pour récupérer les séances depuis la source de données
        // Si seanceList est null, retourner une liste vide
        if (null == seanceList) {
            return Collections.emptyList();
        }
        // Parcourir toutes les séances disponibles et ajouter celles pour la date
        // spécifiée à la liste
        for (final Seance seance : seanceList) {
            if (seance.getDate().equals(date)) {
                seances.add(seance);
            }
        }
        // Retourner la liste des séances pour la date spécifiée
        return seances;
    }

    public Seance getFirstSeanceForFilm(int filmId) {
        String query = "SELECT s.* FROM seance s " +
                "JOIN film_cinema fc ON s.id_film = fc.film_id " + // Changed from filmcinema to film_cinema
                "WHERE fc.film_id = ? " +
                "AND s.date >= CURRENT_DATE " +
                "ORDER BY s.date, s.HD LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Seance(
                        rs.getInt("id_seance"),
                        new SalleService().getSalleById(rs.getInt("id_salle")),
                        rs.getTime("HD"),
                        rs.getTime("HF"),
                        rs.getDate("date"),
                        rs.getDouble("prix"),
                        new FilmcinemaService().getById(rs.getInt("id_film")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting first seance for film: " + filmId, e);
        }
        return null;
    }
}
