package com.gwacheon.naturemuseum.service;

import com.gwacheon.naturemuseum.entity.DailyVisitHourlyEntity;
import com.gwacheon.naturemuseum.repository.DailyVisitHourlyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

	private static final int HOUR_START = 9;
	private static final int HOUR_END   = 18;

	private final DailyVisitHourlyRepository dailyVisitHourlyRepository;

	@Transactional
	public void recordVisit(String hall) {
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		int hour = now.getHour();

		if (hour < HOUR_START || hour > HOUR_END) {
			log.warn("운영 시간 외 방문 기록 시도: {}시, hall: {}", hour, hall);
			return;
		}

		LocalDate date = now.toLocalDate();

		dailyVisitHourlyRepository.findByVisitDateAndHourAndHall(date, hour, hall)
			.map(entity -> {
				entity.increaseVisits(1);
				log.debug("방문 카운트 증가: 날짜={}, 시간={}시, 전시관={}, 증가후={}",
					date, hour, hall, entity.getVisits());
				return entity;
			})
			.orElseGet(() -> {
				DailyVisitHourlyEntity newEntity = dailyVisitHourlyRepository.save(
					DailyVisitHourlyEntity.ofFirstVisit(date, hour, hall)
				);
				log.debug("새 방문 기록 생성: 날짜={}, 시간={}시, 전시관={}", date, hour, hall);
				return newEntity;
			});
	}
}
