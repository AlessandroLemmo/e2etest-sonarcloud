package com.airport_management.exception;


public class PlaneAlreadyInServiceException extends RuntimeException{

	private static final long serialVersionUID = 1L;

    public PlaneAlreadyInServiceException(String message) {
        super(message);
    }
}
