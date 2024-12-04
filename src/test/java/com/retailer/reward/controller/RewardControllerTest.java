package com.retailer.reward.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

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
import com.retailer.reward.dto.CustomerRewardsDto;
import com.retailer.reward.entity.Reward;
import com.retailer.reward.serviceImpl.RewardServiceImpl;
import com.retailer.reward.utils.MessageUtil;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardServiceImpl rewardService;

    @MockBean
    private MessageUtil messageUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateReward_Success() throws Exception {
        // Create a reward instance for the test
        Reward reward = new Reward();
        reward.setCustomerId("12345");
        reward.setTransactionDate(LocalDate.now());
        reward.setAmount(100.0);

        // Mock the message returned by MessageUtil
        when(messageUtil.getMessage("purchase.transaction.success")).thenReturn("Transaction saved successfully.");

        // Perform the POST request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/retail/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reward)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction saved successfully."));

        // Verify that the service method was called once
        verify(rewardService, times(1)).saveReward(any(Reward.class));
    }

    @Test
    void testGetRewardByCustomerIdAndDate_Success() throws Exception {
        String customerId = "12345";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);

        // Create a CustomerRewardsDto to mock the response
        CustomerRewardsDto mockResponse = new CustomerRewardsDto(customerId, "John", 200, null);
        when(rewardService.getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate)).thenReturn(mockResponse);

        // Perform the GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards")
                .param("customerId", customerId)
                .param("fromDate", fromDate.toString())
                .param("toDate", toDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.customerName").value("John"))
                .andExpect(jsonPath("$.totalPoints").value(200));

        // Verify the service method was called once
        verify(rewardService, times(1)).getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate);
    }

    @Test
    void testGetRewardByCustomerId_Success() throws Exception {
        String customerId = "12345";

        // Create a CustomerRewardsDto to mock the response
        CustomerRewardsDto mockResponse = new CustomerRewardsDto(customerId, "John", 150, null);
        when(rewardService.getRewardsByCustomerId(customerId)).thenReturn(mockResponse);

        // Perform the GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.customerName").value("John"))
                .andExpect(jsonPath("$.totalPoints").value(150));

        // Verify the service method was called once
        verify(rewardService, times(1)).getRewardsByCustomerId(customerId);
    }

    @Test
    void testGetRewardByCustomerId_NotFound() throws Exception {
        String customerId = "99999";  // Non-existing customer ID

        // Mock the service to return null for a non-existing customer
        when(rewardService.getRewardsByCustomerId(customerId)).thenReturn(null);

        // Perform the GET request and expect a NOT FOUND response
        mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verify the service method was called once
        verify(rewardService, times(1)).getRewardsByCustomerId(customerId);
    }

    @Test
    void testCreateReward_InvalidData() throws Exception {
        // Create an invalid reward instance (missing required fields)
        Reward reward = new Reward();  // Empty fields

        // Perform the POST request and expect a BAD REQUEST response
        mockMvc.perform(MockMvcRequestBuilders.post("/retail/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reward)))
                .andExpect(status().isBadRequest());

        // Verify the service method was not called
        verify(rewardService, times(0)).saveReward(any(Reward.class));
    }

    @Test
    void testGetRewardByCustomerIdAndDate_InvalidDateRange() throws Exception {
        String customerId = "12345";
        LocalDate fromDate = LocalDate.now().plusDays(1); // Invalid future date

        // Perform the GET request with invalid date range and expect a BAD REQUEST response
        mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards")
                .param("customerId", customerId)
                .param("fromDate", fromDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid date range"));

        // Verify the service method was not called
        verify(rewardService, times(0)).getRewardsByCustomerIdAndDateRange(anyString(), any(), any());
    }
}
