package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


public class CreateAccountDto {

  @Getter
  @Setter
  @ToString
  @AllArgsConstructor
  public static class Request {
    @NotNull
    @Min(1)
    private long userId;

    @NotNull
    @Min(100)
    private long initialBalance;
  }

  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    private long userId;
    private String accountNumber;
    private LocalDateTime registeredAt;

    public static Response from(AccountDto accountDto) {
      return Response.builder()
          .userId(accountDto.getUserId())
          .accountNumber(accountDto.getAccountNumber())
          .registeredAt(accountDto.getRegisteredAt())
          .build();
    }

  }


}