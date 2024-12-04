package com.retailer.reward.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.retailer.reward.dto.CustomerRewardsDto;
import com.retailer.reward.dto.RewardDto;
import com.retailer.reward.entity.Reward;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.repository.RewardRepository;
import com.retailer.reward.service.RewardService;
import com.retailer.reward.utils.MessageUtil;
import com.retailer.reward.utils.RewardCalculatorUtil;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardServiceImpl implements RewardService{

	private final MessageUtil messageUtil;

	@Autowired
	private RewardRepository rewardRepository;

    @Override
	public void saveReward(Reward reward) {
		log.info("RewardService | saveReward | InputData {} ", reward.toString());
		validateReward(reward); // Performing validation
		reward.setPoint(RewardCalculatorUtil.calculateRewardPoint(reward));
		rewardRepository.save(reward);
	}

    @Override
	public CustomerRewardsDto getRewardsByCustomerIdAndDateRange(
			String customerId, LocalDate fromDate,
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
			log.info("No Rewards found for customer {} ", customerId);
			return new CustomerRewardsDto(customerId, "", 0, List.of());
		}
		return mapRewardsToDto(customerId, rewards);
	}

    @Override
	public CustomerRewardsDto getRewardsByCustomerId(String customerId) {
		validateCustomerId(customerId); // Validating customerId
		List<Reward> rewards;
		rewards = rewardRepository.findByCustomerId(customerId);
		if (rewards.isEmpty()) {
			log.info("No Rewards found for customer {} ", customerId);
			return new CustomerRewardsDto(customerId, "", 0, List.of());
		}
		return mapRewardsToDto(customerId, rewards);
	}

	public void validateReward(Reward reward) {
		validateCustomerId(reward.getCustomerId());
		validateAmount(reward.getAmount());
		validateTransactionDate(reward.getTransactionDate());
	}

	public void validateCustomerId(String customerId) {
		if (customerId == null || customerId.trim().isEmpty()) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.customerId.Invalid"),
					HttpStatus.BAD_REQUEST);
		}
	}

	public void validateAmount(Double amount) {
		if (amount == null || amount <= 0) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.amount.Invalid"),
					HttpStatus.BAD_REQUEST);
		}
	}

	public void validateTransactionDate(LocalDate transactionDate) {
		if (transactionDate == null) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.transactionDate.null"),
					HttpStatus.BAD_REQUEST);
		}
		if (transactionDate.isAfter(LocalDate.now())) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.date.future"),
					HttpStatus.BAD_REQUEST);
		}
	}

	public void validateDateRange(LocalDate fromDate, LocalDate toDate) {
		LocalDate today = LocalDate.now();
		if ((fromDate != null && fromDate.isAfter(today)) || (toDate != null && toDate.isAfter(today))) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.date.future"),
					HttpStatus.BAD_REQUEST);
		}
		if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
			throw new InvalidArgumentException(messageUtil.getMessage("invalid.date.range"), HttpStatus.BAD_REQUEST);
		}
	}

	public CustomerRewardsDto mapRewardsToDto(String customerId, List<Reward> rewards) {
		long totalPoints = rewards.stream().mapToLong(Reward::getPoint).sum();
		List<RewardDto> rewardsDto = rewards.stream().map(reward -> new RewardDto(reward.getId(), reward.getAmount(),
				reward.getTransactionDate(), reward.getPoint())).toList();

		String customerName = rewards.isEmpty() ? "" : rewards.get(0).getCustomerName();
		return new CustomerRewardsDto(customerId, customerName, totalPoints, rewardsDto);
	}

	public boolean isDatabaseEmpty() {
		return rewardRepository.count() == 0;
	}

}
