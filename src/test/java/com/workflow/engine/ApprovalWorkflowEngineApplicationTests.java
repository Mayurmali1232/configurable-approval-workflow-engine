package com.workflow.engine;

import com.workflow.engine.constants.*;
import com.workflow.engine.dto.*;
import com.workflow.engine.entity.*;
import com.workflow.engine.exception.*;
import com.workflow.engine.repository.*;
import com.workflow.engine.service.impl.WorkflowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalWorkflowEngineApplicationTests {

    @Mock private RequestRepository requestRepository;
    @Mock private ApprovalStepRepository approvalStepRepository;
    @Mock private ApprovalHistoryRepository historyRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks private WorkflowServiceImpl workflowService;

    @BeforeEach
    void setupSecurityContext() {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateRequest_Success() {

        when(authentication.getName()).thenReturn("alice");

        // Create User
        User user = new User();
        user.setUsername("mayur");
        user.setRole(Role.REQUESTER);

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        // Create ApprovalStep
        ApprovalStep step = new ApprovalStep();
        step.setRequestType("LEAVE");
        step.setStepOrder(1);
        step.setRole(Role.APPROVER);

        when(approvalStepRepository.findByRequestTypeOrderByStepOrderAsc("LEAVE"))
                .thenReturn(List.of(step));

        // Create Request
        Request req = new Request();
        req.setId(1L);
        req.setType("LEAVE");
        req.setStatus(RequestStatus.PENDING);
        req.setCreatedBy("alice");
        req.setCurrentStep(1);

        when(requestRepository.save(any(Request.class)))
                .thenReturn(req);

        // Input DTO
        RequestCreationDto input = new RequestCreationDto();
        input.setType("LEAVE");

        // Call Service
        RequestResponseDto out = workflowService.createRequest(input);

        // Verify
        assertNotNull(out);
        assertEquals("PENDING", out.getStatus());

        verify(historyRepository, times(1))
                .save(any(ApprovalHistory.class));
    }

    @Test
    void testCreateRequest_InvalidRole_ThrowsException() {

        when(authentication.getName()).thenReturn("bob");

        // Create User
        User user = new User();
        user.setUsername("ganesh");
        user.setRole(Role.APPROVER);

        when(userRepository.findByUsername("bob"))
                .thenReturn(Optional.of(user));

        // Input DTO
        RequestCreationDto input = new RequestCreationDto();
        input.setType("LEAVE");

        // Verify Exception
        assertThrows(UnauthorizedActionException.class,
                () -> workflowService.createRequest(input));
    }

   
    @Test
    void testApproveRequest_Success_NextStep() {

        when(authentication.getName()).thenReturn("bob");

        // Create User
        User caller = new User();
        caller.setUsername("ganesh");
        caller.setRole(Role.APPROVER);

        when(userRepository.findByUsername("bob"))
                .thenReturn(Optional.of(caller));

        // Create Request
        Request req = new Request();
        req.setId(1L);
        req.setType("LEAVE");
        req.setStatus(RequestStatus.PENDING);
        req.setCreatedBy("mayur");
        req.setCurrentStep(1);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(req));

        // Create First Approval Step
        ApprovalStep targetStep = new ApprovalStep();
        targetStep.setRequestType("LEAVE");
        targetStep.setStepOrder(1);
        targetStep.setRole(Role.APPROVER);

        when(approvalStepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.of(targetStep));

        // Create Second Approval Step
        ApprovalStep step2 = new ApprovalStep();
        step2.setRequestType("LEAVE");
        step2.setStepOrder(2);
        step2.setRole(Role.ADMIN);

        when(approvalStepRepository.findByRequestTypeOrderByStepOrderAsc("LEAVE"))
                .thenReturn(List.of(targetStep, step2));

        when(requestRepository.save(any(Request.class)))
                .thenReturn(req);

        // Action Request
        ActionRequestDto action = new ActionRequestDto();
        action.setRemarks("Looks valid.");

        // Call Service
        RequestResponseDto out = workflowService.approveRequest(1L, action);

        // Verify
        assertNotNull(out);
        assertEquals(2, out.getCurrentStep());

        verify(historyRepository, times(1))
                .save(any(ApprovalHistory.class));
    }

    @Test
    void testRejectRequest_Success() {

        when(authentication.getName()).thenReturn("bob");

        // Create User
        User caller = new User();
        caller.setUsername("ganesh");
        caller.setRole(Role.APPROVER);

        when(userRepository.findByUsername("bob"))
                .thenReturn(Optional.of(caller));

        // Create Request
        Request req = new Request();
        req.setId(1L);
        req.setType("LEAVE");
        req.setStatus(RequestStatus.PENDING);
        req.setCreatedBy("alice");
        req.setCurrentStep(1);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(req));

        // Create Approval Step
        ApprovalStep targetStep = new ApprovalStep();
        targetStep.setRequestType("LEAVE");
        targetStep.setStepOrder(1);
        targetStep.setRole(Role.APPROVER);

        when(approvalStepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.of(targetStep));

        when(requestRepository.save(any(Request.class)))
                .thenReturn(req);

        // Action Request
        ActionRequestDto action = new ActionRequestDto();
        action.setRemarks("Declined.");

        // Call Service
        RequestResponseDto out = workflowService.rejectRequest(1L, action);

        // Verify
        assertEquals("REJECTED", out.getStatus());
    }

    @Test
    void testAdminOverride_Success() {

        when(authentication.getName()).thenReturn("rushi");

        // Create User
        User caller = new User();
        caller.setUsername("charlie");
        caller.setRole(Role.ADMIN);

        when(userRepository.findByUsername("charlie"))
                .thenReturn(Optional.of(caller));

        // Create Request
        Request req = new Request();
        req.setId(1L);
        req.setType("LEAVE");
        req.setStatus(RequestStatus.PENDING);
        req.setCreatedBy("alice");
        req.setCurrentStep(1);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(req));

        when(requestRepository.save(any(Request.class)))
                .thenReturn(req);

        // Action Request
        ActionRequestDto action = new ActionRequestDto();
        action.setRemarks("Executive direct bypass");

        // Call Service
        RequestResponseDto out = workflowService.adminOverride(1L, action);

        // Verify
        assertEquals("APPROVED", out.getStatus());

        verify(historyRepository, times(1))
                .save(any(ApprovalHistory.class));
    }
}