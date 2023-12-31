package com.example.account.controller;

import com.example.account.dto.CancelBalanceDto;
import com.example.account.dto.TransactionDto;
import com.example.account.dto.UseBalanceDto;
import com.example.account.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.example.account.type.TransactionResultType.S;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
  @MockBean
  private TransactionService transactionService;
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void successUseBalance() throws Exception {
    // given 어떤 데이터가 있을때
    given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .accountNumber("1234567890")
            .transactedAt(LocalDateTime.now())
            .amount(12345L)
            .transactionId("transactionId")
            .transactionResultType(S)
            .build());

    // when 어떤 동작을 하면


    // then 어떤 결과가 나와야 한다
    mockMvc.perform(post("/transaction/use")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new UseBalanceDto.Request(1L, "2345678901", 3000L)
            ))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value("S"))
        .andExpect(jsonPath("$.transactionResult").value("1234567890"))
        .andExpect(jsonPath("$.transactionId").value("transactionId"))
        .andExpect(jsonPath("$.amount").value("12345L"));
  }

  @Test
  void successCancelBalance() throws Exception {
    //given
    given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .accountNumber("1000000000")
            .transactedAt(LocalDateTime.now())
            .amount(12345L)
            .transactionId("transactionIdForCancel")
            .transactionResultType(S)
            .build());
    //when

    //then
    mockMvc.perform(post("/transaction/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new CancelBalanceDto.Request("transactionId", "2000000000", 3000L)
            ))
        ).andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value("1000000000"))
        .andExpect(jsonPath("$.transactionResult").value("S"))
        .andExpect(jsonPath("$.transactionId").value("transactionIdForCancel"))
        .andExpect(jsonPath("$.amount").value(12345L));
  }
}