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
@Table(
	name = "coupons",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_coupons_issue_date_number",
			columnNames = {"issue_date", "number"}
		)
	},
	indexes = {
		@Index(
			name = "idx_coupons_issue_date",
			columnList = "issue_date"
		)
	}
)
public class CouponEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id")
	private Long id;

	/** 발급 기준 날짜(하루 200장 정책 단위) */
	@Column(name = "issue_date", nullable = false)
	private LocalDate issueDate;

	/** 당일 발급 번호(1..200) */
	@Column(name = "number", nullable = false)
	private Integer number;

	/** 생성 시각 */
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	/**
	 * 정적 팩토리 메서드
	 * @param issueDate 발급 날짜
	 * @param number    당일 발급 번호(1~200)
	 */
	public static CouponEntity of(LocalDate issueDate, int number) {
		return CouponEntity.builder()
			.issueDate(issueDate)
			.number(number)
			.build();
	}
}
