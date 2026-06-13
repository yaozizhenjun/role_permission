INSERT INTO row_permission_resource
    (resource_id, resource_name, resource_type, enabled, rule_count, created_at, updated_at)
VALUES
    ('EV_001', '全渠道用户行为埋点事件', 'EVENT', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('EV_002', '核心交易订单事件', 'SUMMARY', TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO row_permission_rule
    (resource_id, rule_name, rule_description, enabled_systems_json, subjects_json, filter_conditions_json, status, created_at, updated_at)
VALUES
    (
        'EV_001',
        '分中心用户背对背访问',
        '分中心用户只能访问自己所在城市的数据',
        '["微光分析中心"]',
        '[{"subjectType":"DEPARTMENT","values":["北京分中心","杭州分中心"]}]',
        '[{"fieldName":"city","fieldLabel":"城市","operator":"EQ","rightType":"USER_ATTRIBUTE","rightValue":"city","rightLabel":"当前用户属性：所在城市"}]',
        'ENABLED',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
