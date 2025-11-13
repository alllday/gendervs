-- Test user insertion
INSERT INTO users (login_id, password_hash, email, phone_encrypted, phone_hash, content_status, is_admin)
VALUES ('testuser', 'password123', 'test@example.com', 'encrypted_phone_123', 'hash_of_phone_123', 'ACTIVE', false);

-- Check created user
SELECT * FROM users;
