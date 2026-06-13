INSERT INTO row_permission_resource
    (resource_id, resource_name, resource_type, enabled, rule_count, created_at, updated_at)
VALUES
    ('EV_001', '全渠道用户行为埋点事件', 'EVENT', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('EV_002', '核心交易订单事件', 'SUMMARY', TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO row_permission_rule
    (resource_id, rule_name, rule_description, status, created_at, updated_at)
VALUES
    (
        'EV_001',
        '分中心用户背对背访问',
        '分中心用户只能访问自己所在城市的数据',
        'ENABLED',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT INTO row_permission_rule_system (rule_id, system_code)
SELECT id, '微光分析中心'
FROM row_permission_rule
WHERE resource_id = 'EV_001' AND rule_name = '分中心用户背对背访问';

INSERT INTO row_permission_rule_subject (rule_id, subject_type, subject_value)
SELECT id, 'DEPARTMENT', '北京分中心'
FROM row_permission_rule
WHERE resource_id = 'EV_001' AND rule_name = '分中心用户背对背访问';

INSERT INTO row_permission_rule_subject (rule_id, subject_type, subject_value)
SELECT id, 'DEPARTMENT', '杭州分中心'
FROM row_permission_rule
WHERE resource_id = 'EV_001' AND rule_name = '分中心用户背对背访问';

INSERT INTO row_permission_rule_filter
    (rule_id, sort_order, field_name, field_label, operator, right_type, right_value, right_label)
SELECT id, 0, 'city', '城市', 'EQ', 'USER_ATTRIBUTE', 'city', '当前用户属性：所在城市'
FROM row_permission_rule
WHERE resource_id = 'EV_001' AND rule_name = '分中心用户背对背访问';
