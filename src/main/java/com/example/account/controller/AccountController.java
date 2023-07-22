package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.CreateAccountDto;
import com.example.account.service.AccountService;
import com.example.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
