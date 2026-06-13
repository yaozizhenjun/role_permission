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
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    KEY idx_row_permission_rule_resource (resource_id),
    KEY idx_row_permission_rule_status (status)
);

CREATE TABLE IF NOT EXISTS row_permission_rule_system (
    rule_id BIGINT NOT NULL,
    system_code VARCHAR(128) NOT NULL,
    PRIMARY KEY (rule_id, system_code),
    CONSTRAINT fk_rule_system_rule
        FOREIGN KEY (rule_id) REFERENCES row_permission_rule (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS row_permission_rule_subject (
    rule_id BIGINT NOT NULL,
    subject_type VARCHAR(32) NOT NULL,
    subject_value VARCHAR(128) NOT NULL,
    PRIMARY KEY (rule_id, subject_type, subject_value),
    CONSTRAINT fk_rule_subject_rule
        FOREIGN KEY (rule_id) REFERENCES row_permission_rule (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS row_permission_rule_filter (
    rule_id BIGINT NOT NULL,
    sort_order INT NOT NULL,
    field_name VARCHAR(128) NOT NULL,
    field_label VARCHAR(128),
    operator VARCHAR(32) NOT NULL,
    right_type VARCHAR(32) NOT NULL,
    right_value VARCHAR(256) NOT NULL,
    right_label VARCHAR(128),
    PRIMARY KEY (rule_id, sort_order),
    CONSTRAINT fk_rule_filter_rule
        FOREIGN KEY (rule_id) REFERENCES row_permission_rule (id)
        ON DELETE CASCADE
);
