package com.workflow.engine;

import com.workflow.engine.dto.RequestResponseDto;
import com.workflow.engine.exception.*;
import com.workflow.engine.service.impl.WorkflowServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkflowControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkflowServiceImpl workflowService;

    @Test
    void testPublicLoginEndpoint_InvalidCredentials_ReturnsBadRequest() throws Exception {
        String invalidLoginPayload = "{\"username\":\"wronguser\",\"password\":\"wrongpass\"}";
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidLoginPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "mayur", roles = {"REQUESTER"})
    void testCreateRequestEndpoint_Success() throws Exception {
        String payload = "{\"type\":\"LEAVE\"}";
        RequestResponseDto mockResponse = new RequestResponseDto();
        mockResponse.setId(1L);
        mockResponse.setStatus("PENDING");
        
        Mockito.when(workflowService.createRequest(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/requests")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "ganesh", roles = {"APPROVER"})
    void testApproveRequestEndpoint_Success() throws Exception {
        String payload = "{\"remarks\":\"Approved\"}";
        RequestResponseDto mockResponse = new RequestResponseDto();
        mockResponse.setId(1L);
        mockResponse.setStatus("PENDING");
        mockResponse.setCurrentStep(2);

        Mockito.when(workflowService.approveRequest(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/requests/1/approve")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ganesh", roles = {"APPROVER"})
    void testRejectRequestEndpoint_Success() throws Exception {
        String payload = "{\"remarks\":\"Rejected\"}";
        RequestResponseDto mockResponse = new RequestResponseDto();
        mockResponse.setId(1L);
        mockResponse.setStatus("REJECTED");

        Mockito.when(workflowService.rejectRequest(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/requests/1/reject")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mayur", roles = {"REQUESTER"})
    void testGetRequestDetailsEndpoint_Success() throws Exception {
        RequestResponseDto mockResponse = new RequestResponseDto();
        mockResponse.setId(1L);
        mockResponse.setStatus("PENDING");

        Mockito.when(workflowService.getRequestDetails(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/requests/1")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "rushi", roles = {"ADMIN"})
    void testGetWorkflowHistoryEndpoint_Success() throws Exception {
        Mockito.when(workflowService.getRequestHistory(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/requests/history/1")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mayur", roles = {"REQUESTER"})
    void testApproveRequest_ResourceNotFoundException() throws Exception {
        Mockito.when(workflowService.approveRequest(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("Request not found"));

        mockMvc.perform(post("/api/v1/requests/999/approve")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"remarks\":\"Test\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "mayur", roles = {"REQUESTER"})
    void testApproveRequest_UnauthorizedActionException() throws Exception {
        Mockito.when(workflowService.approveRequest(eq(1L), any()))
                .thenThrow(new UnauthorizedActionException("Not authorized"));

        mockMvc.perform(post("/api/v1/requests/1/approve")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"remarks\":\"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "mayur", roles = {"REQUESTER"})
    void testApproveRequest_InvalidTransitionException() throws Exception {
        Mockito.when(workflowService.approveRequest(eq(1L), any()))
                .thenThrow(new InvalidTransitionException("Invalid transition"));

        mockMvc.perform(post("/api/v1/requests/1/approve")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"remarks\":\"Test\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "rushi", roles = {"ADMIN"})
    void testAdminOverrideEndpoint_Success() throws Exception {
        RequestResponseDto mockResponse = new RequestResponseDto();
        mockResponse.setId(1L);
        mockResponse.setStatus("APPROVED");

        Mockito.when(workflowService.adminOverride(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/requests/1/override")
                .header("Authorization", "Bearer mock_token_to_trigger_filter_instructions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"remarks\":\"Admin override check\"}"))
                .andExpect(status().isOk());
    }
}