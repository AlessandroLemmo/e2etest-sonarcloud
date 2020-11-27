package com.airport_management.exception;


public class InconsistentDataException extends RuntimeException{

	private static final long serialVersionUID = 1L;

    public InconsistentDataException(String message) {
        super(message);
    }
}
