package com.workflow.engine.dto;

import jakarta.validation.constraints.NotBlank;

public class RequestCreationDto {
    @NotBlank(message = "Workflow transaction classification type is mandatory")
    private String type;
    
    public RequestCreationDto() 
    {
		
	}

	public RequestCreationDto(
			@NotBlank(message = "Workflow transaction classification type is mandatory") String type) {
		
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) 
	{
		this.type = type;
	}

	
	
}