package com.retailer.reward.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.retailer.reward.entity.Reward;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

	List<Reward> findByCustomerId(String customerId);

	List<Reward> findByCustomerIdAndTransactionDateBetween(String customerId, LocalDate from, LocalDate to);

	List<Reward> findByCustomerIdAndTransactionDate(String customerId, LocalDate date);
}
