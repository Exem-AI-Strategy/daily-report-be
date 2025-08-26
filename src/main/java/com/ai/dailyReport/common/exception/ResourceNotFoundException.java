package com.ai.dailyReport.common.exception;

public class ResourceNotFoundException extends BusinessException {
	public ResourceNotFoundException(String message) {
		super(message, "NOT_FOUND");
	}
}