package com.esprit.utils;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// Models
import com.esprit.models.cinemas.*;
import com.esprit.models.films.*;
import com.esprit.models.products.*;
import com.esprit.models.series.*;
import com.esprit.models.users.*;

// Services
import com.esprit.services.cinemas.*;
import com.esprit.services.films.*;
import com.esprit.services.products.*;
import com.esprit.services.series.*;
import com.esprit.services.users.*;
import com.github.javafaker.Faker;

/**
 * Enhanced database seeder utility using JavaFaker for generating realistic
 * fake data for the RAKCHA application.
 * 
 * @author RAKCHA Team
 * @version 2.0.0
 * @since 1.0.0
 */
public class EnhancedDatabaseSeeder {

    private static final Logger log = Logger.getLogger(EnhancedDatabaseSeeder.class.getName());
    private final Faker faker = new Faker();
    private final Random random = new Random();

    // All Services
    // User Services
    private final UserService userService = new UserService();

    // Product Services
    private final ProductService productService = new ProductService();
    private final com.esprit.services.products.CategoryService productCategoryService = new com.esprit.services.products.CategoryService();
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final ShoppingCartService shoppingCartService = new ShoppingCartService();
    private final ReviewService reviewService = new ReviewService();
    private final com.esprit.services.products.CommentService productCommentService = new com.esprit.services.products.CommentService();

    // Cinema Services
    private final CinemaService cinemaService = new CinemaService();
    private final CinemaHallService cinemaHallService = new CinemaHallService();
    private final SeatService seatService = new SeatService();
    private final MovieSessionService movieSessionService = new MovieSessionService();
    private final CinemaRatingService cinemaRatingService = new CinemaRatingService();
    private final CinemaCommentService cinemaCommentService = new CinemaCommentService();

    // Film Services
    private final FilmService filmService = new FilmService();
    private final ActorService actorService = new ActorService();
    private final com.esprit.services.films.CategoryService filmCategoryService = new com.esprit.services.films.CategoryService();
    private final FilmCommentService filmCommentService = new FilmCommentService();
    private final FilmRatingService filmRatingService = new FilmRatingService();
    private final TicketService ticketService = new TicketService();

    // Series Services
    private final IServiceSeriesImpl seriesService = new IServiceSeriesImpl();
    private final IServiceEpisodeImpl episodeService = new IServiceEpisodeImpl();

    /**
     * Seeds the database with realistic fake data using JavaFaker.
     */
    public void seedDatabase(int userCount, int productCount, int filmCount, int cinemaCount, int seriesCount) {
        log.info("Starting comprehensive database seeding with JavaFaker...");

        try {
            // Base entities first
            seedUsers(userCount);
            seedProductCategories();
            seedFilmCategories();
            seedSeriesCategories();
            seedProducts(productCount);
            seedActors(30);
            seedFilms(filmCount);
            seedCinemas(cinemaCount);
            seedCinemaHalls();
            seedSeats();
            seedSeries(seriesCount);
            seedEpisodes();

            // Relational entities
            seedMovieSessions(20);
            seedTickets(50);
            seedOrders(userCount / 2);
            seedShoppingCarts(userCount / 3);
            seedReviews(productCount / 3);
            seedCinemaRatings(cinemaCount * 5);
            seedFilmRatings(filmCount * 3);
            seedComments();

            // Additional series features
            seedFavorites(userCount / 4);
            seedFeedbacks(seriesCount * 2);
            seedActorFilmAssociations();
            seedFilmCinemaAssociations();

            log.info("Comprehensive database seeding completed successfully!");

        } catch (Exception e) {
            log.severe("Error during comprehensive database seeding" + e.getMessage());
            throw new RuntimeException("Comprehensive database seeding failed", e);
        }

    }


