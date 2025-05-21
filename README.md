# Rakcha Desktop: Your Ultimate Entertainment Hub

Welcome to Rakcha Desktop, the ultimate platform for accessing a wide array of entertainment options including movies, series, cinemas, and exclusive products related to these categories. Designed with the user in mind, Rakcha Desktop offers a seamless experience for exploring and enjoying your favorite content across multiple platforms.

![Rakcha Desktop Logo](src/main/resources/Logo.png)

## Table of Contents

- [Rakcha Desktop: Your Ultimate Entertainment Hub](#rakcha-desktop-your-ultimate-entertainment-hub)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
    - [Film & Cinema Management](#film--cinema-management)
    - [Series & Episodes](#series--episodes)
    - [Product Marketplace](#product-marketplace)
    - [User Management](#user-management)
    - [Security & Authentication](#security--authentication)
    - [Payment Processing](#payment-processing)
    - [Communication & Notifications](#communication--notifications)
  - [Technologies Used](#technologies-used)
  - [Architecture](#architecture)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Database Setup](#database-setup)
  - [Usage](#usage)
    - [User Roles](#user-roles)
    - [Basic Operations](#basic-operations)
    - [Advanced Features](#advanced-features)
  - [API Integrations](#api-integrations)
  - [Building from Source](#building-from-source)
  - [Project Structure](#project-structure)
  - [Testing](#testing)
  - [Contributing](#contributing)
  - [License](#license)
  - [Authors](#authors)
  - [Acknowledgments](#acknowledgments)

## Features

Rakcha Desktop offers a comprehensive suite of features organized into several integrated modules:

### Film & Cinema Management

- **Film Database**: Extensive catalog of films with detailed information including descriptions, durations, and categories
- **Actor Management**: Track actors and their filmographies with biographical information
- **Cinema Management**: Comprehensive system for cinema venues, halls, and seating arrangements
- **Scheduling System**: Manage movie seances with date, time, and cinema hall information
- **Ticket Booking**: Purchase and manage tickets for film showings
- **Film Recommendations**: AI-powered recommendation system based on user preferences and ratings
- **Rating & Reviews**: Allow users to rate films and leave detailed comments
- **YouTube Trailer Integration**: Automatically fetch and display film trailers through YouTube API
- **IMDB Data Integration**: Pull rich film metadata from the IMDB database

### Series & Episodes

- **Series Catalog**: Browse and manage TV series with detailed information
- **Episode Tracking**: Track episodes by season with descriptions and media content
- **Favorites System**: Allow users to mark series as favorites for quick access
- **Feedback System**: User comments and ratings for episodes and series
- **Category Management**: Organize series by genre and categories
- **Media Streaming**: Watch episodes directly within the application

### Product Marketplace

- **Product Catalog**: Browse entertainment-related merchandise
- **Shopping Cart**: Add products to cart and manage order items
- **Order Processing**: Complete end-to-end order management system
- **Product Reviews**: Rating and commenting system for products
- **Product Categories**: Organized product navigation by categories
- **QR Code Generation**: Create QR codes for products and promotions
- **Barcode Integration**: Support for product barcode scanning and generation

### User Management

- **Multi-Role System**: Support for Clients, Admins, and Cinema Managers with role-specific functionality
- **User Profiles**: Comprehensive user profile management
- **Profile Customization**: Personalized user experience based on preferences
- **User Activity Tracking**: Monitor and analyze user behaviors
- **Administrative Tools**: Powerful admin dashboard for user management

### Security & Authentication

- **Traditional Login**: Username and password authentication
- **Social Authentication**: Sign in using Google or Microsoft accounts
- **Biometric Authentication**: Face recognition login using OpenCV
- **Password Encryption**: Secure password storage using BCrypt
- **Permission Management**: Role-based access control throughout the application

### Payment Processing

- **Multiple Payment Gateways**: Integration with Stripe and PayPal
- **Secure Transactions**: PCI-compliant payment processing
- **Order History**: Comprehensive tracking of past purchases
- **Invoicing**: PDF generation for receipts and invoices
- **Payment Analytics**: Track and analyze payment data

### Communication & Notifications

- **Email Notifications**: Automated emails for account activities and promotions
- **SMS Notifications**: Text message alerts via Twilio and Vonage
- **In-App Notifications**: Real-time system notifications
- **Chat System**: Direct messaging between users
- **PDF Reports**: Generate and export data in PDF format

## Technologies Used

Rakcha Desktop leverages a variety of modern technologies:

- **Core Platform**:

  - Java 17 - Modern Java features including records and enhanced switch expressions
  - JavaFX 21.0.2 - Rich client platform for desktop applications
  - FXML - XML-based UI markup language for defining JavaFX UI

- **Database**:

  - MySQL 9.0.0 - Robust relational database for data persistence
  - DataSource pattern - Connection pool implementation for efficient database access

- **UI Framework**:

  - JavaFX Controls - Standard UI components
  - ControlsFX 11.2.1 - Extended JavaFX controls
  - FontAwesomeFX - Icon integration
  - Ikonli 12.3.1 - Icon packs for JavaFX
  - AnimateFX & Animated - Animation libraries for dynamic UI effects
  - CalendarFX - Advanced calendar components

- **Authentication**:

  - ScribeJava 8.3.3 - OAuth2 client implementation (Google, Microsoft)
  - Face Recognition with OpenCV 4.9.0 - Biometric authentication
  - JBCrypt 0.4 - Password hashing

- **Payment Processing**:

  - Stripe Java 26.4.0 - Credit card processing
  - PayPal REST SDK 1.14.0 - PayPal integration

- **Media Processing**:

  - JavaCV 1.5.10 - Computer vision capabilities
  - OpenCV Platform GPU - Image and video processing
  - FFmpeg Platform GPL - Video processing and conversion
  - JavaFX Media - Media playback

- **Notifications & Communication**:

  - Twilio SDK 10.4.1 - SMS integration
  - Vonage Client 8.1.0 - Additional SMS capabilities
  - JavaMail API 1.6.2 - Email services
  - Apache Commons Email 1.6.0 - Enhanced email functionality
  - TrayNotification - System tray notifications

- **Document & Data Processing**:

  - iText 5.5.13.4 - PDF generation and manipulation
  - PDFBox 3.0.2 - PDF manipulation library
  - ZXing 3.5.3 - Barcode/QR code generation and scanning
  - JSON 20240303 - JSON parsing and generation

- **Natural Language Processing**:

  - Stanford CoreNLP 4.5.7 - Sentiment analysis for reviews

- **API Integration**:

  - Google API Client 2.6.0 - Google services integration
  - YouTube API v3 - Video content integration
  - AsyncHttpClient 3.0.1 - Asynchronous HTTP requests

- **Build & Quality Tools**:
  - Maven - Build automation and dependency management
  - OpenRewrite - Automated refactoring
  - Logback 1.5.6 - Logging framework
  - SnakeYAML 2.2 - YAML processing for configuration

## Architecture

Rakcha Desktop follows a well-structured MVC (Model-View-Controller) architecture pattern:

- **Models** (`com.esprit.models`):

  - **Film Models** (`com.esprit.models.films`) - Data structures for films, actors, categories, and comments
  - **Cinema Models** (`com.esprit.models.cinemas`) - Cinema, hall, seat, and seance entities
  - **Series Models** (`com.esprit.models.series`) - Series, episodes, and feedback data structures
  - **Product Models** (`com.esprit.models.produits`) - Products, orders, cart, and review entities
  - **User Models** (`com.esprit.models.users`) - User hierarchy with admin, client, and cinema manager roles

- **Views**:

  - FXML files (in `src/main/resources/ui/`) - Declarative UI definitions
  - CSS styles (in `src/main/resources/styles/`) - UI styling
  - Images & Media (in `src/main/resources/images/`) - Visual assets

- **Controllers** (`com.esprit.controllers`):

  - **Film Controllers** (`com.esprit.controllers.films`) - Manage film-related UI interactions
  - **Cinema Controllers** (`com.esprit.controllers.cinemas`) - Handle cinema venue and screening management
  - **Series Controllers** (`com.esprit.controllers.series`) - Control series and episode display
  - **Product Controllers** (`com.esprit.controllers.produits`) - E-commerce functionality
  - **User Controllers** (`com.esprit.controllers.users`) - Authentication and profile management
  - **Navigation Controllers** - Handle sidebar navigation and application flow

- **Services** (`com.esprit.services`):

  - Based on the `IService<T>` interface - Standard CRUD operations
  - **Film Services** (`com.esprit.services.films`) - Film business logic
  - **Cinema Services** (`com.esprit.services.cinemas`) - Cinema and seance management
  - **Series Services** (`com.esprit.services.series`) - Series-specific operations with DTO pattern
  - **Product Services** (`com.esprit.services.produits`) - E-commerce operations
  - **User Services** (`com.esprit.services.users`) - Authentication and user management

- **Utils** (`com.esprit.utils`):
  - **DataSource** - Database connection management
  - **Authentication Utils** - Google/Microsoft OAuth, face recognition
  - **Payment Utils** - Payment processing helpers
  - **Media Utils** - Film trailers and media handling
  - **External APIs** - IMDB, YouTube integration
  - **Communication** - Email, SMS, and notification utilities
  - **Document Generation** - PDF export and reporting
  - **Security** - Cryptography and validation

## Getting Started

These instructions will help you set up the project on your local machine.

### Prerequisites

- Java JDK 17 or later
- Maven 3.6+
- MySQL 8.0+ (with XAMPP or standalone)
- IDE with JavaFX support (IntelliJ IDEA, Eclipse, VS Code with extensions)

### Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/aliammari1/rakcha-desktop.git
   cd rakcha-desktop
   ```

2. **Install dependencies**:

   ```bash
   mvn install
   ```

3. **Configure application**:
   - Set up database credentials in the appropriate configuration file
   - Configure API keys for external services (Google, Microsoft, Stripe, etc.)

### Database Setup

1. **Start MySQL server** (using XAMPP or standalone MySQL)
2. **Run the database script**:
   ```bash
   mysql -u username -p < rakcha_db.sql
   ```
   Or use the provided script:
   ```bash
   ./database.sh
   ```

## Usage

### User Roles

Rakcha Desktop supports multiple user roles with different capabilities:

1. **Client/User**:

   - Browse and watch movies and series
   - Purchase tickets for cinema showings
   - Shop for entertainment-related products
   - Manage personal profile and preferences

2. **Admin**:

   - Manage users and content
   - Access analytics and reports
   - Configure system settings
   - Moderate reviews and comments

3. **Cinema Manager**:
   - Add/update cinema information
   - Manage movie showings and schedules
   - Process ticket sales and promotions
   - View cinema-specific statistics

### Basic Operations

- **Authentication**: Use traditional login, social authentication, or face recognition
- **Content Navigation**: Use the sidebar to switch between movies, series, events, products, and cinemas
- **Profile Management**: Update personal information, preferences, and payment methods

### Advanced Features

- **Cinema Integration**: Find nearby cinemas, view showtimes, and purchase tickets
- **Movie and Series Streaming**: Watch content directly within the application
- **Shopping Experience**: Browse, filter, and purchase entertainment merchandise
- **Social Features**: Rate content, leave reviews, and interact with other users
- **Payment Processing**: Secure checkout with multiple payment options
- **Notifications**: Receive updates about new content, promotions, and account activity

## API Integrations

Rakcha Desktop integrates with several external APIs:

- **Google & Microsoft OAuth**: For social authentication
- **YouTube API**: For movie trailers and related content
- **IMDB API**: For film and series information
- **Stripe & PayPal**: For payment processing
- **Twilio**: For SMS notifications
- **Weather API**: For location-based information

## Building from Source

1. **Clone the repository**:

   ```bash
   git clone https://github.com/aliammari1/rakcha-desktop.git
   ```

2. **Navigate to project directory**:

   ```bash
   cd rakcha-desktop
   ```

3. **Build with Maven**:

   ```bash
   mvn clean package
   ```

4. **Run the application**:
   ```bash
   java -jar target/RAKCHA-1.0-SNAPSHOT.jar
   ```

## Project Structure

```
rakcha-desktop/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── esprit/
│   │   │           ├── controllers/ # UI controllers
│   │   │           │   ├── films/   # Film-related controllers
│   │   │           │   ├── series/  # Series-related controllers
│   │   │           │   ├── users/   # User-related controllers
│   │   │           │   ├── cinemas/ # Cinema-related controllers
│   │   │           │   └── produits/ # Product-related controllers
│   │   │           ├── models/     # Data models
│   │   │           ├── services/   # Business logic
│   │   │           └── utils/      # Utility classes
│   │   └── resources/
│   │       ├── ui/          # FXML UI files
│   │       ├── images/      # Image resources
│   │       ├── styles/      # CSS style files
│   │       └── META-INF/    # Metadata
│   └── test/
│       └── java/            # Test classes
├── docs/                    # Documentation
├── pom.xml                  # Maven configuration
└── README.md                # This file
```

## Testing

Run the tests using Maven:

```bash
mvn test
```

The application includes various test types:

- Unit tests for individual components
- Integration tests for service-to-service communication
- UI tests for the JavaFX interface

## Contributing

We welcome contributions to Rakcha Desktop! Please read our [CONTRIBUTING.md](CONTRIBUTING.md) file for details on how to submit pull requests.

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add some amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Authors

- **Ali Ammari** - _Initial work_ - [aliammari1](https://github.com/aliammari1)

See also the list of [contributors](https://github.com/aliammari1/rakcha-desktop/contributors) who participated in this project.

## Acknowledgments

- Special thanks to all contributors and testers
- Inspiration from modern entertainment platforms
- Libraries and frameworks that made this project possible
