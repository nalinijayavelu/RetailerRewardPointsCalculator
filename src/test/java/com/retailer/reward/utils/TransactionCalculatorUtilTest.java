package com.retailer.reward.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.retailer.reward.entity.Transaction;

class TransactionCalculatorUtilTest {

	@Test
	void testCalculateRewardPoint_AmountAbove100() {
		Transaction transaction = new Transaction();
		transaction.setAmount(120.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(90, points, "Points should be 90 for $120");
	}

	@Test
	void testCalculateRewardPoint_AmountBetween50And100() {
		Transaction transaction = new Transaction();
		transaction.setAmount(70.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(20, points, "Points should be 20 for $70");
	}

	@Test
	void testCalculateRewardPoint_AmountBelow50() {
		Transaction transaction = new Transaction();
		transaction.setAmount(40.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(0, points, "Points should be 0 for $40");
	}

	@Test
	void testCalculateRewardPoint_AmountExactly100() {
		Transaction transaction = new Transaction();
		transaction.setAmount(100.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(50, points, "Points should be 50 for $100");
	}

	@Test
	void testCalculateRewardPoint_AmountExactly50() {
		Transaction transaction = new Transaction();
		transaction.setAmount(50.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(0, points, "Points should be 0 for $50");
	}

	@Test
	void testCalculateRewardPoint_NegativeAmount() {
		Transaction transaction = new Transaction();
		transaction.setAmount(-20.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(0, points, "Points should be 0 for a negative amount");
	}

	@Test
	void testCalculateRewardPoint_ZeroAmount() {
		Transaction transaction = new Transaction();
		transaction.setAmount(0.0);
		int points = TransactionCalculatorUtil.calculateRewardPoint(transaction);
		assertEquals(0, points, "Points should be 0 for $0");
	}
}
