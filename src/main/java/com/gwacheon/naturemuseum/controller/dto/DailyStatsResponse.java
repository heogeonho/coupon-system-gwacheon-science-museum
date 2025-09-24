package com.gwacheon.naturemuseum.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class DailyStatsResponse {
	private String date;
	private Map<String, Integer> visits;
	private Map<String, List<HourBucket>> hourly;
	private Coupons coupons;

	@Getter
	@Builder
	@AllArgsConstructor
	public static class Coupons {
		private int issueCount;
		private int remaining;
		private boolean soldOut;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class HourBucket {
		private int hour;         // 9..18
		private int visits;       // 해당 시간대 방문 수
	}
}
