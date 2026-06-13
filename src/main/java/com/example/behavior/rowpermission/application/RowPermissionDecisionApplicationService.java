package com.example.behavior.rowpermission.application;

import com.example.behavior.rowpermission.application.dto.RowPermissionDtos;
import com.example.behavior.rowpermission.domain.model.RowFilterExpression;
import com.example.behavior.rowpermission.domain.model.RowPermissionRule;
import com.example.behavior.rowpermission.domain.model.RuleStatus;
import com.example.behavior.rowpermission.domain.model.UserContext;
import com.example.behavior.rowpermission.domain.repository.RowPermissionRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class RowPermissionDecisionApplicationService {
    private final RowPermissionRuleRepository ruleRepository;

    public RowPermissionDecisionApplicationService(RowPermissionRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
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

        UserContext userContext = RowPermissionAssembler.toUserContext(request.user);
        List<RowPermissionRule> rules = ruleRepository.findByResourceIdInAndStatus(request.resourceIds, RuleStatus.ENABLED);
        for (RowPermissionRule rule : rules) {
            if (!rule.isAvailableFor(request.systemCode, userContext)) {
                continue;
            }
            List<RowFilterExpression> expressions = rule.resolveFilterExpressions(userContext);
            response.rowPermissionFilters.addAll(RowPermissionAssembler.toFilterDtos(expressions));
            response.finalFilters.addAll(RowPermissionAssembler.toFilterDtos(expressions));
            response.matchedRuleIds.add(rule.getId());
        }
        return response;
    }
}
