package com.retailer.reward.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.retailer.reward.dto.TransationRequestDto;
import com.retailer.reward.serviceImpl.TransactionServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

	private final TransactionServiceImpl transactionServiceImpl;

	@Override
	public void run(String... args) {
		if (transactionServiceImpl.isDatabaseEmpty()) {
			List<TransationRequestDto> initialRewards = List.of(createReward("100", "John", 120.50),
					createReward("101", "Neel", 200.00),
					createReward("102", "Kumar", 300.00),
					createReward("100", "John", 100.00),
					createReward("100", "John", 250.00),
					createReward("103", "Nivi", 250.00));

			initialRewards.forEach(transactionServiceImpl::saveReward);
			log.info("Sample data loaded successfully.");
		} else {
			log.info("Database already initialized. No data loaded.");
		}
	}

	private TransationRequestDto createReward(String customerId, String customerName, double amount) {
		TransationRequestDto transationRequest = new TransationRequestDto();
		transationRequest.setCustomerId(customerId);
		transationRequest.setCustomerName(customerName);
		transationRequest.setAmount(amount);
		return transationRequest;
	}

}
