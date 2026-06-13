package com.example.behavior.rowpermission;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RowPermissionAdminService {
    private final RowPermissionResourceRepository resourceRepository;
    private final RowPermissionRuleRepository ruleRepository;
    private final RowPermissionJson json;

    public RowPermissionAdminService(RowPermissionResourceRepository resourceRepository,
                                     RowPermissionRuleRepository ruleRepository,
                                     RowPermissionJson json) {
        this.resourceRepository = resourceRepository;
        this.ruleRepository = ruleRepository;
        this.json = json;
    }

    @Transactional(readOnly = true)
    public List<RowPermissionDtos.ResourceView> listResources() {
        List<RowPermissionResourceEntity> entities = resourceRepository.findAllByOrderByCreatedAtDesc();
        List<RowPermissionDtos.ResourceView> views = new ArrayList<RowPermissionDtos.ResourceView>();
        for (RowPermissionResourceEntity entity : entities) {
            entity.ruleCount = (int) ruleRepository.countByResourceId(entity.resourceId);
            views.add(toResourceView(entity));
        }
        return views;
    }

    public RowPermissionDtos.ResourceView createResource(RowPermissionDtos.ResourceCreateRequest request) {
        if (resourceRepository.existsByResourceId(request.resourceId)) {
            throw new IllegalArgumentException("资源ID已存在：" + request.resourceId);
        }
        RowPermissionResourceEntity entity = new RowPermissionResourceEntity();
        entity.resourceId = request.resourceId;
        entity.resourceName = request.resourceName;
        entity.resourceType = request.resourceType;
        entity.enabled = Boolean.TRUE;
        entity.ruleCount = 0;
        return toResourceView(resourceRepository.save(entity));
    }

    public void deleteResource(String resourceId) {
        RowPermissionResourceEntity resource = findResource(resourceId);
        ruleRepository.deleteByResourceId(resource.resourceId);
        resourceRepository.delete(resource);
    }

    @Transactional(readOnly = true)
    public List<RowPermissionDtos.RuleView> listRules(String resourceId) {
        findResource(resourceId);
        List<RowPermissionRuleEntity> entities = ruleRepository.findByResourceIdOrderByCreatedAtDesc(resourceId);
        List<RowPermissionDtos.RuleView> views = new ArrayList<RowPermissionDtos.RuleView>();
        for (RowPermissionRuleEntity entity : entities) {
            views.add(toRuleView(entity));
        }
        return views;
    }

    public RowPermissionDtos.RuleView createRule(String resourceId, RowPermissionDtos.RuleCreateRequest request) {
        findResource(resourceId);
        RowPermissionRuleEntity entity = new RowPermissionRuleEntity();
        fillRule(entity, resourceId, request);
        RowPermissionRuleEntity saved = ruleRepository.save(entity);
        refreshRuleCount(resourceId);
        return toRuleView(saved);
    }

    public RowPermissionDtos.RuleView updateRule(Long ruleId, RowPermissionDtos.RuleUpdateRequest request) {
        RowPermissionRuleEntity entity = findRule(ruleId);
        fillRule(entity, entity.resourceId, request);
        return toRuleView(ruleRepository.save(entity));
    }

    public RowPermissionDtos.RuleView updateStatus(Long ruleId, RuleStatus status) {
        RowPermissionRuleEntity entity = findRule(ruleId);
        entity.status = status;
        return toRuleView(ruleRepository.save(entity));
    }

    public void deleteRule(Long ruleId) {
        RowPermissionRuleEntity entity = findRule(ruleId);
        String resourceId = entity.resourceId;
        ruleRepository.delete(entity);
        refreshRuleCount(resourceId);
    }

    private void fillRule(RowPermissionRuleEntity entity, String resourceId, RowPermissionDtos.RuleCreateRequest request) {
        entity.resourceId = resourceId;
        entity.ruleName = request.ruleName;
        entity.ruleDescription = request.ruleDescription;
        entity.enabledSystemsJson = json.write(request.enabledSystems == null ? new ArrayList<String>() : request.enabledSystems);
        entity.subjectsJson = json.write(request.subjects);
        entity.filterConditionsJson = json.write(request.filterConditions);
        entity.status = request.status == null ? RuleStatus.ENABLED : request.status;
    }

    private RowPermissionResourceEntity findResource(String resourceId) {
        if (!StringUtils.hasText(resourceId)) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        return resourceRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("资源不存在：" + resourceId));
    }

    private RowPermissionRuleEntity findRule(Long ruleId) {
        return ruleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("行权限规则不存在：" + ruleId));
    }

    private void refreshRuleCount(String resourceId) {
        RowPermissionResourceEntity resource = findResource(resourceId);
        resource.ruleCount = (int) ruleRepository.countByResourceId(resourceId);
        resourceRepository.save(resource);
    }

    private RowPermissionDtos.ResourceView toResourceView(RowPermissionResourceEntity entity) {
        RowPermissionDtos.ResourceView view = new RowPermissionDtos.ResourceView();
        view.id = entity.id;
        view.resourceId = entity.resourceId;
        view.resourceName = entity.resourceName;
        view.resourceType = entity.resourceType;
        view.ruleCount = entity.ruleCount;
        view.enabled = entity.enabled;
        return view;
    }

    private RowPermissionDtos.RuleView toRuleView(RowPermissionRuleEntity entity) {
        RowPermissionDtos.RuleView view = new RowPermissionDtos.RuleView();
        view.id = entity.id;
        view.resourceId = entity.resourceId;
        view.ruleName = entity.ruleName;
        view.ruleDescription = entity.ruleDescription;
        view.enabledSystems = json.readStringList(entity.enabledSystemsJson);
        view.subjects = json.readSubjects(entity.subjectsJson);
        view.filterConditions = json.readFilters(entity.filterConditionsJson);
        view.status = entity.status;
        return view;
    }
}
