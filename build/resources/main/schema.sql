CREATE TABLE IF NOT EXISTS row_permission_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id VARCHAR(64) NOT NULL,
    resource_name VARCHAR(128) NOT NULL,
    resource_type VARCHAR(32) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    rule_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY uk_row_permission_resource_id (resource_id)
);

CREATE TABLE IF NOT EXISTS row_permission_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id VARCHAR(64) NOT NULL,
    rule_name VARCHAR(128) NOT NULL,
    rule_description VARCHAR(512),
    enabled_systems_json TEXT,
    subjects_json TEXT NOT NULL,
    filter_conditions_json TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    KEY idx_row_permission_rule_resource (resource_id),
    KEY idx_row_permission_rule_status (status)
);
