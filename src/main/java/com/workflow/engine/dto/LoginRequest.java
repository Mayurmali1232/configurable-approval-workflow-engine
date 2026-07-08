package com.workflow.engine.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;
    
    @NotBlank(message = "Password cannot be empty")
    private String password;
    
    public LoginRequest() {
		// TODO Auto-generated constructor stub
	}

	public LoginRequest(@NotBlank(message = "Username cannot be empty") String username,
			@NotBlank(message = "Password cannot be empty") String password) {
		super();
		this.username = username;
		this.password = password;
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
    
    
}