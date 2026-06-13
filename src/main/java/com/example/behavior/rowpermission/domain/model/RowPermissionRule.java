package com.example.behavior.rowpermission.domain.model;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "row_permission_rule")
public class RowPermissionRule {
    public static final String DENY_ALL_VALUE = "__ROW_PERMISSION_DENY_ALL__";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false, length = 64)
    private String resourceId;

    @Column(name = "rule_name", nullable = false, length = 128)
    private String ruleName;

    @Column(name = "rule_description", length = 512)
    private String ruleDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "row_permission_rule_system", joinColumns = @JoinColumn(name = "rule_id"))
    @Column(name = "system_code", nullable = false, length = 128)
    private Set<String> enabledSystems = new LinkedHashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "row_permission_rule_subject", joinColumns = @JoinColumn(name = "rule_id"))
    private Set<AuthorizedSubject> subjects = new LinkedHashSet<AuthorizedSubject>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "row_permission_rule_filter", joinColumns = @JoinColumn(name = "rule_id"))
    @OrderColumn(name = "sort_order")
    private List<RowFilterExpression> filterExpressions = new ArrayList<RowFilterExpression>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private RuleStatus status = RuleStatus.ENABLED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected RowPermissionRule() {
    }

    private RowPermissionRule(String resourceId,
                              String ruleName,
                              String ruleDescription,
                              Set<String> enabledSystems,
                              Set<AuthorizedSubject> subjects,
                              List<RowFilterExpression> filterExpressions,
                              RuleStatus status) {
        this.resourceId = requireText(resourceId, "资源ID不能为空");
        update(ruleName, ruleDescription, enabledSystems, subjects, filterExpressions, status);
    }

    public static RowPermissionRule create(String resourceId,
                                           String ruleName,
                                           String ruleDescription,
                                           Set<String> enabledSystems,
                                           Set<AuthorizedSubject> subjects,
                                           List<RowFilterExpression> filterExpressions,
                                           RuleStatus status) {
        return new RowPermissionRule(resourceId, ruleName, ruleDescription, enabledSystems, subjects, filterExpressions, status);
    }

    public void update(String ruleName,
                       String ruleDescription,
                       Set<String> enabledSystems,
                       Set<AuthorizedSubject> subjects,
                       List<RowFilterExpression> filterExpressions,
                       RuleStatus status) {
        if (subjects == null || subjects.isEmpty()) {
            throw new IllegalArgumentException("授权对象不能为空");
        }
        if (filterExpressions == null || filterExpressions.isEmpty()) {
            throw new IllegalArgumentException("过滤规则不能为空");
        }
        this.ruleName = requireText(ruleName, "权限名称不能为空");
        this.ruleDescription = ruleDescription;
        this.enabledSystems.clear();
        if (enabledSystems != null) {
            this.enabledSystems.addAll(enabledSystems);
        }
        this.subjects.clear();
        this.subjects.addAll(subjects);
        this.filterExpressions.clear();
        this.filterExpressions.addAll(filterExpressions);
        this.status = status == null ? RuleStatus.ENABLED : status;
    }

    public boolean isAvailableFor(String systemCode, UserContext userContext) {
        return status == RuleStatus.ENABLED && matchesSystem(systemCode) && matchesSubject(userContext);
    }

    public List<RowFilterExpression> resolveFilterExpressions(UserContext userContext) {
        List<RowFilterExpression> resolved = new ArrayList<RowFilterExpression>();
        for (RowFilterExpression expression : filterExpressions) {
            if (expression.getRightType() == FilterValueType.LITERAL) {
                resolved.add(expression);
                continue;
            }
            String value = userContext.resolveAttribute(expression.getRightValue(), DENY_ALL_VALUE);
            resolved.add(expression.resolveWithLiteralValue(value));
        }
        return resolved;
    }

    public void changeStatus(RuleStatus status) {
        this.status = status == null ? RuleStatus.ENABLED : status;
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

    public String getRuleName() {
        return ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public Set<String> getEnabledSystems() {
        return Collections.unmodifiableSet(enabledSystems);
    }

    public Set<AuthorizedSubject> getSubjects() {
        return Collections.unmodifiableSet(subjects);
    }

    public List<RowFilterExpression> getFilterExpressions() {
        return Collections.unmodifiableList(filterExpressions);
    }

    public RuleStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private boolean matchesSystem(String systemCode) {
        return enabledSystems.isEmpty() || enabledSystems.contains(systemCode);
    }

    private boolean matchesSubject(UserContext userContext) {
        for (AuthorizedSubject subject : subjects) {
            if (subject.matches(userContext)) {
                return true;
            }
        }
        return false;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
