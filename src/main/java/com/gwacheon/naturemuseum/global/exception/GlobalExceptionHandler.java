package com.gwacheon.naturemuseum.global.exception;

import com.gwacheon.naturemuseum.global.response.ApiResponse;
import com.gwacheon.naturemuseum.reservation.exception.DuplicateReservationException;
import com.gwacheon.naturemuseum.reservation.exception.ReservationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ReservationNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleReservationNotFound(ReservationNotFoundException e) {
		log.warn("[ReservationNotFoundException] {}", e.getMessage());
		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(DuplicateReservationException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateReservation(DuplicateReservationException e) {
		log.warn("[DuplicateReservationException] {}", e.getMessage());
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException e) {
		log.error("[RuntimeException] {}", e.getMessage(), e);
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("[Exception] {}", e.getMessage(), e);
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error("INTERNAL_SERVER_ERROR"));
	}
}
