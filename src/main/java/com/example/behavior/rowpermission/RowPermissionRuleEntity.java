package com.example.behavior.rowpermission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "row_permission_rule")
public class RowPermissionRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "resource_id", nullable = false, length = 64)
    public String resourceId;

    @Column(name = "rule_name", nullable = false, length = 128)
    public String ruleName;

    @Column(name = "rule_description", length = 512)
    public String ruleDescription;

    @Lob
    @Column(name = "enabled_systems_json")
    public String enabledSystemsJson;

    @Lob
    @Column(name = "subjects_json", nullable = false)
    public String subjectsJson;

    @Lob
    @Column(name = "filter_conditions_json", nullable = false)
    public String filterConditionsJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    public RuleStatus status = RuleStatus.ENABLED;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
