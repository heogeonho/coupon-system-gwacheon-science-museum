package com.gwacheon.naturemuseum.reservation.controller;

import com.gwacheon.naturemuseum.global.response.ApiResponse;
import com.gwacheon.naturemuseum.reservation.controller.dto.CreateReservationRequest;
import com.gwacheon.naturemuseum.reservation.controller.dto.ReservationDetailResponse;
import com.gwacheon.naturemuseum.reservation.controller.dto.ReservationSummaryResponse;
import com.gwacheon.naturemuseum.reservation.entity.BoothType;
import com.gwacheon.naturemuseum.reservation.service.ReservationCommandService;
import com.gwacheon.naturemuseum.reservation.service.ReservationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReservationController {

	private final ReservationQueryService queryService;
	private final ReservationCommandService commandService;

	/**
	 * 전체 예약 현황 조회
	 */
	@GetMapping("/reservations/summary")
	public ResponseEntity<ApiResponse<ReservationSummaryResponse>> getSummary() {
		return ResponseEntity.ok(ApiResponse.ok(queryService.getSummary()));
	}

	/**
	 * 특정 부스/회차 상세 예약자 목록 조회
	 */
	@GetMapping("/admin/reservations/detail")
	public ResponseEntity<ApiResponse<ReservationDetailResponse>> getDetail(
		@RequestParam String date,
		@RequestParam BoothType booth,
		@RequestParam Integer round
	) {
		return ResponseEntity.ok(ApiResponse.ok(
			queryService.getDetail(date, booth, round)
		));
	}

	/**
	 * 예약 생성
	 */
	@PostMapping("/admin/reservations")
	public ResponseEntity<ApiResponse<Long>> createReservation(
		@RequestBody CreateReservationRequest request
	) {
		Long reservationId = commandService.createReservation(request);
		return ResponseEntity.ok(ApiResponse.ok(reservationId));
	}

	/**
	 * 예약 삭제
	 */
	@DeleteMapping("/admin/reservations/{reservationId}")
	public ResponseEntity<ApiResponse<Void>> deleteReservation(
		@PathVariable Long reservationId
	) {
		commandService.deleteReservation(reservationId);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}
}