    /**
     * Seeds specific entity types based on user choice
     */
    public void seedSpecific(String entityType, int count) {
        log.info("Seeding " + count + " " + entityType + " entities...");

        try {
            switch (entityType.toLowerCase()) {
                case "users":
                    seedUsers(count);
                    break;
                case "products":
                    seedProducts(count);
                    break;
                case "films":
                    seedFilms(count);
                    break;
                case "cinemas":
                    seedCinemas(count);
                    break;
                case "series":
                    seedSeries(count);
                    break;
                case "actors":
                    seedActors(count);
                    break;
                case "moviesessions":
                    seedMovieSessions(count);
                    break;
                case "tickets":
                    seedTickets(count);
                    break;
                case "orders":
                    seedOrders(count);
                    break;
                case "reviews":
                    seedReviews(count);
                    break;
                case "comments":
                    seedComments();
                    break;
                case "ratings":
                    seedCinemaRatings(count);
                    seedFilmRatings(count);
                    break;
                case "favorites":
                    seedFavorites(count);
                    break;
                case "feedbacks":
                    seedFeedbacks(count);
                    break;
                case "categories":
                    seedProductCategories();
                    seedFilmCategories();
                    seedSeriesCategories();
                    break;
                default:
                    log.warning("Unknown entity type: " + entityType);
            }

        } catch (Exception e) {
            log.severe("Error seeding " + entityType + " entities: " + e.getMessage());
            throw new RuntimeException("Failed to seed " + entityType, e);
        }

    }


    /**
     * Seeds users with realistic fake data using JavaFaker.
     */
    private void seedUsers(int count) {
        log.info("Seeding " + count + " users with JavaFaker...");

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


        log.info("Created " + count + " users with realistic data");
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
            productCategoryService.create(productCategory);
        }


