package com.ai.dailyReport.common;

import com.ai.dailyReport.common.exception.ConflictException;
import com.ai.dailyReport.common.exception.ForbiddenException;
import com.ai.dailyReport.common.exception.ResourceNotFoundException;
import com.ai.dailyReport.common.exception.UnauthorizedException;
import com.ai.dailyReport.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, String path) {
		return ResponseEntity.status(status).body(
			ErrorResponse.builder()
				.status(status.value())
				.error(error)
				.message(message)
				.path(path)
				.build()
		);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e, HttpServletRequest req) {
		return build(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), req.getRequestURI());
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ErrorResponse> handleConflict(ConflictException e, HttpServletRequest req) {
	 return build(HttpStatus.CONFLICT, "CONFLICT", e.getMessage(), req.getRequestURI());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e, HttpServletRequest req) {
		return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage(), req.getRequestURI());
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e, HttpServletRequest req) {
		return build(HttpStatus.FORBIDDEN, "FORBIDDEN", e.getMessage(), req.getRequestURI());
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class })
	public ResponseEntity<ErrorResponse> handleBadRequest(Exception e, HttpServletRequest req) {
		String message = e.getMessage();
		if (e instanceof MethodArgumentNotValidException manv && manv.getBindingResult().hasFieldErrors()) {
			message = manv.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		}
		return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message, req.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception e, HttpServletRequest req) {
    e.printStackTrace();
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Unexpected error", req.getRequestURI());
	}
}
