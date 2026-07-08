package com.workflow.engine.dto;



public class AuthResponse {
    private String token;
    private String username;
    
    public AuthResponse() {
		// TODO Auto-generated constructor stub
	}

	public AuthResponse(String token, String username) {
		super();
		this.token = token;
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
    
    
}