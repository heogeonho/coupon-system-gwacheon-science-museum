package com.gwacheon.naturemuseum.service;

import com.gwacheon.naturemuseum.controller.dto.IssueCouponResponse;
import com.gwacheon.naturemuseum.entity.CouponEntity;
import com.gwacheon.naturemuseum.entity.DailyStatsEntity;
import com.gwacheon.naturemuseum.repository.CouponRepository;
import com.gwacheon.naturemuseum.repository.DailyStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

	private static final int DAILY_CAPACITY = 150;             // 하루 최대 발급 수량
	private static final ZoneId KST = ZoneId.of("Asia/Seoul"); // 한국 시간대 기준

	private final CouponRepository couponRepository;
	private final DailyStatsRepository dailyStatsRepository;

	/**
	 * 오늘 날짜 기준으로 쿠폰 1장을 발급한다.
	 * - 발급 가능하면 번호(1~150)를 새로 할당
	 * - 이미 매진 상태라면 발급 없이 soldOut=true로 응답
	 */
	@Transactional
	public IssueCouponResponse issueToday() {
		LocalDateTime now = LocalDateTime.now(KST);
		LocalDate today = now.toLocalDate();

		try {
			// 비관적 락을 사용하여 동시성 제어
			DailyStatsEntity stats = dailyStatsRepository.findByIdWithPessimisticLock(today)
				.orElseGet(() -> {
					// 없으면 새로 생성하고 저장
					DailyStatsEntity newStats = DailyStatsEntity.initOf(today);
					return dailyStatsRepository.save(newStats);
				});

			// 현재 발급 수량 확인
			int currentIssued = stats.getIssued();

			// 매진 체크 (빠른 실패)
			if (currentIssued >= DAILY_CAPACITY) {
				int remaining = 0;
				return IssueCouponResponse.builder()
					.date(today.toString())
					.issued(currentIssued)
					.remaining(remaining)
					.soldOut(true)
					.build();
			}

			// 발급 수량 증가 시도
			int newIssued = stats.increaseIssuedUpTo(DAILY_CAPACITY);

			// 실제로 증가했는지 확인 (동시성으로 인한 재확인)
			boolean actuallyIssued = newIssued > currentIssued;

			if (actuallyIssued) {
				try {
					// 쿠폰 엔티티 저장 (유니크 제약조건 위반 시 예외 발생 가능)
					CouponEntity coupon = CouponEntity.of(today, newIssued);
					couponRepository.save(coupon);

					log.info("쿠폰 발급 성공 - 날짜: {}, 번호: {}", today, newIssued);

				} catch (Exception e) {
					log.error("쿠폰 저장 실패 - 날짜: {}, 번호: {}", today, newIssued, e);
					// 쿠폰 저장 실패 시 통계 롤백은 트랜잭션에 의해 자동 처리됨
					throw new RuntimeException("쿠폰 발급 중 오류가 발생했습니다.", e);
				}
			}

			// 남은 수량 계산
			int remaining = Math.max(0, DAILY_CAPACITY - newIssued);
			boolean soldOut = (remaining == 0) && !actuallyIssued;

			return IssueCouponResponse.builder()
				.date(today.toString())
				.issued(newIssued)
				.remaining(remaining)
				.soldOut(soldOut)
				.build();

		} catch (Exception e) {
			log.error("쿠폰 발급 처리 중 오류 발생", e);
			throw new RuntimeException("쿠폰 발급 중 오류가 발생했습니다.", e);
		}
	}
}