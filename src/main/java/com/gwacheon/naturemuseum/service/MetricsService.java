package com.gwacheon.naturemuseum.service;

import com.gwacheon.naturemuseum.entity.DailyVisitHourlyEntity;
import com.gwacheon.naturemuseum.repository.DailyVisitHourlyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class MetricsService {

	private static final int HOUR_START = 9;
	private static final int HOUR_END   = 18;

	private final DailyVisitHourlyRepository dailyVisitHourlyRepository;

	@Transactional
	public void recordVisit() {
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		int hour = now.getHour();
		if (hour < HOUR_START || hour > HOUR_END) return;

		LocalDate date = now.toLocalDate();

		dailyVisitHourlyRepository.findByVisitDateAndHour(date, hour)
			.map(e -> { e.increaseVisits(1); return e; })
			.orElseGet(() -> dailyVisitHourlyRepository.save(DailyVisitHourlyEntity.ofFirstVisit(date, hour)));
	}
}
