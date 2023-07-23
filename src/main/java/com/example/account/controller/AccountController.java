package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountInfoDto;
import com.example.account.dto.CreateAccountDto;
import com.example.account.dto.DeleteAccountDto;
import com.example.account.service.AccountService;
import com.example.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
  private final AccountService accountService;
  private final RedisTestService redisTestService;

  @PostMapping("/account")
  public CreateAccountDto.Response createAccount(
      @RequestBody @Valid CreateAccountDto.Request request
  ) {
    return CreateAccountDto.Response.from(
        accountService.createAccount(
            request.getUserId(),
            request.getInitialBalance()
        )
    );
  }


//  @DeleteMapping ("/account")
//  public DeleteAccountDto.Response deleteAccount(
//      @RequestBody @Valid DeleteAccountDto.Request request
//  ) {
//    return DeleteAccountDto.Response.from(
//        accountService.deleteAccount(
//            request.getUserId(),
//            request.getAccountNumber()
//        )
//    );
//  }

  @DeleteMapping("/account")
  public DeleteAccountDto.Response createAccount(
      @RequestBody @Valid DeleteAccountDto.Request request
  ) {
    return DeleteAccountDto.Response.from(
        accountService.deleteAccount(
            request.getUserId(),
            request.getAccountNumber()
        )
    );
  }

  @GetMapping("/account")
  public List<AccountInfoDto> getAccountsByUserId(@RequestParam("user_id") Long userId) {

    return accountService.getAccountsByUserId(userId)
        .stream().map(accountDto -> AccountInfoDto.builder()
            .accountNumber(accountDto.getAccountNumber())
            .balance(accountDto.getBalance()).build())
        .collect(Collectors.toList());

  }


  @GetMapping("/get-lock")
  public String getLock() {
    return redisTestService.getLock();
  }

  @GetMapping("/account/{id}")
  public Account getAccount(
      @PathVariable Long id) {
    return accountService.getAccount(id);
  }
}
