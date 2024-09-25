INSERT INTO roles (id, name)
VALUES ('11111111-1111-1111-1111-111111111111', 'USER'),
       ('22222222-2222-2222-2222-222222222222', 'ADMIN');

INSERT INTO users (id, email, password, name, enabled)
VALUES ('11111111-1111-1111-1111-111111111111', 'testuser@example.com',
        '{bcrypt}$2a$10$TxcFIXiKZqQf4.pcZryBduceOdZGdaS4gJm/Ee9MZCC32uw0dXR3y', 'Test User with password Password123',
        true),
       ('22222222-2222-2222-2222-222222222222', 'testuser_disabled@example.com',
        '{bcrypt}$2a$10$TxcFIXiKZqQf4.pcZryBduceOdZGdaS4gJm/Ee9MZCC32uw0dXR3y', 'Test User disabled',
        false);

INSERT INTO user_roles (user_id, role_id)
VALUES ('11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111');

INSERT INTO refresh_tokens (id, user_id, token, expiry_date)
VALUES ('11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'expired-refresh-token',
        '2000-01-01 00:00:00'),
       ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'valid-refresh-token',
        '2100-01-01 00:00:00');
