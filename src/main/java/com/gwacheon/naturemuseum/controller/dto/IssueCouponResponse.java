package com.gwacheon.naturemuseum.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class IssueCouponResponse {
	private String date;      // yyyy-MM-dd
	private int issued;       // 방금 발급된 번호
	private int remaining;    // 남은 수량
	private boolean soldOut;  // 소진 여부
}
