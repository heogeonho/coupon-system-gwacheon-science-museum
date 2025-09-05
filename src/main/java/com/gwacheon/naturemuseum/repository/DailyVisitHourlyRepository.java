package com.gwacheon.naturemuseum.repository;

import com.gwacheon.naturemuseum.entity.DailyVisitHourlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyVisitHourlyRepository extends JpaRepository<DailyVisitHourlyEntity, Long> {
	List<DailyVisitHourlyEntity> findAllByVisitDate(LocalDate visitDate);
}
