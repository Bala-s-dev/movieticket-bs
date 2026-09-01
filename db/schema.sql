CREATE TABLE users (
    user_id     BIGINT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    phone       VARCHAR(20)  NOT NULL,
    password    VARCHAR(255) NOT NULL
);

CREATE TABLE admins (
    admin_id    BIGINT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    phone       VARCHAR(20)  NOT NULL,
    password    VARCHAR(255) NOT NULL
);

CREATE TABLE movies (
    movie_id            BIGINT PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    description         TEXT,
    language            VARCHAR(50),
    genre               VARCHAR(50),
    duration_minutes    INT NOT NULL,
    release_date        DATE,
    rating              DOUBLE NOT NULL DEFAULT 0,
    active              BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE theatres (
    theatre_id  BIGINT PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    location    VARCHAR(200) NOT NULL,
    admin_id    BIGINT NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_theatre_admin FOREIGN KEY (admin_id) REFERENCES admins(admin_id)
);

CREATE TABLE screens (
    screen_id   BIGINT PRIMARY KEY,
    screen_name VARCHAR(100) NOT NULL,
    theatre_id  BIGINT NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_screen_theatre FOREIGN KEY (theatre_id) REFERENCES theatres(theatre_id)
);

CREATE TABLE seats (
    seat_id     BIGINT PRIMARY KEY,
    screen_id   BIGINT NOT NULL,
    row_letter  CHAR(1) NOT NULL,
    seat_number INT NOT NULL,
    category    VARCHAR(10) NOT NULL,
    row_order   INT NOT NULL,
    CONSTRAINT fk_seat_screen FOREIGN KEY (screen_id) REFERENCES screens(screen_id),
    UNIQUE KEY uq_seat_label (screen_id, row_letter, seat_number)
);

CREATE TABLE shows (
    show_id         BIGINT PRIMARY KEY,
    movie_id        BIGINT NOT NULL,
    screen_id       BIGINT NOT NULL,
    start_datetime  DATETIME NOT NULL,
    end_datetime    DATETIME NOT NULL,
    price_gold      DOUBLE NOT NULL,
    price_platinum  DOUBLE NOT NULL,
    price_silver    DOUBLE NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_show_movie FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    CONSTRAINT fk_show_screen FOREIGN KEY (screen_id) REFERENCES screens(screen_id)
);

-- Per-show seat availability. Deliberately separate from `seats`:
-- the same physical seat has an independent row here per show.
CREATE TABLE show_seats (
    show_id     BIGINT NOT NULL,
    seat_id     BIGINT NOT NULL,
    status      VARCHAR(10) NOT NULL DEFAULT 'AVAILABLE',
    PRIMARY KEY (show_id, seat_id),
    CONSTRAINT fk_showseat_show FOREIGN KEY (show_id) REFERENCES shows(show_id),
    CONSTRAINT fk_showseat_seat FOREIGN KEY (seat_id) REFERENCES seats(seat_id)
);

CREATE TABLE bookings (
    booking_id          BIGINT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    show_id             BIGINT NOT NULL,
    booking_datetime    DATETIME NOT NULL,
    total_amount        DOUBLE NOT NULL,
    status              VARCHAR(10) NOT NULL,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_booking_show FOREIGN KEY (show_id) REFERENCES shows(show_id)
);

CREATE TABLE booking_seats (
    booking_id  BIGINT NOT NULL,
    seat_id     BIGINT NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    CONSTRAINT fk_bs_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    CONSTRAINT fk_bs_seat FOREIGN KEY (seat_id) REFERENCES seats(seat_id)
);
