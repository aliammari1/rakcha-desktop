-- ========================================================
-- 1. AUTHENTICATION & USERS
-- ========================================================

CREATE TABLE IF NOT EXISTS users
(
  id                    SERIAL PRIMARY KEY,
  first_name            VARCHAR(50)  NOT NULL,
  last_name             VARCHAR(50)  NOT NULL,
  email                 VARCHAR(180) NOT NULL UNIQUE,
  phone_number          VARCHAR(20),
  password_hash         VARCHAR(255) NOT NULL,
  role                  VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'CLIENT', 'CINEMA_MANAGER')),
  address               VARCHAR(255),
  birth_date            DATE,
  profile_picture_url   TEXT,
  is_verified           BOOLEAN   DEFAULT FALSE,
  totp_secret           VARCHAR(255),
  created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  failed_login_attempts INT       DEFAULT 0,
  last_failed_login     TIMESTAMP DEFAULT NULL,
  is_locked             BOOLEAN   DEFAULT FALSE,
  locked_until          TIMESTAMP DEFAULT NULL,

  is_active             BOOLEAN   DEFAULT TRUE,
  deactivated_at        TIMESTAMP DEFAULT NULL,
  deactivation_reason   TEXT      DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS password_history
(
  id            SERIAL PRIMARY KEY,
  user_id       INT          NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_history FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_resets
(
  id           SERIAL PRIMARY KEY,
  user_id      INT REFERENCES users (id) ON DELETE CASCADE,
  selector     VARCHAR(20)  NOT NULL,
  hashed_token VARCHAR(100) NOT NULL,
  requested_at TIMESTAMP    NOT NULL,
  expires_at   TIMESTAMP    NOT NULL
);

-- ========================================================
-- 2. MEDIA (Movies, Series, Actors)
-- ========================================================

CREATE TABLE IF NOT EXISTS categories
(
  id          SERIAL PRIMARY KEY,
  name        VARCHAR(50) NOT NULL,
  type        VARCHAR(20) CHECK (type IN ('MOVIE', 'SERIE', 'PRODUCT')),
  description TEXT
);

CREATE TABLE IF NOT EXISTS actors
(
  id        SERIAL PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  image_url TEXT,
  biography TEXT
);

CREATE TABLE IF NOT EXISTS movies
(
  id           SERIAL PRIMARY KEY,
  title        VARCHAR(255) NOT NULL,
  image_url    TEXT,
  duration_min INT          NOT NULL,
  description  TEXT,
  release_year INT          NOT NULL,
  trailer_url  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS series
(
  id           SERIAL PRIMARY KEY,
  title        VARCHAR(255) NOT NULL,
  summary      TEXT,
  director     VARCHAR(100),
  country      VARCHAR(50),
  image_url    TEXT,
  release_year INT
);

CREATE TABLE IF NOT EXISTS seasons
(
  id            SERIAL PRIMARY KEY,
  series_id     INT REFERENCES series (id) ON DELETE CASCADE,
  season_number INT NOT NULL,
  title         VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS episodes
(
  id             SERIAL PRIMARY KEY,
  season_id      INT REFERENCES seasons (id) ON DELETE CASCADE,
  episode_number INT          NOT NULL,
  title          VARCHAR(255) NOT NULL,
  image_url      TEXT,
  video_url      TEXT,
  duration_min   INT
);

-- Join Tables for Media
CREATE TABLE IF NOT EXISTS movie_actors
(
  movie_id INT REFERENCES movies (id) ON DELETE CASCADE,
  actor_id INT REFERENCES actors (id) ON DELETE CASCADE,
  PRIMARY KEY (movie_id, actor_id)
);

CREATE TABLE IF NOT EXISTS movie_categories
(
  movie_id    INT REFERENCES movies (id) ON DELETE CASCADE,
  category_id INT REFERENCES categories (id) ON DELETE CASCADE,
  PRIMARY KEY (movie_id, category_id)
);

-- ========================================================
-- 3. CINEMA & TICKETING (The Core Feature)
-- ========================================================

CREATE TABLE IF NOT EXISTS cinemas
(
  id         SERIAL PRIMARY KEY,
  name       VARCHAR(100) NOT NULL,
  address    VARCHAR(255) NOT NULL,
  manager_id INT REFERENCES users (id),
  logo_url   TEXT,
  status     VARCHAR(50) DEFAULT 'OPEN'
);

CREATE TABLE IF NOT EXISTS cinema_halls
(
  id        SERIAL PRIMARY KEY,
  cinema_id INT REFERENCES cinemas (id) ON DELETE CASCADE,
  name      VARCHAR(50) NOT NULL, -- e.g., "IMAX Hall 1"
  capacity  INT         NOT NULL
);

CREATE TABLE IF NOT EXISTS seats
(
  id          SERIAL PRIMARY KEY,
  hall_id     INT REFERENCES cinema_halls (id) ON DELETE CASCADE,
  row_label   VARCHAR(5)  NOT NULL,          -- e.g., "A", "B"
  seat_number VARCHAR(10) NOT NULL,          -- e.g., "1", "12"
  type        VARCHAR(20) DEFAULT 'STANDARD' -- STANDARD, VIP, WHEELCHAIR
);

CREATE TABLE IF NOT EXISTS screenings
(
  id         SERIAL PRIMARY KEY,
  movie_id   INT REFERENCES movies (id) ON DELETE CASCADE,
  hall_id    INT REFERENCES cinema_halls (id) ON DELETE CASCADE,
  start_time TIMESTAMP      NOT NULL,
  end_time   TIMESTAMP      NOT NULL,
  price      DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets
(
  id            SERIAL PRIMARY KEY,
  user_id       INT REFERENCES users (id),
  screening_id  INT REFERENCES screenings (id),
  seat_id       INT REFERENCES seats (id),
  purchase_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  status        VARCHAR(20) DEFAULT 'VALID', -- VALID, CANCELLED, USED
  qr_code       TEXT,
  price_paid    DECIMAL(10, 2) NOT NULL,
  UNIQUE (screening_id, seat_id)             -- Crucial: Prevents double booking
);

-- ========================================================
-- 4. E-COMMERCE (Merchandise)
-- ========================================================

CREATE TABLE IF NOT EXISTS products
(
  id             SERIAL PRIMARY KEY,
  name           VARCHAR(100)   NOT NULL,
  description    TEXT,
  price          DECIMAL(10, 2) NOT NULL,
  image_url      TEXT,
  stock_quantity INT            NOT NULL DEFAULT 0,
  category_id    INT REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS shopping_carts
(
  id         SERIAL PRIMARY KEY,
  user_id    INT REFERENCES users (id) ON DELETE CASCADE,
  product_id INT REFERENCES products (id) ON DELETE CASCADE,
  quantity   INT DEFAULT 1,
  UNIQUE (user_id, product_id)
);

CREATE TABLE IF NOT EXISTS orders
(
  id               SERIAL PRIMARY KEY,
  user_id          INT REFERENCES users (id),
  order_date       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  status           VARCHAR(50) DEFAULT 'PENDING',
  shipping_address VARCHAR(255)   NOT NULL,
  phone_number     VARCHAR(20)    NOT NULL,
  total_amount     DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items
(
  id         SERIAL PRIMARY KEY,
  order_id   INT REFERENCES orders (id) ON DELETE CASCADE,
  product_id INT REFERENCES products (id),
  quantity   INT            NOT NULL,
  unit_price DECIMAL(10, 2) NOT NULL -- Price snapshot at purchase time
);

-- ========================================================
-- 5. SOCIAL, INTERACTIONS & SYSTEM
-- ========================================================
CREATE TABLE IF NOT EXISTS user_favorites
(
  id         SERIAL PRIMARY KEY,
  user_id    INT REFERENCES users (id) ON DELETE CASCADE,
  movie_id   INT REFERENCES movies (id) ON DELETE CASCADE,
  series_id  INT REFERENCES series (id) ON DELETE CASCADE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT one_favorite_target CHECK (
    (movie_id IS NOT NULL AND series_id IS NULL) OR
    (movie_id IS NULL AND series_id IS NOT NULL)
    ),
  UNIQUE (user_id, movie_id, series_id) -- Prevent duplicates
);

CREATE TABLE IF NOT EXISTS reviews
(
  id         SERIAL PRIMARY KEY,
  user_id    INT REFERENCES users (id) ON DELETE CASCADE,
  rating     INT CHECK (rating BETWEEN 1 AND 5),
  comment    TEXT,
  sentiment  VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  movie_id   INT REFERENCES movies (id),
  series_id  INT REFERENCES series (id),
  product_id INT REFERENCES products (id),
  cinema_id  INT REFERENCES cinemas (id),
  CONSTRAINT one_review_target CHECK (
    (movie_id IS NOT NULL)::int +
    (series_id IS NOT NULL)::int +
    (product_id IS NOT NULL)::int +
    (cinema_id IS NOT NULL)::int = 1
    )
);

CREATE TABLE IF NOT EXISTS friendships
(
  id           SERIAL PRIMARY KEY,
  requester_id INT REFERENCES users (id),
  addressee_id INT REFERENCES users (id),
  status       VARCHAR(20) DEFAULT 'PENDING',
  created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- New Table: For user-to-user or support chat
CREATE TABLE IF NOT EXISTS chat_messages
(
  id          BIGSERIAL PRIMARY KEY,
  sender_id   INT REFERENCES users (id),
  receiver_id INT REFERENCES users (id),
  content     TEXT NOT NULL,
  is_read     BOOLEAN   DEFAULT FALSE,
  sent_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- New Table: For App Notifications
CREATE TABLE IF NOT EXISTS notifications
(
  id         BIGSERIAL PRIMARY KEY,
  user_id    INT REFERENCES users (id) ON DELETE CASCADE,
  title      VARCHAR(100),
  message    TEXT,
  is_read    BOOLEAN   DEFAULT FALSE,
  type       VARCHAR(50), -- e.g., 'ORDER_UPDATE', 'FRIEND_REQ'
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- New Table: Centralized Payments
CREATE TABLE IF NOT EXISTS payments
(
  id             SERIAL PRIMARY KEY,
  user_id        INT REFERENCES users (id),
  amount         DECIMAL(10, 2) NOT NULL,
  payment_method VARCHAR(50),                -- 'STRIPE', 'PAYPAL', 'CASH'
  status         VARCHAR(50) DEFAULT 'COMPLETED',
  transaction_id VARCHAR(255),               -- External ID from Stripe/PayPal
  order_id       INT REFERENCES orders (id), -- Nullable (can pay for ticket OR order)
  ticket_id      INT REFERENCES tickets (id),
  created_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);
