package com.vacant.account.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacant.account.constants.ApiPath;
import com.vacant.account.constants.CoreConstant;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.model.BalanceAmount;
import com.vacant.account.model.in.BankStatementRequest;
import com.vacant.account.model.in.BankStatementResponse;
import com.vacant.account.services.AccountStatementService;
import com.vacant.account.services.LoggingService;
import com.vacant.account.utils.NumberUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountStatementController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AccountStatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoggingService loggingService;

    @MockBean
    private AccountStatementService accountStatementService;

    @Test
    void getBankStatement_validInput_then200() throws Exception {
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.BANK_STATEMENT)
                        .header("X-PARTNER-ID", "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getBankStatement_noPartnerIdHeader_then400() throws Exception {
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.BANK_STATEMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBankStatement_noAccountNo_then400() throws Exception {
        var request = BankStatementRequest.builder()
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.BANK_STATEMENT)
                        .header("X-PARTNER-ID", "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBankStatement_validInputAndOutput_then200() throws Exception {
        var partnerId = "12345";

        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        var totalDebitEntries = BankStatementResponse.DebitCreditEntries.builder()
                .numberOfEntries("1")
                .amount(NumberUtil.setBalanceAmount(new BigDecimal("1000000"), CoreConstant.CURRENCY_CODE))
                .build();

        var detailBalance = BankStatementResponse.DetailBalance.builder()
                .startAmount(List.of(BankStatementResponse.DetailBalanceAmount.builder()
                        .amount(BalanceAmount.builder()
                                .value("1000000.00")
                                .currency(CoreConstant.CURRENCY_CODE)
                                .build())
                        .build()))
                .endAmount(List.of(BankStatementResponse.DetailBalanceAmount.builder()
                        .amount(BalanceAmount.builder()
                                .value("900000.00")
                                .currency(CoreConstant.CURRENCY_CODE)
                                .build())
                        .build()))
                .build();
        var data = BankStatementResponse.DetailData.builder()
                .transactionDate("2023-06-13T12:08:56+07:00")
                .transactionId("12345")
                .type("DEBIT")
                .amount(NumberUtil.setBalanceAmount(new BigDecimal("100000"), CoreConstant.CURRENCY_CODE))
                .detailBalance(detailBalance)
                .build();

        var response = BankStatementResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .totalDebitEntries(totalDebitEntries)
                .detailData(List.of(data))
                .build();

        Mockito.when(accountStatementService.getAccountStatement(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.BANK_STATEMENT)
                        .header("X-PARTNER-ID", partnerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.totalDebitEntries.numberOfEntries").value("1"))
                .andExpect(jsonPath("$.detailData[0].amount.value").value("100000.00"));
    }
}
