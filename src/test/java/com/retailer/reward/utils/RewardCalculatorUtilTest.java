package com.retailer.reward.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.retailer.reward.entity.Reward;

class RewardCalculatorUtilTest {

	@Test
	void testCalculateRewardPoint_AmountAbove100() {
		Reward reward = new Reward();
		reward.setAmount(120.0);
		int points = RewardCalculatorUtil.calculateRewardPoint(reward);
		assertEquals(90, points, "Points should be 90 for $120");
	}

	@Test
	void testCalculateRewardPoint_AmountBetween50And100() {
		Reward reward = new Reward();
		reward.setAmount(70.0);
		int points = RewardCalculatorUtil.calculateRewardPoint(reward);
		assertEquals(20, points, "Points should be 20 for $70");
	}

	@Test
	void testCalculateRewardPoint_AmountBelow50() {
		Reward reward = new Reward();
		reward.setAmount(40.0);
		int points = RewardCalculatorUtil.calculateRewardPoint(reward);
		assertEquals(0, points, "Points should be 0 for $40");
	}

	@Test
	void testCalculateRewardPoint_AmountExactly100() {
		Reward reward = new Reward();
		reward.setAmount(100.0);
		int points = RewardCalculatorUtil.calculateRewardPoint(reward);
		assertEquals(50, points, "Points should be 50 for $100");
	}

	@Test
	void testCalculateRewardPoint_AmountExactly50() {
		Reward reward = new Reward();
		reward.setAmount(50.0);
		int points = RewardCalculatorUtil.calculateRewardPoint(reward);
		assertEquals(0, points, "Points should be 0 for $50");
	}

	@Test
	void testCalculateRewardPoint_NegativeAmount() {
		Reward reward = new Reward();
		reward.setAmount(-20.0);
		int points = RewardCalculatorUtil.calculateRewardPoint(reward);
		assertEquals(0, points, "Points should be 0 for a negative amount");
	}

	@Test
	void testCalculateRewardPoint_ZeroAmount() {
		Reward reward = new Reward();
		reward.setAmount(0.0);
		int points = RewardCalculatorUtil.calculateRewardPoint(reward);
		assertEquals(0, points, "Points should be 0 for $0");
	}
}
