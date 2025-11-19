package com.gwacheon.naturemuseum.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 부스 타입 정의
 */
@Getter
@RequiredArgsConstructor
public enum BoothType {

	AIR_ROCKET("에어로켓 만들기"),
	NURIHO_3D("누리호 3D 입체모형 만들기"),
	EARTH_MOON_MODEL("지구와 달의 운동모형 만들기"),
	NURIHO_PAPER_ROCKET("누리호 종이 로켓 만들기"),
	NURIHO_TABLET_ROCKET("발포정 누리호 로켓 만들기");

	private final String displayName;
}