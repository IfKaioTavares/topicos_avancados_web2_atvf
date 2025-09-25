CREATE TYPE iot_resources_enum AS ENUM('INACTIVE', 'FREE', 'RESERVED');

CREATE TABLE IF NOT EXISTS iot_resources (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,
    resource_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status iot_resources_enum NOT NULL DEFAULT 'INACTIVE',
    timeout_usage_in_minutes INT NOT NULL CHECK (timeout_usage_in_minutes > 0),
    locked_for_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS iot_resource_reserves (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,
    user_id INT NOT NULL,
    resource_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    predicted_end_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_iot_resource_reserves_users FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_iot_resource_reserves_iot_resources FOREIGN KEY(resource_id) REFERENCES iot_resources(id)
);

-- 1) Habilite a extensão necessária para a EXCLUDE (uma vez por banco)
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- 2) (Opcional mas recomendado) garanta que o intervalo é válido
ALTER TABLE iot_resource_reserves
ADD CONSTRAINT chk_valid_interval
CHECK (start_time < COALESCE(end_time, predicted_end_time));

-- 3) Proíba sobreposição de reservas ATIVAS do mesmo recurso
--    usando [start, end) com end = COALESCE(end_time, predicted_end_time)
--    e aplicando apenas quando active = true (e não deletado, se desejar)
ALTER TABLE iot_resource_reserves
ADD CONSTRAINT no_time_overlap_active
EXCLUDE USING gist (
    resource_id WITH =,
    tsrange(start_time, COALESCE(end_time, predicted_end_time), '[)') WITH &&
) WHERE (active AND NOT deleted);