        log.info("Created " + categories.length + " realistic product categories");
    }


    /**
     * Seeds products with realistic fake data using JavaFaker.
     */
    private void seedProducts(int count) {
        log.info("Seeding " + count + " products with JavaFaker...");

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


        log.info("Created " + count + " products with realistic data");
    }


    /**
     * Seeds actors with realistic fake data using JavaFaker.
     */
    private void seedActors(int count) {
        log.info("Seeding " + count + " actors with JavaFaker...");

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


        log.info("Created " + count + " actors with realistic data");
    }


    /**
     * Seeds films with realistic fake data using JavaFaker.
     */
    private void seedFilms(int count) {
        log.info("Seeding " + count + " films with JavaFaker...");

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


        log.info("Created " + count + " films with realistic data");
    }


    /**
     * Seeds cinemas with realistic fake data using JavaFaker.
     */
    private void seedCinemas(int count) {
        log.info("Seeding " + count + " cinemas with JavaFaker...");

        // Get available cinema managers
        List<User> allUsers = userService.read(PageRequest.defaultPage()).getContent();
        List<CinemaManager> cinemaManagers = allUsers.stream()
                .filter(user -> user instanceof CinemaManager)
                .map(user -> (CinemaManager) user)
                .collect(Collectors.toList());

        if (cinemaManagers.isEmpty()) {
            log.warning("No cinema managers found. Creating some cinema managers first...");
            // Create some cinema managers if none exist
            for (int i = 0; i < Math.min(count, 10); i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String email = faker.internet().emailAddress();
                String address = faker.address().fullAddress();
                String phone = faker.phoneNumber().cellPhone();
                Date birthDate = new Date(faker.date().birthday(25, 60).getTime());

                CinemaManager manager = new CinemaManager(firstName, lastName, phone, "password123",
                        "responsable de cinema", address, birthDate, email, faker.avatar().image());
                userService.create(manager);

                // Refresh the manager to get the ID assigned by the database
                List<User> updatedUsers = userService.read(PageRequest.defaultPage()).getContent();
                CinemaManager createdManager = (CinemaManager) updatedUsers.stream()
                        .filter(user -> user instanceof CinemaManager &&
                                user.getFirstName().equals(firstName) &&
                                user.getLastName().equals(lastName) &&
                                user.getEmail().equals(email))
                        .findFirst()
                        .orElse(manager);

                cinemaManagers.add(createdManager);
            }

            log.info("Created " + cinemaManagers.size() + " cinema managers");
        }


        for (int i = 0; i < count; i++) {
            String name = "Cinema " + faker.company().name();
            String address = faker.address().fullAddress();

            // Assign a random cinema manager (can assign same manager to multiple cinemas)
            CinemaManager manager = cinemaManagers.get(random.nextInt(cinemaManagers.size()));

            // Double-check that the manager has an ID
            if (manager.getId() == null) {
                log.warning("Manager " + manager.getFirstName() + " " + manager.getLastName()
                        + " has no ID, skipping cinema creation");
                continue;
            }


            Cinema cinema = Cinema.builder()
                    .name(name)
                    .address(address)
                    .manager(manager)
                    .logoPath(faker.internet().image())
                    .status("active")
                    .build();

            try {
                cinemaService.create(cinema);
                log.info("Created cinema: " + name + " with manager: " + manager.getFirstName() + " "
                        + manager.getLastName());
            } catch (Exception e) {
                log.severe("Failed to create cinema: " + name + " - " + e.getMessage());
            }

        }


        log.info("Created " + count + " cinemas with realistic data");
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


        log.info("Created " + hallCount + " cinema halls");
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


        log.info("Created " + totalSeats + " seats");
    }


    /**
     * Seeds series with realistic fake data using JavaFaker.
     */
    private void seedSeries(int count) {
        log.info("Seeding " + count + " series with JavaFaker...");

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


        log.info("Created " + count + " series with realistic data");
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


        log.info("Created " + episodeCount + " episodes");
    }


    /**
     * Seeds film categories with realistic data.
     */
    private void seedFilmCategories() {
        log.info("Seeding film categories...");

        String[] categories = {
                "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Fantasy",
                "Thriller", "Romance", "Animation", "Documentary", "Adventure",
                "Crime", "Family", "Musical", "Mystery", "War", "Western", "Biography"
        }
;

        for (String categoryName : categories) {
            try {
                com.esprit.models.films.Category category = com.esprit.models.films.Category.builder()
                        .name(categoryName)
                        .description("Popular " + categoryName.toLowerCase() + " films")
                        .build();
                filmCategoryService.create(category);
            } catch (Exception e) {
                log.warning("Category " + categoryName + " might already exist: " + e.getMessage());
            }

        }


        log.info("Created " + categories.length + " film categories");
    }


    /**
     * Seeds movie sessions with realistic data.
     */
    private void seedMovieSessions(int count) {
        log.info("Seeding " + count + " movie sessions...");

        List<Film> films = filmService.read(PageRequest.defaultPage()).getContent();
        List<Cinema> cinemas = cinemaService.read(PageRequest.defaultPage()).getContent();
        List<CinemaHall> halls = cinemaHallService.read(PageRequest.defaultPage()).getContent();

        if (films.isEmpty() || cinemas.isEmpty() || halls.isEmpty()) {
            log.warning("Need films, cinemas, and halls to create movie sessions");
            return;
        }


        for (int i = 0; i < count; i++) {
            Film randomFilm = films.get(random.nextInt(films.size()));
            CinemaHall randomHall = halls.get(random.nextInt(halls.size()));

            // Generate random date within next 30 days
            LocalDate sessionDate = LocalDate.now().plusDays(random.nextInt(30));

            // Generate random time
            int hour = random.nextInt(12) + 10; // 10 AM to 9 PM
            int minute = random.nextInt(2) * 30; // 0 or 30 minutes
            LocalTime sessionTime = LocalTime.of(hour, minute);

            double price = faker.number().randomDouble(2, 8, 25); // $8-25

            MovieSession session = MovieSession.builder()
                    .sessionDate(Date.valueOf(sessionDate))
                    .startTime(Time.valueOf(sessionTime))
                    .price(price)
                    .film(randomFilm)
                    .cinemaHall(randomHall)
                    .build();

            movieSessionService.create(session);
        }


        log.info("Created " + count + " movie sessions");
    }


    /**
     * Seeds tickets with realistic data.
     */
    private void seedTickets(int count) {
        log.info("Seeding " + count + " tickets...");

        List<User> users = userService.read(PageRequest.defaultPage()).getContent();
        List<MovieSession> sessions = movieSessionService.read(PageRequest.defaultPage()).getContent();
        List<CinemaHall> halls = cinemaHallService.read(PageRequest.defaultPage()).getContent();

        if (users.isEmpty() || sessions.isEmpty() || halls.isEmpty()) {
            log.warning("Need users, movie sessions, and cinema halls to create tickets");
            return;
        }


        // Get seats from all cinema halls
        List<Seat> seats = new ArrayList<>();
        for (CinemaHall hall : halls) {
            seats.addAll(seatService.getSeatsByCinemaHallId(hall.getId()));
        }


        if (seats.isEmpty()) {
            log.warning("No seats found to create tickets");
            return;
        }


        // Filter for clients only
        List<User> clients = users.stream()
                .filter(user -> "client".equals(user.getRole()))
                .toList();

        if (clients.isEmpty()) {
            log.warning("No client users found for ticket creation");
            return;
        }


        for (int i = 0; i < count; i++) {
            User randomClient = clients.get(random.nextInt(clients.size()));
            MovieSession randomSession = sessions.get(random.nextInt(sessions.size()));
            Seat randomSeat = seats.get(random.nextInt(seats.size()));

            // Convert User to Client
            Client client = Client.builder()
                    .id(randomClient.getId())
                    .firstName(randomClient.getFirstName())
                    .lastName(randomClient.getLastName())
                    .email(randomClient.getEmail())
                    .password(randomClient.getPassword())
                    .role(randomClient.getRole())
                    .build();

            Ticket ticket = Ticket.builder()
                    .client(client)
                    .movieSession(randomSession)
                    .numberOfSeats(1)
                    .price(randomSession.getPrice().floatValue())
                    .build();

            // Add the seat to reserved seats
            ticket.getReservedSeats().add(randomSeat);

            ticketService.create(ticket);
        }


        log.info("Created " + count + " tickets");
    }


    /**
     * Seeds orders with realistic data.
     */
    private void seedOrders(int count) {
        log.info("Seeding " + count + " orders...");

        List<User> users = userService.read(PageRequest.defaultPage()).getContent();
        List<Product> products = productService.read(PageRequest.defaultPage()).getContent();

        if (users.isEmpty() || products.isEmpty()) {
            log.warning("Need users and products to create orders");
            return;
        }


        // Filter for clients only
        List<User> clients = users.stream()
                .filter(user -> "client".equals(user.getRole()))
                .toList();

        if (clients.isEmpty()) {
            log.warning("No client users found for order creation");
            return;
        }


        for (int i = 0; i < count; i++) {
            User randomClient = clients.get(random.nextInt(clients.size()));

            // Generate random order date within last 30 days
            LocalDate orderDate = LocalDate.now().minusDays(random.nextInt(30));

            String status = faker.options().option("pending", "completed", "shipped", "delivered", "cancelled");

            Order order = Order.builder()
                    .client((Client) randomClient)
                    .orderDate(Date.valueOf(orderDate))
                    .status(status)
                    .phoneNumber(faker.number().numberBetween(10000000, 99999999))
                    .address(faker.address().fullAddress())
                    .build();

            try {
                // Use createOrder method that returns the generated ID
                Long orderId = orderService.createOrder(order);

                // Set the ID in the order object for creating order items
                order.setId(orderId);

                // Create order items for this order
                int itemCount = random.nextInt(3) + 1; // 1-3 items per order
                for (int j = 0; j < itemCount; j++) {
                    Product randomProduct = products.get(random.nextInt(products.size()));
                    int quantity = random.nextInt(3) + 1; // 1-3 quantity

                    OrderItem orderItem = OrderItem.builder()
                            .product(randomProduct)
                            .quantity(quantity)
                            .order(order)
                            .build();

                    orderItemService.create(orderItem);
                }

            } catch (Exception e) {
                log.warning("Failed to create order for client " + randomClient.getFirstName() + ": " + e.getMessage());
            }

        }


        log.info("Created " + count + " orders with items");
    }


    /**
     * Seeds shopping carts with realistic data.
     */
    private void seedShoppingCarts(int count) {
        log.info("Seeding " + count + " shopping carts...");

        List<User> users = userService.read(PageRequest.defaultPage()).getContent();
        List<Product> products = productService.read(PageRequest.defaultPage()).getContent();

        if (users.isEmpty() || products.isEmpty()) {
            log.warning("Need users and products to create shopping carts");
            return;
        }


        // Filter for clients only
        List<User> clients = users.stream()
                .filter(user -> "client".equals(user.getRole()))
                .toList();

        if (clients.isEmpty()) {
            log.warning("No client users found for shopping cart creation");
            return;
        }


        for (int i = 0; i < count; i++) {
            User randomClient = clients.get(random.nextInt(clients.size()));
            Product randomProduct = products.get(random.nextInt(products.size()));
            int quantity = random.nextInt(5) + 1; // 1-5 quantity

            ShoppingCart cart = ShoppingCart.builder()
                    .user(randomClient)
                    .product(randomProduct)
                    .quantity(quantity)
                    .build();

            shoppingCartService.create(cart);
        }


        log.info("Created " + count + " shopping cart items");
    }


    /**
     * Seeds product reviews with realistic data.
     */
    private void seedReviews(int count) {
        log.info("Seeding " + count + " product reviews...");

        List<User> users = userService.read(PageRequest.defaultPage()).getContent();
        List<Product> products = productService.read(PageRequest.defaultPage()).getContent();

        if (users.isEmpty() || products.isEmpty()) {
            log.warning("Need users and products to create reviews");
            return;
        }


        // Filter for clients only
        List<User> clients = users.stream()
                .filter(user -> "client".equals(user.getRole()))
                .toList();

        if (clients.isEmpty()) {
            log.warning("No client users found for review creation");
            return;
        }


        for (int i = 0; i < count; i++) {
            User randomClient = clients.get(random.nextInt(clients.size()));
            Product randomProduct = products.get(random.nextInt(products.size()));

            int rating = faker.number().numberBetween(1, 6); // 1-5 stars

            Review review = Review.builder()
                    .client((Client) randomClient)
                    .product(randomProduct)
                    .rating(rating)
                    .build();

            reviewService.create(review);
        }


        log.info("Created " + count + " product reviews");
    }


    /**
     * Seeds cinema ratings with realistic data.
     */
    private void seedCinemaRatings(int count) {
        log.info("Seeding " + count + " cinema ratings...");

        List<User> users = userService.read(PageRequest.defaultPage()).getContent();
        List<Cinema> cinemas = cinemaService.read(PageRequest.defaultPage()).getContent();

        if (users.isEmpty() || cinemas.isEmpty()) {
            log.warning("Need users and cinemas to create ratings");
            return;
        }


        // Filter for clients only
        List<User> clients = users.stream()
                .filter(user -> "client".equals(user.getRole()))
                .toList();

        if (clients.isEmpty()) {
            log.warning("No client users found for cinema rating creation");
            return;
        }


        for (int i = 0; i < count; i++) {
            User randomClient = clients.get(random.nextInt(clients.size()));
            Cinema randomCinema = cinemas.get(random.nextInt(cinemas.size()));

            int rating = faker.number().numberBetween(1, 6); // 1-5 stars

            CinemaRating cinemaRating = CinemaRating.builder()
                    .cinema(randomCinema)
                    .client((Client) randomClient)
                    .rating(rating)
                    .build();

            cinemaRatingService.create(cinemaRating);
        }


        log.info("Created " + count + " cinema ratings");
    }


    /**
     * Seeds film ratings with realistic data.
     */
    private void seedFilmRatings(int count) {
        log.info("Seeding " + count + " film ratings...");

        List<User> users = userService.read(PageRequest.defaultPage()).getContent();
        List<Film> films = filmService.read(PageRequest.defaultPage()).getContent();

        if (users.isEmpty() || films.isEmpty()) {
            log.warning("Need users and films to create ratings");
            return;
        }


        // Filter for clients only
        List<User> clients = users.stream()
                .filter(user -> "client".equals(user.getRole()))
                .toList();

        if (clients.isEmpty()) {
            log.warning("No client users found for film rating creation");
            return;
        }


        for (int i = 0; i < count; i++) {
            User randomClient = clients.get(random.nextInt(clients.size()));
            Film randomFilm = films.get(random.nextInt(films.size()));

            int rating = faker.number().numberBetween(1, 6); // 1-5 stars

            FilmRating filmRating = FilmRating.builder()
                    .film(randomFilm)
                    .client((Client) randomClient)
                    .rating(rating)
                    .build();

            filmRatingService.create(filmRating);
        }


        log.info("Created " + count + " film ratings");
    }


    /**
     * Seeds various comments (product, film, cinema).
     */
    private void seedComments() {
        log.info("Seeding comments for products, films, and cinemas...");

        List<User> users = userService.read(PageRequest.defaultPage()).getContent();
        if (users.isEmpty()) {
            log.warning("Need users to create comments");
            return;
        }


        // Filter for clients only
        List<User> clients = users.stream()
                .filter(user -> "client".equals(user.getRole()))
                .toList();

        if (clients.isEmpty()) {
            log.warning("No client users found for comment creation");
            return;
        }


        // Product comments
        List<Product> products = productService.read(PageRequest.defaultPage()).getContent();
        if (!products.isEmpty()) {
            for (int i = 0; i < Math.min(20, products.size()); i++) {
                User randomClient = clients.get(random.nextInt(clients.size()));
                Product randomProduct = products.get(random.nextInt(products.size()));

                Comment productComment = Comment.builder()
                        .client((Client) randomClient)
                        .product(randomProduct)
                        .commentText(faker.lorem().paragraph())
                        .build();

                productCommentService.create(productComment);
            }

            log.info("Created product comments");
        }


        // Film comments
        List<Film> films = filmService.read(PageRequest.defaultPage()).getContent();
        if (!films.isEmpty()) {
            for (int i = 0; i < Math.min(30, films.size()); i++) {
                User randomClient = clients.get(random.nextInt(clients.size()));
                Film randomFilm = films.get(random.nextInt(films.size()));

                FilmComment filmComment = FilmComment.builder()
                        .comment(faker.lorem().paragraph())
                        .client((Client) randomClient)
                        .film(randomFilm)
                        .build();

                filmCommentService.create(filmComment);
            }

            log.info("Created film comments");
        }


        // Cinema comments
        List<Cinema> cinemas = cinemaService.read(PageRequest.defaultPage()).getContent();
        if (!cinemas.isEmpty()) {
            for (int i = 0; i < Math.min(15, cinemas.size()); i++) {
                User randomClient = clients.get(random.nextInt(clients.size()));
                Cinema randomCinema = cinemas.get(random.nextInt(cinemas.size()));

                CinemaComment cinemaComment = CinemaComment.builder()
                        .cinema(randomCinema)
                        .client((Client) randomClient)
                        .commentText(faker.lorem().paragraph())
                        .sentiment(faker.options().option("positive", "negative", "neutral"))
                        .build();

                cinemaCommentService.create(cinemaComment);
            }

            log.info("Created cinema comments");
        }


        log.info("Completed seeding all comments");
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


    /**
     * Seeds series categories data.
     */
    private void seedSeriesCategories() {
        log.info("Seeding series categories...");
        // Implementation to be added based on your requirements
        log.info("Series categories seeding completed");
    }


    /**
     * Seeds favorites data.
     */
    private void seedFavorites(int count) {
        log.info("Seeding " + count + " favorites...");
        // Implementation to be added based on your requirements
        log.info("Favorites seeding completed");
    }


    /**
     * Seeds feedbacks data.
     */
    private void seedFeedbacks(int count) {
        log.info("Seeding " + count + " feedbacks...");
        // Implementation to be added based on your requirements
        log.info("Feedbacks seeding completed");
    }


    /**
     * Seeds actor-film associations.
     */
    private void seedActorFilmAssociations() {
        log.info("Seeding actor-film associations...");
        // Implementation to be added based on your requirements
        log.info("Actor-film associations seeding completed");
    }


    /**
     * Seeds film-cinema associations.
     */
    private void seedFilmCinemaAssociations() {
        log.info("Seeding film-cinema associations...");
        // Implementation to be added based on your requirements
        log.info("Film-cinema associations seeding completed");
    }

}

