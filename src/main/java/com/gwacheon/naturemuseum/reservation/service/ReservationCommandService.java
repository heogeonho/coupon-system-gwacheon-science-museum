package com.gwacheon.naturemuseum.reservation.service;

import com.gwacheon.naturemuseum.reservation.controller.dto.CreateReservationRequest;
import com.gwacheon.naturemuseum.reservation.entity.ReservationEntity;
import com.gwacheon.naturemuseum.reservation.entity.SessionEntity;
import com.gwacheon.naturemuseum.reservation.exception.DuplicateReservationException;
import com.gwacheon.naturemuseum.reservation.exception.ReservationNotFoundException;
import com.gwacheon.naturemuseum.reservation.repository.ReservationRepository;
import com.gwacheon.naturemuseum.reservation.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 예약 생성/수정/삭제 전용 서비스
 * CQRS 패턴 적용: Command 책임만 담당
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {

	private final SessionRepository sessionRepository;
	private final ReservationRepository reservationRepository;

	/**
	 * 예약 생성
	 */
	public Long createReservation(CreateReservationRequest request) {
		SessionEntity session = findOrCreateSession(request);
		validateSlotAvailability(session, request.getSlotNo());
		ReservationEntity reservation = createReservationEntity(session, request);
		ReservationEntity saved = reservationRepository.save(reservation);
		return saved.getId();
	}

	/**
	 * 예약 삭제
	 */
	public void deleteReservation(Long reservationId) {
		ReservationEntity reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new ReservationNotFoundException(reservationId));
		reservationRepository.delete(reservation);
	}

	private SessionEntity findOrCreateSession(CreateReservationRequest request) {
		LocalDate date = LocalDate.parse(request.getDate());

		return sessionRepository
			.findByBoothTypeAndSessionDateAndRoundNo(
				request.getBoothType(),
				date,
				request.getRound()
			)
			.orElseGet(() -> createNewSession(request, date));
	}

	private SessionEntity createNewSession(CreateReservationRequest request, LocalDate date) {
		SessionEntity newSession = SessionEntity.of(
			request.getBoothType(),
			date,
			request.getRound()
		);
		return sessionRepository.save(newSession);
	}

	private ReservationEntity createReservationEntity(SessionEntity session, CreateReservationRequest request) {
		return ReservationEntity.of(
			session,
			request.getSlotNo(),
			request.getName(),
			request.getPhone()
		);
	}

	private void validateSlotAvailability(SessionEntity session, Integer slotNo) {
		boolean exists = reservationRepository.existsBySessionAndSlotNo(session, slotNo);
		if (exists) {
			throw new DuplicateReservationException(
				session.getBoothType(),
				session.getSessionDate(),
				session.getRoundNo(),
				slotNo
			);
		}
	}
}
