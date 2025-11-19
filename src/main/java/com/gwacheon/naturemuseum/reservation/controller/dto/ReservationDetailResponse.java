package com.gwacheon.naturemuseum.reservation.controller.dto;

import com.gwacheon.naturemuseum.reservation.entity.BoothType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReservationDetailResponse {

	private String date;
	private BoothType boothType;
	private Integer roundNo;
	private List<SlotInfo> slots;

	@Getter
	@Builder
	public static class SlotInfo {
		private Integer slotNo;
		private Long reservationId;
		private String name;
		private String phone;
	}
}
