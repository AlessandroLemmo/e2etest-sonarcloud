package com.airport_management.exception;


public class PlaneWithAssociateFlightException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PlaneWithAssociateFlightException(String message) {
		super(message);
	}
}
