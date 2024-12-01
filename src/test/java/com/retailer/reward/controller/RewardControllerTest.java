package com.retailer.reward.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailer.reward.entity.Reward;
import com.retailer.reward.service.RewardService;
import com.retailer.reward.utils.MessageUtil;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @MockBean
    private MessageUtil messageUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateReward_Success() throws Exception {
        Reward reward = new Reward();
        reward.setCustomerId("12345");
        reward.setTransactionDate(LocalDate.now());
        reward.setAmount(100.0);
        when(messageUtil.getMessage("purchase.transaction.success")).thenReturn("Transaction saved successfully.");
        mockMvc.perform(MockMvcRequestBuilders.post("/retail/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reward)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction saved successfully."));
        verify(rewardService, times(1)).saveReward(any(Reward.class));
    }

    @Test
    void testGetReward_Success() throws Exception {
        String customerId = "12345";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);
        Map<String, Map<String, Integer>> mockResponse = new HashMap<>();
        Map<String, Integer> monthlyData = new HashMap<>();
        monthlyData.put("January", 120);
        mockResponse.put(customerId, monthlyData);
        when(rewardService.getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate)).thenReturn(mockResponse);
        mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards")
                .param("customerId", customerId)
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-01-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.12345.January").value(120));
        verify(rewardService, times(1)).getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate);
    }

    @Test
    void testGetRewardByCustomerId_Success() throws Exception {
        String customerId = "12345";
        Map<String, Map<String, Integer>> mockResponse = new HashMap<>();
        Map<String, Integer> monthlyData = new HashMap<>();
        monthlyData.put("January", 120);
        mockResponse.put(customerId, monthlyData);
        when(rewardService.getRewardsByCustomerId(customerId)).thenReturn(mockResponse);
        mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.12345.January").value(120));
        verify(rewardService, times(1)).getRewardsByCustomerId(customerId);
    }

}
