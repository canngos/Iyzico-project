CREATE TABLE payment (
  id    BIGINT PRIMARY KEY,
  price DECIMAL(30, 8) NOT NULL
);

CREATE TABLE flight (
    flight_id BIGINT PRIMARY KEY,
    flight_name VARCHAR(20) NOT NULL,
    origin VARCHAR(20) NOT NULL,
    destination VARCHAR(20) NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE seat (
    seat_id INT PRIMARY KEY,
    seat_name VARCHAR(20) NOT NULL,
    flight_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id)
);

CREATE TABLE booked_seats (
    id INT PRIMARY KEY,
    seat_id INT NOT NULL,
    flight_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (seat_id) REFERENCES seat(seat_id),
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id)
);

ALTER TABLE booked_seats ADD CONSTRAINT unique_seat_flight UNIQUE (seat_id, flight_id);


