package com.retailer.reward.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.retailer.reward.entity.Reward;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.repository.RewardRepository;
import com.retailer.reward.utils.MessageUtil;
import com.retailer.reward.utils.RewardCalculatorUtil;

class RewardServiceTest {

	@Mock
	private MessageUtil messageUtil;

	@Mock
	private RewardRepository rewardRepository;

	@InjectMocks
	private RewardService rewardService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCalculateRewards() {
		Reward reward = new Reward();
		reward.setCustomerId("12345");
		reward.setTransactionDate(LocalDate.of(2024, 1, 15));
		reward.setAmount(120.0);
		List<Reward> rewards = List.of(reward);
		Map<String, Map<String, Integer>> expectedRewards = Map.of("12345", Map.of("January", 90, "Total", 90));
		Map<String, Map<String, Integer>> result = rewardService.calculateRewards(rewards);
		assertThat(result).isEqualTo(expectedRewards);
	}

	@Test
	void testSaveReward() {
		Reward reward = new Reward();
		reward.setCustomerId("12345");
		reward.setTransactionDate(LocalDate.now());
		reward.setAmount(120.0);
		rewardService.saveReward(reward);
		verify(rewardRepository, times(1)).save(reward);
		assertThat(reward.getPoint()).isEqualTo(90);
	}

	@Test
	void testGetRewardsByCustomerIdAndDateRange() {
		String customerId = "12345";
		LocalDate fromDate = LocalDate.of(2024, 1, 1);
		LocalDate toDate = LocalDate.of(2024, 1, 31);
		Reward reward = new Reward();
		reward.setCustomerId(customerId);
		reward.setTransactionDate(LocalDate.of(2024, 1, 15));
		reward.setAmount(120.0);
		List<Reward> rewards = List.of(reward);
		when(rewardRepository.findByCustomerIdAndTransactionDateBetween(customerId, fromDate, toDate))
				.thenReturn(rewards);
		Map<String, Map<String, Integer>> result = rewardService.getRewardsByCustomerIdAndDateRange(customerId,
				fromDate, toDate);
		assertThat(result).isNotEmpty();
		assertThat(result.get(customerId).get("January")).isEqualTo(90);
		verify(rewardRepository, times(1)).findByCustomerIdAndTransactionDateBetween(customerId, fromDate, toDate);
	}

	@Test
	void testSaveReward_ThrowsInvalidArgumentException_WhenInvalidCustomerId() {
		Reward reward = new Reward();
		reward.setCustomerId(null);
		reward.setTransactionDate(LocalDate.now());
		reward.setAmount(120.0);
		when(messageUtil.getMessage("validation.customerId.Invalid")).thenReturn("Invalid Customer ID");
		InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
				() -> rewardService.saveReward(reward));
		assertThat(exception.getMessage()).isEqualTo("Invalid Customer ID");
	}

	@Test
	void testGetRewardsByCustomerIdAndDateRange_ThrowsInvalidArgumentException_WhenInvalidDateRange() {
		String customerId = "12345";
		LocalDate fromDate = LocalDate.of(2024, 2, 1);
		LocalDate toDate = LocalDate.of(2024, 1, 31);
		when(messageUtil.getMessage("invalid.date.range")).thenReturn("Invalid date range");
		InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
				() -> rewardService.getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate));
		assertThat(exception.getMessage()).isEqualTo("Invalid date range");
	}

	@Test
	void testGetRewardsByCustomerIdAndDateRange_ReturnsEmpty_WhenNoRewardsFound() {
		String customerId = "12345";
		LocalDate fromDate = LocalDate.of(2024, 1, 1);
		LocalDate toDate = LocalDate.of(2024, 1, 31);
		when(rewardRepository.findByCustomerIdAndTransactionDateBetween(customerId, fromDate, toDate))
				.thenReturn(Collections.emptyList());
		Map<String, Map<String, Integer>> result = rewardService.getRewardsByCustomerIdAndDateRange(customerId,
				fromDate, toDate);
		assertThat(result).isEmpty();
	}

	@Test
	void testGetRewardsByCustomerId_Success() {
		String customerId = "12345";
		Reward reward = new Reward();
		reward.setCustomerId(customerId);
		reward.setTransactionDate(LocalDate.of(2024, 1, 15));
		reward.setAmount(120.0);
		List<Reward> rewards = List.of(reward);
		Map<String, Map<String, Integer>> expectedRewards = Map.of("12345", Map.of("January", 90, "Total", 90));
		when(rewardRepository.findByCustomerId(customerId)).thenReturn(rewards);
		Map<String, Map<String, Integer>> result = rewardService.getRewardsByCustomerId(customerId);
		assertThat(result).isEqualTo(expectedRewards);
		verify(rewardRepository, times(1)).findByCustomerId(customerId);
	}

	@Test
	void testGetRewardsByCustomerId_NoRewardsFound() {
		String customerId = "12345";
		when(rewardRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

		Map<String, Map<String, Integer>> result = rewardService.getRewardsByCustomerId(customerId);
		assertThat(result).isEmpty();
		verify(rewardRepository, times(1)).findByCustomerId(customerId);
	}

	@Test
	void testGetRewardsByCustomerId_InvalidCustomerId() {
		String invalidCustomerId = null;
		assertThrows(InvalidArgumentException.class, () -> rewardService.getRewardsByCustomerId(invalidCustomerId),
				"Invalid customer ID should throw an exception");
	}

	@Test
	void testGetRewardsByCustomerId_ValidCustomerId_EmptyRewards() {
		String customerId = "12345";
		when(rewardRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());
		Map<String, Map<String, Integer>> result = rewardService.getRewardsByCustomerId(customerId);
		assertThat(result).isEmpty(); // Ensure the result is an empty map
		verify(rewardRepository, times(1)).findByCustomerId(customerId); // Verify the method was called once
	}

}
