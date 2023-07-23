package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

  @Test
  @DisplayName("계좌 삭제 성공")
  void deleteAccountSuccess() {
    // given 어떤 데이터가 있을때
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
            .accountUser(user)
            .balance(0L)
            .accountNumber("100000012").build()));

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

    // when 어떤 동작을 하면
    AccountDto accountDto = accountService.deleteAccount(1L, "1234567890");


    // then 어떤 결과가 나와야 한다
    verify(accountRepository, times(1)).save(captor.capture());
    assertEquals(12L, accountDto.getUserId());
    assertEquals("100000012", captor.getValue().getAccountNumber());
    assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
  }// 계좌 삭제 성공

  @Test
  @DisplayName("해당 유저 없음 - 계좌 해지 실패")
  void deleteAccount_UserNotFound() {
    //given
    AccountUser user = AccountUser.builder()
        .id(15L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());


    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1234567890"));

    //then
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
  }// 계좌 해지 실페 1 해당 유저 없음

  @Test
  @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
  void deleteAccount_AccountNotFound() {
    // given 어떤 데이터가 있을때
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.empty());


    // when 어떤 동작을 하면
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1234567890"));

    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());

  }// 계좌 해지 실패 2 해당 계좌 없음

  @Test
  @DisplayName("계좌 소유주 다름 - 계좌 해지 실패")
  void deleteAccountFailed_userUnMatch() {
    // given 어떤 데이터가 있을때
    AccountUser poby = AccountUser.builder()
        .id(12L)
        .name("poby").build();
    AccountUser harry = AccountUser.builder()
        .id(13L)
        .name("harry").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(poby));
    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
            .accountUser(harry)
            .balance(0L)
            .accountNumber("100000012").build()));

    // when 어떤 동작을 하면
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1234567890"));

    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.USER_ACCOUNT_UNMATCHED, exception.getErrorCode());

  }// 계좌 해지 실패 3 계좌 소유주 다름

  @Test
  @DisplayName("계좌 잔액 있음 - 계좌 해지 실패")
  void deleteAccountFailed_balanceNotEmpty() {
    // given 어떤 데이터가 있을때
    AccountUser poby = AccountUser.builder()
        .id(12L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(poby));
    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
            .accountUser(poby)
            .balance(100L)
            .accountNumber("100000012").build()));

    // when 어떤 동작을 하면
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1234567890"));

    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());

  }// 계좌 해지 실패 4 계좌 잔액 있음

  @Test
  @DisplayName("계좌 이미 없음 - 계좌 해지 실패")
  void deleteAccountFailed_alreadyUnregistered() {
    // given 어떤 데이터가 있을때
    AccountUser poby = AccountUser.builder()
        .id(12L)
        .name("poby").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(poby));
    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
            .accountUser(poby)
            .accountStatus(AccountStatus.UNREGISTERED)
            .balance(0L)
            .accountNumber("100000012").build()));

    // when 어떤 동작을 하면
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1234567890"));

    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());

  }// 계좌 해지 실패 5 계좌 이미 없음

  @Test
  void successGetAccountByUserId() {
    // given 어떤 데이터가 있을때
    AccountUser poby = AccountUser.builder()
        .id(12L)
        .name("poby").build();
    List<Account> accounts = Arrays.asList(
        Account.builder()
            .accountUser(poby)
            .accountNumber("1234567890")
            .balance(1000L)
            .build(),
        Account.builder()
            .accountUser(poby)
            .accountNumber("2345678901")
            .balance(2000L)
            .build(),
        Account.builder()
            .accountUser(poby)
            .accountNumber("3456789012")
            .balance(3000L)
            .build()
    );
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(poby));
    given(accountRepository.findByAccountUser(any()))
        .willReturn(accounts);


    // when 어떤 동작을 하면
    List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

    // then 어떤 결과가 나와야 한다
    assertEquals(3, accountDtos.size());
    assertEquals("1234567890", accountDtos.get(0).getAccountNumber());
    assertEquals(1000, accountDtos.get(0).getBalance());
    assertEquals("2345678901", accountDtos.get(1).getAccountNumber());
    assertEquals(2000, accountDtos.get(1).getBalance());
    assertEquals("3456789012", accountDtos.get(2).getAccountNumber());
    assertEquals(3000, accountDtos.get(2).getBalance());
  }

  @Test
  void failedToGetAccounts(){
    // given 어떤 데이터가 있을때
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    // when 어떤 동작을 하면
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.getAccountsByUserId(1L));

    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

  }
}
