INSERT INTO accommodation (id, name, city, address, stars, room_type, capacity, meal_plan, price_per_night)
VALUES (1, 'Demo Hotel', 'Prague', 'Demo Street 123', 4, 'Single', 2, 'ALL_INCLUSIVE', 100.0);

INSERT INTO tour (id, title, description, destination, start_date, end_date, capacity, price)
VALUES (1, 'Demo Greece Tour', 'Demo tour for testing', 'Greece', '2025-06-01', '2025-06-08', 3, 500.0);

INSERT INTO tour_accomodation (tour_id, accomodation_id) VALUES (1, 1);

INSERT INTO person (id, first_name, last_name, date_of_birth,PERSON_TYPE)
VALUES (1, 'Alice', 'Demo', '1995-05-05',1);

INSERT INTO users (id, username, password, phone_number, email, role)
VALUES (1, 'alice', 'password', '123456789', 'alice@example.com', 'CUSTOMER');

INSERT INTO booking (id, reservation_number, total_price, created_at, tour_id)
VALUES (1, 1001, 800.0, '2025-05-01', 1);

INSERT INTO reservation (id, start_date, end_date, reservation_price, accommodation_id, booking_id)
VALUES (1, '2025-06-01T12:00:00', '2025-06-03T12:00:00', 300.0, 1, 1);

INSERT INTO booking_person (booking_id, person_id) VALUES (1, 1);

ALTER SEQUENCE booking_seq RESTART WITH 2;
ALTER SEQUENCE reservation_seq RESTART WITH 2;

-- Person/User share the same identifier space (JOINED inheritance), so keep the sequence in sync with inserted demo IDs.
CREATE SEQUENCE IF NOT EXISTS person_seq START WITH 2 INCREMENT BY 1;
ALTER SEQUENCE person_seq RESTART WITH 2;

CREATE SEQUENCE IF NOT EXISTS booking_reservation_number_seq START WITH 1002 INCREMENT BY 1;
ALTER SEQUENCE booking_reservation_number_seq RESTART WITH 1002;
