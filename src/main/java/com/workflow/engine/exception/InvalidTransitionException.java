package com.workflow.engine.exception;

public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException(String message) 
    { 
    	super(message); 
    }
}