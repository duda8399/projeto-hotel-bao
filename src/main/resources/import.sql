INSERT INTO client (name, email, password, phone, created_at, updated_at) VALUES('Maria Silva', 'maria@example.com', 'senha123', '31999990000', NOW(), NOW());
INSERT INTO client (name, email, password, phone, created_at, updated_at) VALUES ('João Souza', 'joao@example.com', 'segredo', '31988887777', NOW(), NOW());

-- Inserts: bedroom
INSERT INTO bedroom (description, value, image_url, created_at, updated_at) VALUES ('Quarto com cama de casal e varanda', 250.00, 'https://example.com/quarto1.jpg', NOW(), NOW());
INSERT INTO bedroom (description, value, image_url, created_at, updated_at) VALUES ('Suíte com hidromassagem e vista para o mar', 450.00, 'https://example.com/quarto2.jpg', NOW(), NOW());

-- Inserts: accommodation
INSERT INTO accommodation (client_id, bedroom_id, check_in_date, check_out_date, created_at, updated_at) VALUES (1, 1, '2025-07-01 14:00:00', '2025-07-05 12:00:00', NOW(), NOW());
INSERT INTO accommodation (client_id, bedroom_id, check_in_date, check_out_date, created_at, updated_at) VALUES (2, 2, '2025-07-10 15:00:00', '2025-07-15 11:00:00', NOW(), NOW());