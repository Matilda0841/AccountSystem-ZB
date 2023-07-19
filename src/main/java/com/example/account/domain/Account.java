package com.example.account.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account {
  @Id // PK 지정 >> 이건 좀 신기하네
  @GeneratedValue
  private Long id;
  private String accountNumber;
  @Enumerated(EnumType.STRING)
  private AccountStatus accountStatus;
}
