package com.workflow.engine.entity;

import com.workflow.engine.constants.Role;
import jakarta.persistence.*;


@Entity
@Table(name = "approval_steps")

public class ApprovalStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_type", nullable = false)
    private String requestType;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    public ApprovalStep() {
		// TODO Auto-generated constructor stub
	}

	public ApprovalStep(Long id, String requestType, Integer stepOrder, Role role) {
		super();
		this.id = id;
		this.requestType = requestType;
		this.stepOrder = stepOrder;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Integer getStepOrder() {
		return stepOrder;
	}

	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
    
    
}