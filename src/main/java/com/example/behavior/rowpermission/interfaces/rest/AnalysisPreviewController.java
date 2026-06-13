package com.example.behavior.rowpermission.interfaces.rest;

import com.example.behavior.rowpermission.application.RowPermissionDecisionApplicationService;
import com.example.behavior.rowpermission.application.dto.RowPermissionDtos;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisPreviewController {
    private final RowPermissionDecisionApplicationService decisionApplicationService;

    public AnalysisPreviewController(RowPermissionDecisionApplicationService decisionApplicationService) {
        this.decisionApplicationService = decisionApplicationService;
    }

    @PostMapping("/preview-with-row-permission")
    public RowPermissionDtos.AnalysisPreviewResponse preview(@Valid @RequestBody RowPermissionDtos.AnalysisRequestDto request) {
        return decisionApplicationService.apply(request);
    }
}
