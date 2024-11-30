package com.retailer.reward.utils;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.retailer.reward.entity.Reward;
import com.retailer.reward.service.RewardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RewardCalculator {

	// Method to calculate rewards for each customer, per month and total
	public static Map<String, Map<String, Integer>> calculateRewards(List<Reward> rewards) {
    	 Map<String, Map<String, Integer>> rewardsMap = new HashMap<>();

         // Iterate through the list of rewards
         for (Reward reward : rewards) {
             int points = calculateRewardPoint(reward);
             //To Get full month name
             String monthName = reward.getTransactionDate().getMonth()
                     .getDisplayName(TextStyle.FULL, Locale.ENGLISH); 

             // Updating the map with points per customer and per month
             rewardsMap.computeIfAbsent(reward.getCustomerId(), k -> new HashMap<>())
                     .merge(monthName, points, Integer::sum);
         }

         //calculating the total reward points for each customer
         for (Map<String, Integer> monthPoints : rewardsMap.values()) {
             int totalPoints = monthPoints.values().stream().mapToInt(Integer::intValue).sum();
             monthPoints.put("Total", totalPoints);
         }

         return rewardsMap;
    }
    
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
