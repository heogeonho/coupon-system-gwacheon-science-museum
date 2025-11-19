package com.gwacheon.naturemuseum.reservation.service;

import com.gwacheon.naturemuseum.reservation.controller.dto.ReservationDetailResponse;
import com.gwacheon.naturemuseum.reservation.controller.dto.ReservationSummaryResponse;
import com.gwacheon.naturemuseum.reservation.entity.BoothType;
import com.gwacheon.naturemuseum.reservation.entity.ReservationEntity;
import com.gwacheon.naturemuseum.reservation.entity.SessionEntity;
import com.gwacheon.naturemuseum.reservation.repository.ReservationRepository;
import com.gwacheon.naturemuseum.reservation.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 예약 조회 전용 서비스
 * CQRS 패턴 적용: Query 책임만 담당
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

	private final SessionRepository sessionRepository;
	private final ReservationRepository reservationRepository;

	private static final List<String> TARGET_DATES = Arrays.asList("2025-11-22", "2025-11-23");

	/**
	 * 전체 예약 현황 조회 (날짜별/부스별/회차별)
	 * 예약 데이터가 없어도 모든 부스와 회차를 count 0으로 표시
	 */
	public ReservationSummaryResponse getSummary() {
		List<ReservationEntity> allReservations = reservationRepository.findAll();

		Map<String, List<ReservationEntity>> reservationsByDate = groupByDate(allReservations);
		Map<String, List<ReservationSummaryResponse.BoothSummary>> summaryByDate =
			buildSummaryForAllDatesAndBooths(reservationsByDate);

		return ReservationSummaryResponse.builder()
			.dateMap(summaryByDate)
			.build();
	}

	/**
	 * 특정 부스/회차의 상세 예약자 목록 조회
	 */
	public ReservationDetailResponse getDetail(String dateStr, BoothType boothType, Integer roundNo) {
		LocalDate date = LocalDate.parse(dateStr);

		SessionEntity session = findSession(boothType, date, roundNo);

		if (session == null) {
			return buildEmptyDetail(dateStr, boothType, roundNo);
		}

		List<ReservationEntity> reservations = reservationRepository.findBySession(session);
		List<ReservationDetailResponse.SlotInfo> slots = buildSlotInfos(reservations);

		return ReservationDetailResponse.builder()
			.date(dateStr)
			.boothType(boothType)
			.roundNo(roundNo)
			.slots(slots)
			.build();
	}

	private Map<String, List<ReservationEntity>> groupByDate(List<ReservationEntity> reservations) {
		return reservations.stream()
			.collect(Collectors.groupingBy(
				r -> r.getSession().getSessionDate().toString()
			));
	}

	private Map<String, List<ReservationSummaryResponse.BoothSummary>> buildSummaryForAllDatesAndBooths(
		Map<String, List<ReservationEntity>> reservationsByDate
	) {
		Map<String, List<ReservationSummaryResponse.BoothSummary>> result = new LinkedHashMap<>();

		// 모든 대상 날짜에 대해 부스 요약 생성 (예약 없어도 count 0)
		for (String date : TARGET_DATES) {
			List<ReservationEntity> reservations = reservationsByDate.getOrDefault(date, Collections.emptyList());
			Map<BoothType, List<ReservationEntity>> reservationsByBooth = groupByBooth(reservations);
			List<ReservationSummaryResponse.BoothSummary> boothSummaries =
				buildAllBoothSummaries(reservationsByBooth);
			result.put(date, boothSummaries);
		}

		return result;
	}

	private Map<BoothType, List<ReservationEntity>> groupByBooth(List<ReservationEntity> reservations) {
		return reservations.stream()
			.collect(Collectors.groupingBy(r -> r.getSession().getBoothType()));
	}

	private List<ReservationSummaryResponse.BoothSummary> buildAllBoothSummaries(
		Map<BoothType, List<ReservationEntity>> reservationsByBooth
	) {
		// 모든 부스 타입을 순회하며 Summary 생성 (데이터 없으면 count 0)
		return Arrays.stream(BoothType.values())
			.map(boothType -> {
				List<ReservationEntity> reservations = reservationsByBooth.getOrDefault(
					boothType,
					Collections.emptyList()
				);
				return buildBoothSummary(boothType, reservations);
			})
			.collect(Collectors.toList());
	}

	private ReservationSummaryResponse.BoothSummary buildBoothSummary(
		BoothType boothType,
		List<ReservationEntity> reservations
	) {
		Map<Integer, Long> countByRound = countByRound(reservations);
		List<ReservationSummaryResponse.RoundCount> rounds = buildRoundCounts(countByRound);

		return ReservationSummaryResponse.BoothSummary.builder()
			.boothType(boothType)
			.boothName(boothType.getDisplayName())
			.rounds(rounds)
			.build();
	}

	private Map<Integer, Long> countByRound(List<ReservationEntity> reservations) {
		return reservations.stream()
			.collect(Collectors.groupingBy(
				r -> r.getSession().getRoundNo(),
				Collectors.counting()
			));
	}

	private List<ReservationSummaryResponse.RoundCount> buildRoundCounts(Map<Integer, Long> countByRound) {
		List<ReservationSummaryResponse.RoundCount> rounds = new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			rounds.add(ReservationSummaryResponse.RoundCount.builder()
				.roundNo(i)
				.count(countByRound.getOrDefault(i, 0L).intValue())
				.build());
		}
		return rounds;
	}

	private SessionEntity findSession(BoothType boothType, LocalDate date, Integer roundNo) {
		return sessionRepository
			.findByBoothTypeAndSessionDateAndRoundNo(boothType, date, roundNo)
			.orElse(null);
	}

	private ReservationDetailResponse buildEmptyDetail(String dateStr, BoothType boothType, Integer roundNo) {
		return ReservationDetailResponse.builder()
			.date(dateStr)
			.boothType(boothType)
			.roundNo(roundNo)
			.slots(Collections.emptyList())
			.build();
	}

	private List<ReservationDetailResponse.SlotInfo> buildSlotInfos(List<ReservationEntity> reservations) {
		return reservations.stream()
			.map(this::toSlotInfo)
			.sorted(Comparator.comparing(ReservationDetailResponse.SlotInfo::getSlotNo))
			.collect(Collectors.toList());
	}

	private ReservationDetailResponse.SlotInfo toSlotInfo(ReservationEntity reservation) {
		return ReservationDetailResponse.SlotInfo.builder()
			.slotNo(reservation.getSlotNo())
			.reservationId(reservation.getId())
			.name(reservation.getName())
			.phone(reservation.getPhoneNumber())
			.build();
	}
}
