package com.retailer.reward.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.retailer.reward.entity.Reward;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.repository.RewardRepository;
import com.retailer.reward.utils.MessageUtil;
import com.retailer.reward.utils.RewardCalculator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

	private final MessageUtil messageUtil;

	@Autowired
	private RewardRepository rewardRepository;

	public Map<String, Map<String, Integer>> calculateRewards(List<Reward> rewards) {
		return RewardCalculator.calculateRewards(rewards);
	}

	public void saveReward(Reward reward) {
		validateReward(reward); // Performing validation
		reward.setPoint(RewardCalculator.calculateRewardPoint(reward));
		rewardRepository.save(reward);
	}

	public Map<String, Map<String, Integer>> getRewardsByCustomerIdAndDateRange(String customerId, LocalDate from,
			LocalDate to) {
		validateCustomerId(customerId); // Validating customerId
		validateDateRange(from, to); // Validating date range
		List<Reward> rewards;

		if (from != null && to != null) {
			rewards = rewardRepository.findByCustomerIdAndTransactionDateBetween(customerId, from, to);
		} else if (from == null && to == null) {
			rewards = rewardRepository.findByCustomerIdAndTransactionDate(customerId, LocalDate.now());
		} else {
			log.error("Invalid date range parameters provided");
			throw new InvalidArgumentException(messageUtil.getMessage("invalid.date.range"), HttpStatus.BAD_REQUEST);
		}
		if (rewards.isEmpty()) {
			log.info("No Rewards found {}");
			return Map.of();
		}
		return calculateRewards(rewards);
	}

	private void validateReward(Reward reward) {
		validateCustomerId(reward.getCustomerId());
		validateAmount(reward.getAmount());
		validateTransactionDate(reward.getTransactionDate());
	}

	private void validateCustomerId(String customerId) {
		if (customerId == null || customerId.trim().isEmpty()) {
			log.error("Invalid customer ID : null");
			throw new InvalidArgumentException(messageUtil.getMessage("validation.customerId.Invalid"),
					HttpStatus.BAD_REQUEST);
		}
	}

	private void validateAmount(Double amount) {
		if (amount == null || amount <= 0) {
			log.error("Invalid transaction amount: {}", amount);
			throw new InvalidArgumentException(messageUtil.getMessage("validation.amount.Invalid"),
					HttpStatus.BAD_REQUEST);
		}
	}

	private void validateTransactionDate(LocalDate transactionDate) {
		if (transactionDate == null) {
			log.error("Transaction date cannot be null");
			throw new InvalidArgumentException(messageUtil.getMessage("validation.transactionDate.null"),
					HttpStatus.BAD_REQUEST);
		}
		if (transactionDate.isAfter(LocalDate.now())) {
			log.error("Transaction date cannot be in the future: {}", transactionDate);
			throw new InvalidArgumentException(messageUtil.getMessage("validation.date.future"),
					HttpStatus.BAD_REQUEST);
		}
	}

	private void validateDateRange(LocalDate from, LocalDate to) {
		LocalDate today = LocalDate.now();
		if ((from != null && from.isAfter(today)) || (to != null && to.isAfter(today))) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.date.future"),
					HttpStatus.BAD_REQUEST);
		}
		if (from != null && to != null && from.isAfter(to)) {
			throw new InvalidArgumentException(messageUtil.getMessage("invalid.date.range"), HttpStatus.BAD_REQUEST);
		}
	}
}
