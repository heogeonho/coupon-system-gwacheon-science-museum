package com.gwacheon.naturemuseum.controller;

import com.gwacheon.naturemuseum.controller.dto.IssueCouponResponse;
import com.gwacheon.naturemuseum.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

	private final CouponService couponService;

	@PostMapping("/issue")
	public ResponseEntity<IssueCouponResponse> issue() {
		return ResponseEntity.ok(couponService.issueToday());
	}
}
