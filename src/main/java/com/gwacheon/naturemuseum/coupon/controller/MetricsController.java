package com.gwacheon.naturemuseum.coupon.controller;

import com.gwacheon.naturemuseum.coupon.controller.dto.VisitRequest;
import com.gwacheon.naturemuseum.coupon.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics")
public class MetricsController {

	private final MetricsService metricsService;

	@PostMapping("/visit")
	public ResponseEntity<Void> visit(@RequestBody VisitRequest request) {
		metricsService.recordVisit(request.getHall());
		return ResponseEntity.accepted().build();
	}
}