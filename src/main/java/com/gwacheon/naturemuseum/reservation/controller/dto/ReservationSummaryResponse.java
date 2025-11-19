package com.gwacheon.naturemuseum.reservation.controller.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.gwacheon.naturemuseum.reservation.entity.BoothType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ReservationSummaryResponse {

	@JsonUnwrapped
	private Map<String, List<BoothSummary>> dateMap;

	@Getter
	@Builder
	public static class BoothSummary {
		private BoothType boothType;
		private String boothName;
		private List<RoundCount> rounds;
	}

	@Getter
	@Builder
	public static class RoundCount {
		private Integer roundNo;
		private Integer count;
	}
}
