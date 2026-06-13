package com.example.behavior.rowpermission.application;

import com.example.behavior.rowpermission.application.dto.RowPermissionDtos;
import com.example.behavior.rowpermission.domain.model.AuthorizedSubject;
import com.example.behavior.rowpermission.domain.model.RowFilterExpression;
import com.example.behavior.rowpermission.domain.model.RowPermissionResource;
import com.example.behavior.rowpermission.domain.model.RowPermissionRule;
import com.example.behavior.rowpermission.domain.model.SubjectType;
import com.example.behavior.rowpermission.domain.model.UserContext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class RowPermissionAssembler {
    private RowPermissionAssembler() {
    }

    static RowPermissionDtos.ResourceView toResourceView(RowPermissionResource resource) {
        RowPermissionDtos.ResourceView view = new RowPermissionDtos.ResourceView();
        view.id = resource.getId();
        view.resourceId = resource.getResourceId();
        view.resourceName = resource.getResourceName();
        view.resourceType = resource.getResourceType();
        view.ruleCount = resource.getRuleCount();
        view.enabled = resource.getEnabled();
        return view;
    }

    static RowPermissionDtos.RuleView toRuleView(RowPermissionRule rule) {
        RowPermissionDtos.RuleView view = new RowPermissionDtos.RuleView();
        view.id = rule.getId();
        view.resourceId = rule.getResourceId();
        view.ruleName = rule.getRuleName();
        view.ruleDescription = rule.getRuleDescription();
        view.enabledSystems = new ArrayList<String>(rule.getEnabledSystems());
        view.subjects = toSubjectDtos(rule.getSubjects());
        view.filterConditions = toFilterDtos(rule.getFilterExpressions());
        view.status = rule.getStatus();
        return view;
    }

    static Set<String> toSystemSet(List<String> enabledSystems) {
        return enabledSystems == null ? new LinkedHashSet<String>() : new LinkedHashSet<String>(enabledSystems);
    }

    static Set<AuthorizedSubject> toSubjects(List<RowPermissionDtos.SubjectConditionDto> subjectDtos) {
        Set<AuthorizedSubject> subjects = new LinkedHashSet<AuthorizedSubject>();
        if (subjectDtos == null) {
            return subjects;
        }
        for (RowPermissionDtos.SubjectConditionDto subjectDto : subjectDtos) {
            for (String value : subjectDto.values) {
                subjects.add(AuthorizedSubject.of(subjectDto.subjectType, value));
            }
        }
        return subjects;
    }

    static List<RowFilterExpression> toFilterExpressions(List<RowPermissionDtos.FilterConditionDto> filterDtos) {
        List<RowFilterExpression> expressions = new ArrayList<RowFilterExpression>();
        if (filterDtos == null) {
            return expressions;
        }
        for (RowPermissionDtos.FilterConditionDto dto : filterDtos) {
            expressions.add(RowFilterExpression.of(
                    dto.fieldName,
                    dto.fieldLabel,
                    dto.operator,
                    dto.rightType,
                    dto.rightValue,
                    dto.rightLabel
            ));
        }
        return expressions;
    }

    static List<RowPermissionDtos.FilterConditionDto> toFilterDtos(List<RowFilterExpression> expressions) {
        List<RowPermissionDtos.FilterConditionDto> dtos = new ArrayList<RowPermissionDtos.FilterConditionDto>();
        for (RowFilterExpression expression : expressions) {
            dtos.add(toFilterDto(expression));
        }
        return dtos;
    }

    static RowPermissionDtos.FilterConditionDto toFilterDto(RowFilterExpression expression) {
        RowPermissionDtos.FilterConditionDto dto = new RowPermissionDtos.FilterConditionDto();
        dto.fieldName = expression.getFieldName();
        dto.fieldLabel = expression.getFieldLabel();
        dto.operator = expression.getOperator();
        dto.rightType = expression.getRightType();
        dto.rightValue = expression.getRightValue();
        dto.rightLabel = expression.getRightLabel();
        return dto;
    }

    static UserContext toUserContext(RowPermissionDtos.UserContextDto user) {
        return new UserContext(user.userId, user.userName, user.departmentNames, user.attributes);
    }

    private static List<RowPermissionDtos.SubjectConditionDto> toSubjectDtos(Set<AuthorizedSubject> subjects) {
        List<RowPermissionDtos.SubjectConditionDto> dtos = new ArrayList<RowPermissionDtos.SubjectConditionDto>();
        for (SubjectType subjectType : SubjectType.values()) {
            RowPermissionDtos.SubjectConditionDto dto = new RowPermissionDtos.SubjectConditionDto();
            dto.subjectType = subjectType;
            for (AuthorizedSubject subject : subjects) {
                if (subject.getSubjectType() == subjectType) {
                    dto.values.add(subject.getSubjectValue());
                }
            }
            if (!dto.values.isEmpty()) {
                dtos.add(dto);
            }
        }
        return dtos;
    }
}
