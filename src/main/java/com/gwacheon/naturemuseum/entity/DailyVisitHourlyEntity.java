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
			name = "uk_visit_date_hour",
			columnNames = {"visit_date", "hour"}
		)
	},
	indexes = {
		@Index(name = "idx_visit_date", columnList = "visit_date")
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

	public static DailyVisitHourlyEntity initOf(LocalDate date, int hour) {
		return DailyVisitHourlyEntity.builder()
			.visitDate(date)
			.hour(hour)
			.visits(0)
			.build();
	}

	private static void validateHour(int hour) {
		if (hour < HOUR_START || hour > HOUR_END) {
			throw new IllegalArgumentException("hour must be between 9 and 18: " + hour);
		}
	}
}
