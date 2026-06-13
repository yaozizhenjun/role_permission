package com.example.behavior.rowpermission;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RowPermissionDecisionService {
    private static final String DENY_ALL_VALUE = "__ROW_PERMISSION_DENY_ALL__";

    private final RowPermissionRuleRepository ruleRepository;
    private final RowPermissionJson json;

    public RowPermissionDecisionService(RowPermissionRuleRepository ruleRepository, RowPermissionJson json) {
        this.ruleRepository = ruleRepository;
        this.json = json;
    }

    @Transactional(readOnly = true)
    public RowPermissionDtos.AnalysisPreviewResponse apply(RowPermissionDtos.AnalysisRequestDto request) {
        RowPermissionDtos.AnalysisPreviewResponse response = new RowPermissionDtos.AnalysisPreviewResponse();
        if (request.existingFilters != null) {
            response.existingFilters.addAll(request.existingFilters);
            response.finalFilters.addAll(request.existingFilters);
        }

        if (CollectionUtils.isEmpty(request.resourceIds)) {
            return response;
        }

        List<RowPermissionRuleEntity> rules =
                ruleRepository.findByResourceIdInAndStatus(request.resourceIds, RuleStatus.ENABLED);
        for (RowPermissionRuleEntity rule : rules) {
            if (!systemMatches(rule, request.systemCode)) {
                continue;
            }
            if (!userMatches(rule, request.user)) {
                continue;
            }
            List<RowPermissionDtos.FilterConditionDto> filters = resolveFilters(rule, request.user);
            response.rowPermissionFilters.addAll(filters);
            response.finalFilters.addAll(filters);
            response.matchedRuleIds.add(rule.id);
        }
        return response;
    }

    private boolean systemMatches(RowPermissionRuleEntity rule, String systemCode) {
        List<String> systems = json.readStringList(rule.enabledSystemsJson);
        return systems.isEmpty() || systems.contains(systemCode);
    }

    private boolean userMatches(RowPermissionRuleEntity rule, RowPermissionDtos.UserContextDto user) {
        List<RowPermissionDtos.SubjectConditionDto> subjects = json.readSubjects(rule.subjectsJson);
        for (RowPermissionDtos.SubjectConditionDto subject : subjects) {
            if (subject.subjectType == SubjectType.USER_ACCOUNT && subject.values.contains(user.userId)) {
                return true;
            }
            if (subject.subjectType == SubjectType.DEPARTMENT && intersects(subject.values, user.departmentNames)) {
                return true;
            }
        }
        return false;
    }

    private List<RowPermissionDtos.FilterConditionDto> resolveFilters(RowPermissionRuleEntity rule,
                                                                      RowPermissionDtos.UserContextDto user) {
        List<RowPermissionDtos.FilterConditionDto> configured = json.readFilters(rule.filterConditionsJson);
        List<RowPermissionDtos.FilterConditionDto> resolved = new ArrayList<RowPermissionDtos.FilterConditionDto>();
        for (RowPermissionDtos.FilterConditionDto condition : configured) {
            RowPermissionDtos.FilterConditionDto item = new RowPermissionDtos.FilterConditionDto();
            item.fieldName = condition.fieldName;
            item.fieldLabel = condition.fieldLabel;
            item.operator = condition.operator;
            item.rightType = FilterValueType.LITERAL;
            item.rightLabel = condition.rightLabel;
            item.rightValue = resolveRightValue(condition, user);
            resolved.add(item);
        }
        return resolved;
    }

    private String resolveRightValue(RowPermissionDtos.FilterConditionDto condition,
                                     RowPermissionDtos.UserContextDto user) {
        if (condition.rightType == FilterValueType.LITERAL) {
            return condition.rightValue;
        }
        Map<String, String> attributes = user.attributes;
        if (attributes == null || !attributes.containsKey(condition.rightValue)) {
            return DENY_ALL_VALUE;
        }
        String value = attributes.get(condition.rightValue);
        return value == null || value.trim().isEmpty() ? DENY_ALL_VALUE : value;
    }

    private boolean intersects(List<String> expected, List<String> actual) {
        if (CollectionUtils.isEmpty(expected) || CollectionUtils.isEmpty(actual)) {
            return false;
        }
        Set<String> actualSet = new HashSet<String>(actual);
        for (String item : expected) {
            if (actualSet.contains(item)) {
                return true;
            }
        }
        return false;
    }
}
