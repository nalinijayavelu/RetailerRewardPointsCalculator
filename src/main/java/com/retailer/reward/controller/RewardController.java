package com.retailer.reward.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.retailer.reward.entity.Reward;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.service.RewardService;
import com.retailer.reward.utils.MessageUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/retail/rewards")
@RequiredArgsConstructor
@Slf4j
public class RewardController {

	@Autowired
	private RewardService rewardService;

	private final MessageUtil messageUtil;

	@PostMapping
	public ResponseEntity<String> createReward(@Valid @RequestBody Reward reward) {
		rewardService.saveReward(reward);
		return ResponseEntity.status(HttpStatus.CREATED).body(messageUtil.getMessage("purchase.transaction.success"));
	}

	@GetMapping
	public ResponseEntity<Map<String, Map<String, Integer>>> getReward(@RequestParam String customerId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

		// Calling service to fetch rewards
		Map<String, Map<String, Integer>> purchases = rewardService.getRewardsByCustomerIdAndDateRange(customerId,
				fromDate, toDate);
		return ResponseEntity.ok(purchases);
	}
}