# 🎬 RAKCHA - Ultimate Entertainment Hub

<div align="center">

![RAKCHA Logo](src/main/resources/Logo.png)

[![License](https://img.shields.io/badge/license-Commercial-red.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.7-green.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![Build Status](https://github.com/aliammari1/rakcha-desktop/actions/workflows/ci.yml/badge.svg)](https://github.com/aliammari1/rakcha-desktop/actions)
[![CodeQL](https://github.com/aliammari1/rakcha-desktop/actions/workflows/codeql.yml/badge.svg)](https://github.com/aliammari1/rakcha-desktop/security/code-scanning)

**A sophisticated JavaFX desktop application for managing cinemas, films, series, products, and user experiences**

[Features](#-features) • [Architecture](#-architecture) • [Installation](#-getting-started) • [Usage](#-user-roles) • [API](#-api-integrations) • [Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Database Configuration](#-database-configuration)
- [Docker Deployment](#-docker-deployment)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Testing](#-testing)
- [API Integrations](#-api-integrations)
- [Contributing](#-contributing)
- [License](#-license)
- [Disclaimer](#%EF%B8%8F-disclaimer)
- [Authors](#-authors)

---

## 🎯 Overview

RAKCHA is a comprehensive entertainment management platform built with JavaFX 21. It provides a complete ecosystem for managing cinemas, films, TV series, e-commerce products, and user interactions. The application features a modern UI with animations, multi-database support, OAuth authentication, payment processing, and real-time notifications.

### ✨ Key Highlights

- 🎥 **Cinema Management** - Complete cinema, hall, seat, and movie session management
- 📺 **Series Streaming** - TV series catalog with seasons, episodes, and watch progress tracking
- 🛒 **E-Commerce** - Full shopping cart, orders, and payment processing
- 🔐 **Multi-Auth** - Traditional login, Google/Microsoft OAuth, and Face Recognition
- 💳 **Payments** - Stripe and PayPal integration
- 📊 **Analytics** - Sentiment analysis, statistics, and reporting
- 🌐 **Multi-Database** - MySQL, PostgreSQL, SQLite, and H2 support
- 🐳 **Docker Ready** - Complete containerization with Docker Compose

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              RAKCHA Desktop Application                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌──────────────────────────────────────────────────────────────────────────┐   │
│  │                           PRESENTATION LAYER                              │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐ │   │
│  │  │   JavaFX    │ │    FXML     │ │     CSS     │ │  Controllers (MVC)  │ │   │
│  │  │   Views     │ │   Layouts   │ │   Styles    │ │  - Users (21)       │ │   │
│  │  │             │ │             │ │             │ │  - Films (8)        │ │   │
│  │  │  Splash     │ │  Sidebar    │ │  Dashboard  │ │  - Cinemas (11)     │ │   │
│  │  │  Dashboard  │ │  Dialogs    │ │  Modern UI  │ │  - Products (12)    │ │   │
│  │  │  Forms      │ │  Cards      │ │  Animations │ │  - Series (9)       │ │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────────────┘   │
│                                       │                                          │
│  ┌──────────────────────────────────────────────────────────────────────────┐   │
│  │                            BUSINESS LAYER                                 │   │
│  │  ┌───────────────────────────────────────────────────────────────────┐   │   │
│  │  │                         Services (IService<T>)                     │   │   │
│  │  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────────────┐  │   │   │
│  │  │  │   User    │ │   Film    │ │  Cinema   │ │     Product       │  │   │   │
│  │  │  │ Services  │ │ Services  │ │ Services  │ │    Services       │  │   │   │
│  │  │  │           │ │           │ │           │ │                   │  │   │   │
│  │  │  │ - User    │ │ - Film    │ │ - Cinema  │ │ - Product         │  │   │   │
│  │  │  │ - Friend  │ │ - Actor   │ │ - Hall    │ │ - Cart            │  │   │   │
│  │  │  │ - Message │ │ - Ticket  │ │ - Seat    │ │ - Order           │  │   │   │
│  │  │  │ - Notify  │ │ - Category│ │ - Session │ │ - Payment         │  │   │   │
│  │  │  │ - Watch   │ │           │ │           │ │                   │  │   │   │
│  │  │  └───────────┘ └───────────┘ └───────────┘ └───────────────────┘  │   │   │
│  │  │  ┌───────────┐ ┌─────────────────────────────────────────────────┐│   │   │
│  │  │  │  Series   │ │              Search Service                     ││   │   │
│  │  │  │ Services  │ │  - Universal search with Caffeine caching       ││   │   │
│  │  │  │ - Series  │ │  - Role-based filtering (Client/Admin/Manager)  ││   │   │
│  │  │  │ - Season  │ │  - Auto-suggestions and trending                ││   │   │
│  │  │  │ - Episode │ │                                                 ││   │   │
│  │  │  │ - Favorite│ └─────────────────────────────────────────────────┘│   │   │
│  │  │  └───────────┘                                                    │   │   │
│  │  └───────────────────────────────────────────────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────────────────┘   │
│                                       │                                          │
│  ┌──────────────────────────────────────────────────────────────────────────┐   │
│  │                              DATA LAYER                                   │   │
│  │  ┌─────────────────────────────────────────────────────────────────────┐ │   │
│  │  │                           Models (Entities)                          │ │   │
│  │  │  ┌────────────────┐  ┌────────────────┐  ┌────────────────────────┐ │ │   │
│  │  │  │     Users      │  │     Films      │  │       Cinemas          │ │ │   │
│  │  │  │ - User         │  │ - Film         │  │ - Cinema               │ │ │   │
│  │  │  │ - Admin        │  │ - Actor        │  │ - CinemaHall           │ │ │   │
│  │  │  │ - Client       │  │ - Ticket       │  │ - Seat                 │ │ │   │
│  │  │  │ - CinemaManager│  │                │  │ - MovieSession         │ │ │   │
│  │  │  │ - Friendship   │  │                │  │                        │ │ │   │
│  │  │  │ - Achievement  │  │                │  │                        │ │ │   │
│  │  │  │ - Notification │  │                │  │                        │ │ │   │
│  │  │  └────────────────┘  └────────────────┘  └────────────────────────┘ │ │   │
│  │  │  ┌────────────────┐  ┌────────────────┐                             │ │   │
│  │  │  │    Products    │  │     Series     │                             │ │   │
│  │  │  │ - Product      │  │ - Series       │                             │ │   │
│  │  │  │ - Order        │  │ - Season       │                             │ │   │
│  │  │  │ - OrderItem    │  │ - Episode      │                             │ │   │
│  │  │  │ - ShoppingCart │  │ - Favorite     │                             │ │   │
│  │  │  │ - Payment      │  │                │                             │ │   │
│  │  │  └────────────────┘  └────────────────┘                             │ │   │
│  │  └─────────────────────────────────────────────────────────────────────┘ │   │
│  │                                                                           │   │
│  │  ┌─────────────────────────────────────────────────────────────────────┐ │   │
│  │  │                     DataSource (Multi-Database)                      │ │   │
│  │  │     MySQL │ PostgreSQL │ SQLite │ H2  +  HikariCP Connection Pool   │ │   │
│  │  └─────────────────────────────────────────────────────────────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────────────┘   │
│                                                                                  │
│  ┌──────────────────────────────────────────────────────────────────────────┐   │
│  │                           UTILITIES LAYER                                 │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ │   │
│  │  │ Authentication│ │   Payment   │ │    Media     │ │  Communication   │ │   │
│  │  │              │ │              │ │              │ │                  │ │   │
│  │  │ SignInGoogle │ │  Stripe API  │ │ CloudinaryAPI│ │   UserMail       │ │   │
│  │  │ SignInMS     │ │  PayPal SDK  │ │ FilmTrailer  │ │   UserSMS        │ │   │
│  │  │ FaceRecog    │ │              │ │ IMDB API     │ │   TrayNotify     │ │   │
│  │  │ BCrypt       │ │              │ │              │ │                  │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────────┘ │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ │   │
│  │  │  Navigation  │ │  Documents   │ │   Security   │ │    Validators    │ │   │
│  │  │              │ │              │ │              │ │                  │ │   │
│  │  │ Breadcrumb   │ │   UserPDF    │ │SecurityConfig│ │ EmailValidator   │ │   │
│  │  │ ScreenNav    │ │   QR/Barcode │ │SessionManager│ │ PasswordValidator│ │   │
│  │  │ QuickNav     │ │   iText      │ │              │ │                  │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────────────┘   │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘

                              EXTERNAL INTEGRATIONS
┌─────────────────────────────────────────────────────────────────────────────────┐
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │
│  │  Google  │ │Microsoft │ │  Stripe  │ │  PayPal  │ │  Twilio  │ │  Vonage  │ │
│  │  OAuth   │ │  OAuth   │ │ Payments │ │ Payments │ │   SMS    │ │   SMS    │ │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │
│  │ YouTube  │ │   IMDB   │ │Cloudinary│ │  OpenCV  │ │  ZXing   │ │  VADER   │ │
│  │   API    │ │ Scraper  │ │  Storage │ │   Face   │ │ QR/Codes │ │Sentiment │ │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## ✨ Features

### 🎞️ Film & Cinema Management
| Feature | Description |
|---------|-------------|
| 🎥 Film Catalog | Comprehensive film database with descriptions, durations, and categories |
| 👨‍🎤 Actor Management | Track actors with filmographies and biographical information |
| 🏢 Cinema Management | Manage cinema venues, halls, and seating arrangements |
| 📅 Movie Sessions | Schedule screenings with date, time, and cinema hall assignment |
| 🎟️ Ticket Booking | Purchase and manage tickets with seat selection |
| ⭐ Ratings & Reviews | User ratings and sentiment-analyzed comments |
| ▶️ YouTube Trailers | Automatic trailer fetching via YouTube API |
| 🌐 IMDB Integration | Rich film metadata from IMDB scraping |

### 📺 Series & Episodes
| Feature | Description |
|---------|-------------|
| 📚 Series Catalog | Browse TV series with seasons and episodes |
| 📊 Watch Progress | Track viewing progress across episodes |
| ❤️ Favorites | Mark series as favorites for quick access |
| 📈 Statistics | View series analytics and user engagement |

### 🛍️ Product Marketplace
| Feature | Description |
|---------|-------------|
| 📋 Product Catalog | Entertainment merchandise and collectibles |
| 🛒 Shopping Cart | Full cart management with quantity updates |
| 💳 Checkout | Secure payment processing via Stripe/PayPal |
| 📦 Order Tracking | Complete order lifecycle management |
| 📱 QR Codes | Product QR code generation via ZXing |
| 📊 Analytics | Order and sales analytics dashboard |

### 👥 User Management
| Feature | Description |
|---------|-------------|
| 👨‍💼 Role-Based Access | Client, Admin, and Cinema Manager roles |
| 👤 User Profiles | Comprehensive profile management |
| 🤝 Social Features | Friend system with messaging and chat |
| 🏆 Achievements | Gamification with user achievements |
| 📣 Notifications | Real-time in-app and system tray notifications |
| 📋 Watchlist | Personal watchlist management |

### 🔐 Security & Authentication
| Feature | Description |
|---------|-------------|
| 🔑 Traditional Login | Username/password with BCrypt hashing |
| 🔄 OAuth 2.0 | Google and Microsoft social authentication |
| 👁️ Face Recognition | Biometric login using OpenCV |
| 🔒 Password Security | History tracking and strength validation |
| 🛡️ Session Management | Secure session handling with cleanup |

### 💳 Payment Processing
| Feature | Description |
|---------|-------------|
| 💰 Stripe Integration | Credit card processing |
| 🅿️ PayPal SDK | Alternative payment method |
| 📜 Order History | Complete transaction tracking |
| 🧾 PDF Invoices | Automated receipt generation |

### 📨 Communication
| Feature | Description |
|---------|-------------|
| 📧 Email | SMTP email with templates |
| 📱 SMS | Twilio and Vonage integration |
| 🔔 Push Notifications | System tray notifications |
| 💬 In-App Chat | Real-time messaging system |

---

## 🛠 Technology Stack

### Core Platform
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Core language with modern features |
| JavaFX | 21.0.7 | Desktop UI framework |
| Maven | 3.6+ | Build automation and dependency management |

### UI & Design
| Library | Version | Purpose |
|---------|---------|---------|
| ControlsFX | 11.2.2 | Extended JavaFX controls |
| JFoenix | 9.0.10 | Material Design components |
| Ikonli | 12.4.0 | Icon packs (Material Design 2) |
| AnimateFX | 1.3.0 | UI animations |
| ValidatorFX | 0.6.1 | Form validation |

### Database & Persistence
| Technology | Version | Purpose |
|------------|---------|---------|
| MySQL | 9.3.0 | Production database |
| PostgreSQL | 42.7.1 | Alternative production database |
| SQLite | 3.50.2 | Development/embedded database |
| HikariCP | 5.1.0 | Connection pooling |

### Authentication & Security
| Library | Purpose |
|---------|---------|
| ScribeJava 8.3.3 | OAuth 2.0 (Google, Microsoft) |
| JBCrypt 0.4 | Password hashing |
| OpenCV 4.9+ | Face recognition |

### Payment Processing
| Service | Version | Purpose |
|---------|---------|---------|
| Stripe Java | 29.4.0 | Credit card payments |
| PayPal REST SDK | 1.14.0 | PayPal transactions |

### External APIs
| API | Purpose |
|-----|---------|
| YouTube Data API v3 | Film trailers |
| Google API Client 2.8.0 | Google services |
| IMDB Scraper | Film metadata |
| Cloudinary | Media storage |

### Communication
| Service | Version | Purpose |
|---------|---------|---------|
| Twilio | 11.0.0 | SMS notifications |
| Vonage | 9.3.1 | SMS backup |
| JavaMail | 1.6.2 | Email services |

### Document Processing
| Library | Version | Purpose |
|---------|---------|---------|
| iText | 5.5.13.4 | PDF generation |
| PDFBox | 3.0.5 | PDF manipulation |
| ZXing | 3.5.3 | QR/Barcode generation |

### Search & Caching
| Library | Version | Purpose |
|---------|---------|---------|
| Caffeine | 3.1.8 | High-performance caching |
| Apache Lucene | 9.12.1 | Full-text search |

### Analytics
| Library | Purpose |
|---------|---------|
| VADER Sentiment | Review sentiment analysis |

### Development & Testing
| Tool | Version | Purpose |
|------|---------|---------|
| JUnit Jupiter | 6.0.0-M1 | Unit testing |
| TestFX | 4.0.18 | JavaFX UI testing |
| AssertJ | 3.27.3 | Fluent assertions |
| Monocle | 21 | Headless testing |
| Lombok | 1.18.38 | Boilerplate reduction |
| JavaFaker | 1.0.2 | Test data generation |
| Logback | 1.5.21 | Logging |
| SpotBugs | 4.8.2.0 | Static analysis |
| OpenRewrite | 5.36.0 | Automated refactoring |

---

## 🚀 Getting Started

### Prerequisites

- ☕ **Java JDK 21** or later
- 🛠️ **Maven 3.6+**
- 🗄️ **Database** (MySQL 8.0+, PostgreSQL, or SQLite)
- 💻 **IDE** with JavaFX support (IntelliJ IDEA, Eclipse, VS Code)

### System Requirements

| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| OS | Windows 10, macOS 10.15, Ubuntu 20.04 | Latest versions |
| Processor | Intel Core i3 | Intel Core i5/i7 |
| RAM | 4 GB | 8 GB+ |
| Storage | 500 MB | SSD with 2GB+ |
| Graphics | DirectX 11 support | Dedicated GPU (for Face Recognition) |

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/aliammari1/rakcha-desktop.git
cd rakcha-desktop
```

2. **Configure environment variables**
```bash
cp .env.example .env
# Edit .env with your API keys and database credentials
```

3. **Build the project**
```bash
mvn clean install -DskipTests
```

4. **Run the application**
```bash
mvn javafx:run
```

### Environment Variables

Create a `.env` file with the following:

```env
# Database Configuration
DB_URL=jdbc:sqlite:./data/rakcha.db
DB_USER=
DB_PASSWORD=

# OAuth (Optional)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
MICROSOFT_CLIENT_ID=your_microsoft_client_id
MICROSOFT_CLIENT_SECRET=your_microsoft_client_secret

# Payment (Optional)
STRIPE_API_KEY=your_stripe_key
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_secret

# Communication (Optional)
TWILIO_ACCOUNT_SID=your_twilio_sid
TWILIO_AUTH_TOKEN=your_twilio_token
VONAGE_API_KEY=your_vonage_key
VONAGE_API_SECRET=your_vonage_secret

# Media Storage (Optional)
CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name
YOUTUBE_API_KEY=your_youtube_api_key
```

---

## 📁 Project Structure

```
rakcha-desktop/
├── src/
│   ├── main/
│   │   ├── java/com/esprit/
│   │   │   ├── MainApp.java              # Application entry point
│   │   │   ├── components/               # Reusable UI components
│   │   │   │   └── UniversalSearchBox.java
│   │   │   ├── controllers/              # MVC Controllers
│   │   │   │   ├── SidebarController.java
│   │   │   │   ├── SplashScreenController.java
│   │   │   │   ├── cinemas/              # Cinema management (11 controllers)
│   │   │   │   ├── films/                # Film management (8 controllers)
│   │   │   │   ├── products/             # E-commerce (12 controllers)
│   │   │   │   ├── series/               # Series management (9 controllers)
│   │   │   │   └── users/                # User management (21 controllers)
│   │   │   ├── enums/                    # Enumerations
│   │   │   │   ├── CategoryType.java
│   │   │   │   ├── CinemaStatus.java
│   │   │   │   ├── OrderStatus.java
│   │   │   │   ├── TicketStatus.java
│   │   │   │   └── UserRole.java
│   │   │   ├── exceptions/               # Custom exceptions
│   │   │   ├── models/                   # Data models
│   │   │   │   ├── cinemas/              # Cinema, Hall, Seat, Session
│   │   │   │   ├── films/                # Film, Actor, Ticket
│   │   │   │   ├── products/             # Product, Order, Cart, Payment
│   │   │   │   ├── series/               # Series, Season, Episode
│   │   │   │   └── users/                # User hierarchy (14 models)
│   │   │   ├── services/                 # Business logic
│   │   │   │   ├── IService.java         # Generic service interface
│   │   │   │   ├── cinemas/
│   │   │   │   ├── films/
│   │   │   │   ├── products/
│   │   │   │   ├── search/               # Universal search service
│   │   │   │   ├── series/
│   │   │   │   └── users/
│   │   │   └── utils/                    # Utilities
│   │   │       ├── DataSource.java       # Database connection
│   │   │       ├── SessionManager.java   # User session handling
│   │   │       ├── PaymentProcessor.java # Stripe integration
│   │   │       ├── SignInGoogle.java     # Google OAuth
│   │   │       ├── SignInMicrosoft.java  # Microsoft OAuth
│   │   │       ├── FaceRecognition.java  # OpenCV face auth
│   │   │       ├── CloudinaryStorage.java
│   │   │       ├── UserMail.java         # Email service
│   │   │       ├── UserSMSAPI.java       # SMS service
│   │   │       ├── UserPDF.java          # PDF generation
│   │   │       └── validators/           # Input validation
│   │   └── resources/
│   │       ├── ui/                       # FXML layouts
│   │       │   ├── sidebar.fxml
│   │       │   ├── splash/
│   │       │   ├── admin/
│   │       │   ├── users/
│   │       │   ├── films/
│   │       │   ├── cinemas/
│   │       │   ├── products/
│   │       │   ├── series/
│   │       │   └── styles/               # CSS stylesheets
│   │       ├── images/                   # Application assets
│   │       ├── haar/                     # OpenCV cascade files
│   │       └── *.sql                     # Database schemas
│   ├── packaging/                        # jpackage resources
│   │   ├── windows/
│   │   ├── macos/
│   │   └── linux/
│   └── test/                             # Test suites
├── .github/
│   └── workflows/
│       ├── ci.yml                        # CI pipeline
│       ├── build-and-deploy.yml          # Native installers
│       └── codeql.yml                    # Security scanning
├── config/
│   └── mysql.cnf                         # MySQL configuration
├── docs/                                 # JavaDoc documentation
├── docker-compose.yml                    # Docker services
├── Dockerfile                            # Application container
├── pom.xml                               # Maven configuration
└── README.md
```

---

## 🗄️ Database Configuration

### Quick Start (SQLite - Development)

No configuration needed! SQLite database is created automatically:
```env
DB_URL=jdbc:sqlite:./data/rakcha.db
```

### MySQL (Production)

```env
DB_URL=jdbc:mysql://localhost:3306/rakcha_db?serverTimezone=UTC
DB_USER=rakcha_user
DB_PASSWORD=your_password
```

### PostgreSQL

```env
DB_URL=jdbc:postgresql://localhost:5432/rakcha_db
DB_USER=rakcha_user
DB_PASSWORD=your_password
```

---

## 🐳 Docker Deployment

### Quick Start

```bash
# Development mode (with Adminer UI)
docker-compose --profile dev up -d

# Production mode
docker-compose up -d

# View logs
docker-compose logs -f rakcha-app

# Stop services
docker-compose down
```

### Services

| Service | Port | Description |
|---------|------|-------------|
| rakcha-app | 8080 | Main application |
| rakcha-db | 3306 | MySQL database |
| rakcha-redis | 6379 | Redis cache |
| adminer | 8081 | Database admin UI (dev only) |

---

## 🔄 CI/CD Pipeline

The project uses GitHub Actions for continuous integration and deployment:

### Workflows

| Workflow | Trigger | Purpose |
|----------|---------|---------|
| `ci.yml` | Push/PR | Build, test, code quality, security scan |
| `build-and-deploy.yml` | Tags/Releases | Native installers (Windows, macOS, Linux) |
| `codeql.yml` | Push | Security vulnerability scanning |

### Pipeline Stages

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Build &   │───▶│    Code     │───▶│  Security   │───▶│   Package   │
│    Test     │    │   Quality   │    │    Scan     │    │  Installer  │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                   │                  │                  │
     ▼                   ▼                  ▼                  ▼
  JUnit Tests       SpotBugs           OWASP/Trivy        jpackage
  JaCoCo Coverage   JavaDoc            CodeQL             .msi/.deb/.pkg
```

### Native Installers

Built automatically on release tags:
- **Windows**: `.msi` installer
- **macOS**: `.pkg` installer  
- **Linux**: `.deb` package + portable archive

---

## 🧪 Testing

### Run Tests

```bash
# All tests
mvn test

# With coverage report
mvn test jacoco:report

# Headless mode (CI)
mvn test -Dtestfx.robot=glass -Dtestfx.headless=true
```

### Test Structure

```
src/test/java/com/esprit/
├── MainAppTest.java          # Application startup tests
├── controllers/              # Controller unit tests
├── integration/              # Integration tests
├── tests/                    # Feature tests
│   ├── CinemaEnhancementsTest.java
│   └── CinemaVerification.java
└── utils/                    # Utility tests
```

---

## 🔌 API Integrations

| Service | Purpose | Documentation |
|---------|---------|---------------|
| Google OAuth | Social authentication | [Google Identity](https://developers.google.com/identity) |
| Microsoft OAuth | Social authentication | [Microsoft Identity](https://docs.microsoft.com/en-us/azure/active-directory/develop/) |
| Stripe | Payment processing | [Stripe Docs](https://stripe.com/docs) |
| PayPal | Payment processing | [PayPal Developer](https://developer.paypal.com/) |
| YouTube Data API | Film trailers | [YouTube API](https://developers.google.com/youtube/v3) |
| Cloudinary | Media storage | [Cloudinary Docs](https://cloudinary.com/documentation) |
| Twilio | SMS notifications | [Twilio Docs](https://www.twilio.com/docs) |
| Vonage | SMS backup | [Vonage API](https://developer.vonage.com/) |

---

## 👤 User Roles

| Role | Description | Capabilities |
|------|-------------|--------------|
| **Client** | Regular user | Browse content, purchase tickets/products, manage profile, social features |
| **Cinema Manager** | Venue administrator | Manage cinema, halls, seats, movie sessions, view statistics |
| **Admin** | System administrator | Full access, user management, analytics, system configuration |

---

## 👥 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. 🍴 Fork the repository
2. 🌿 Create a feature branch (`git checkout -b feature/amazing-feature`)
3. 💾 Commit your changes (`git commit -m 'Add amazing feature'`)
4. 📤 Push to the branch (`git push origin feature/amazing-feature`)
5. 🔍 Open a Pull Request

See also:
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [Security Policy](SECURITY.md)
- [Changelog](CHANGELOG.md)
- [Roadmap](ROADMAP.md)

---

## 📜 License

This project is licensed under a **Commercial Use License** - see the [LICENSE](LICENSE) file for details.

**Key Points:**
- ✅ Free for personal and educational use
- ✅ Free for study and learning
- ✅ Free for contributions and improvements
- ❌ Commercial use requires explicit permission

---

## ⚠️ Disclaimer

**Code Ownership**: The source code in this repository is owned by the author(s). However, **no ownership is claimed over any assets** (images, icons, fonts, media files, or other third-party resources) used in this project. All assets remain the property of their respective owners.

**Educational Purpose**: This project is developed **strictly for educational purposes**. It is intended to demonstrate software development concepts, JavaFX application architecture, and integration with various APIs and services.

**Third-Party Components**: This project uses various third-party libraries and assets. Users are responsible for reviewing all third-party licenses and ensuring compliance with their terms.

**Commercial Use**: If you wish to use this project or any part of it for **commercial purposes**, please contact:

📧 **ammari.ali.0001@gmail.com**

---

## ✍️ Authors

- **Ali Ammari** - *Lead Developer* - [@aliammari1](https://github.com/aliammari1)

See the list of [contributors](https://github.com/aliammari1/rakcha-desktop/contributors) who participated in this project.

---

## 🙏 Acknowledgments

- 👏 All contributors and testers
- 💡 Modern entertainment platforms for inspiration
- 📚 Open-source libraries that made this project possible
- 🎓 ESPRIT School of Engineering

---

<div align="center">

**Made with ❤️ by the RAKCHA Team**

[⬆ Back to Top](#-rakcha---ultimate-entertainment-hub)

</div>
