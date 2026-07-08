package com.workflow.engine.service;

import com.workflow.engine.dto.ActionRequestDto;
import com.workflow.engine.dto.ApprovalHistoryDto;
import com.workflow.engine.dto.RequestCreationDto;
import com.workflow.engine.dto.RequestResponseDto;
import java.util.List;

public interface WorkflowService {
    RequestResponseDto createRequest(RequestCreationDto dto);
    RequestResponseDto getRequestDetails(Long id);
    RequestResponseDto approveRequest(Long id, ActionRequestDto dto);
    RequestResponseDto rejectRequest(Long id, ActionRequestDto dto);
    RequestResponseDto adminOverride(Long id, ActionRequestDto dto);
    List<ApprovalHistoryDto> getRequestHistory(Long id);
}