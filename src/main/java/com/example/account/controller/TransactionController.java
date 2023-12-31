package com.example.account.controller;

import com.example.account.dto.CancelBalanceDto;
import com.example.account.dto.UseBalanceDto;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/*
잔액 관련 컨트롤러
1. 잔액 사용
2. 잔액 사용 취소
3. 거래 확인
*/
@RestController
@Slf4j //로그
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/transaction/use")
  public UseBalanceDto.Response useBalance(
      @Valid @RequestBody UseBalanceDto.Request request
  ) {

    try {
      return UseBalanceDto.Response.from(
          transactionService.useBalance(request.getUserId(),
              request.getAccountNumber(), request.getAmount())
      );
    }catch (AccountException e){
      log.error("Failed to use balance.");

      transactionService.saveFailedUseTransaction(
          request.getAccountNumber(),
          request.getAmount()
      );
      throw e;
    }
  }

  @PostMapping("/transaction/cancel")
  public CancelBalanceDto.Response cancelBalance(
      @Valid @RequestBody CancelBalanceDto.Request request
  ) {

    try {
      return CancelBalanceDto.Response.from(
          transactionService.
              cancelBalance(request.getTransactionId()
                  , request.getAccountNumber()
                  , request.getAmount())
      );
    }catch (AccountException e){
      log.error("Failed to use balance.");

      transactionService.saveFailedCancelTransaction(
          request.getAccountNumber(),
          request.getAmount()
      );
      throw e;
    }
  }

}
