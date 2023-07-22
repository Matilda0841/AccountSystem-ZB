package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
  private Long userId;
  private String accountNumber;
  private Long balance;

  private LocalDateTime registeredAt;
  private LocalDateTime unregisteredAt;

  public static AccountDto fromEntity(Account account) {
    return AccountDto.builder()
        .userId(account.getAccountUser().getId())
        .accountNumber(account.getAccountNumber())
        .registeredAt(account.getRegisteredAt())
        .build();
  }

}
