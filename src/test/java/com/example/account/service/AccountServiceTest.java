package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
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

  @Mock
  private AccountUserRepository accountUserRepository;

  @InjectMocks
  private AccountService accountService;

  @Test
  @DisplayName("계좌 생성 성공")
  void createAccountSuccess() {
    // given 어떤 데이터가 있을때
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(accountRepository.findFirstByOrderByIdDesc())
        .willReturn(Optional.of(Account.builder()
            .accountNumber("100000012").build()));
    given(accountRepository.save(any()))
        .willReturn(Account.builder()
            .accountUser(user).accountNumber("100000015").build());

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

    // when 어떤 동작을 하면
    AccountDto accountDto = accountService.createAccount(1L, 1000L);


    // then 어떤 결과가 나와야 한다
    verify(accountRepository, times(1)).save(captor.capture());
    assertEquals(12L, accountDto.getUserId());
    assertEquals("100000013", captor.getValue().getAccountNumber());
  }// 계좌 생성 성공


  @Test
  @DisplayName("첫 계좌 생성")
  void createFirstAccount() {
    //given
    AccountUser user = AccountUser.builder()
        .id(15L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(accountRepository.findFirstByOrderByIdDesc())
        .willReturn(Optional.empty());
    given(accountRepository.save(any()))
        .willReturn(Account.builder()
            .accountUser(user)
            .accountNumber("1000000013").build());

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

    //when
    AccountDto account = accountService.createAccount(1L, 1000L);
    //then
    verify(accountRepository, times(1)).save(captor.capture());
    assertEquals(account.getUserId(), 15L);
    assertEquals("1000000000", captor.getValue().getAccountNumber());
  }// 첫 계좌 생성


  @Test
  @DisplayName("해당 유저 없음 - 계좌 생성 실패")
  void createAccount_UserNotFound() {
    //given
    AccountUser user = AccountUser.builder()
        .id(15L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());


    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.createAccount(1L, 1000L));

    //then
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
  }// 계좌 생성 실패

  @Test
  @DisplayName("유저 당 최대 계좌는 10개")
  void createAccount_maxAccountIs10() {
    // given 어떤 데이터가 있을때
    AccountUser user = AccountUser.builder()
        .id(15L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(accountRepository.countByAccountUser(any()))
        .willReturn(10);

    // when 어떤 동작을 하면
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.createAccount(1L, 1000L));

    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());


  }// 계좌가 10개 이상은 안되요

}
