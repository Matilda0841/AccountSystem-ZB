package com.example.account.controller;


import com.example.account.dto.AccountDto;
import com.example.account.dto.CreateAccountDto;
import com.example.account.dto.DeleteAccountDto;
import com.example.account.service.AccountService;
import com.example.account.service.RedisTestService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
  @MockBean
  private AccountService accountService;

  @MockBean
  private RedisTestService redisTestService;

  @Autowired
  private MockMvc mockMvc;


  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void successCreateAccount() throws Exception {
    // given 어떤 데이터가 있을때
    given(accountService.createAccount(anyLong(), anyLong()))
        .willReturn(AccountDto.builder()
            .userId(1L)
            .accountNumber("1234567890")
            .registeredAt(LocalDateTime.now())
            .unregisteredAt(LocalDateTime.now())
            .build());
    // when 어떤 동작을 하면
    // then 어떤 결과가 나와야 한다
    mockMvc.perform(post("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new CreateAccountDto.Request(1L, 100L)
            )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andDo(print());
  }

//  @Test
//  void successGetAccount() throws Exception {
//    // given 어떤 데이터가 있을때
//    given(accountService.getAccount(anyLong()))
//        .willReturn(Account.builder()
//            .accountNumber("3456")
//            .accountStatus(AccountStatus.IN_USE)
//            .build());
//    // when 어떤 동작을 하면
//    // 객체가 아닌 문자열이기에 when 없음
//    // then 어떤 결과가 나와야 한다
//    mockMvc.perform(get("/account/876"))
//        .andDo(print())
//        .andExpect(jsonPath("$.accountNumber").value("3456"))
//        .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
//        .andExpect(status().isOk());
//  }

  @Test
  void successDeleteAccount() throws Exception {
    // given 어떤 데이터가 있을때
    given(accountService.deleteAccount(anyLong(), anyString()))
        .willReturn(AccountDto.builder()
            .userId(1L)
            .accountNumber("1234567890")
            .registeredAt(LocalDateTime.now())
            .unregisteredAt(LocalDateTime.now())
            .build());
    // when 어떤 동작을 하면
    // then 어떤 결과가 나와야 한다
    mockMvc.perform(delete("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new DeleteAccountDto.Request(1L, "1111111111")
            )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andDo(print());
  }

}