package com.example.behavior.rowpermission;

import com.example.behavior.rowpermission.application.RowPermissionAdminApplicationService;
import com.example.behavior.rowpermission.application.RowPermissionDecisionApplicationService;
import com.example.behavior.rowpermission.application.dto.RowPermissionDtos;
import com.example.behavior.rowpermission.domain.model.FilterOperator;
import com.example.behavior.rowpermission.domain.model.FilterValueType;
import com.example.behavior.rowpermission.domain.model.ResourceType;
import com.example.behavior.rowpermission.domain.model.RuleStatus;
import com.example.behavior.rowpermission.domain.model.SubjectType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RowPermissionDecisionServiceTest {
    @Autowired
    private RowPermissionDecisionApplicationService decisionService;

    @Autowired
    private RowPermissionAdminApplicationService adminService;

    @Test
    void appendsCityFilterWhenDepartmentMatches() {
        createCityRule("EV_TEST_001");

        RowPermissionDtos.AnalysisRequestDto request = new RowPermissionDtos.AnalysisRequestDto();
        request.systemCode = "微光分析中心";
        request.resourceIds = Arrays.asList("EV_TEST_001");
        request.user = new RowPermissionDtos.UserContextDto();
        request.user.userId = "u001";
        request.user.departmentNames = Arrays.asList("北京分中心");
        request.user.attributes.put("city", "北京");

        RowPermissionDtos.AnalysisPreviewResponse response = decisionService.apply(request);

        assertThat(response.matchedRuleIds).hasSize(1);
        assertThat(response.rowPermissionFilters).hasSize(1);
        assertThat(response.rowPermissionFilters.get(0).fieldName).isEqualTo("city");
        assertThat(response.rowPermissionFilters.get(0).rightValue).isEqualTo("北京");
        assertThat(response.finalFilters).hasSize(1);
    }

    @Test
    void skipsRuleWhenUserDoesNotMatchSubject() {
        createCityRule("EV_TEST_002");

        RowPermissionDtos.AnalysisRequestDto request = new RowPermissionDtos.AnalysisRequestDto();
        request.systemCode = "微光分析中心";
        request.resourceIds = Arrays.asList("EV_TEST_002");
        request.user = new RowPermissionDtos.UserContextDto();
        request.user.userId = "u002";
        request.user.departmentNames = Arrays.asList("总部");
        request.user.attributes.put("city", "上海");

        RowPermissionDtos.AnalysisPreviewResponse response = decisionService.apply(request);

        assertThat(response.matchedRuleIds).isEmpty();
        assertThat(response.rowPermissionFilters).isEmpty();
        assertThat(response.finalFilters).isEmpty();
    }

    private void createCityRule(String resourceId) {
        RowPermissionDtos.ResourceCreateRequest resource = new RowPermissionDtos.ResourceCreateRequest();
        resource.resourceId = resourceId;
        resource.resourceName = "测试事件";
        resource.resourceType = ResourceType.EVENT;
        adminService.createResource(resource);

        RowPermissionDtos.RuleCreateRequest rule = new RowPermissionDtos.RuleCreateRequest();
        rule.ruleName = "分中心用户背对背访问";
        rule.ruleDescription = "分中心用户只能访问自己所在城市的数据";
        rule.enabledSystems = Arrays.asList("微光分析中心");

        RowPermissionDtos.SubjectConditionDto subject = new RowPermissionDtos.SubjectConditionDto();
        subject.subjectType = SubjectType.DEPARTMENT;
        subject.values = Arrays.asList("北京分中心", "杭州分中心");
        rule.subjects = Arrays.asList(subject);

        RowPermissionDtos.FilterConditionDto filter = new RowPermissionDtos.FilterConditionDto();
        filter.fieldName = "city";
        filter.fieldLabel = "城市";
        filter.operator = FilterOperator.EQ;
        filter.rightType = FilterValueType.USER_ATTRIBUTE;
        filter.rightValue = "city";
        filter.rightLabel = "当前用户属性：所在城市";
        rule.filterConditions = Arrays.asList(filter);

        rule.status = RuleStatus.ENABLED;
        adminService.createRule(resourceId, rule);
    }
}
