package com.example.behavior.rowpermission;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RowPermissionDtos {
    private RowPermissionDtos() {
    }

    public static class ResourceCreateRequest {
        @NotBlank
        public String resourceId;

        @NotBlank
        public String resourceName;

        @NotNull
        public ResourceType resourceType;
    }

    public static class ResourceView {
        public Long id;
        public String resourceId;
        public String resourceName;
        public ResourceType resourceType;
        public Integer ruleCount;
        public Boolean enabled;
    }

    public static class RuleCreateRequest {
        @NotBlank
        public String ruleName;

        public String ruleDescription;

        public List<String> enabledSystems = new ArrayList<String>();

        @Valid
        @NotEmpty
        public List<SubjectConditionDto> subjects = new ArrayList<SubjectConditionDto>();

        @Valid
        @NotEmpty
        public List<FilterConditionDto> filterConditions = new ArrayList<FilterConditionDto>();

        public RuleStatus status = RuleStatus.ENABLED;
    }

    public static class RuleUpdateRequest extends RuleCreateRequest {
    }

    public static class RuleView {
        public Long id;
        public String resourceId;
        public String ruleName;
        public String ruleDescription;
        public List<String> enabledSystems = new ArrayList<String>();
        public List<SubjectConditionDto> subjects = new ArrayList<SubjectConditionDto>();
        public List<FilterConditionDto> filterConditions = new ArrayList<FilterConditionDto>();
        public RuleStatus status;
    }

    public static class SubjectConditionDto {
        @NotNull
        public SubjectType subjectType;

        @NotEmpty
        public List<String> values = new ArrayList<String>();
    }

    public static class FilterConditionDto {
        @NotBlank
        public String fieldName;

        public String fieldLabel;

        @NotNull
        public FilterOperator operator;

        @NotNull
        public FilterValueType rightType;

        @NotBlank
        public String rightValue;

        public String rightLabel;
    }

    public static class UserContextDto {
        @NotBlank
        public String userId;

        public String userName;

        public List<String> departmentNames = new ArrayList<String>();

        public Map<String, String> attributes = new HashMap<String, String>();
    }

    public static class AnalysisRequestDto {
        @NotBlank
        public String systemCode;

        @NotEmpty
        public List<String> resourceIds = new ArrayList<String>();

        @Valid
        @NotNull
        public UserContextDto user;

        @Valid
        public List<FilterConditionDto> existingFilters = new ArrayList<FilterConditionDto>();
    }

    public static class AnalysisPreviewResponse {
        public List<FilterConditionDto> existingFilters = new ArrayList<FilterConditionDto>();
        public List<FilterConditionDto> rowPermissionFilters = new ArrayList<FilterConditionDto>();
        public List<FilterConditionDto> finalFilters = new ArrayList<FilterConditionDto>();
        public List<Long> matchedRuleIds = new ArrayList<Long>();
    }
}
