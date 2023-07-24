package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
