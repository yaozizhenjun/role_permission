package com.example.behavior.rowpermission.infrastructure.persistence;

import com.example.behavior.rowpermission.domain.model.RowPermissionResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface SpringDataRowPermissionResourceJpaRepository extends JpaRepository<RowPermissionResource, Long> {
    Optional<RowPermissionResource> findByResourceId(String resourceId);

    boolean existsByResourceId(String resourceId);

    List<RowPermissionResource> findAllByOrderByCreatedAtDesc();
}
