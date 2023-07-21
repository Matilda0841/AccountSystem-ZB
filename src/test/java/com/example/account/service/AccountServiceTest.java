package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import com.example.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
  @Mock
  private AccountRepository accountRepository;
  @InjectMocks
  private AccountService accountService;

  @Test
  void textSuccess() {
    // given 어떤 데이터가 있을때
    given(accountRepository.findById(anyLong()))
        .willReturn(Optional.of(Account.builder()
            .accountStatus(AccountStatus.UNREGISTERED)
            .accountNumber("65789")
            .build()));
    ArgumentCaptor<Long>captor = ArgumentCaptor.forClass(Long.class);
    // when 어떤 동작을 하면
    Account account = accountService.getAccount(455L);
    // then 어떤 결과가 나와야 한다
    verify(accountRepository,times(1)).findById(captor.capture());
    verify(accountRepository,times(0)).save(any());
    assertEquals(455L, captor.getValue());
    assertEquals("65789", account.getAccountNumber());
    assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
  }
  @Test
  void textFailedToSearchAccount() {
    // given 어떤 데이터가 있을때
    // when 어떤 동작을 하면
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> accountService.getAccount(-10L));

    //then
    assertEquals("Minus", exception.getMessage());
  }

  @Test
  void test1() {
    given(accountRepository.findById(anyLong()))
        .willReturn(Optional.of(Account.builder()
            .accountStatus(AccountStatus.UNREGISTERED)
            .accountNumber("65789").build()));
    ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

    //when
    Account account = accountService.getAccount(4555L);

    //then
    verify(accountRepository, times(1)).findById(captor.capture());
    verify(accountRepository, times(0)).save(any());
    assertEquals(4555L, captor.getValue());
    assertNotEquals(45515L, captor.getValue());
    assertEquals("65789", account.getAccountNumber());
    assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
  }

  @Test
  void testGetAccount2() {
    given(accountRepository.findById(anyLong()))
        .willReturn(Optional.of(Account.builder()
            .accountStatus(AccountStatus.UNREGISTERED)
            .accountNumber("65789").build()));
    ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

    //when
    Account account = accountService.getAccount(4555L);

    //then

    verify(accountRepository, times(1)).findById(captor.capture());
    verify(accountRepository, times(0)).save(any());
    assertEquals(4555L, captor.getValue());
    assertNotEquals(45515L, captor.getValue());
    assertEquals("65789", account.getAccountNumber());
    assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
  }
}