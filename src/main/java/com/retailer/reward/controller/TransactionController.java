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

import com.retailer.reward.dto.CustomerTransactionsDto;
import com.retailer.reward.dto.TransationRequestDto;
import com.retailer.reward.serviceImpl.TransactionServiceImpl;
import com.retailer.reward.utils.MessageUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/retail/rewards")
@RequiredArgsConstructor
public class TransactionController {

	@Autowired
	private TransactionServiceImpl transactionService;

	private final MessageUtil messageUtil;

	@PostMapping
	public ResponseEntity<String> createReward(@Valid @RequestBody TransationRequestDto transationRequest) {
		transactionService.saveReward(transationRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(messageUtil.getMessage("purchase.transaction.success"));
	}

	@GetMapping
	public ResponseEntity<CustomerTransactionsDto> getRewardByCustomerIdAndDate(@RequestParam String customerId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		// Calling service to fetch transactions
		CustomerTransactionsDto customerRewards = transactionService.getRewardsByCustomerIdAndDateRange(customerId, fromDate,
				toDate);
		return ResponseEntity.ok(customerRewards);
	}

	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerTransactionsDto> getRewardByCustomerId(@PathVariable String customerId) {
		// Calling service to fetch transactions by customerId
		CustomerTransactionsDto customerRewards = transactionService.getRewardsByCustomerId(customerId);
		return ResponseEntity.ok(customerRewards);
	}
}