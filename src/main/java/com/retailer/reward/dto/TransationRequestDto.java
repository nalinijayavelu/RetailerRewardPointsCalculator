package com.retailer.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransationRequestDto {
	private String customerId;
	private double amount;
	private String customerName;
}