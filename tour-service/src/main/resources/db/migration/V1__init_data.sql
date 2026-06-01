CREATE SEQUENCE IF NOT EXISTS tour_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS activity_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS trip_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS activity (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    duration INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS tour (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    capacity INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS tour_activity (
    tour_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    PRIMARY KEY (tour_id, activity_id),
    FOREIGN KEY (tour_id) REFERENCES tour(id),
    FOREIGN KEY (activity_id) REFERENCES activity(id)
);

CREATE TABLE IF NOT EXISTS trip (
    id BIGINT PRIMARY KEY,
    carrier VARCHAR(255) NOT NULL,
    depart_at TIMESTAMP NOT NULL,
    arrive_at TIMESTAMP NOT NULL,
    from_location VARCHAR(255) NOT NULL,
    to_location VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    tour_id BIGINT NOT NULL,
    FOREIGN KEY (tour_id) REFERENCES tour(id)
);

CREATE TABLE IF NOT EXISTS tour_accommodations_id (
    tour_id BIGINT NOT NULL,
    accommodations_id BIGINT NOT NULL,
    FOREIGN KEY (tour_id) REFERENCES tour(id)
);

INSERT INTO activity (
    id,
    name,
    description,
    duration,
    price,
    start_time,
    end_time
)
VALUES
    (1, 'Louvre Museum Visit', 'Guided visit to the Louvre Museum', 180, 45.00, '2026-06-02 10:00:00', '2026-06-02 13:00:00'),
    (2, 'Colosseum Tour', 'Guided tour of the Colosseum', 150, 40.00, '2026-07-11 09:30:00', '2026-07-11 12:00:00'),
    (3, 'Berlin Walking Tour', 'Historical walking tour in Berlin', 120, 25.00, '2026-08-04 14:00:00', '2026-08-04 16:00:00'),
    (4, 'Vienna Opera Evening', 'Evening cultural program in Vienna', 180, 80.00, '2026-09-14 19:00:00', '2026-09-14 22:00:00'),
    (5, 'Prague Castle Visit', 'Guided visit to Prague Castle', 150, 35.00, '2026-10-06 10:00:00', '2026-10-06 12:30:00'),
    (6, 'Barcelona Bike Tour', 'Bike tour near the beach', 120, 30.00, '2026-11-03 09:00:00', '2026-11-03 11:00:00'),
    (7, 'Amsterdam Canal Cruise', 'Evening cruise through Amsterdam canals', 90, 28.00, '2026-12-02 18:00:00', '2026-12-02 19:30:00'),
    (8, 'Lisbon Food Walk', 'Local food tasting walk', 150, 50.00, '2027-01-12 12:00:00', '2027-01-12 14:30:00'),
    (9, 'Athens Acropolis Visit', 'Guided Acropolis visit', 180, 55.00, '2027-02-08 10:00:00', '2027-02-08 13:00:00'),
    (10, 'Budapest Thermal Bath', 'Relaxing thermal bath visit', 180, 45.00, '2027-03-16 15:00:00', '2027-03-16 18:00:00')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO tour (
    id,
    title,
    destination,
    start_date,
    end_date,
    status,
    description,
    capacity,
    price
)
VALUES
    (1, 'Paris Weekend Tour', 'Paris', '2026-06-01', '2026-06-05', 'ACTIVE', 'A short cultural trip to Paris.', 18, 599.99),
    (2, 'Rome Summer Tour', 'Rome', '2026-07-10', '2026-07-17', 'ACTIVE', 'One week summer tour in Rome.', 13, 749.99),
    (3, 'Berlin History Tour', 'Berlin', '2026-08-03', '2026-08-08', 'ACTIVE', 'Historical tour through Berlin.', 20, 399.99),
    (4, 'Vienna Culture Tour', 'Vienna', '2026-09-12', '2026-09-18', 'ACTIVE', 'Culture and music tour in Vienna.', 12, 699.99),
    (5, 'Prague City Tour', 'Prague', '2026-10-05', '2026-10-09', 'ACTIVE', 'City tour through Prague old town.', 16, 349.99),
    (6, 'Barcelona Beach Tour', 'Barcelona', '2026-11-01', '2026-11-07', 'ACTIVE', 'Beach and city tour in Barcelona.', 14, 899.99),
    (7, 'Amsterdam Canal Tour', 'Amsterdam', '2026-12-01', '2026-12-05', 'ACTIVE', 'Canals, museums and city walks.', 10, 649.99),
    (8, 'Lisbon Food Tour', 'Lisbon', '2027-01-10', '2027-01-15', 'ACTIVE', 'Food and culture trip to Lisbon.', 12, 579.99),
    (9, 'Athens Ancient Greece Tour', 'Athens', '2027-02-07', '2027-02-13', 'ACTIVE', 'Historical tour focused on ancient Greece.', 15, 729.99),
    (10, 'Cancelled Budapest Tour', 'Budapest', '2027-03-15', '2027-03-20', 'ACTIVE', 'Cancelled demo tour.', 10, 499.99)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO tour_activity (tour_id, activity_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10)
    ON CONFLICT DO NOTHING;

INSERT INTO trip (
    id,
    carrier,
    depart_at,
    arrive_at,
    from_location,
    to_location,
    type,
    tour_id
)
VALUES
    (1, 'Air France', '2026-06-01 08:00:00', '2026-06-01 10:00:00', 'Prague', 'Paris', 'FLIGHT', 1),
    (2, 'Air France', '2026-06-05 18:00:00', '2026-06-05 20:00:00', 'Paris', 'Prague', 'FLIGHT', 1),
    (3, 'Ryanair', '2026-07-10 07:30:00', '2026-07-10 09:20:00', 'Prague', 'Rome', 'FLIGHT', 2),
    (4, 'Ryanair', '2026-07-17 19:00:00', '2026-07-17 20:50:00', 'Rome', 'Prague', 'FLIGHT', 2),
    (5, 'FlixBus', '2026-08-03 06:00:00', '2026-08-03 11:00:00', 'Prague', 'Berlin', 'BUS', 3),
    (6, 'FlixBus', '2026-08-08 17:00:00', '2026-08-08 22:00:00', 'Berlin', 'Prague', 'BUS', 3),
    (7, 'RegioJet', '2026-09-12 09:00:00', '2026-09-12 13:00:00', 'Prague', 'Vienna', 'TRAIN', 4),
    (8, 'RegioJet', '2026-09-18 15:00:00', '2026-09-18 19:00:00', 'Vienna', 'Prague', 'TRAIN', 4),
    (9, 'Local Bus', '2026-10-05 09:00:00', '2026-10-05 10:00:00', 'Prague Main Station', 'Prague Old Town', 'BUS', 5),
    (10, 'Local Bus', '2026-10-09 17:00:00', '2026-10-09 18:00:00', 'Prague Old Town', 'Prague Main Station', 'BUS', 5)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO tour_accommodations_id (tour_id, accommodations_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10)
    ON CONFLICT DO NOTHING;

SELECT setval('tour_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM tour), false);
SELECT setval('activity_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM activity), false);
SELECT setval('trip_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM trip), false);