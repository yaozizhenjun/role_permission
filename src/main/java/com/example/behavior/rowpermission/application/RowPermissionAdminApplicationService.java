package com.example.behavior.rowpermission.application;

import com.example.behavior.rowpermission.application.dto.RowPermissionDtos;
import com.example.behavior.rowpermission.domain.model.RowPermissionResource;
import com.example.behavior.rowpermission.domain.model.RowPermissionRule;
import com.example.behavior.rowpermission.domain.model.RuleStatus;
import com.example.behavior.rowpermission.domain.repository.RowPermissionResourceRepository;
import com.example.behavior.rowpermission.domain.repository.RowPermissionRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RowPermissionAdminApplicationService {
    private final RowPermissionResourceRepository resourceRepository;
    private final RowPermissionRuleRepository ruleRepository;

    public RowPermissionAdminApplicationService(RowPermissionResourceRepository resourceRepository,
                                                RowPermissionRuleRepository ruleRepository) {
        this.resourceRepository = resourceRepository;
        this.ruleRepository = ruleRepository;
    }

    @Transactional(readOnly = true)
    public List<RowPermissionDtos.ResourceView> listResources() {
        List<RowPermissionDtos.ResourceView> views = new ArrayList<RowPermissionDtos.ResourceView>();
        for (RowPermissionResource resource : resourceRepository.findAllOrderByCreatedAtDesc()) {
            resource.refreshRuleCount(ruleRepository.countByResourceId(resource.getResourceId()));
            views.add(RowPermissionAssembler.toResourceView(resource));
        }
        return views;
    }

    public RowPermissionDtos.ResourceView createResource(RowPermissionDtos.ResourceCreateRequest request) {
        if (resourceRepository.existsByResourceId(request.resourceId)) {
            throw new IllegalArgumentException("资源ID已存在：" + request.resourceId);
        }
        RowPermissionResource resource = RowPermissionResource.create(
                request.resourceId,
                request.resourceName,
                request.resourceType
        );
        return RowPermissionAssembler.toResourceView(resourceRepository.save(resource));
    }

    public void deleteResource(String resourceId) {
        RowPermissionResource resource = findResource(resourceId);
        ruleRepository.deleteByResourceId(resource.getResourceId());
        resourceRepository.delete(resource);
    }

    @Transactional(readOnly = true)
    public List<RowPermissionDtos.RuleView> listRules(String resourceId) {
        findResource(resourceId);
        List<RowPermissionDtos.RuleView> views = new ArrayList<RowPermissionDtos.RuleView>();
        for (RowPermissionRule rule : ruleRepository.findByResourceIdOrderByCreatedAtDesc(resourceId)) {
            views.add(RowPermissionAssembler.toRuleView(rule));
        }
        return views;
    }

    public RowPermissionDtos.RuleView createRule(String resourceId, RowPermissionDtos.RuleCreateRequest request) {
        findResource(resourceId);
        RowPermissionRule rule = RowPermissionRule.create(
                resourceId,
                request.ruleName,
                request.ruleDescription,
                RowPermissionAssembler.toSystemSet(request.enabledSystems),
                RowPermissionAssembler.toSubjects(request.subjects),
                RowPermissionAssembler.toFilterExpressions(request.filterConditions),
                request.status
        );
        RowPermissionRule saved = ruleRepository.save(rule);
        refreshRuleCount(resourceId);
        return RowPermissionAssembler.toRuleView(saved);
    }

    public RowPermissionDtos.RuleView updateRule(Long ruleId, RowPermissionDtos.RuleUpdateRequest request) {
        RowPermissionRule rule = findRule(ruleId);
        rule.update(
                request.ruleName,
                request.ruleDescription,
                RowPermissionAssembler.toSystemSet(request.enabledSystems),
                RowPermissionAssembler.toSubjects(request.subjects),
                RowPermissionAssembler.toFilterExpressions(request.filterConditions),
                request.status
        );
        return RowPermissionAssembler.toRuleView(ruleRepository.save(rule));
    }

    public RowPermissionDtos.RuleView updateStatus(Long ruleId, RuleStatus status) {
        RowPermissionRule rule = findRule(ruleId);
        rule.changeStatus(status);
        return RowPermissionAssembler.toRuleView(ruleRepository.save(rule));
    }

    public void deleteRule(Long ruleId) {
        RowPermissionRule rule = findRule(ruleId);
        String resourceId = rule.getResourceId();
        ruleRepository.delete(rule);
        refreshRuleCount(resourceId);
    }

    private RowPermissionResource findResource(String resourceId) {
        if (!StringUtils.hasText(resourceId)) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        return resourceRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("资源不存在：" + resourceId));
    }

    private RowPermissionRule findRule(Long ruleId) {
        return ruleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("行权限规则不存在：" + ruleId));
    }

    private void refreshRuleCount(String resourceId) {
        RowPermissionResource resource = findResource(resourceId);
        resource.refreshRuleCount(ruleRepository.countByResourceId(resourceId));
        resourceRepository.save(resource);
    }
}
