package com.airport_management.exception;


public class FlightNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public FlightNotFoundException(String message) {
		super(message);
	}
}
