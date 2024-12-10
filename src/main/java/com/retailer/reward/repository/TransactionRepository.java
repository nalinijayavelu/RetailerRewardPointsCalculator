package com.retailer.reward.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.retailer.reward.entity.Transaction;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByCustomerId(String customerId);

	List<Transaction> findByCustomerIdAndTransactionDateBetween(String customerId, LocalDate from, LocalDate to);

	List<Transaction> findByCustomerIdAndTransactionDate(String customerId, LocalDate date);
}
