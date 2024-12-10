package com.retailer.reward.service;

import java.time.LocalDate;

import com.retailer.reward.dto.CustomerTransactionsDto;
import com.retailer.reward.dto.TransationRequestDto;

public interface TransactionService {
	
	public void saveReward(TransationRequestDto transationRequest);
	
	public CustomerTransactionsDto getRewardsByCustomerIdAndDateRange(String customerId, LocalDate fromDate,
			LocalDate toDate);
	
	public CustomerTransactionsDto getRewardsByCustomerId(String customerId);
}
