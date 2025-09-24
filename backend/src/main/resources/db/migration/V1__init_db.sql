CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TYPE user_role_enum AS ENUM('ADMIN', 'USER');

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,
    username VARCHAR(50)NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role_enum NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TYPE resource_status_ENUM AS ENUM('FREE', 'RESERVED');

CREATE TABLE IF NOT EXISTS iot_resources(
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status resource_status_ENUM NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

INSERT INTO users (public_id, username, password_hash, role) VALUES
(uuid_generate_v4(), 'admin', '$2a$12$5eFdvtN3WLaNnh7gaSV2/.1f2gJa1B4MA/CkV4YSp5fW0fWfJJKka', 'ADMIN');
-- Password is '12345678'