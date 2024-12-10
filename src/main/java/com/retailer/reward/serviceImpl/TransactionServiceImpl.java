package com.retailer.reward.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.retailer.reward.dto.CustomerTransactionsDto;
import com.retailer.reward.dto.MonthlyTransactionsDto;
import com.retailer.reward.dto.TransactionDto;
import com.retailer.reward.dto.TransationRequestDto;
import com.retailer.reward.entity.Transaction;
import com.retailer.reward.exception.InvalidArgumentException;
import com.retailer.reward.repository.TransactionRepository;
import com.retailer.reward.service.TransactionService;
import com.retailer.reward.utils.MessageUtil;
import com.retailer.reward.utils.TransactionCalculatorUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	private final MessageUtil messageUtil;

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public void saveReward(TransationRequestDto transationRequest) {
		log.info("Initiating transaction request {} ", transationRequest);
		Transaction transaction = new Transaction(transationRequest);
		validateReward(transaction); // Performing validation
		transaction.setPoint(TransactionCalculatorUtil.calculateRewardPoint(transaction));
		transactionRepository.save(transaction);
	}

	@Override
	public CustomerTransactionsDto getRewardsByCustomerIdAndDateRange(String customerId, LocalDate fromDate,
			LocalDate toDate) {
		validateCustomerId(customerId); // Validating customerId
		validateDateRange(fromDate, toDate); // Validating date range

		List<Transaction> transactions = (fromDate != null && toDate != null)
				? transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, fromDate, toDate)
				: transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId,
						LocalDate.now().minusMonths(3).withDayOfMonth(1), LocalDate.now());

		return transactions.isEmpty() ? new CustomerTransactionsDto(customerId, "", 0, Map.of())
				: mapRewardsToDto(customerId, transactions);
	}

	@Override
	public CustomerTransactionsDto getRewardsByCustomerId(String customerId) {
		validateCustomerId(customerId); // Validating customerId
		List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);

		return transactions.isEmpty() ? new CustomerTransactionsDto(customerId, "", 0, Map.of())
				: mapRewardsToDto(customerId, transactions);
	}

	public void validateReward(Transaction transaction) {
		validateCustomerId(transaction.getCustomerId());
		validateAmount(transaction.getAmount());
		validateTransactionDate(transaction.getTransactionDate());
	}

	public void validateCustomerId(String customerId) {
		if (customerId == null || customerId.trim().isEmpty()) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.customerId.Invalid"),
					HttpStatus.BAD_REQUEST);
		}
	}

	public void validateAmount(Double amount) {
		if (amount == null || amount <= 0) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.amount.Invalid"),
					HttpStatus.BAD_REQUEST);
		}
	}

	public void validateTransactionDate(LocalDate transactionDate) {
		if (transactionDate == null) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.transactionDate.null"),
					HttpStatus.BAD_REQUEST);
		}
		if (transactionDate.isAfter(LocalDate.now())) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.date.future"),
					HttpStatus.BAD_REQUEST);
		}
	}

	public void validateDateRange(LocalDate fromDate, LocalDate toDate) {
		LocalDate today = LocalDate.now();
		if ((fromDate != null && fromDate.isAfter(today)) || (toDate != null && toDate.isAfter(today))) {
			throw new InvalidArgumentException(messageUtil.getMessage("validation.date.future"),
					HttpStatus.BAD_REQUEST);
		}
		if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
			throw new InvalidArgumentException(messageUtil.getMessage("invalid.date.range"), HttpStatus.BAD_REQUEST);
		}
	}

	public CustomerTransactionsDto mapRewardsToDto(String customerId, List<Transaction> transactions) {
		long totalPoints = transactions.stream().mapToLong(Transaction::getPoint).sum();

		// Group by month and year
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMuuuu");
		Map<String, List<Transaction>> groupedByMonth = transactions.stream()
				.collect(Collectors.groupingBy(txn -> txn.getTransactionDate().format(formatter)));

		// Create transactionsByMonth map
		Map<String, MonthlyTransactionsDto> transactionsByMonth = groupedByMonth.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> {
					List<TransactionDto> monthlyTransactions = entry.getValue().stream()
							.map(txn -> new TransactionDto(txn.getId(), txn.getAmount(), txn.getTransactionDate(),
									txn.getPoint()))
							.toList();
					long monthlyPoints = entry.getValue().stream().mapToLong(Transaction::getPoint).sum();
					return new MonthlyTransactionsDto(monthlyPoints, monthlyTransactions);
				}));

		String customerName = transactions.isEmpty() ? "" : transactions.get(0).getCustomerName();
		return new CustomerTransactionsDto(customerId, customerName, totalPoints, transactionsByMonth);
	}

	public boolean isDatabaseEmpty() {
		return transactionRepository.count() == 0;
	}

}
