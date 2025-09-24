package com.gwacheon.naturemuseum.repository;

import com.gwacheon.naturemuseum.entity.DailyVisitHourlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyVisitHourlyRepository extends JpaRepository<DailyVisitHourlyEntity, Long> {

	/**
	 * 특정 날짜, 시간, 전시관의 방문 기록 조회
	 */
	Optional<DailyVisitHourlyEntity> findByVisitDateAndHourAndHall(LocalDate visitDate, Integer hour, String hall);

	/**
	 * 특정 날짜의 모든 방문 기록 조회 (전시관별 통계 생성용)
	 */
	List<DailyVisitHourlyEntity> findAllByVisitDate(LocalDate visitDate);

	/**
	 * 특정 날짜, 특정 전시관의 모든 시간대 방문 기록 조회
	 */
	List<DailyVisitHourlyEntity> findAllByVisitDateAndHallOrderByHour(LocalDate visitDate, String hall);
}