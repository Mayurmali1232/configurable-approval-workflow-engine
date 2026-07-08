package com.workflow.engine.dto;

import jakarta.validation.constraints.NotBlank;


public class ActionRequestDto {
    @NotBlank(message = "Action justification feedback details required")
    private String remarks;
    
    public ActionRequestDto()
    {
    	
	}

	public ActionRequestDto(@NotBlank(message = "Action justification feedback details required") String remarks) 
	{
		super();
		this.remarks = remarks;
	}

	public String getRemarks() 
	{
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
    
    
}