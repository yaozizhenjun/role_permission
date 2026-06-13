package com.example.behavior.rowpermission.infrastructure.persistence;

import com.example.behavior.rowpermission.domain.model.RowPermissionRule;
import com.example.behavior.rowpermission.domain.model.RuleStatus;
import com.example.behavior.rowpermission.domain.repository.RowPermissionRuleRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaRowPermissionRuleRepository implements RowPermissionRuleRepository {
    private final SpringDataRowPermissionRuleJpaRepository jpaRepository;

    public JpaRowPermissionRuleRepository(SpringDataRowPermissionRuleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<RowPermissionRule> findById(Long ruleId) {
        return jpaRepository.findById(ruleId);
    }

    @Override
    public List<RowPermissionRule> findByResourceIdOrderByCreatedAtDesc(String resourceId) {
        return jpaRepository.findByResourceIdOrderByCreatedAtDesc(resourceId);
    }

    @Override
    public List<RowPermissionRule> findByResourceIdInAndStatus(Collection<String> resourceIds, RuleStatus status) {
        return jpaRepository.findByResourceIdInAndStatus(resourceIds, status);
    }

    @Override
    public long countByResourceId(String resourceId) {
        return jpaRepository.countByResourceId(resourceId);
    }

    @Override
    public RowPermissionRule save(RowPermissionRule rule) {
        return jpaRepository.save(rule);
    }

    @Override
    public void delete(RowPermissionRule rule) {
        jpaRepository.delete(rule);
    }

    @Override
    public void deleteByResourceId(String resourceId) {
        jpaRepository.deleteByResourceId(resourceId);
    }
}
