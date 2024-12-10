package com.retailer.reward.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTransactionsDto {
    private long points;
    private List<TransactionDto> transactions;
}