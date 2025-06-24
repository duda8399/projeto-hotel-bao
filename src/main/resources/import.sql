-- Insert: user admin
INSERT INTO client (name, email, password, phone, role, created_at, updated_at) VALUES('Usuário administrador', 'admin', '$2a$10$MYWChDsHdPTUSWvqQ2mxZ.pTpm654bnw/qQypDij3e6UjkB5Qz3GG', '', 'ADMIN', NOW(), NOW());

-- Inserts: accommodation
INSERT INTO accommodation (description, value, image_url, created_at, updated_at) VALUES ('Quarto com cama de casal e varanda', 250.00, 'https://example.com/quarto1.jpg', NOW(), NOW());
INSERT INTO accommodation (description, value, image_url, created_at, updated_at) VALUES ('Suíte com hidromassagem e vista para o mar', 450.00, 'https://example.com/quarto2.jpg', NOW(), NOW());
