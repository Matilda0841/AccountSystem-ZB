package com.example.account.domain;

import com.example.account.type.AccountStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

  @ManyToOne
  private AccountUser accountUser;
  private String accountNumber;

  @Enumerated(EnumType.STRING)
  private AccountStatus accountStatus;
  private long balance;

  private LocalDateTime unRegisteredAt;
  private LocalDateTime registeredAt;
}
