package com.gwacheon.naturemuseum.service;

import com.gwacheon.naturemuseum.controller.dto.IssueCouponResponse;
import com.gwacheon.naturemuseum.entity.CouponEntity;
import com.gwacheon.naturemuseum.entity.DailyStatsEntity;
import com.gwacheon.naturemuseum.repository.CouponRepository;
import com.gwacheon.naturemuseum.repository.DailyStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CouponService {

	private static final int DAILY_CAPACITY = 200;

	private final CouponRepository couponRepository;
	private final DailyStatsRepository dailyStatsRepository;

	@Transactional
	public IssueCouponResponse issueToday() {
		LocalDate today = LocalDate.now();

		// 오늘 row 없으면 생성(issued=0)
		DailyStatsEntity stats = dailyStatsRepository.findById(today)
			.orElseGet(() -> dailyStatsRepository.save(DailyStatsEntity.initOf(today)));

		int before = stats.getIssued() == null ? 0 : stats.getIssued();
		int after  = stats.increaseIssuedUpTo(DAILY_CAPACITY); // 예외 없이 200에서 클램프

		boolean newlyIssued = after > before; // 이번 호출로 실제 1장 발급되었는지
		if (newlyIssued) {
			// 1..200만 저장됨 (200을 초과하지 않음)
			couponRepository.save(CouponEntity.of(today, after));
		}

		// 규칙:
		// - 200번째를 방금 발급했다면 soldOut=false (remaining=0)
		// - 이미 200인 상태에서 또 호출이면 soldOut=true
		boolean soldOut = (after >= DAILY_CAPACITY) && !newlyIssued;

		int remaining = Math.max(0, DAILY_CAPACITY - after);

		return IssueCouponResponse.builder()
			.date(today.toString())
			.issued(after)        // 1..200 (이미 소진 상태면 200 유지)
			.remaining(remaining) // 0..199
			.soldOut(soldOut)
			.build();
	}
}
