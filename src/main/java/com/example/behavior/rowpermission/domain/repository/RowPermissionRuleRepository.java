package com.example.behavior.rowpermission.domain.repository;

import com.example.behavior.rowpermission.domain.model.RowPermissionRule;
import com.example.behavior.rowpermission.domain.model.RuleStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RowPermissionRuleRepository {
    Optional<RowPermissionRule> findById(Long ruleId);

    List<RowPermissionRule> findByResourceIdOrderByCreatedAtDesc(String resourceId);

    List<RowPermissionRule> findByResourceIdInAndStatus(Collection<String> resourceIds, RuleStatus status);

    long countByResourceId(String resourceId);

    RowPermissionRule save(RowPermissionRule rule);

    void delete(RowPermissionRule rule);

    void deleteByResourceId(String resourceId);
}
