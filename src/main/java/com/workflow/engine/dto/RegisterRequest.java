package com.workflow.engine.dto;

import jakarta.validation.constraints.NotBlank;



public class RegisterRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;
    
    @NotBlank(message = "Password cannot be empty")
    private String password;
    
    @NotBlank(message = "Role mapping specification required")
    private String role;
    
    public RegisterRequest() {
		// TODO Auto-generated constructor stub
	}

	public RegisterRequest(@NotBlank(message = "Username cannot be empty") String username,
			@NotBlank(message = "Password cannot be empty") String password,
			@NotBlank(message = "Role mapping specification required") String role) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
    
    
}