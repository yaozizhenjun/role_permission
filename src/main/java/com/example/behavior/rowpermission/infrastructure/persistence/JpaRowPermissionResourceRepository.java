package com.example.behavior.rowpermission.infrastructure.persistence;

import com.example.behavior.rowpermission.domain.model.RowPermissionResource;
import com.example.behavior.rowpermission.domain.repository.RowPermissionResourceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaRowPermissionResourceRepository implements RowPermissionResourceRepository {
    private final SpringDataRowPermissionResourceJpaRepository jpaRepository;

    public JpaRowPermissionResourceRepository(SpringDataRowPermissionResourceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<RowPermissionResource> findByResourceId(String resourceId) {
        return jpaRepository.findByResourceId(resourceId);
    }

    @Override
    public boolean existsByResourceId(String resourceId) {
        return jpaRepository.existsByResourceId(resourceId);
    }

    @Override
    public List<RowPermissionResource> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public RowPermissionResource save(RowPermissionResource resource) {
        return jpaRepository.save(resource);
    }

    @Override
    public void delete(RowPermissionResource resource) {
        jpaRepository.delete(resource);
    }
}
