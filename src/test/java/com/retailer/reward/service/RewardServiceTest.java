package com.retailer.reward.service;

import com.retailer.reward.entity.Reward;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.repository.RewardRepository;
import com.retailer.reward.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RewardServiceTest {

	@Mock
	private RewardRepository rewardRepository;

	@Mock
	private MessageUtil messageUtil;

	private RewardService rewardService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// Test case for saving a reward (valid)
	@Test
	void saveReward_validReward_success() {
		Reward reward = new Reward();
		reward.setCustomerId("123");
		reward.setAmount(100.0);
		reward.setTransactionDate(LocalDate.now());

		when(rewardRepository.save(any(Reward.class))).thenReturn(reward);

		rewardService.saveReward(reward);

		verify(rewardRepository, times(1)).save(reward);
	}

	// Test case for invalid amount
	@Test
	void saveReward_invalidAmount_throwInvalidArgumentException() {
		Reward reward = new Reward();
		reward.setCustomerId("123");
		reward.setAmount(-1.0); // Invalid amount
		reward.setTransactionDate(LocalDate.now());

		InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
			rewardService.saveReward(reward);
		});

		assertEquals("Invalid transaction amount", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}

	// Test case for invalid customer ID
	@Test
	void saveReward_invalidCustomerId_throwInvalidArgumentException() {
		Reward reward = new Reward();
		reward.setCustomerId(""); // Invalid customer ID
		reward.setAmount(100.0);
		reward.setTransactionDate(LocalDate.now());

		InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
			rewardService.saveReward(reward);
		});

		assertEquals("Invalid customer ID", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}

	// Test case for invalid transaction date (future date)
	@Test
	void saveReward_invalidTransactionDate_futureDate_throwInvalidArgumentException() {
		Reward reward = new Reward();
		reward.setCustomerId("123");
		reward.setAmount(100.0);
		reward.setTransactionDate(LocalDate.now().plusDays(1)); // Invalid future date

		InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
			rewardService.saveReward(reward);
		});

		assertEquals("Transaction date cannot be in the future", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}

	// Test case for calculating rewards with date range
	@Test
	void getRewardsByCustomerIdAndDateRange_validRange_returnsRewards() {
		String customerId = "123";
		LocalDate from = LocalDate.now().minusDays(5);
		LocalDate to = LocalDate.now();

		Reward reward1 = new Reward();
		reward1.setCustomerId(customerId);
		reward1.setAmount(100.0);
		reward1.setTransactionDate(LocalDate.now().minusDays(1));
		reward1.setPoint(10);

		Reward reward2 = new Reward();
		reward2.setCustomerId(customerId);
		reward2.setAmount(200.0);
		reward2.setTransactionDate(LocalDate.now().minusDays(2));
		reward2.setPoint(20);

		List<Reward> rewards = List.of(reward1, reward2);
		when(rewardRepository.findByCustomerIdAndTransactionDateBetween(customerId, from, to)).thenReturn(rewards);

		Map<String, Map<String, Integer>> result = rewardService.getRewardsByCustomerIdAndDateRange(customerId, from,
				to);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		verify(rewardRepository, times(1)).findByCustomerIdAndTransactionDateBetween(customerId, from, to);
	}

	// Test case for invalid date range (from > to)
	@Test
	void getRewardsByCustomerIdAndDateRange_invalidDateRange_throwInvalidArgumentException() {
		String customerId = "123";
		LocalDate from = LocalDate.now().plusDays(1); // Invalid range (from > to)
		LocalDate to = LocalDate.now();

		InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
			rewardService.getRewardsByCustomerIdAndDateRange(customerId, from, to);
		});

		assertEquals("Invalid date range", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}

	// Test case for empty rewards list
	@Test
	void getRewardsByCustomerIdAndDateRange_emptyRewards_returnsEmptyMap() {
		String customerId = "123";
		LocalDate from = LocalDate.now().minusDays(10);
		LocalDate to = LocalDate.now();

		when(rewardRepository.findByCustomerIdAndTransactionDateBetween(customerId, from, to))
				.thenReturn(Collections.emptyList());

		Map<String, Map<String, Integer>> result = rewardService.getRewardsByCustomerIdAndDateRange(customerId, from,
				to);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	// Test case for missing customer ID in getRewardsByCustomerIdAndDateRange
	@Test
	void getRewardsByCustomerIdAndDateRange_invalidCustomerId_throwInvalidArgumentException() {
		String customerId = ""; // Invalid customer ID
		LocalDate from = LocalDate.now().minusDays(5);
		LocalDate to = LocalDate.now();

		InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
			rewardService.getRewardsByCustomerIdAndDateRange(customerId, from, to);
		});

		assertEquals("Invalid customer ID", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}
}
