package com.retailer.reward.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reward {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank(message = "{validation.customerId.notBlank}")
	private String customerId;

	@NotBlank(message = "{validation.customerName.notBlank}")
	private String customerName;

	@Positive(message = "{validation.amount.positive}")
	private double amount;

	@NotNull(message = "{validation.transactionDate.notNull}")
	private LocalDate transactionDate;

	private long point;

	// Custom constructor for specific use cases
	public Reward(String customerId, String customerName, double amount, LocalDate transactionDate, long point) {
		this.customerId = customerId;
		this.customerName = customerName;
		this.amount = amount;
		this.transactionDate = transactionDate;
		this.point = point;
	}

	public Reward(long id, String customerId, String customerName, double amount, long point,
			LocalDate transactionDate) {
		this.id = id;
		this.customerId = customerId;
		this.customerName = customerName;
		this.amount = amount;
		this.transactionDate = transactionDate;
		this.point = point;
	}

}
