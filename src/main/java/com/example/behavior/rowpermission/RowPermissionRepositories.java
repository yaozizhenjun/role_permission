package com.example.behavior.rowpermission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface RowPermissionResourceRepository extends JpaRepository<RowPermissionResourceEntity, Long> {
    Optional<RowPermissionResourceEntity> findByResourceId(String resourceId);

    boolean existsByResourceId(String resourceId);

    List<RowPermissionResourceEntity> findAllByOrderByCreatedAtDesc();
}

interface RowPermissionRuleRepository extends JpaRepository<RowPermissionRuleEntity, Long> {
    List<RowPermissionRuleEntity> findByResourceIdOrderByCreatedAtDesc(String resourceId);

    List<RowPermissionRuleEntity> findByResourceIdInAndStatus(Collection<String> resourceIds, RuleStatus status);

    long countByResourceId(String resourceId);

    void deleteByResourceId(String resourceId);
}
