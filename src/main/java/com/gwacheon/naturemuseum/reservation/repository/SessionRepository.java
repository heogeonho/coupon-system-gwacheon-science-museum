package com.gwacheon.naturemuseum.reservation.repository;

import com.gwacheon.naturemuseum.reservation.entity.BoothType;
import com.gwacheon.naturemuseum.reservation.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

	Optional<SessionEntity> findByBoothTypeAndSessionDateAndRoundNo(
		BoothType boothType,
		LocalDate sessionDate,
		Integer roundNo
	);
}
