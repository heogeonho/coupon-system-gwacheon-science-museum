package com.gwacheon.naturemuseum.coupon.repository;

import com.gwacheon.naturemuseum.coupon.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
}
