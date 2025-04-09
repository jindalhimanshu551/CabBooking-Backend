package com.genpact.CabBookingApp.cabBookingApp.exception;

public class UserAlreadyExistWithuserIdException extends RuntimeException {
	public UserAlreadyExistWithuserIdException() {
		
	}
	
	public UserAlreadyExistWithuserIdException(String message) {
		super(message);
	}

}
