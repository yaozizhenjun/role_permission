package com.example.behavior.rowpermission;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisPreviewController {
    private final RowPermissionDecisionService decisionService;

    public AnalysisPreviewController(RowPermissionDecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/preview-with-row-permission")
    public RowPermissionDtos.AnalysisPreviewResponse preview(@Valid @RequestBody RowPermissionDtos.AnalysisRequestDto request) {
        return decisionService.apply(request);
    }
}
