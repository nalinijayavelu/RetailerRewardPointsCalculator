package com.retailer.reward.utils;

import com.retailer.reward.entity.Reward;

public class RewardCalculatorUtil {

	public static int calculateRewardPoint(Reward reward) {
		int point = 0;
		if (reward.getAmount() > 100) {
			point += (reward.getAmount() - 100) * 2;
		}
		if (reward.getAmount() > 50) {
			point += Math.min(reward.getAmount() - 50, 50);
		}
		return point;
	}
}
