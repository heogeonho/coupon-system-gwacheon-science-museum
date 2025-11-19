package com.gwacheon.naturemuseum.reservation.controller.dto;

import com.gwacheon.naturemuseum.reservation.entity.BoothType;
import lombok.Getter;

@Getter
public class CreateReservationRequest {

	private String date;
	private BoothType boothType;
	private Integer round;
	private Integer slotNo;
	private String name;
	private String phone;
}
