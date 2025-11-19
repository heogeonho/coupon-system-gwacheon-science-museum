package com.gwacheon.naturemuseum.reservation.repository;

import com.gwacheon.naturemuseum.reservation.entity.ReservationEntity;
import com.gwacheon.naturemuseum.reservation.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

	List<ReservationEntity> findBySession(SessionEntity session);

	@Query("SELECT r FROM ReservationEntity r " +
		"JOIN FETCH r.session s " +
		"WHERE s.sessionDate = :date")
	List<ReservationEntity> findAllByDate(@Param("date") LocalDate date);

	boolean existsBySessionAndSlotNo(SessionEntity session, Integer slotNo);
}
