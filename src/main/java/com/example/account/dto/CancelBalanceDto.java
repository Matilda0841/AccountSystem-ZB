package com.example.account.dto;

import com.example.account.type.TransactionResultType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class CancelBalanceDto {
  @Getter
  @Setter
  @AllArgsConstructor
  @ToString
  public static class Request {
    @NotNull
    private String transactionId;

    @NotBlank
    @Size(min = 10, max = 10)
    private String accountNumber;

    @NotNull
    @Min(10)
    @Max(1000_000_000)
    private Long amount;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @ToString
  public static class Response{
    private String accountNumber;
    private TransactionResultType transactionResult;
    private String transactionId;
    private Long amount;
    private LocalDateTime transactedAt;

    public static CancelBalanceDto.Response from(TransactionDto transactionDto) {
      return CancelBalanceDto.Response.builder()
          .accountNumber(transactionDto.getAccountNumber())
          .transactionResult(transactionDto.getTransactionResultType())
          .transactionId(transactionDto.getTransactionId())
          .amount(transactionDto.getAmount())
          .transactedAt(transactionDto.getTransactedAt())
          .build();
    }

  }
}
