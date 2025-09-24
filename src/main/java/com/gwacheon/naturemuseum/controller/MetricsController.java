package com.gwacheon.naturemuseum.controller;

import com.gwacheon.naturemuseum.controller.dto.VisitRequest;
import com.gwacheon.naturemuseum.service.MetricsService;
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