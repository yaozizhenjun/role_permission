package com.example.behavior.rowpermission.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "row_permission_resource")
public class RowPermissionResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false, unique = true, length = 64)
    private String resourceId;

    @Column(name = "resource_name", nullable = false, length = 128)
    private String resourceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 32)
    private ResourceType resourceType;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Column(name = "rule_count", nullable = false)
    private Integer ruleCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected RowPermissionResource() {
    }

    private RowPermissionResource(String resourceId, String resourceName, ResourceType resourceType) {
        this.resourceId = requireText(resourceId, "资源ID不能为空");
        this.resourceName = requireText(resourceName, "资源名称不能为空");
        this.resourceType = requireNonNull(resourceType, "资源类型不能为空");
    }

    public static RowPermissionResource create(String resourceId, String resourceName, ResourceType resourceType) {
        return new RowPermissionResource(resourceId, resourceName, resourceType);
    }

    public void refreshRuleCount(long ruleCount) {
        this.ruleCount = (int) ruleCount;
    }

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

    public Long getId() {
        return id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Integer getRuleCount() {
        return ruleCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
