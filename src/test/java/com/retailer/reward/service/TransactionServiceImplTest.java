package com.retailer.reward.service;

import com.retailer.reward.dto.CustomerTransactionsDto;
import com.retailer.reward.dto.TransationRequestDto;
import com.retailer.reward.entity.Transaction;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.repository.TransactionRepository;
import com.retailer.reward.serviceImpl.TransactionServiceImpl;
import com.retailer.reward.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Save reward successfully when data is valid")
    void testSaveReward_Success() {
        TransationRequestDto request = new TransationRequestDto("C001", 120.0, "John Doe");
        Transaction transaction = new Transaction(request);

        when(transactionRepository.save(any())).thenReturn(transaction);

        assertDoesNotThrow(() -> transactionService.saveReward(request));
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test Throw exception for invalid amount in reward")
    void testSaveReward_InvalidAmount() {
        TransationRequestDto request = new TransationRequestDto("C001", -10.0, "John Doe");
        Exception exception = assertThrows(InvalidArgumentException.class, () -> transactionService.saveReward(request));

        assertEquals("Invalid amount", exception.getMessage());
    }

    @Test
    @DisplayName("Test Retrieve rewards for customer successfully")
    void testGetRewardsByCustomerId_Success() {
        Transaction transaction = new Transaction(1L, "C001", "John Doe", 120.0, LocalDate.now(), 50);
        when(transactionRepository.findByCustomerId("C001")).thenReturn(List.of(transaction));

        CustomerTransactionsDto response = transactionService.getRewardsByCustomerId("C001");

        assertNotNull(response);
        assertEquals("C001", response.getCustomerId());
        assertEquals(50, response.getTotalPoints());
    }

    @Test
    @DisplayName("Test Return empty rewards for customer when no transactions exist")
    void testGetRewardsByCustomerId_EmptyList() {
        when(transactionRepository.findByCustomerId("C001")).thenReturn(Collections.emptyList());

        CustomerTransactionsDto response = transactionService.getRewardsByCustomerId("C001");

        assertNotNull(response);
        assertEquals("C001", response.getCustomerId());
        assertEquals(0, response.getTotalPoints());
    }

    @Test
    @DisplayName("Test Throw exception for null customer ID validation")
    void testValidateCustomerId_Null() {
        Exception exception = assertThrows(InvalidArgumentException.class, () -> transactionService.validateCustomerId(null));

        assertEquals("Invalid customerId", exception.getMessage());
    }

    @Test
    @DisplayName("Test Throw exception for future transaction date validation")
    void testValidateTransactionDate_FutureDate() {
        Exception exception = assertThrows(InvalidArgumentException.class, () -> transactionService.validateTransactionDate(LocalDate.now().plusDays(1)));

        assertEquals("Transaction date cannot be in the future", exception.getMessage());
    }
    
    @Test
    @DisplayName("Test Throw exception for invalid date range with fromDate after today")
    void testValidateDateRange_FromDateAfterToday() {
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now();
        when(messageUtil.getMessage("validation.date.future")).thenReturn("Future dates are not allowed.");

        InvalidArgumentException exception = assertThrows(
            InvalidArgumentException.class,
            () -> transactionService.validateDateRange(fromDate, toDate)
        );

        assertEquals("Future dates are not allowed.", exception.getMessage());
    }

    @Test
    @DisplayName("Test Throw exception for invalid date range with toDate before fromDate")
    void testValidateDateRange_ToDateBeforeFromDate() {
        LocalDate fromDate = LocalDate.now().minusDays(1);
        LocalDate toDate = LocalDate.now().minusDays(2);
        when(messageUtil.getMessage("invalid.date.range")).thenReturn("Invalid date range.");

        InvalidArgumentException exception = assertThrows(
            InvalidArgumentException.class,
            () -> transactionService.validateDateRange(fromDate, toDate)
        );

        assertEquals("Invalid date range.", exception.getMessage());
    }

    @Test
    @DisplayName("Test Pass validation for a valid date range")
    void testValidateDateRange_ValidRange() {
        LocalDate fromDate = LocalDate.now().minusDays(2);
        LocalDate toDate = LocalDate.now();

        assertDoesNotThrow(() -> transactionService.validateDateRange(fromDate, toDate));
    }

    @Test
    @DisplayName("Test Allow null dates in date range validation")
    void testValidateDateRange_NullDates() {
        assertDoesNotThrow(() -> transactionService.validateDateRange(null, null));
    }

    @Test
    @DisplayName("Test Map rewards to DTO with multiple months' transactions")
    void testMapRewardsToDto_MultipleMonths() {
        Transaction txn1 = new Transaction(1L, "C001", "John Doe", 100.0, LocalDate.of(2024, 11, 1), 50);
        Transaction txn2 = new Transaction(2L, "C001", "John Doe", 200.0, LocalDate.of(2024, 12, 1), 150);

        List<Transaction> transactions = List.of(txn1, txn2);

        CustomerTransactionsDto dto = transactionService.mapRewardsToDto("C001", transactions);

        assertNotNull(dto);
        assertEquals("C001", dto.getCustomerId());
        assertEquals(200, dto.getTotalPoints());
        assertEquals(2, dto.getTransactionsByMonth().size());
        assertTrue(dto.getTransactionsByMonth().containsKey("Nov2024"));
        assertTrue(dto.getTransactionsByMonth().containsKey("Dec2024"));
    }

    @Test
    @DisplayName("Test Map rewards to DTO with empty transactions list")
    void testMapRewardsToDto_EmptyTransactions() {
        List<Transaction> transactions = Collections.emptyList();

        CustomerTransactionsDto dto = transactionService.mapRewardsToDto("C001", transactions);

        assertNotNull(dto);
        assertEquals("C001", dto.getCustomerId());
        assertEquals(0, dto.getTotalPoints());
        assertTrue(dto.getTransactionsByMonth().isEmpty());
    }
}
