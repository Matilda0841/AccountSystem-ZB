package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.account.type.AccountStatus.IN_USE;

@Service
@RequiredArgsConstructor // final >> 사용가능하게 해줌
public class AccountService {

  private final AccountRepository accountRepository;
  private final AccountUserRepository accountUserRepository;

  /*
   사용자가 있는지 조회
    계좌 번호를 생성하고
    계좌를 저장하고, 그 정보를 넘긴다.
  */
  @Transactional
  public AccountDto createAccount(Long userId, Long initialBalance) {
    AccountUser accountUser = accountUserRepository.findById(userId)
        .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
    // 람다식 메서드 따로 구체적으로 해야하지만, 지금 모르니까 1회용 함수를 만들어 둔 거임

    validateCreateAccount(accountUser);

    String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
        .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
        .orElse("1000000000");

    return AccountDto.fromEntity(
        accountRepository.save(Account.builder()
            .accountUser(accountUser)
            .accountStatus(IN_USE)
            .accountNumber(newAccountNumber)
            .balance(initialBalance)
            .registeredAt(LocalDateTime.now())
            .build())
    );

  }

  private void validateCreateAccount(AccountUser accountUser) {
    if (accountRepository.countByAccountUser(accountUser) >= 10) {
      throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
    }
  }

  @Transactional
  public Account getAccount(Long id) {
    if (id < 0) {
      throw new RuntimeException("Minus");
    }
    return accountRepository.findById(id)
        .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
  }

  @Transactional
  public AccountDto deleteAccount(long userId, String accountNumber) {
    AccountUser accountUser = accountUserRepository.findById(userId)
        .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
    Account account = accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

    validateDeleteAccount(accountUser, account);

    account.setAccountStatus(AccountStatus.UNREGISTERED);
    account.setUnRegisteredAt(LocalDateTime.now());

    accountRepository.save(account);

    return AccountDto.fromEntity(account);
  }

  private void validateDeleteAccount(AccountUser accountUser, Account account) {
    if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
      throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCHED);
    }

    if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
      throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
    }

    if (account.getBalance() > 0) {
      throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
    }

  }
}