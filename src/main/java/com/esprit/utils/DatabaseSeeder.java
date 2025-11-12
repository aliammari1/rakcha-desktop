package com.esprit.utils;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.Seat;
import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import com.esprit.models.products.Product;
import com.esprit.models.products.ProductCategory;
import com.esprit.models.series.Episode;
import com.esprit.models.series.Series;
import com.esprit.models.users.Admin;
import com.esprit.models.users.Client;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.User;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.SeatService;
import com.esprit.services.films.ActorService;
import com.esprit.services.films.FilmService;
import com.esprit.services.products.CategoryService;
import com.esprit.services.products.ProductService;
import com.esprit.services.series.IServiceEpisodeImpl;
import com.esprit.services.series.IServiceSeriesImpl;
import com.esprit.services.users.UserService;
import com.github.javafaker.Faker;

import lombok.extern.slf4j.Slf4j;

/**
 * Enhanced database seeder utility using JavaFaker for generating realistic
 * fake data
 * for the RAKCHA application.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class DatabaseSeeder {

    private final Faker faker = new Faker();
    private final Random random = new Random();

    // Services
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final FilmService filmService = new FilmService();
    private final ActorService actorService = new ActorService();
    private final CinemaService cinemaService = new CinemaService();
    private final CinemaHallService cinemaHallService = new CinemaHallService();
    private final SeatService seatService = new SeatService();
    private final IServiceSeriesImpl seriesService = new IServiceSeriesImpl();
    private final IServiceEpisodeImpl episodeService = new IServiceEpisodeImpl();

    /**
     * Seeds the database with realistic fake data using JavaFaker.
     */
    public void seedDatabase(int userCount, int productCount, int filmCount, int cinemaCount, int seriesCount) {
        log.info("Starting enhanced database seeding with JavaFaker...");

        try {
            seedUsers(userCount);
            seedProductCategories();
            seedProducts(productCount);
            seedActors(30);
            seedFilms(filmCount);
            seedCinemas(cinemaCount);
            seedCinemaHalls();
            seedSeats();
            seedSeries(seriesCount);
            seedEpisodes();

            log.info("Enhanced database seeding completed successfully!");

        } catch (Exception e) {
            log.error("Error during enhanced database seeding", e);
            throw new RuntimeException("Enhanced database seeding failed", e);
        }

    }


    /**
     * Seeds users with realistic fake data using JavaFaker.
     */
    private void seedUsers(int count) {
        log.info("Seeding {} users with JavaFaker...", count);

        String[] roles = { "client", "admin", "responsable de cinema" }
;

        for (int i = 0; i < count; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String role = roles[random.nextInt(roles.length)];
            String email = faker.internet().emailAddress();
            String address = faker.address().fullAddress();
            String phone = faker.phoneNumber().cellPhone();

            // Generate realistic birth date (18-65 years old)
            Date birthDate = new Date(faker.date().birthday(18, 65).getTime());

            User user = switch (role) {
                case "admin" -> new Admin(firstName, lastName, phone, "password123", role, address,
                        birthDate, email, faker.avatar().image());
                case "responsable de cinema" ->
                    new CinemaManager(firstName, lastName, phone, "password123", role, address,
                            birthDate, email, faker.avatar().image());
                default -> new Client(firstName, lastName, phone, "password123", role, address,
                        birthDate, email, faker.avatar().image());
            }
;

            userService.create(user);
        }


        log.info("Created {} users with realistic data", count);
    }


    /**
     * Seeds product categories with realistic names.
     */
    private void seedProductCategories() {
        log.info("Seeding product categories...");

        String[][] categories = {
                { "Electronics", "Electronic devices, gadgets and technology products" }
,
                { "Books & Media", "Books, magazines, audiobooks and educational materials" }
,
                { "Fashion & Clothing", "Clothing, shoes, accessories and fashion items" }
,
                { "Home & Garden", "Home improvement, furniture and gardening supplies" }
,
                { "Sports & Fitness", "Sports equipment, fitness gear and outdoor activities" }
,
                { "Toys & Games", "Toys, board games, video games and entertainment" }
,
                { "Health & Beauty", "Personal care, cosmetics and health products" }
,
                { "Automotive", "Car accessories, parts and automotive supplies" }

        }
;

        for (String[] category : categories) {
            ProductCategory productCategory = ProductCategory.builder()
                    .categoryName(category[0])
                    .description(category[1])
                    .build();
            categoryService.create(productCategory);
        }


        log.info("Created {} realistic product categories", categories.length);
    }


    /**
     * Seeds products with realistic fake data using JavaFaker.
     */
    private void seedProducts(int count) {
        log.info("Seeding {} products with JavaFaker...", count);

        for (int i = 0; i < count; i++) {
            String name = faker.commerce().productName();
            String description = faker.lorem().sentence(15);
            int price = Integer.parseInt(faker.commerce().price().replace(".", ""));
            int quantity = faker.number().numberBetween(10, 500);

            Product product = Product.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .image(faker.internet().image())
                    .build();

            productService.create(product);
        }


        log.info("Created {} products with realistic data", count);
    }


    /**
     * Seeds actors with realistic fake data using JavaFaker.
     */
    private void seedActors(int count) {
        log.info("Seeding {} actors with JavaFaker...", count);

        for (int i = 0; i < count; i++) {
            String name = faker.name().fullName();
            String biography = faker.lorem().paragraph(3);

            Actor actor = Actor.builder()
                    .name(name)
                    .biography(biography)
                    .image(faker.internet().avatar())
                    .numberOfAppearances(faker.number().numberBetween(1, 50))
                    .build();

            actorService.create(actor);
        }


        log.info("Created {} actors with realistic data", count);
    }


    /**
     * Seeds films with realistic fake data using JavaFaker.
     */
    private void seedFilms(int count) {
        log.info("Seeding {} films with JavaFaker...", count);

        for (int i = 0; i < count; i++) {
            String title = faker.funnyName().name() + ": " + faker.book().title();
            String description = faker.lorem().paragraph(2);
            int releaseYear = faker.number().numberBetween(1980, 2024);

            // Generate realistic movie duration (90-180 minutes)
            int totalMinutes = faker.number().numberBetween(90, 180);
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;

            Film film = Film.builder()
                    .name(title)
                    .description(description)
                    .releaseYear(releaseYear)
                    .duration(Time.valueOf(LocalTime.of(hours, minutes)))
                    .image(faker.internet().image())
                    .build();

            filmService.create(film);
        }


        log.info("Created {} films with realistic data", count);
    }


    /**
     * Seeds cinemas with realistic fake data using JavaFaker.
     */
    private void seedCinemas(int count) {
        log.info("Seeding {} cinemas with JavaFaker...", count);

        for (int i = 0; i < count; i++) {
            String name = "Cinema " + faker.company().name();
            String address = faker.address().fullAddress();

            Cinema cinema = Cinema.builder()
                    .name(name)
                    .address(address)
                    .logoPath(faker.internet().image())
                    .status("active")
                    .build();

            cinemaService.create(cinema);
        }


        log.info("Created {} cinemas with realistic data", count);
    }


    /**
     * Seeds cinema halls for all cinemas.
     */
    private void seedCinemaHalls() {
        log.info("Seeding cinema halls...");

        List<Cinema> cinemas = cinemaService.read(PageRequest.defaultPage()).getContent();
        int hallCount = 0;

        for (Cinema cinema : cinemas) {
            int numHalls = faker.number().numberBetween(2, 6); // 2-5 halls per cinema

            for (int i = 1; i <= numHalls; i++) {
                CinemaHall hall = CinemaHall.builder()
                        .name("Hall " + i)
                        .seatCapacity(faker.number().numberBetween(80, 250))
                        .cinema(cinema)
                        .build();

                cinemaHallService.create(hall);
                hallCount++;
            }

        }


        log.info("Created {} cinema halls", hallCount);
    }


    /**
     * Seeds seats for all cinema halls.
     */
    private void seedSeats() {
        log.info("Seeding seats...");

        List<CinemaHall> halls = cinemaHallService.read(PageRequest.defaultPage()).getContent();
        int totalSeats = 0;

        for (CinemaHall hall : halls) {
            int capacity = hall.getSeatCapacity();
            int seatsPerRow = 10;
            int rows = (capacity / seatsPerRow) + 1;
            int seatCounter = 0;

            for (int row = 1; row <= rows && seatCounter < capacity; row++) {
                for (int seatNum = 1; seatNum <= seatsPerRow && seatCounter < capacity; seatNum++) {
                    Seat seat = Seat.builder()
                            .rowNumber(row)
                            .seatNumber(seatNum)
                            .isOccupied(faker.bool().bool()) // Random occupancy
                            .cinemaHall(hall)
                            .build();

                    seatService.create(seat);
                    seatCounter++;
                    totalSeats++;
                }

            }

        }


        log.info("Created {} seats", totalSeats);
    }


    /**
     * Seeds series with realistic fake data using JavaFaker.
     */
    private void seedSeries(int count) {
        log.info("Seeding {} series with JavaFaker...", count);

        for (int i = 0; i < count; i++) {
            String name = faker.book().title() + " Series";
            String summary = faker.lorem().paragraph(2);

            Series series = Series.builder()
                    .name(name)
                    .summary(summary)
                    .director(faker.name().fullName())
                    .country(faker.country().name())
                    .image(faker.internet().image())
                    .liked(faker.number().numberBetween(0, 1000))
                    .numberOfLikes(faker.number().numberBetween(100, 5000))
                    .disliked(faker.number().numberBetween(0, 200))
                    .build();

            seriesService.create(series);
        }


        log.info("Created {} series with realistic data", count);
    }


    /**
     * Seeds episodes for all series.
     */
    private void seedEpisodes() {
        log.info("Seeding episodes...");

        List<Series> seriesList = seriesService.read(PageRequest.defaultPage()).getContent();
        int episodeCount = 0;

        for (Series series : seriesList) {
            int seasons = faker.number().numberBetween(1, 5); // 1-4 seasons

            for (int season = 1; season <= seasons; season++) {
                int episodesInSeason = faker.number().numberBetween(6, 15); // 6-14 episodes

                for (int episodeNum = 1; episodeNum <= episodesInSeason; episodeNum++) {
                    String title = "S" + season + "E" + episodeNum + ": " + faker.book().title();

                    Episode episode = Episode.builder()
                            .title(title)
                            .episodeNumber(episodeNum)
                            .season(season)
                            .seriesId(Math.toIntExact(series.getId()))
                            .image(faker.internet().image())
                            .video("episode_s" + season + "e" + episodeNum + ".mp4")
                            .build();

                    episodeService.create(episode);
                    episodeCount++;
                }

            }

        }


        log.info("Created {} episodes", episodeCount);
    }


    /**
     * Quick seed method for development/testing with realistic data.
     */
    public void quickSeed() {
        log.info("Starting quick seed with realistic fake data...");
        seedDatabase(100, 200, 50, 8, 15);
    }


    /**
     * Minimal seed for basic testing.
     */
    public void minimalSeed() {
        log.info("Starting minimal seed...");
        seedDatabase(20, 50, 15, 3, 5);
    }


    /**
     * Full seed for comprehensive testing.
     */
    public void fullSeed() {
        log.info("Starting full seed...");
        seedDatabase(500, 1000, 200, 20, 50);
    }

}

