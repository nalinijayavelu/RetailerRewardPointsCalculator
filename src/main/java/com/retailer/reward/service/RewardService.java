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
import com.retailer.reward.utils.RewardCalculatorUtil;

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
		return RewardCalculatorUtil.calculateRewards(rewards);
	}

	public void saveReward(Reward reward) {
		log.info("RewardService | saveReward | InputData {} ", reward.toString());
		validateReward(reward); // Performing validation
		reward.setPoint(RewardCalculatorUtil.calculateRewardPoint(reward));
		rewardRepository.save(reward);
	}

	public Map<String, Map<String, Integer>> getRewardsByCustomerIdAndDateRange(String customerId, LocalDate fromDate,
			LocalDate toDate) {
		validateCustomerId(customerId); // Validating customerId
		validateDateRange(fromDate, toDate); // Validating date range
		List<Reward> rewards;

		if (fromDate != null && toDate != null) {
			rewards = rewardRepository.findByCustomerIdAndTransactionDateBetween(customerId, fromDate, toDate);
		} else if (fromDate == null && toDate == null) {
			rewards = rewardRepository.findByCustomerIdAndTransactionDate(customerId, LocalDate.now());
		} else {
			throw new InvalidArgumentException(messageUtil.getMessage("invalid.date.range"), HttpStatus.BAD_REQUEST);
		}
		if (rewards.isEmpty()) {
			log.info("No Rewards found {}");
			return Map.of();
		}
		return calculateRewards(rewards);
	}

	public Map<String, Map<String, Integer>> getRewardsByCustomerId(String customerId) {
		validateCustomerId(customerId); // Validating customerId
		List<Reward> rewards;
		rewards = rewardRepository.findByCustomerId(customerId);
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
			throw new InvalidArgumentException(messageUtil.getMessage("validation.customerId.Invalid"),
					HttpStatus.BAD_REQUEST);
		}
	}

	private void validateAmount(Double amount) {
		if (amount == null || amount <= 0) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.amount.Invalid"),
					HttpStatus.BAD_REQUEST);
		}
	}

	private void validateTransactionDate(LocalDate transactionDate) {
		if (transactionDate == null) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.transactionDate.null"),
					HttpStatus.BAD_REQUEST);
		}
		if (transactionDate.isAfter(LocalDate.now())) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.date.future"),
					HttpStatus.BAD_REQUEST);
		}
	}

	private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
		LocalDate today = LocalDate.now();
		if ((fromDate != null && fromDate.isAfter(today)) || (toDate != null && toDate.isAfter(today))) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.date.future"),
					HttpStatus.BAD_REQUEST);
		}
		if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
			throw new InvalidArgumentException(messageUtil.getMessage("invalid.date.range"), HttpStatus.BAD_REQUEST);
		}
	}
}
