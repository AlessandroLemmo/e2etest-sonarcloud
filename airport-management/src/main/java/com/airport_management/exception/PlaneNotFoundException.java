package com.airport_management.exception;


public class PlaneNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public PlaneNotFoundException(String message) {
		super(message);
	}
	
}
