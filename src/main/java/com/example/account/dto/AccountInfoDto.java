package com.example.account.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfoDto {
  private  String accountNumber;
  private Long balance;

}
