package com.example.behavior.rowpermission.domain.repository;

import com.example.behavior.rowpermission.domain.model.RowPermissionResource;

import java.util.List;
import java.util.Optional;

public interface RowPermissionResourceRepository {
    Optional<RowPermissionResource> findByResourceId(String resourceId);

    boolean existsByResourceId(String resourceId);

    List<RowPermissionResource> findAllOrderByCreatedAtDesc();

    RowPermissionResource save(RowPermissionResource resource);

    void delete(RowPermissionResource resource);
}
