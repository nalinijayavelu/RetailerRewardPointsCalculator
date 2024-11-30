package com.retailer.reward.controller;

import com.retailer.reward.entity.Reward;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.service.RewardService;
import com.retailer.reward.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RewardControllerTest {

    @Mock
    private RewardService rewardService;

    @Mock
    private MessageUtil messageUtil;

    private RewardController rewardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for creating a reward (valid)
    @Test
    void createReward_validReward_success() {
        Reward reward = new Reward();
        reward.setCustomerId("123");
        reward.setAmount(100.0);
        reward.setTransactionDate(LocalDate.now());

        when(messageUtil.getMessage("purchase.transaction.success")).thenReturn("Transaction successful");

        ResponseEntity<String> response = rewardController.createReward(reward);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Transaction successful", response.getBody());
        verify(rewardService, times(1)).saveReward(reward);
    }

    // Test case for creating a reward with invalid data (invalid amount)
    @Test
    void createReward_invalidAmount_throwInvalidArgumentException() {
        Reward reward = new Reward();
        reward.setCustomerId("123");
        reward.setAmount(-1.0); // Invalid amount
        reward.setTransactionDate(LocalDate.now());

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
            rewardController.createReward(reward);
        });

        assertEquals("Invalid transaction amount", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // Test case for getting rewards with valid parameters
    @Test
    void getReward_validParameters_returnsRewards() {
        String customerId = "123";
        LocalDate fromDate = LocalDate.now().minusDays(5);
        LocalDate toDate = LocalDate.now();

        Map<String, Map<String, Integer>> rewardsMap = new HashMap<>();
        rewardsMap.put("2024-11-01", Map.of("points", 100));

        when(rewardService.getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate)).thenReturn(rewardsMap);

        ResponseEntity<Map<String, Map<String, Integer>>> response = rewardController.getReward(customerId, fromDate, toDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rewardsMap, response.getBody());
        verify(rewardService, times(1)).getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate);
    }

    // Test case for getting rewards with invalid customer ID
    @Test
    void getReward_invalidCustomerId_throwInvalidArgumentException() {
        String customerId = ""; // Invalid customer ID
        LocalDate fromDate = LocalDate.now().minusDays(5);
        LocalDate toDate = LocalDate.now();

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
            rewardController.getReward(customerId, fromDate, toDate);
        });

        assertEquals("Invalid customer ID", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // Test case for missing customerId parameter
    @Test
    void getReward_missingCustomerId_throwInvalidArgumentException() {
        String customerId = null; // Missing customerId

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
            rewardController.getReward(customerId, null, null);
        });

        assertEquals("Customer ID is required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // Test case for invalid date range (from > to)
    @Test
    void getReward_invalidDateRange_throwInvalidArgumentException() {
        String customerId = "123";
        LocalDate fromDate = LocalDate.now().plusDays(1); // Invalid range (from > to)
        LocalDate toDate = LocalDate.now();

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
            rewardController.getReward(customerId, fromDate, toDate);
        });

        assertEquals("Invalid date range", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // Test case for empty rewards list
    @Test
    void getReward_emptyRewards_returnsEmptyMap() {
        String customerId = "123";
        LocalDate fromDate = LocalDate.now().minusDays(10);
        LocalDate toDate = LocalDate.now();

        Map<String, Map<String, Integer>> rewardsMap = new HashMap<>();

        when(rewardService.getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate)).thenReturn(rewardsMap);

        ResponseEntity<Map<String, Map<String, Integer>>> response = rewardController.getReward(customerId, fromDate, toDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(rewardService, times(1)).getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate);
    }
}
