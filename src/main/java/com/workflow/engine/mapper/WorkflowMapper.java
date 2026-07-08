package com.workflow.engine.mapper;

import com.workflow.engine.dto.ApprovalHistoryDto;
import com.workflow.engine.dto.RequestResponseDto;
import com.workflow.engine.entity.ApprovalHistory;
import com.workflow.engine.entity.Request;

public class WorkflowMapper {

    public static RequestResponseDto toResponseDto(Request request) {
        if (request == null) return null;
        RequestResponseDto dto = new RequestResponseDto();
        dto.setId(request.getId());
        dto.setType(request.getType());
        dto.setStatus(request.getStatus().name());
        dto.setCreatedBy(request.getCreatedBy());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setCurrentStep(request.getCurrentStep());
        return dto;
    }

    public static ApprovalHistoryDto toHistoryDto(ApprovalHistory history) {
        if (history == null) return null;
        ApprovalHistoryDto dto = new ApprovalHistoryDto();
        dto.setId(history.getId());
        dto.setRequestId(history.getRequest().getId());
        dto.setAction(history.getAction().name());
        dto.setActionBy(history.getActionBy());
        dto.setActionAt(history.getActionAt());
        dto.setRemarks(history.getRemarks());
        return dto;
    }
}