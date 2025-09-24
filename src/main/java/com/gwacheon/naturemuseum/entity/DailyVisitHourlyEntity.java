package com.gwacheon.naturemuseum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(
	name = "daily_visit_hourly",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_visit_date_hour_hall",
			columnNames = {"visit_date", "hour", "hall"}
		)
	},
	indexes = {
		@Index(name = "idx_visit_date", columnList = "visit_date"),
		@Index(name = "idx_visit_date_hall", columnList = "visit_date, hall")
	}
)
public class DailyVisitHourlyEntity {

	private static final int HOUR_START = 9;
	private static final int HOUR_END   = 18;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "visit_hourly_id")
	private Long id;

	/** 날짜 (일 단위) */
	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;

	/** 몇 시대(예: 9 → 09:00~09:59) */
	@Column(name = "hour", nullable = false)
	private Integer hour;

	/** 전시관 구분 (A, B, C, D 등) */
	@Column(name = "hall", length = 1, nullable = false)
	private String hall;

	/** 해당 시간대 방문자 수 */
	@Column(name = "visits", nullable = false)
	private Integer visits;

	/** 생성/갱신 시각 */
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		if (visits == null) visits = 0;
		if (updatedAt == null) updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	/* --- 도메인 메서드 --- */
	public void increaseVisits(int delta) {
		this.visits = (this.visits == null ? 0 : this.visits) + Math.max(delta, 0);
	}

	public static DailyVisitHourlyEntity ofFirstVisit(LocalDate date, int hour, String hall) {
		validateHour(hour);
		validateHall(hall);

		return DailyVisitHourlyEntity.builder()
			.visitDate(date)
			.hour(hour)
			.hall(hall)
			.visits(1)
			.build();
	}

	private static void validateHour(int hour) {
		if (hour < HOUR_START || hour > HOUR_END) {
			throw new IllegalArgumentException("hour must be between 9 and 18: " + hour);
		}
	}

	private static void validateHall(String hall) {
		if (hall == null || hall.trim().isEmpty() || hall.length() != 1) {
			throw new IllegalArgumentException("hall must be a single character: " + hall);
		}
	}
}