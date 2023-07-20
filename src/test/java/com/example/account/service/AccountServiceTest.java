package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {
  @Autowired
  private AccountService accountService;
  @BeforeEach
  void init(){
    accountService.createAccount();

  }
  @Test
  void testGetAccount(){
    // given 어떤 데이터가 있을때
    accountService.createAccount();
    // when 어떤 동작을 하면
    Account account = accountService.getAccount(1L);
    // then 어떤 결과가 나와야 한다
    assertEquals("40000",account.getAccountNumber());
    assertEquals(AccountStatus.IN_USE, account.getAccountStatus());
  }
  @Test
  void testGetAccount2(){
    // given 어떤 데이터가 있을때
    accountService.createAccount();
    // when 어떤 동작을 하면
    Account account = accountService.getAccount(2L);
    // then 어떤 결과가 나와야 한다
    assertEquals("40000",account.getAccountNumber());
    assertEquals(AccountStatus.IN_USE, account.getAccountStatus());
  }
}