package com.retailer.reward.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.retailer.reward.entity.Transaction;

class TransactionCalculatorUtilTest {

	@Test
    @DisplayName("Calculate reward points for an amount above $100")
	void testCalculateRewardPoint_AmountAbove100() {
		Transaction transaction = new Transaction();
		transaction.setAmount(120.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(90, points, "Points should be 90 for $120");
	}

	@Test
    @DisplayName("Calculate reward points for an amount between $50 and $100")
	void testCalculateRewardPoint_AmountBetween50And100() {
		Transaction transaction = new Transaction();
		transaction.setAmount(70.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(20, points, "Points should be 20 for $70");
	}

	@Test
    @DisplayName("Calculate reward points for an amount below $50")
	void testCalculateRewardPoint_AmountBelow50() {
		Transaction transaction = new Transaction();
		transaction.setAmount(40.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(0, points, "Points should be 0 for $40");
	}

	@Test
    @DisplayName("Calculate reward points for an amount exactly $100")
	void testCalculateRewardPoint_AmountExactly100() {
		Transaction transaction = new Transaction();
		transaction.setAmount(100.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(50, points, "Points should be 50 for $100");
	}

	@Test
    @DisplayName("Calculate reward points for an amount exactly $50")
	void testCalculateRewardPoint_AmountExactly50() {
		Transaction transaction = new Transaction();
		transaction.setAmount(50.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(0, points, "Points should be 0 for $50");
	}

	@Test
    @DisplayName("Calculate reward points for a negative amount")
	void testCalculateRewardPoint_NegativeAmount() {
		Transaction transaction = new Transaction();
		transaction.setAmount(-20.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(0, points, "Points should be 0 for a negative amount");
	}

	@Test
    @DisplayName("Calculate reward points for a zero amount")
	void testCalculateRewardPoint_ZeroAmount() {
		Transaction transaction = new Transaction();
		transaction.setAmount(0.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(0, points, "Points should be 0 for $0");
	}
}
