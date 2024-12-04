package com.retailer.reward.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.retailer.reward.entity.Reward;
import com.retailer.reward.serviceImpl.RewardServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

	private final RewardServiceImpl rewardServiceImpl;

	@Override
	public void run(String... args) {
		if (rewardServiceImpl.isDatabaseEmpty()) {
			List<Reward> initialRewards = List.of(createReward("100", "John", 120.50, LocalDate.now().minusDays(1)),
					createReward("101", "Neel", 200.00, LocalDate.now()),
					createReward("102", "Kumar", 300.00, LocalDate.now().minusDays(2)),
					createReward("100", "John", 100.00, LocalDate.now().minusDays(7)),
					createReward("103", "Nivi", 250.00, LocalDate.now().minusDays(10)));

			initialRewards.forEach(rewardServiceImpl::saveReward);
			log.info("Sample data loaded successfully.");
		} else {
			log.info("Database already initialized. No data loaded.");
		}
	}

	private Reward createReward(String customerId, String customerName, double amount, LocalDate transactionDate) {
		Reward reward = new Reward();
		reward.setCustomerId(customerId);
		reward.setCustomerName(customerName);
		reward.setAmount(amount);
		reward.setTransactionDate(transactionDate);
		return reward;
	}

}
