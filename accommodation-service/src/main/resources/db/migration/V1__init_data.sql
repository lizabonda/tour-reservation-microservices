CREATE SEQUENCE IF NOT EXISTS accommodation_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS reservation_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS accommodation (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    stars INTEGER NOT NULL,
    room_type VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL,
    price_per_night DOUBLE PRECISION NOT NULL,
    meal_plan VARCHAR(255) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reservation_price DOUBLE PRECISION NOT NULL,
    accommodation_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    number_of_persons INTEGER NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id)
);

INSERT INTO accommodation (
    id,
    name,
    city,
    address,
    stars,
    room_type,
    capacity,
    price_per_night,
    meal_plan,
    deleted
)
VALUES
    (1, 'Paris Central Hotel', 'Paris', '12 Rue de Rivoli, Paris', 4, 'DOUBLE', 2, 120.00, 'BED_BREAKFAST', false),
    (2, 'Rome Garden Hotel', 'Rome', '45 Via Roma, Rome', 4, 'DOUBLE', 2, 110.00, 'HALF_BOARD', false),
    (3, 'Berlin City Hostel', 'Berlin', '8 Alexanderplatz, Berlin', 3, 'TWIN', 2, 70.00, 'ROOM_ONLY', false),
    (4, 'Vienna Royal Hotel', 'Vienna', '21 Ringstrasse, Vienna', 5, 'SUITE', 3, 210.00, 'FULL_BOARD', false),
    (5, 'Prague Old Town Inn', 'Prague', '5 Old Town Square, Prague', 4, 'DOUBLE', 2, 95.00, 'BED_BREAKFAST', false),
    (6, 'Barcelona Beach Resort', 'Barcelona', '33 Beach Avenue, Barcelona', 5, 'SUITE', 4, 250.00, 'ALL_INCLUSIVE', false),
    (7, 'Amsterdam Canal Hotel', 'Amsterdam', '17 Canal Street, Amsterdam', 4, 'DOUBLE', 2, 130.00, 'BED_BREAKFAST', false),
    (8, 'Lisbon Sunny Apartments', 'Lisbon', '9 Ocean Road, Lisbon', 3, 'APARTMENT', 4, 140.00, 'ROOM_ONLY', false),
    (9, 'Athens Classic Hotel', 'Athens', '22 Acropolis Street, Athens', 4, 'TWIN', 2, 100.00, 'HALF_BOARD', false),
    (10, 'Budapest Danube Hotel', 'Budapest', '18 Danube Promenade, Budapest', 4, 'DOUBLE', 2, 90.00, 'BED_BREAKFAST', false)
    ON CONFLICT (id) DO NOTHING;

SELECT setval('accommodation_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM accommodation), false);

INSERT INTO reservation (
    id,
    start_date,
    end_date,
    reservation_price,
    accommodation_id,
    booking_id,
    number_of_persons,
    status
)
VALUES
    (1, '2026-06-01', '2026-06-05', 480.00, 1, 1, 2, 'CREATED'),
    (2, '2026-07-10', '2026-07-17', 770.00, 2, 2, 2, 'CREATED'),
    (3, '2026-08-03', '2026-08-08', 350.00, 3, 3, 1, 'CREATED'),
    (4, '2026-09-12', '2026-09-18', 1260.00, 4, 4, 3, 'CREATED'),
    (5, '2026-10-05', '2026-10-09', 380.00, 5, 5, 2, 'CREATED'),
    (6, '2026-11-01', '2026-11-07', 1500.00, 6, 6, 2, 'CREATED'),
    (7, '2026-12-01', '2026-12-05', 520.00, 7, 7, 2, 'CREATED'),
    (8, '2027-01-10', '2027-01-15', 700.00, 8, 8, 2, 'CREATED'),
    (9, '2027-02-07', '2027-02-13', 600.00, 9, 9, 2, 'CREATED'),
    (10, '2027-03-15', '2027-03-20', 450.00, 10, 10, 2, 'CREATED')
ON CONFLICT (id) DO NOTHING;

SELECT setval('reservation_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM reservation), false);