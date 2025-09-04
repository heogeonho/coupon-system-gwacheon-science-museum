package com.gwacheon.naturemuseum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Builder 전용
@Builder
@Entity
@Table(name = "daily_stats")
public class DailyStatsEntity {

	/** 날짜 PK (하루 단위 1행) */
	@Id
	@Column(name = "stats_date", nullable = false)
	private LocalDate date;

	/** 방문 수 (기본 0) */
	@Column(name = "visits", nullable = false)
	private Integer visits;

	/** 지도 찾기 수 (보류 시 null 허용) */
	@Column(name = "map_searches")
	private Integer mapSearches;

	/** 당일 누적 발급 수 (쿠폰 번호는 issued 증가 후 값) */
	@Column(name = "issued", nullable = false)
	private Integer issued;

	/** 갱신 시각 */
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	/* --- JPA 라이프사이클 --- */
	@PrePersist
	void onCreate() {
		if (visits == null) visits = 0;
		if (issued == null) issued = 0;
		if (updatedAt == null) updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	/* --- 도메인 메서드 --- */

	/**
	 * issued를 capacity까지만 1 증가(클램프). 초과 시 예외 없이 현 상태 유지.
	 * @return 증가 후(또는 유지된) issued 값
	 */
	public int increaseIssuedUpTo(int capacity) {
		if (this.issued == null) this.issued = 0;
		if (this.issued < capacity) {
			this.issued += 1;
		}
		return this.issued; // 0..capacity
	}

	/** 방문 수 증가 (음수 무시) */
	public void increaseVisits(int delta) {
		this.visits = (this.visits == null ? 0 : this.visits) + Math.max(delta, 0);
	}

	/** 지도 찾기 수 증가 (음수 무시) */
	public void increaseMapSearches(int delta) {
		if (this.mapSearches == null) this.mapSearches = 0;
		this.mapSearches += Math.max(delta, 0);
	}

	/* --- 정적 팩토리 --- */

	/** 해당 날짜의 기본값(방문=0, issued=0, mapSearches=null)으로 초기화 */
	public static DailyStatsEntity initOf(LocalDate date) {
		return DailyStatsEntity.builder()
			.date(date)
			.visits(0)
			.mapSearches(null)
			.issued(0)
			.build();
	}
}
