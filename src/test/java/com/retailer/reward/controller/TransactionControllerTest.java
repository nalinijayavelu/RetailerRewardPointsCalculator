package com.retailer.reward.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailer.reward.dto.CustomerTransactionsDto;
import com.retailer.reward.dto.TransationRequestDto;
import com.retailer.reward.serviceImpl.TransactionServiceImpl;
import com.retailer.reward.utils.MessageUtil;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TransactionServiceImpl transactionService;

	@MockBean
	private MessageUtil messageUtil;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
    @DisplayName("Test Creation of Reward - Success Scenario")
	void testCreateReward_Success() throws Exception {
		// Create a reward instance for the test
		TransationRequestDto transationRequest = new TransationRequestDto("123", 200.0, "John Doe");

		// Mock the message returned by MessageUtil
		when(messageUtil.getMessage("purchase.transaction.success")).thenReturn("Transaction saved successfully.");

		// Perform the POST request and validate the response
		mockMvc.perform(MockMvcRequestBuilders.post("/retail/rewards").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transationRequest))).andExpect(status().isCreated())
				.andExpect(content().string("Transaction saved successfully."));

		verify(transactionService, times(1)).saveReward(any(TransationRequestDto.class));
	}

	@Test
    @DisplayName("Test Get Reward By Customer ID and Date - Success Scenario")
	void testGetRewardByCustomerIdAndDate_Success() throws Exception {
		String customerId = "12345";
		LocalDate fromDate = LocalDate.of(2024, 1, 1);
		LocalDate toDate = LocalDate.of(2024, 1, 31);

		// Create a CustomerRewardsDto to mock the response
		CustomerTransactionsDto mockResponse = new CustomerTransactionsDto(customerId, "John", 200, null);
		when(transactionService.getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate))
				.thenReturn(mockResponse);

		// Perform the GET request and validate the response
		mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards").param("customerId", customerId)
				.param("fromDate", fromDate.toString()).param("toDate", toDate.toString())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(customerId))
				.andExpect(jsonPath("$.customerName").value("John")).andExpect(jsonPath("$.totalPoints").value(200));

		verify(transactionService, times(1)).getRewardsByCustomerIdAndDateRange(customerId, fromDate, toDate);
	}

	@Test
    @DisplayName("Test Get Reward By Customer ID - Success Scenario")
	void testGetRewardByCustomerId_Success() throws Exception {
		String customerId = "12345";

		// Create a CustomerRewardsDto to mock the response
		CustomerTransactionsDto mockResponse = new CustomerTransactionsDto(customerId, "John", 150, null);
		when(transactionService.getRewardsByCustomerId(customerId)).thenReturn(mockResponse);

		// Perform the GET request and validate the response
		mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards/{customerId}", customerId)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(customerId))
				.andExpect(jsonPath("$.customerName").value("John")).andExpect(jsonPath("$.totalPoints").value(150));

		verify(transactionService, times(1)).getRewardsByCustomerId(customerId);
	}

	@Test
    @DisplayName("Test Get Reward By Customer ID - Not Found Scenario")
	void testGetRewardByCustomerId_NotFound() throws Exception {
		String customerId = "99999";
		// Mock the service to return null for a non-existing customer
		when(transactionService.getRewardsByCustomerId(customerId)).thenReturn(null);

		// Perform the GET request and expect a NOT FOUND response
		mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards/{customerId}", customerId)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
    @DisplayName("Test Create Reward - Invalid Data Scenario")
	void testCreateReward_InvalidData() throws Exception {
		// Create an invalid reward instance (missing required fields)
		TransationRequestDto reward = new TransationRequestDto(); // Empty fields

		// Perform the POST request and expect a BAD REQUEST response
		mockMvc.perform(MockMvcRequestBuilders.post("/retail/rewards").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reward))).andExpect(status().isBadRequest());
		verify(transactionService, times(0)).saveReward(any(TransationRequestDto.class));
	}

	@Test
    @DisplayName("Test Get Reward By Customer ID and Date - Invalid Date Range case")
	void testGetRewardByCustomerIdAndDate_InvalidDateRange() throws Exception {
		String customerId = "12345";
		LocalDate fromDate = LocalDate.now().plusDays(1); // Invalid future date

		// Perform the GET request with invalid date range and expect a BAD REQUEST
		mockMvc.perform(MockMvcRequestBuilders.get("/retail/rewards").param("customerId", customerId).param("fromDate",
				fromDate.toString())).andExpect(status().isBadRequest())
				.andExpect(content().string("Invalid date range"));

		verify(transactionService, times(0)).getRewardsByCustomerIdAndDateRange(anyString(), any(), any());
	}

}
