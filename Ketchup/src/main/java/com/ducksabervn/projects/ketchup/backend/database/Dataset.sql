USE ketchup;
INSERT INTO users (email, username, password, is_admin) VALUES
    ('a@a.com', 'a', 'a', TRUE);
INSERT INTO movies (movie_id, title, genre, duration, rating, showtime, seat_price) VALUES
                                                                                        ('b7d3844f-dc91-4663-913e-8ae458965368', 'Inception',       'Sci-Fi',   148, 'PG-13', '2025-06-01 10:00:00', 10),
                                                                                        ('d3233e95-229e-46e1-940a-924ca8ba9d28', 'Inception',       'Sci-Fi',   148, 'PG-13', '2025-06-01 13:00:00', 10),
                                                                                        ('2456c134-2193-4c1c-8be1-eaf4d29c7f77', 'Inception',       'Sci-Fi',   148, 'PG-13', '2025-06-01 16:00:00', 10),
                                                                                        ('9f980c86-6bfc-4f51-b58c-3ca677e60e4c', 'The Dark Knight', 'Action',   152, 'PG-13', '2025-06-01 11:00:00', 12),
                                                                                        ('4bee7387-7e9d-4b10-8bb5-41e1b130cdfb', 'The Dark Knight', 'Action',   152, 'PG-13', '2025-06-01 14:30:00', 12),
                                                                                        ('b0b11c54-65e2-4ae0-be05-8003436f94a3', 'The Dark Knight', 'Action',   152, 'PG-13', '2025-06-01 18:00:00', 12),
                                                                                        ('da58841a-25af-4174-a880-f4a899a2a2fd', 'Interstellar',    'Sci-Fi',   169, 'PG',    '2025-06-01 09:00:00', 11),
                                                                                        ('99a4ba2f-c99e-4770-b5d8-35653f2c6552', 'Interstellar',    'Sci-Fi',   169, 'PG',    '2025-06-01 13:30:00', 11),
                                                                                        ('52653a7b-7f75-4843-b95d-cb7b1fc26d41', 'Interstellar',    'Sci-Fi',   169, 'PG',    '2025-06-01 18:00:00', 11),
                                                                                        ('381ecce1-8016-4810-be83-781533e4516b', 'The Godfather',   'Crime',    175, 'R',     '2025-06-01 10:30:00', 13),
                                                                                        ('3c9cc47b-846b-4519-b221-1f30d2fde005', 'The Godfather',   'Crime',    175, 'R',     '2025-06-01 15:00:00', 13),
                                                                                        ('b8058330-4457-4143-9c0a-9604dfe4dd3d', 'Parasite',        'Thriller', 132, 'R',     '2025-06-01 12:00:00', 10),
                                                                                        ('469c4ea8-4d5c-4418-8db2-4a5aefb81a9f', 'Parasite',        'Thriller', 132, 'R',     '2025-06-01 15:30:00', 10),
                                                                                        ('310424e1-77da-47c5-b558-b02ea8491b37', 'Parasite',        'Thriller', 132, 'R',     '2025-06-01 19:00:00', 10);
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('35862bc5-2471-4544-bdc2-62e1031017b2', 'a@a.com', 'b7d3844f-dc91-4663-913e-8ae458965368', '2025-06-01 10:00:00', 30, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('35862bc5-2471-4544-bdc2-62e1031017b2', 'A1'),
                                                    ('35862bc5-2471-4544-bdc2-62e1031017b2', 'A2'),
                                                    ('35862bc5-2471-4544-bdc2-62e1031017b2', 'B5');
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('80325f11-409d-4678-8487-3815e353b8a1', 'a@a.com', 'd3233e95-229e-46e1-940a-924ca8ba9d28', '2025-06-01 13:00:00', 30, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('80325f11-409d-4678-8487-3815e353b8a1', 'C3'),
                                                    ('80325f11-409d-4678-8487-3815e353b8a1', 'D7'),
                                                    ('80325f11-409d-4678-8487-3815e353b8a1', 'D8');
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('10b5df25-48d1-467d-aeac-c2f22346ad78', 'a@a.com', '9f980c86-6bfc-4f51-b58c-3ca677e60e4c', '2025-06-01 11:00:00', 48, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('10b5df25-48d1-467d-aeac-c2f22346ad78', 'A1'),
                                                    ('10b5df25-48d1-467d-aeac-c2f22346ad78', 'A2'),
                                                    ('10b5df25-48d1-467d-aeac-c2f22346ad78', 'A3'),
                                                    ('10b5df25-48d1-467d-aeac-c2f22346ad78', 'B1');
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('a0ed3b06-1899-494f-bc29-01b2ea109417', 'a@a.com', '4bee7387-7e9d-4b10-8bb5-41e1b130cdfb', '2025-06-01 14:30:00', 24, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('a0ed3b06-1899-494f-bc29-01b2ea109417', 'E5'),
                                                    ('a0ed3b06-1899-494f-bc29-01b2ea109417', 'E6');
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('cb01ff99-71a7-47b1-ac98-fb664fa325bd', 'a@a.com', 'da58841a-25af-4174-a880-f4a899a2a2fd', '2025-06-01 09:00:00', 33, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('cb01ff99-71a7-47b1-ac98-fb664fa325bd', 'F10'),
                                                    ('cb01ff99-71a7-47b1-ac98-fb664fa325bd', 'F11'),
                                                    ('cb01ff99-71a7-47b1-ac98-fb664fa325bd', 'F12');
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('ea5ebede-b61d-42bf-befd-23c302f0299c', 'a@a.com', '99a4ba2f-c99e-4770-b5d8-35653f2c6552', '2025-06-01 13:30:00', 33, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('ea5ebede-b61d-42bf-befd-23c302f0299c', 'G1'),
                                                    ('ea5ebede-b61d-42bf-befd-23c302f0299c', 'G2'),
                                                    ('ea5ebede-b61d-42bf-befd-23c302f0299c', 'H5');
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('34a29185-1690-4ea2-aec0-5bafeb71a1ce', 'a@a.com', '381ecce1-8016-4810-be83-781533e4516b', '2025-06-01 10:30:00', 52, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('34a29185-1690-4ea2-aec0-5bafeb71a1ce', 'A4'),
                                                    ('34a29185-1690-4ea2-aec0-5bafeb71a1ce', 'A5'),
                                                    ('34a29185-1690-4ea2-aec0-5bafeb71a1ce', 'B6'),
                                                    ('34a29185-1690-4ea2-aec0-5bafeb71a1ce', 'C6');
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('fa918753-89a8-495f-abe6-040ef5218585', 'a@a.com', 'b8058330-4457-4143-9c0a-9604dfe4dd3d', '2025-06-01 12:00:00', 20, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('fa918753-89a8-495f-abe6-040ef5218585', 'B3'),
                                                    ('fa918753-89a8-495f-abe6-040ef5218585', 'B4');
INSERT INTO bookings (booking_id, user_email, movie_id, showtime, total_price, is_processed) VALUES
    ('93386c55-8a94-4610-ba14-2ca38cb42f67', 'a@a.com', '469c4ea8-4d5c-4418-8db2-4a5aefb81a9f', '2025-06-01 15:30:00', 20, TRUE);
INSERT INTO booking_seats (booking_id, seat_id) VALUES
                                                    ('93386c55-8a94-4610-ba14-2ca38cb42f67', 'D9'),
                                                    ('93386c55-8a94-4610-ba14-2ca38cb42f67', 'D10');