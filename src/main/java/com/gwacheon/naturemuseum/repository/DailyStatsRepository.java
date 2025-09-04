package com.gwacheon.naturemuseum.repository;

import com.gwacheon.naturemuseum.entity.DailyStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyStatsRepository extends JpaRepository<DailyStatsEntity, LocalDate> {
}
