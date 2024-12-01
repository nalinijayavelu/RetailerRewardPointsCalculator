package com.retailer.reward.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.retailer.reward.entity.Reward;

class RewardCalculatorUtilTest {

    @Test
    void testCalculateRewardPoint_WhenAmountIsOver100() {
        Reward reward = new Reward();
        reward.setAmount(120.0);
        int points = RewardCalculatorUtil.calculateRewardPoint(reward);
        assertThat(points).isEqualTo(90);
    }

    @Test
    void testCalculateRewardPoint_WhenAmountIsBetween50And100() {
        Reward reward = new Reward();
        reward.setAmount(70.0);
        int points = RewardCalculatorUtil.calculateRewardPoint(reward);
        assertThat(points).isEqualTo(20); // 70-50 = 20 points
    }

    @Test
    void testCalculateRewardPoint_WhenAmountIsBelow50() {
        Reward reward = new Reward();
        reward.setAmount(40.0);
        int points = RewardCalculatorUtil.calculateRewardPoint(reward);
        assertThat(points).isEqualTo(0);
    }

    @Test
    void testCalculateRewards() {
        Reward reward1 = new Reward();
        reward1.setCustomerId("12345");
        reward1.setTransactionDate(LocalDate.of(2024, 1, 15));
        reward1.setAmount(120.0);

        Reward reward2 = new Reward();
        reward2.setCustomerId("12345");
        reward2.setTransactionDate(LocalDate.of(2024, 1, 20));
        reward2.setAmount(70.0);

        Reward reward3 = new Reward();
        reward3.setCustomerId("67890");
        reward3.setTransactionDate(LocalDate.of(2024, 2, 5));
        reward3.setAmount(40.0);

        List<Reward> rewards = Arrays.asList(reward1, reward2, reward3);
        Map<String, Map<String, Integer>> rewardsMap = RewardCalculatorUtil.calculateRewards(rewards);
        assertThat(rewardsMap).hasSize(2);
        Map<String, Integer> customer12345 = rewardsMap.get("12345");
        assertThat(customer12345).hasSize(2);
        assertThat(customer12345.get("January")).isEqualTo(110);
        assertThat(customer12345.get("Total")).isEqualTo(110);
        Map<String, Integer> customer67890 = rewardsMap.get("67890");
        assertThat(customer67890).hasSize(2);
        assertThat(customer67890.get("February")).isEqualTo(0);
        assertThat(customer67890.get("Total")).isEqualTo(0);
    }
}
