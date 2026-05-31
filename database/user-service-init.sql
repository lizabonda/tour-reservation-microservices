INSERT INTO person (id, person_type, first_name, last_name, date_of_birth)
VALUES
    (1, 1, 'Anna', 'Novakova', '1995-04-12'),
    (2, 1, 'Petr', 'Svoboda', '1990-09-23'),
    (3, 0, 'Marie', 'Dvorakova', '1988-02-15'),
    (4, 0, 'Jan', 'Cerny', '1999-11-05'),
    (5, 0, 'Lucie', 'Prochazkova', '1997-07-19'),
    (6, 0, 'Tomas', 'Kucera', '1985-01-30'),
    (7, 0, 'Eva', 'Vesela', '1993-03-08'),
    (8, 0, 'Martin', 'Horak', '1991-12-14'),
    (9, 0, 'Klara', 'Nemcova', '2000-06-21'),
    (10, 0, 'David', 'Pokorny', '1987-10-02')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password, phone_number, email, role)
VALUES
    (1, 'anna.customer', 'password', '+420111222333', 'anna.customer@example.com', 'CUSTOMER'),
    (2, 'petr.admin', 'admin', '+420444555666', 'petr.admin@example.com', 'ADMIN')
    ON CONFLICT (id) DO NOTHING;

SELECT setval('person_seq', (SELECT MAX(id) FROM person), true);