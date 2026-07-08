package com.workflow.engine.dto;


import java.time.LocalDateTime;


public class ApprovalHistoryDto {
    private Long id;
    private Long requestId;
    private String action;
    private String actionBy;
    private LocalDateTime actionAt;
    private String remarks;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRequestId() {
		return requestId;
	}
	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getActionBy() {
		return actionBy;
	}
	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}
	public LocalDateTime getActionAt() {
		return actionAt;
	}
	public void setActionAt(LocalDateTime actionAt) {
		this.actionAt = actionAt;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
    
    
    
    
}