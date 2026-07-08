package com.workflow.engine.controller;

import com.workflow.engine.dto.ActionRequestDto;
import com.workflow.engine.dto.ApprovalHistoryDto;
import com.workflow.engine.dto.RequestCreationDto;
import com.workflow.engine.dto.RequestResponseDto;
import com.workflow.engine.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
public class WorkflowController 
{

    private WorkflowService workflowService;

    // Constructor Injection
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // Create Request
    @PostMapping
    public ResponseEntity<RequestResponseDto> createRequest(
            @Valid @RequestBody RequestCreationDto request) {

        RequestResponseDto response = workflowService.createRequest(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get Request Details
    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDto> getRequestDetails(@PathVariable Long id) {

        RequestResponseDto response = workflowService.getRequestDetails(id);

        return ResponseEntity.ok(response);
    }

    // Approve Request
    @PostMapping("/{id}/approve")
    public ResponseEntity<RequestResponseDto> approveRequest(@PathVariable Long id, @Valid @RequestBody ActionRequestDto request) {

        RequestResponseDto response = workflowService.approveRequest(id, request);

        return ResponseEntity.ok(response);
    }

    // Reject Request
    @PostMapping("/{id}/reject")
    public ResponseEntity<RequestResponseDto> rejectRequest(
            @PathVariable Long id,
            @Valid @RequestBody ActionRequestDto request) {

        RequestResponseDto response = workflowService.rejectRequest(id, request);

        return ResponseEntity.ok(response);
    }

    // Admin Override
    @PostMapping("/{id}/override")
    public ResponseEntity<RequestResponseDto> overrideRequest(
            @PathVariable Long id,
            @Valid @RequestBody ActionRequestDto request) {

        RequestResponseDto response = workflowService.adminOverride(id, request);

        return ResponseEntity.ok(response);
    }

    // Get Request History
    @GetMapping("/history/{id}")
    public ResponseEntity<List<ApprovalHistoryDto>> getRequestHistory(
            @PathVariable Long id) {

        List<ApprovalHistoryDto> history = workflowService.getRequestHistory(id);

        return ResponseEntity.ok(history);
    }
}