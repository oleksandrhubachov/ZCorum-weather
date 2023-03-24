package com.zcorum.weather.exception;


public class WrongDateFormatException extends RuntimeException {

	public WrongDateFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
