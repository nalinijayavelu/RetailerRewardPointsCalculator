package com.retailer.reward.service;

import com.retailer.reward.dto.CustomerRewardsDto;
import com.retailer.reward.entity.Reward;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.repository.RewardRepository;
import com.retailer.reward.serviceImpl.RewardServiceImpl;
import com.retailer.reward.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RewardServiceImplTest {

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private MessageUtil messageUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveReward_Success() {
        Reward reward = new Reward();
        reward.setCustomerId("CUST001");
        reward.setAmount(120.0);
        reward.setTransactionDate(LocalDate.now());

        when(rewardRepository.save(reward)).thenReturn(reward);

        assertDoesNotThrow(() -> rewardService.saveReward(reward));
        verify(rewardRepository, times(1)).save(reward);
    }

    @Test
    void testSaveReward_InvalidCustomerId() {
        Reward reward = new Reward();
        reward.setCustomerId("");
        reward.setAmount(120.0);
        reward.setTransactionDate(LocalDate.now());

        when(messageUtil.getMessage("validation.customerId.Invalid")).thenReturn("Invalid Customer ID");

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, 
            () -> rewardService.saveReward(reward));
        assertEquals("Invalid Customer ID", exception.getMessage());
    }

    @Test
    void testMapRewardsToDto_ValidRewards() {
        Reward reward1 = new Reward(1L, "CUST001", "John", 150.0, 50, LocalDate.now());
        Reward reward2 = new Reward(2L, "CUST001", "John", 80.0, 30, LocalDate.now().minusDays(1));
        List<Reward> rewards = List.of(reward1, reward2);

        CustomerRewardsDto result = rewardService.mapRewardsToDto("CUST001", rewards);

        assertNotNull(result);
        assertEquals("CUST001", result.getCustomerId());
        assertEquals("John", result.getCustomerName());
        assertEquals(80, result.getTotalPoints());
        assertEquals(2, result.getTransactions().size());
    }

    @Test
    void testMapRewardsToDto_EmptyRewards() {
        List<Reward> rewards = List.of();

        CustomerRewardsDto result = rewardService.mapRewardsToDto("CUST001", rewards);

        assertNotNull(result);
        assertEquals("CUST001", result.getCustomerId());
        assertEquals("", result.getCustomerName());
        assertEquals(0, result.getTotalPoints());
        assertTrue(result.getTransactions().isEmpty());
    }

    @Test
    void testValidateDateRange_ValidRange() {
        LocalDate fromDate = LocalDate.now().minusDays(1);
        LocalDate toDate = LocalDate.now();

        assertDoesNotThrow(() -> rewardService.validateDateRange(fromDate, toDate));
    }

    @Test
    void testValidateDateRange_FutureDate() {
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now();

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
            () -> rewardService.validateDateRange(fromDate, toDate));

        assertEquals("Transaction date cannot be in the future", exception.getMessage());
    }

    @Test
    void testValidateDateRange_FromDateAfterToDate() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().minusDays(1);

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
            () -> rewardService.validateDateRange(fromDate, toDate));

        assertEquals("Invalid date range", exception.getMessage());
    }

    @Test
    void testValidateTransactionDate_ValidDate() {
        LocalDate transactionDate = LocalDate.now();

        assertDoesNotThrow(() -> rewardService.validateTransactionDate(transactionDate));
    }

    @Test
    void testValidateTransactionDate_NullDate() {
        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
            () -> rewardService.validateTransactionDate(null));

        assertEquals("Transaction date cannot be null", exception.getMessage());
    }

    @Test
    void testValidateTransactionDate_FutureDate() {
        LocalDate transactionDate = LocalDate.now().plusDays(1);

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
            () -> rewardService.validateTransactionDate(transactionDate));

        assertEquals("Transaction date cannot be in the future", exception.getMessage());
    }

    @Test
    void testValidateAmount_InvalidAmount() {
        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
            () -> rewardService.validateAmount(-1.0));

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void testValidateAmount_ValidAmount() {
        assertDoesNotThrow(() -> rewardService.validateAmount(50.0));
    }

    @Test
    void testGetRewardsByCustomerId_Success() {
        Reward reward = new Reward(1L, "CUST001", "John", 150.0, 50, LocalDate.now());
        List<Reward> rewards = List.of(reward);

        when(rewardRepository.findByCustomerId("CUST001")).thenReturn(rewards);

        CustomerRewardsDto result = rewardService.getRewardsByCustomerId("CUST001");

        assertNotNull(result);
        assertEquals("CUST001", result.getCustomerId());
        assertEquals(50, result.getTotalPoints());
        assertEquals(1, result.getTransactions().size());
    }

    @Test
    void testGetRewardsByCustomerId_EmptyRewards() {
        when(rewardRepository.findByCustomerId("CUST001")).thenReturn(List.of());

        CustomerRewardsDto result = rewardService.getRewardsByCustomerId("CUST001");

        assertNotNull(result);
        assertEquals("CUST001", result.getCustomerId());
        assertEquals(0, result.getTotalPoints());
        assertTrue(result.getTransactions().isEmpty());
    }

    @Test
    void testGetRewardsByCustomerId_InvalidCustomerId() {
        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
            () -> rewardService.getRewardsByCustomerId(""));

        assertEquals("Invalid Customer ID", exception.getMessage());
    }


    @Test
    void testGetRewardsByCustomerIdAndDateRange_EmptyRewards() {
        when(rewardRepository.findByCustomerIdAndTransactionDateBetween("CUST001", LocalDate.now().minusDays(5), LocalDate.now()))
            .thenReturn(List.of());

        CustomerRewardsDto result = rewardService.getRewardsByCustomerIdAndDateRange("CUST001", LocalDate.now().minusDays(5), LocalDate.now());

        assertNotNull(result);
        assertEquals("CUST001", result.getCustomerId());
        assertEquals(0, result.getTotalPoints());
        assertTrue(result.getTransactions().isEmpty());
    }

}
