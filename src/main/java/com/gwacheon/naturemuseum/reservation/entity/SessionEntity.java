package com.gwacheon.naturemuseum.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 체험 부스 회차 정보
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(
	name = "sessions",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_sessions_booth_date_round",
			columnNames = {"booth_type", "session_date", "round_no"}
		)
	}
)
public class SessionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "session_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "booth_type", nullable = false, length = 50)
	private BoothType boothType;

	@Column(name = "session_date", nullable = false)
	private LocalDate sessionDate;

	@Column(name = "round_no", nullable = false)
	private Integer roundNo;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	public static SessionEntity of(BoothType boothType, LocalDate sessionDate, int roundNo) {
		return SessionEntity.builder()
			.boothType(boothType)
			.sessionDate(sessionDate)
			.roundNo(roundNo)
			.build();
	}
}