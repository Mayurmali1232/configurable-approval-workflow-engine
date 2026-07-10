package com.workflow.engine.service.impl;

import com.workflow.engine.constants.*;
import com.workflow.engine.dto.*;
import com.workflow.engine.entity.*;
import com.workflow.engine.exception.*;
import com.workflow.engine.mapper.WorkflowMapper;
import com.workflow.engine.repository.*;
import com.workflow.engine.service.WorkflowService;

// Clean Java Imports: Replacing Lombok annotations with standard Java features
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    // 1. STANDARD JAVA LOGGING
    private static final Logger log = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    private final RequestRepository requestRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final ApprovalHistoryRepository historyRepository;
    private final UserRepository userRepository;

    // 2. STANDARD CONSTRUCTOR INJECTION: Replacing 
    @Autowired
    public WorkflowServiceImpl(RequestRepository requestRepository,
                               ApprovalStepRepository approvalStepRepository,
                               ApprovalHistoryRepository historyRepository,
                               UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.approvalStepRepository = approvalStepRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    // Helper method to get logged-in username from Spring Security
    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Helper method to find user details from the database
    private User getCurrentUserEntity() {
        return userRepository.findByUsername(getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + getCurrentUser()));
    }

    @Override
    @Transactional
    public RequestResponseDto createRequest(RequestCreationDto dto) {
        User caller = getCurrentUserEntity();
        
        // Only a REQUESTER can create requests
        if (caller.getRole() != Role.REQUESTER) {
            throw new UnauthorizedActionException("Only Requesters can create a new workflow request.");
        }

        // Validate if the workflow configuration exists in the database
        String type = dto.getType().toUpperCase();
        List<ApprovalStep> workflowSteps = approvalStepRepository.findByRequestTypeOrderByStepOrderAsc(type);
        if (workflowSteps.isEmpty()) {
            throw new InvalidTransitionException("No workflow configuration found for type: " + type);
        }

        // Create and save the new Request using standard manual instantiation
        Request request = new Request();
        request.setType(type);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedBy(caller.getUsername());
        request.setCreatedAt(LocalDateTime.now());
        request.setCurrentStep(1); // Start at step 1
        
        Request savedRequest = requestRepository.save(request);

        // Log creation in history using standard manual instantiation
        ApprovalHistory history = new ApprovalHistory();
        history.setRequest(savedRequest);
        history.setAction(WorkflowAction.CREATED);
        history.setActionBy(caller.getUsername());
        history.setActionAt(LocalDateTime.now());
        history.setRemarks("Workflow request initialized successfully.");
        
        historyRepository.save(history);

        log.info("Workflow created successfully. ID: {}, Type: {}, User: {}", savedRequest.getId(), type, caller.getUsername());
        return WorkflowMapper.toResponseDto(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestResponseDto getRequestDetails(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id));
        return WorkflowMapper.toResponseDto(request);
    }

    @Override
    @Transactional
    public RequestResponseDto approveRequest(Long id, ActionRequestDto dto) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id));
        User caller = getCurrentUserEntity();

        // Validation: Request must be PENDING
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidTransitionException("Cannot approve a request that is not PENDING.");
        }
        
        // Validation: You cannot approve your own request
        if (request.getCreatedBy().equals(caller.getUsername())) {
            throw new UnauthorizedActionException("You cannot approve your own request.");
        }

        // Validation: Get the current rule from DB and check the user's role
        ApprovalStep currentStepRule = approvalStepRepository.findByRequestTypeAndStepOrder(request.getType(), request.getCurrentStep())
                .orElseThrow(() -> new WorkflowException("Workflow step configuration missing for step: " + request.getCurrentStep()));

        if (caller.getRole() != currentStepRule.getRole()) {
            throw new UnauthorizedActionException("Your role (" + caller.getRole() + ") is not authorized to approve step " + request.getCurrentStep());
        }

        // Calculate next state
        List<ApprovalStep> totalSteps = approvalStepRepository.findByRequestTypeOrderByStepOrderAsc(request.getType());
        
        if (request.getCurrentStep() < totalSteps.size()) {
            // Move to next step
            request.setCurrentStep(request.getCurrentStep() + 1);
        } else {
            // No more steps left, fully approve the request
            request.setStatus(RequestStatus.APPROVED);
        }

        Request updatedRequest = requestRepository.save(request);

        // Save History using standard object instantiation
        ApprovalHistory history = new ApprovalHistory();
        history.setRequest(updatedRequest);
        history.setAction(WorkflowAction.APPROVED);
        history.setActionBy(caller.getUsername());
        history.setActionAt(LocalDateTime.now());
        history.setRemarks(dto.getRemarks());
        
        historyRepository.save(history);

        log.info("Request ID {} APPROVED by user {}", id, caller.getUsername());
        return WorkflowMapper.toResponseDto(updatedRequest);
    }

    @Override
    @Transactional
    public RequestResponseDto rejectRequest(Long id, ActionRequestDto dto) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id));
        User caller = getCurrentUserEntity();

        // Validation: Request must be PENDING
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidTransitionException("Cannot reject a closed request.");
        }

        // Validation: Check if the user has the correct role for this step
        ApprovalStep currentStepRule = approvalStepRepository.findByRequestTypeAndStepOrder(request.getType(), request.getCurrentStep())
                .orElseThrow(() -> new WorkflowException("Workflow step configuration missing."));

        if (caller.getRole() != currentStepRule.getRole()) {
            throw new UnauthorizedActionException("Your role is not authorized to reject this step.");
        }

        // Update status to REJECTED (Terminates the workflow)
        request.setStatus(RequestStatus.REJECTED);
        Request updatedRequest = requestRepository.save(request);

        // Save History using standard object instantiation
        ApprovalHistory history = new ApprovalHistory();
        history.setRequest(updatedRequest);
        history.setAction(WorkflowAction.REJECTED);
        history.setActionBy(caller.getUsername());
        history.setActionAt(LocalDateTime.now());
        history.setRemarks(dto.getRemarks());
        
        historyRepository.save(history);

        log.info("Request ID {} REJECTED by user {}", id, caller.getUsername());
        return WorkflowMapper.toResponseDto(updatedRequest);
    }

    @Override
    @Transactional
    public RequestResponseDto adminOverride(Long id, ActionRequestDto dto) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id));
        User caller = getCurrentUserEntity();

        // 1. Validation: Only ADMIN can override
        if (caller.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Admin role required to perform an override.");
        }
        
        // 2. Validation: Request must be PENDING
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidTransitionException("Cannot override a completed request.");
        }

        // 3. Get total steps configured for this type to push the currentStep to the final boundary
        List<ApprovalStep> totalSteps = approvalStepRepository.findByRequestTypeOrderByStepOrderAsc(request.getType());
        
        // 4. Forcefully set status to APPROVED and move current step to maximum count
        request.setStatus(RequestStatus.APPROVED);
        if (!totalSteps.isEmpty()) {
            request.setCurrentStep(totalSteps.size()); // It will directly jump to Step 3 (or the max step)
        }
        
        Request updatedRequest = requestRepository.save(request);

        // 5. Save History with [Admin Override] tag
        ApprovalHistory history = new ApprovalHistory();
        history.setRequest(updatedRequest);
        history.setAction(WorkflowAction.OVERRIDDEN);
        history.setActionBy(caller.getUsername());
        history.setActionAt(LocalDateTime.now());
        history.setRemarks("[Admin Override] " + dto.getRemarks());
        
        historyRepository.save(history);

        log.info("Request ID {} forcefully OVERRIDDEN by Admin {}", id, caller.getUsername());
        return WorkflowMapper.toResponseDto(updatedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalHistoryDto> getRequestHistory(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new ResourceNotFoundException("No request history found for ID: " + id);
        }
        
        return historyRepository.findByRequestIdOrderByActionAtAsc(id).stream()
                .map(WorkflowMapper::toHistoryDto)
                .collect(Collectors.toList());
    }
}