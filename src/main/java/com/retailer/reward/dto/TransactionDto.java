package com.retailer.reward.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
	private long id;
    private double amount;
    private LocalDate date;
    private long rewardPoints;
}