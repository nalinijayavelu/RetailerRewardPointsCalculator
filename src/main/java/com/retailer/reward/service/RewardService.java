package com.retailer.reward.service;

import java.time.LocalDate;

import com.retailer.reward.dto.CustomerRewardsDto;
import com.retailer.reward.entity.Reward;

public interface RewardService {
	
	public void saveReward(Reward reward);
	
	public CustomerRewardsDto getRewardsByCustomerIdAndDateRange(String customerId, LocalDate fromDate,
			LocalDate toDate);
	
	public CustomerRewardsDto getRewardsByCustomerId(String customerId);
}
