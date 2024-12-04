package com.retailer.reward.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRewardsDto {
    private String customerId;
    private String customerName;
    private long totalPoints;
    private List<RewardDto> transactions;
}
