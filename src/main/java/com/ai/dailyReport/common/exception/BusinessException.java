package com.ai.dailyReport.common.exception;

public class BusinessException extends RuntimeException {
	private final String error;

	public BusinessException(String message, String error) {
		super(message);
		this.error = error;
	}

	public String getError() {
		return error;
	}
}