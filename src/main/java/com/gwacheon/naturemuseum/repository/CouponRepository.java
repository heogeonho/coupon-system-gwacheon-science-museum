package com.gwacheon.naturemuseum.repository;

import com.gwacheon.naturemuseum.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {

	/**
	 * 특정 날짜 기준 가장 마지막으로 발급된 쿠폰 조회
	 * → 필요 시 오늘 몇 번까지 발급되었는지 확인할 때 사용 가능
	 */
	Optional<CouponEntity> findTopByIssueDateOrderByNumberDesc(LocalDate issueDate);
}
