package com.gwacheon.naturemuseum.repository;

import com.gwacheon.naturemuseum.entity.DailyVisitHourlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyVisitHourlyRepository extends JpaRepository<DailyVisitHourlyEntity, Long> {

	List<DailyVisitHourlyEntity> findAllByVisitDate(LocalDate visitDate);

	Optional<DailyVisitHourlyEntity> findByVisitDateAndHour(LocalDate visitDate, Integer hour);

}
