package com.gwacheon.naturemuseum.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gwacheon.naturemuseum.controller.dto.DailyStatsResponse;
import com.gwacheon.naturemuseum.controller.dto.DailyStatsResponse.HourBucket;
import com.gwacheon.naturemuseum.entity.DailyStatsEntity;
import com.gwacheon.naturemuseum.entity.DailyVisitHourlyEntity;
import com.gwacheon.naturemuseum.repository.DailyStatsRepository;
import com.gwacheon.naturemuseum.repository.DailyVisitHourlyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

	private static final int DAILY_CAPACITY = 150;
	private static final int HOUR_START = 9;
	private static final int HOUR_END = 18;
	private static final List<String> HALLS = List.of("A", "B", "C", "D");

	private final DailyStatsRepository dailyStatsRepository;
	private final DailyVisitHourlyRepository dailyVisitHourlyRepository;

	@Transactional(readOnly = true)
	public DailyStatsResponse getDaily(String dateStr) {
		LocalDate date = LocalDate.parse(dateStr);

		// 쿠폰 통계 조회
		DailyStatsEntity stats = dailyStatsRepository.findById(date)
			.orElseGet(() -> DailyStatsEntity.initOf(date));

		// 시간대별 방문 데이터 조회
		List<DailyVisitHourlyEntity> hourlyEntities = dailyVisitHourlyRepository.findAllByVisitDate(date);

		// 전시관별 + 시간대별로 그룹핑
		Map<String, Map<Integer, Integer>> hallHourVisitsMap = hourlyEntities.stream()
			.collect(Collectors.groupingBy(
				DailyVisitHourlyEntity::getHall,
				Collectors.toMap(
					DailyVisitHourlyEntity::getHour,
					entity -> entity.getVisits() != null ? entity.getVisits() : 0,
					Integer::sum  // 동일한 hall-hour 조합이 있을 경우 합산
				)
			));

		// 1. visits 맵 생성 (전체 + 관별 합계)
		Map<String, Integer> visitsMap = new HashMap<>();
		int totalVisits = 0;

		for (String hall : HALLS) {
			Map<Integer, Integer> hourVisits = hallHourVisitsMap.getOrDefault(hall, Collections.emptyMap());
			int hallTotal = hourVisits.values().stream().mapToInt(Integer::intValue).sum();
			visitsMap.put(hall, hallTotal);
			totalVisits += hallTotal;
		}
		visitsMap.put("ALL", totalVisits);

		// 2. hourly 맵 생성 (전체 + 관별 시간대 분포)
		Map<String, List<HourBucket>> hourlyMap = new HashMap<>();

		// ALL (전체) 시간대 통계
		List<HourBucket> allHourly = new ArrayList<>();
		for (int hour = HOUR_START; hour <= HOUR_END; hour++) {
			int totalHourVisits = 0;
			for (String hall : HALLS) {
				totalHourVisits += hallHourVisitsMap
					.getOrDefault(hall, Collections.emptyMap())
					.getOrDefault(hour, 0);
			}
			allHourly.add(HourBucket.builder().hour(hour).visits(totalHourVisits).build());
		}
		hourlyMap.put("ALL", allHourly);

		// 관별 시간대 통계
		for (String hall : HALLS) {
			List<HourBucket> hallHourly = new ArrayList<>();
			Map<Integer, Integer> hourVisits = hallHourVisitsMap.getOrDefault(hall, Collections.emptyMap());

			for (int hour = HOUR_START; hour <= HOUR_END; hour++) {
				int visits = hourVisits.getOrDefault(hour, 0);
				hallHourly.add(HourBucket.builder().hour(hour).visits(visits).build());
			}
			hourlyMap.put(hall, hallHourly);
		}

		// 3. 쿠폰 정보
		int issued = stats.getIssued() != null ? stats.getIssued() : 0;
		int remaining = Math.max(0, DAILY_CAPACITY - issued);
		boolean soldOut = (remaining == 0);

		log.debug("일별 통계 조회 완료 - 날짜: {}, 전체 방문: {}, 쿠폰 발급: {}", date, totalVisits, issued);

		return DailyStatsResponse.builder()
			.date(date.toString())
			.visits(visitsMap)
			.hourly(hourlyMap)
			.coupons(DailyStatsResponse.Coupons.builder()
				.issueCount(issued)
				.remaining(remaining)
				.soldOut(soldOut)
				.build())
			.build();
	}
}