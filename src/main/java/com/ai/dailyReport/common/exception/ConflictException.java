package com.ai.dailyReport.common.exception;

public class ConflictException extends BusinessException {
	public ConflictException(String message) {
		super(message, "CONFLICT");
	}
}
