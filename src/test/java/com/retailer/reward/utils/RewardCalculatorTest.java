package com.retailer.reward.utils;

import com.retailer.reward.entity.Reward;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RewardCalculatorTest {

    // Test case for calculating rewards with a single reward
    @Test
    void calculateRewards_singleReward_correctPoints() {
        Reward reward = new Reward();
        reward.setCustomerId("123");
        reward.setAmount(120.0);
        reward.setTransactionDate(LocalDate.of(2024, 12, 1));

        List<Reward> rewards = Collections.singletonList(reward);
        Map<String, Map<String, Integer>> rewardsMap = RewardCalculator.calculateRewards(rewards);

        assertNotNull(rewardsMap);
        assertEquals(1, rewardsMap.size());
        assertTrue(rewardsMap.containsKey("123"));

        Map<String, Integer> monthPoints = rewardsMap.get("123");
        assertNotNull(monthPoints);
        assertEquals(1, monthPoints.size());
        assertTrue(monthPoints.containsKey("December"));
        assertEquals(40, monthPoints.get("December")); // Points calculation should give 40 points
    }

    // Test case for calculating multiple rewards for the same customer in the same month
    @Test
    void calculateRewards_multipleRewards_sameMonth_correctPoints() {
        Reward reward1 = new Reward();
        reward1.setCustomerId("123");
        reward1.setAmount(120.0);
        reward1.setTransactionDate(LocalDate.of(2024, 12, 1));

        Reward reward2 = new Reward();
        reward2.setCustomerId("123");
        reward2.setAmount(80.0);
        reward2.setTransactionDate(LocalDate.of(2024, 12, 5));

        List<Reward> rewards = Arrays.asList(reward1, reward2);
        Map<String, Map<String, Integer>> rewardsMap = RewardCalculator.calculateRewards(rewards);

        assertNotNull(rewardsMap);
        assertEquals(1, rewardsMap.size());
        assertTrue(rewardsMap.containsKey("123"));

        Map<String, Integer> monthPoints = rewardsMap.get("123");
        assertNotNull(monthPoints);
        assertEquals(2, monthPoints.size());
        assertTrue(monthPoints.containsKey("December"));
        assertEquals(70, monthPoints.get("December")); // 40 points from reward1 + 30 points from reward2
    }

    // Test case for calculating rewards for different customers
    @Test
    void calculateRewards_differentCustomers_correctPoints() {
        Reward reward1 = new Reward();
        reward1.setCustomerId("123");
        reward1.setAmount(120.0);
        reward1.setTransactionDate(LocalDate.of(2024, 12, 1));

        Reward reward2 = new Reward();
        reward2.setCustomerId("456");
        reward2.setAmount(60.0);
        reward2.setTransactionDate(LocalDate.of(2024, 12, 1));

        List<Reward> rewards = Arrays.asList(reward1, reward2);
        Map<String, Map<String, Integer>> rewardsMap = RewardCalculator.calculateRewards(rewards);

        assertNotNull(rewardsMap);
        assertEquals(2, rewardsMap.size());
        assertTrue(rewardsMap.containsKey("123"));
        assertTrue(rewardsMap.containsKey("456"));

        Map<String, Integer> monthPoints123 = rewardsMap.get("123");
        assertEquals(40, monthPoints123.get("December"));

        Map<String, Integer> monthPoints456 = rewardsMap.get("456");
        assertEquals(10, monthPoints456.get("December")); // 10 points for reward2
    }

    // Test case for calculating total points for each customer
    @Test
    void calculateRewards_totalPoints_correct() {
        Reward reward1 = new Reward();
        reward1.setCustomerId("123");
        reward1.setAmount(120.0);
        reward1.setTransactionDate(LocalDate.of(2024, 12, 1));

        Reward reward2 = new Reward();
        reward2.setCustomerId("123");
        reward2.setAmount(80.0);
        reward2.setTransactionDate(LocalDate.of(2024, 12, 5));

        List<Reward> rewards = Arrays.asList(reward1, reward2);
        Map<String, Map<String, Integer>> rewardsMap = RewardCalculator.calculateRewards(rewards);

        assertNotNull(rewardsMap);
        assertTrue(rewardsMap.containsKey("123"));

        Map<String, Integer> monthPoints = rewardsMap.get("123");
        assertNotNull(monthPoints);
        assertTrue(monthPoints.containsKey("December"));
        assertEquals(70, monthPoints.get("December")); // 40 points from reward1 + 30 points from reward2
        assertEquals(70, monthPoints.get("Total")); // Total points should be 70
    }

    // Test case for calculating reward points when amount is below 50
    @Test
    void calculateRewardPoint_below50_noPoints() {
        Reward reward = new Reward();
        reward.setAmount(40.0); // Below 50, no points
        int points = RewardCalculator.calculateRewardPoint(reward);
        assertEquals(0, points);
    }

    // Test case for calculating reward points when amount is between 50 and 100
    @Test
    void calculateRewardPoint_between50and100_pointsCalculated() {
        Reward reward = new Reward();
        reward.setAmount(80.0); // 80-50 = 30, 30 points
        int points = RewardCalculator.calculateRewardPoint(reward);
        assertEquals(30, points);
    }

    // Test case for calculating reward points when amount is above 100
    @Test
    void calculateRewardPoint_above100_pointsCalculated() {
        Reward reward = new Reward();
        reward.setAmount(150.0); // (150-100)*2 = 100 points
        int points = RewardCalculator.calculateRewardPoint(reward);
        assertEquals(100, points);
    }

    // Test case for empty list of rewards
    @Test
    void calculateRewards_emptyList_emptyMap() {
        List<Reward> rewards = Collections.emptyList();
        Map<String, Map<String, Integer>> rewardsMap = RewardCalculator.calculateRewards(rewards);

        assertNotNull(rewardsMap);
        assertTrue(rewardsMap.isEmpty());
    }
}
