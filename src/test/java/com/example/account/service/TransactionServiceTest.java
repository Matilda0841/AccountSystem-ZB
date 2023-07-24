package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.account.type.AccountStatus.*;
import static com.example.account.type.TransactionResultType.*;
import static com.example.account.type.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
  public static final long USE_AMOUNT = 200L;
  @Mock
  TransactionRepository transactionRepository;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountUserRepository accountUserRepository;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  void successUseBalance() {
    // given 어떤 데이터가 있을때
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("Pobi").build();
    Account account = Account.builder()
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012")
        .build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));
    given(transactionRepository.save(any()))
        .willReturn(Transaction.builder()
            .account(account)
            .transactionType(USE)
            .transactionResultType(S)
            .transactedAt(LocalDateTime.now())
            .amount(1000L)
            .balanceSnapshot(9000L)
            .build());

    ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);


    // when 어떤 동작을 하면

    TransactionDto transactionDto = transactionService.useBalance(1L, "1000000000", USE_AMOUNT);
    // then 어떤 결과가 나와야 한다
    verify(transactionRepository, times(1)).save(captor.capture());
    assertEquals(200L, captor.getValue().getAmount());
    assertEquals(9800L, captor.getValue().getBalanceSnapshot());
    assertEquals(S, transactionDto.getTransactionResultType());
    assertEquals(USE, transactionDto.getTransactionType());
    assertEquals(9000L, transactionDto.getBalanceSnapshot());
    assertEquals(1000L, transactionDto.getAmount());
  }

  @Test
  @DisplayName("해당 유저 없음 - 잔액 사용 실패")
  void useBalance_UserNotFound() {
    //given
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());


    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.useBalance(1L, "1234567890", 10000L));

    //then
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
  }// 잔액 사용 실패 1. 해당 계좌 없음

  @Test
  @DisplayName("해당 계좌 없음 - 잔액 사용 실패")
  void useBalance_AccountNotFound() {
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
        () -> transactionService.useBalance(1L, "1234567890", 10000L));

    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());

  }// 잔액 사용 실패 2 해당 계좌 없음

  @Test
  @DisplayName("계좌 소유주 다름 - 잔액 사용 실패")
  void useBalance_Failed_userUnMatch() {
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
        () -> transactionService.useBalance(1L, "1234567890", 10000L));

    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.USER_ACCOUNT_UNMATCHED, exception.getErrorCode());

  }// 잔액 사용 실패 3 계좌 소유주 다름

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
        () -> transactionService.useBalance(1L, "1234567890", 10000L));


    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());

  }// 계좌 해지 실패 5 계좌 이미 없음

  @Test
  @DisplayName("잔액 부족 - 잔액 사용 실패")
  void UseBalance_exceedAmount() {
    // given 어떤 데이터가 있을때
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("Pobi").build();
    Account account = Account.builder()
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(100L)
        .accountNumber("1000000012")
        .build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));


    // when 어떤 동작을 하면
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.useBalance(1L, "1234567890", 10000L));


    // then 어떤 결과가 나와야 한다
    assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());
    verify(transactionRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("실패 트렌젝션 저장 성공")
  void saveFailedUseTransaction() {
    // given 어떤 데이터가 있을때
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("Pobi").build();
    Account account = Account.builder()
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012")
        .build();

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));
    given(transactionRepository.save(any()))
        .willReturn(Transaction.builder()
            .account(account)
            .transactionType(USE)
            .transactionResultType(S)
            .transactedAt(LocalDateTime.now())
            .amount(1000L)
            .balanceSnapshot(9000L)
            .build());

    ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);


    // when 어떤 동작을 하면

    transactionService.saveFailedUseTransaction("1000000012", USE_AMOUNT);
    // then 어떤 결과가 나와야 한다
    verify(transactionRepository, times(1)).save(captor.capture());
    assertEquals(USE_AMOUNT, captor.getValue().getAmount());
    assertEquals(10000L, captor.getValue().getBalanceSnapshot());
    assertEquals(F, captor.getValue().getTransactionResultType());
  }
}