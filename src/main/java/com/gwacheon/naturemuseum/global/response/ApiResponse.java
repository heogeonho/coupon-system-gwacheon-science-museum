package com.gwacheon.naturemuseum.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
	private final boolean success;
	private final T data;        // 성공 시 값
	private final String error;  // 실패 시 메시지

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(true, data, null);
	}

	public static <T> ApiResponse<T> error(String message) {
		return new ApiResponse<>(false, null, message);
	}
}
