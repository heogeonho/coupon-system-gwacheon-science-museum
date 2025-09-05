package com.gwacheon.naturemuseum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gwacheon.naturemuseum.controller.dto.DailyStatsResponse;
import com.gwacheon.naturemuseum.global.response.ApiResponse;
import com.gwacheon.naturemuseum.service.StatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/stats")
public class StatsController {

	public final StatsService statsService;

	@GetMapping("daily")
	public ResponseEntity<ApiResponse<DailyStatsResponse>> stats(@RequestParam(name = "date") String date) {
		return ResponseEntity.ok(ApiResponse.ok(statsService.getDaily(date)));
	}
}
