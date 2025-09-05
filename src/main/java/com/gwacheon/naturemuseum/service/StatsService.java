package com.gwacheon.naturemuseum.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gwacheon.naturemuseum.controller.dto.DailyStatsResponse;
import com.gwacheon.naturemuseum.controller.dto.DailyStatsResponse.HourBucket;
import com.gwacheon.naturemuseum.entity.DailyStatsEntity;
import com.gwacheon.naturemuseum.entity.DailyVisitHourlyEntity;
import com.gwacheon.naturemuseum.repository.DailyStatsRepository;
import com.gwacheon.naturemuseum.repository.DailyVisitHourlyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {

	private static final int DAILY_CAPACITY = 200;
	private static final int HOUR_START = 9;
	private static final int HOUR_END = 18;

	private final DailyStatsRepository dailyStatsRepository;
	private final DailyVisitHourlyRepository dailyVisitHourlyRepository;

	@Transactional(readOnly = true)
	public DailyStatsResponse getDaily(String dateStr) {
		LocalDate date = LocalDate.parse(dateStr);

		// 일자 집계(없으면 0 기본 생성 이미지로 응답만)
		DailyStatsEntity stats = dailyStatsRepository.findById(date)
			.orElseGet(() -> DailyStatsEntity.initOf(date));

		// 시간대 집계 조회 → Map(hour -> visits)
		List<DailyVisitHourlyEntity> hourlyEntities = dailyVisitHourlyRepository.findAllByVisitDate(date);
		Map<Integer, Integer> hourToVisits = new HashMap<>();
		for (DailyVisitHourlyEntity e : hourlyEntities) {
			hourToVisits.put(e.getHour(), e.getVisits() == null ? 0 : e.getVisits());
		}

		// 9~18 버킷(누락 시간대는 0) + 합계
		List<HourBucket> hourly = new ArrayList<>(HOUR_END - HOUR_START + 1);
		int visitsSum = 0;
		for (int h = HOUR_START; h <= HOUR_END; h++) {
			int v = hourToVisits.getOrDefault(h, 0);
			hourly.add(HourBucket.builder().hour(h).visits(v).build());
			visitsSum += v;
		}

		// 쿠폰 정보
		int issued = stats.getIssued() == null ? 0 : stats.getIssued();
		int remaining = Math.max(0, DAILY_CAPACITY - issued);
		boolean soldOut = (remaining == 0);

		// visits는 "시간대 합계"로 일관되게 반환
		return DailyStatsResponse.builder()
			.date(date.toString())
			.visits(visitsSum)
			.hourly(hourly)
			.coupons(DailyStatsResponse.Coupons.builder()
				.issueCount(issued)
				.remaining(remaining)
				.soldOut(soldOut)
				.build())
			.build();
	}
}
