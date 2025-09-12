package com.gwacheon.naturemuseum.repository;

import com.gwacheon.naturemuseum.entity.DailyStatsEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyStatsRepository extends JpaRepository<DailyStatsEntity, LocalDate> {

	/**
	 * 비관적 락을 사용하여 DailyStatsEntity 조회
	 * 동시성 제어를 위해 해당 행에 대한 배타적 잠금을 획득
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT d FROM DailyStatsEntity d WHERE d.date = :date")
	Optional<DailyStatsEntity> findByIdWithPessimisticLock(@Param("date") LocalDate date);
}