package com.retailer.reward.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTransactionsDto {
    private String customerId;
    private String customerName;
    private long totalPoints;
    private Map<String, MonthlyTransactionsDto> transactionsByMonth;
}
