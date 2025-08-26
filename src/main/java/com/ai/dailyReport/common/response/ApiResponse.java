package com.ai.dailyReport.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {
	private final LocalDateTime timestamp = LocalDateTime.now();
	private final String status;   // "SUCCESS" | "FAIL"
	private final String message;  // 선택
	private final T data;

	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder().status("SUCCESS").data(data).build();
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return ApiResponse.<T>builder().status("SUCCESS").message(message).data(data).build();
	}

	public static <T> ApiResponse<T> fail(String message) {
		return ApiResponse.<T>builder().status("FAIL").message(message).build();
	}
}