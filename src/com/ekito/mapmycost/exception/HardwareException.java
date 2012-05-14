package com.ekito.mapmycost.exception;

public class HardwareException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 8141102250777678805L;

	public HardwareException(Exception e) {
		super(e);
	}

	public HardwareException(String message) {
		super(message);
	}

	public HardwareException(String message, Exception e) {
		super(message, e);
	}

}
