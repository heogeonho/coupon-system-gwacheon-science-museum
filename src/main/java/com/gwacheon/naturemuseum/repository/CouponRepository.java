package com.gwacheon.naturemuseum.repository;

import com.gwacheon.naturemuseum.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
}
