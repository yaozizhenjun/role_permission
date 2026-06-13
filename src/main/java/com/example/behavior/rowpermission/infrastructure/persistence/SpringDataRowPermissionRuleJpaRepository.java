package com.example.behavior.rowpermission.infrastructure.persistence;

import com.example.behavior.rowpermission.domain.model.RowPermissionRule;
import com.example.behavior.rowpermission.domain.model.RuleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

interface SpringDataRowPermissionRuleJpaRepository extends JpaRepository<RowPermissionRule, Long> {
    List<RowPermissionRule> findByResourceIdOrderByCreatedAtDesc(String resourceId);

    List<RowPermissionRule> findByResourceIdInAndStatus(Collection<String> resourceIds, RuleStatus status);

    long countByResourceId(String resourceId);

    void deleteByResourceId(String resourceId);
}
