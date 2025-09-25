CREATE TYPE audit_action_enum AS ENUM(
    'RESOURCE_CREATED', 'RESOURCE_UPDATED', 'RESOURCE_DELETED', 'RESOURCE_STATUS_UPDATED',
    'RESERVE_CREATED', 'RESERVE_RELEASED', 'RESERVE_EXPIRED',
    'USER_LOGIN', 'USER_CREATED', 'USER_UPDATED',
    'DEVICE_STATUS_UPDATE', 'DEVICE_COMMAND_SENT'
);

CREATE TYPE audit_result_enum AS ENUM('SUCCESS', 'FAILURE', 'PARTIAL_SUCCESS');

CREATE TABLE IF NOT EXISTS audit_logs (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    user_id UUID,
    username VARCHAR(100),
    action audit_action_enum NOT NULL,
    resource_id UUID,
    resource_name VARCHAR(200),
    result audit_result_enum NOT NULL,
    details TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- Índices para performance
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_resource_id ON audit_logs(resource_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_result ON audit_logs(result);