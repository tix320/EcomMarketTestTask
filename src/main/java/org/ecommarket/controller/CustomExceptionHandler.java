package org.ecommarket.controller;

import org.ecommarket.excpetion.IpRequestLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice()
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@ResponseBody
	@ExceptionHandler(IpRequestLimitExceededException.class)
	@ResponseStatus(HttpStatus.BAD_GATEWAY)
	String ipRequestLimitExceededExceptionHandler(IpRequestLimitExceededException ex) {
		return "Bad gateway";
	}

	@ResponseBody
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String runtimeExceptionHandler(RuntimeException ex) {
		ex.printStackTrace();
		return "Internal server error";
	}
}
