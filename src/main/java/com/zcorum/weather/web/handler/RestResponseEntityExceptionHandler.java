package com.zcorum.weather.web.handler;

import com.zcorum.weather.exception.WrongDateFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {WrongDateFormatException.class})
	protected ResponseEntity<WrongDateFormatException> handleEvvEnvironmentException(WrongDateFormatException ex) {
		return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
	}
}
