CREATE SEQUENCE IF NOT EXISTS booking_number_seq
    START WITH 1001
    INCREMENT BY 1;

ALTER TABLE booking
    ALTER COLUMN booking_number SET DEFAULT nextval('booking_number_seq');

INSERT INTO booking (
    id,
    booking_number,
    total_price,
    created_at,
    tour_id,
    status
)
VALUES
    (1, 1001, 1679.98, '2026-01-15', 1, 'CREATED'),
    (2, 1002, 2269.98, '2026-02-01', 2, 'PAID'),
    (3, 1003, 749.99, '2026-02-10', 3, 'CREATED'),
    (4, 1004, 3359.97, '2026-03-05', 4, 'PAID'),
    (5, 1005, 1079.98, '2026-03-20', 5, 'CREATED'),
    (6, 1006, 2449.98, '2026-04-01', 6, 'CREATED'),
    (7, 1007, 1429.98, '2026-04-12', 7, 'CREATED'),
    (8, 1008, 1299.98, '2026-05-01', 8, 'PAID'),
    (9, 1009, 1559.98, '2026-05-10', 9, 'CREATED'),
    (10, 1010, 999.98, '2026-05-20', 10, 'CREATED')
ON CONFLICT (id) DO NOTHING;

SELECT setval(
               'booking_number_seq',
               (SELECT COALESCE(MAX(booking_number), 1000) FROM booking)
       );

INSERT INTO booking_person_ids (booking_id, person_id)
VALUES
    (1, 1),
    (1, 3),
    (2, 2),
    (2, 4),
    (3, 5),
    (4, 6),
    (4, 7),
    (4, 8),
    (5, 9),
    (5, 10),
    (6, 1),
    (6, 5),
    (7, 3),
    (7, 4),
    (8, 6),
    (8, 7),
    (9, 8),
    (9, 9),
    (10, 2),
    (10, 10)
    ON CONFLICT DO NOTHING;

INSERT INTO booking_reservation_ids (booking_id, reservation_id)
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

INSERT INTO booking_activity_ids (booking_id, activity_id)
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

INSERT INTO payment (
    id,
    payment_number,
    amount,
    date,
    status,
    booking_id,
    user_id
)
VALUES
    (1, 5001, 1679.98, '2026-01-15 10:00:00', 'PENDING', 1, 1),
    (2, 5002, 2269.98, '2026-02-01 12:00:00', 'CONFIRMED', 2, 2),
    (3, 5003, 749.99, '2026-02-10 09:30:00', 'PENDING', 3, 5),
    (4, 5004, 3359.97, '2026-03-05 14:30:00', 'CONFIRMED', 4, 6),
    (5, 5005, 1079.98, '2026-03-20 09:15:00', 'PENDING', 5, 9),
    (6, 5006, 2449.98, '2026-04-01 11:20:00', 'PENDING', 6, 1),
    (7, 5007, 1429.98, '2026-04-12 16:45:00', 'PENDING', 7, 3),
    (8, 5008, 1299.98, '2026-05-01 11:00:00', 'CONFIRMED', 8, 6),
    (9, 5009, 1559.98, '2026-05-10 13:10:00', 'PENDING', 9, 8),
    (10, 5010, 999.98, '2026-05-20 08:50:00', 'PENDING', 10, 2)
ON CONFLICT (id) DO NOTHING;