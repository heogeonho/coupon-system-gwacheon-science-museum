package com.gwacheon.naturemuseum.coupon.controller;

import com.gwacheon.naturemuseum.coupon.controller.dto.IssueCouponResponse;
import com.gwacheon.naturemuseum.global.response.ApiResponse;
import com.gwacheon.naturemuseum.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

	private final CouponService couponService;

	@PostMapping("/issue")
	public ResponseEntity<ApiResponse<IssueCouponResponse>> issue() {
		return ResponseEntity.ok(ApiResponse.ok(couponService.issueToday()));
	}
}
