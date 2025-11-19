package com.gwacheon.naturemuseum.reservation.exception;

public class ReservationNotFoundException extends RuntimeException {

	public ReservationNotFoundException(Long reservationId) {
		super(String.format("예약을 찾을 수 없습니다. ID: %d", reservationId));
	}
}
