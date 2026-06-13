package com.example.behavior.rowpermission.interfaces.rest;

import com.example.behavior.rowpermission.application.RowPermissionAdminApplicationService;
import com.example.behavior.rowpermission.application.dto.RowPermissionDtos;
import com.example.behavior.rowpermission.domain.model.RuleStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/row-permission")
public class RowPermissionController {
    private final RowPermissionAdminApplicationService adminApplicationService;

    public RowPermissionController(RowPermissionAdminApplicationService adminApplicationService) {
        this.adminApplicationService = adminApplicationService;
    }

    @GetMapping("/resources")
    public List<RowPermissionDtos.ResourceView> listResources() {
        return adminApplicationService.listResources();
    }

    @PostMapping("/resources")
    public RowPermissionDtos.ResourceView createResource(@Valid @RequestBody RowPermissionDtos.ResourceCreateRequest request) {
        return adminApplicationService.createResource(request);
    }

    @DeleteMapping("/resources/{resourceId}")
    public void deleteResource(@PathVariable String resourceId) {
        adminApplicationService.deleteResource(resourceId);
    }

    @GetMapping("/resources/{resourceId}/rules")
    public List<RowPermissionDtos.RuleView> listRules(@PathVariable String resourceId) {
        return adminApplicationService.listRules(resourceId);
    }

    @PostMapping("/resources/{resourceId}/rules")
    public RowPermissionDtos.RuleView createRule(@PathVariable String resourceId,
                                                 @Valid @RequestBody RowPermissionDtos.RuleCreateRequest request) {
        return adminApplicationService.createRule(resourceId, request);
    }

    @PutMapping("/rules/{ruleId}")
    public RowPermissionDtos.RuleView updateRule(@PathVariable Long ruleId,
                                                 @Valid @RequestBody RowPermissionDtos.RuleUpdateRequest request) {
        return adminApplicationService.updateRule(ruleId, request);
    }

    @PatchMapping("/rules/{ruleId}/status")
    public RowPermissionDtos.RuleView updateStatus(@PathVariable Long ruleId, @RequestParam RuleStatus status) {
        return adminApplicationService.updateStatus(ruleId, status);
    }

    @DeleteMapping("/rules/{ruleId}")
    public void deleteRule(@PathVariable Long ruleId) {
        adminApplicationService.deleteRule(ruleId);
    }
}
