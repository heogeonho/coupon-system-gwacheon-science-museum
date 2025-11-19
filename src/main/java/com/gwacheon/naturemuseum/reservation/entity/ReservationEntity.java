package com.gwacheon.naturemuseum.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 체험 부스 예약 정보
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(
	name = "reservations",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_reservations_session_slot",
			columnNames = {"session_id", "slot_no"}
		)
	}
)
public class ReservationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private SessionEntity session;

	@Column(name = "slot_no", nullable = false)
	private Integer slotNo;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	public static ReservationEntity of(SessionEntity session, int slotNo, String name, String phoneNumber) {
		return ReservationEntity.builder()
			.session(session)
			.slotNo(slotNo)
			.name(name)
			.phoneNumber(phoneNumber)
			.build();
	}
}
