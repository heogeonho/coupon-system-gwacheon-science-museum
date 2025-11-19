package com.gwacheon.naturemuseum.reservation.exception;

import com.gwacheon.naturemuseum.reservation.entity.BoothType;

import java.time.LocalDate;

public class DuplicateReservationException extends RuntimeException {

	public DuplicateReservationException(BoothType boothType, LocalDate date, Integer round, Integer slotNo) {
		super(String.format("이미 예약된 슬롯입니다. 부스: %s, 날짜: %s, 회차: %d, 슬롯: %d",
			boothType.getDisplayName(), date, round, slotNo));
	}
}
