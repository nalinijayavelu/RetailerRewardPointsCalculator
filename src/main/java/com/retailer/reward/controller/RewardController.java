package com.retailer.reward.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.retailer.reward.dto.CustomerRewardsDto;
import com.retailer.reward.entity.Reward;
import com.retailer.reward.serviceImpl.RewardServiceImpl;
import com.retailer.reward.utils.MessageUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/retail/rewards")
@RequiredArgsConstructor
public class RewardController {

	@Autowired
	private RewardServiceImpl rewardService;

	private final MessageUtil messageUtil;

	@PostMapping
	public ResponseEntity<String> createReward(@Valid @RequestBody Reward reward) {
		rewardService.saveReward(reward);
		return ResponseEntity.status(HttpStatus.CREATED).body(messageUtil.getMessage("purchase.transaction.success"));
	}

	@GetMapping
	public ResponseEntity<CustomerRewardsDto> getRewardByCustomerIdAndDate(@RequestParam String customerId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		// Calling service to fetch rewards
		CustomerRewardsDto rewardsDto = rewardService.getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate);
		return ResponseEntity.ok(rewardsDto);
	}

	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerRewardsDto> getRewardByCustomerId(@PathVariable String customerId) {
		// Calling service to fetch rewards by customerId
		CustomerRewardsDto rewardsDto = rewardService.getRewardsByCustomerId(customerId);
		return ResponseEntity.ok(rewardsDto);
	}
}