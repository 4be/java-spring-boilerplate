package com.vacant.account.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacant.account.constants.ApiPath;
import com.vacant.account.constants.CoreConstant;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import com.simas.account.model.*;
import com.simas.account.model.in.*;
import com.vacant.account.services.AccountService;
import com.vacant.account.services.LoggingService;
import com.vacant.account.utils.ResponseConstructor;
import com.vacant.account.model.Account;
import com.vacant.account.model.AccountBalance;
import com.vacant.account.model.BalanceAmount;
import com.vacant.account.model.in.*;
import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoggingService loggingService;

    @MockBean
    private AccountService accountService;


    @Test
    void getPartnerAccounts_validInput_then200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PARTNER_ACCOUNT_INQUIRY)
                        .param("customerId", "12345"))
                .andExpect(status().isOk());
    }


    @Test
    void getPartnerAccounts_noCustomerIdParameter_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PARTNER_ACCOUNT_INQUIRY))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPartnerAccounts_validInputAndOutput_then200() throws Exception {
        var customerId = "12345";

        var account = Account.builder()
                .customerId(customerId)
                .accountNo("1234567890")
                .accountName("JHONY")
                .build();

        var response = PartnerAccountInquiryResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accounts(List.of(account))
                .build();

        var expectedResponse = objectMapper.writeValueAsString(response);

        Mockito.when(accountService.getPartnerAccounts(customerId)).thenReturn(response);
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PARTNER_ACCOUNT_INQUIRY)
                        .param("customerId", customerId))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getPartnerAccounts_validInputButFailed_then200() throws Exception {
        var customerId = "12345";

        var account = Account.builder()
                .customerId(customerId)
                .accountNo("1234567890")
                .accountName("JHONY")
                .build();

        var response = PartnerAccountInquiryResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accounts(List.of(account))
                .build();

        var expectedResponse = objectMapper.writeValueAsString(response);

        Mockito.when(accountService.getPartnerAccounts(customerId)).thenReturn(response);
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PARTNER_ACCOUNT_INQUIRY)
                        .param("customerId", customerId))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString());
    }

    @Test
    void createAccount_validInput_then200() throws Exception {
        var request = CreateAccountRequest.builder()
                .partnerReferenceNo(UUID.randomUUID().toString())
                .cif("12345")
                .additionalInfo(CreateAccountAdditionalInfo.builder()
                        .category("6002").build())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.REGISTRATION_ACCOUNT_CREATION)
                        .header("X-PARTNER-ID", "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void createAccount_cifIsNull() throws Exception {
        var request = CreateAccountRequest.builder()
                .partnerReferenceNo(UUID.randomUUID().toString())
                .additionalInfo(CreateAccountAdditionalInfo.builder()
                        .category("6002").build())
                .build();

        var response = CreateAccountResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .partnerReferenceNo(request.getPartnerReferenceNo())
                .accountId("12345678")
                .build();

        Mockito.when(accountService.createAccount(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.REGISTRATION_ACCOUNT_CREATION)
                        .header("X-PARTNER-ID", "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAccount_validInputAndOutput_then200() throws Exception {
        var request = CreateAccountRequest.builder()
                .partnerReferenceNo(UUID.randomUUID().toString())
                .cif("12345")
                .additionalInfo(CreateAccountAdditionalInfo.builder()
                        .category("6002").build())
                .build();

        var response = CreateAccountResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .partnerReferenceNo(request.getPartnerReferenceNo())
                .accountId("12345678")
                .build();

        Mockito.when(accountService.createAccount(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.REGISTRATION_ACCOUNT_CREATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.accountId").value("12345678"));
    }

    @Test
    void getAccountBalance_validInput_then200() throws Exception {
        var request = BalanceInquiryRequest.builder()
                .accountNo("12345678")
                .partnerReferenceNo("123")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.BALANCE_INQUIRY)
                        .header("X-PARTNER-ID", "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void createAccount_accountNoIsNull_then400() throws Exception {
        var request = BalanceInquiryRequest.builder()
                .partnerReferenceNo("123")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.BALANCE_INQUIRY)
                        .header("X-PARTNER-ID", "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountBalance_validInputAndOutput_then200() throws Exception {
        var request = BalanceInquiryRequest.builder()
                .accountNo("12345678")
                .partnerReferenceNo("123")
                .build();

        var accountBalance = AccountBalance.builder()
                .balanceType("Tabungan Simas Gold")
                .availableBalance(BalanceAmount.builder().value("1000000").currency(CoreConstant.CURRENCY_CODE).build())
                .status("0001").build();

        var response = BalanceInquiryResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .partnerReferenceNo(request.getPartnerReferenceNo())
                .name("STEVEN")
                .accountInfos(List.of(accountBalance))
                .build();

        Mockito.when(accountService.getAccountBalance(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.BALANCE_INQUIRY)
                        .header("X-PARTNER-ID", "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.name").value("STEVEN"))
                .andExpect(jsonPath("$.accountInfos[0].balanceType").value("Tabungan Simas Gold"));
    }

    @Test
    void checkAccounts_AccountsAreValid() throws Exception {
        CheckAccountsRequest request = CheckAccountsRequest.builder()
                .cif("12345")
                .accountIds(List.of("1234567890"))
                .build();

        CheckAccountsResponse response = CheckAccountsResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accountsAreValid(true)
                .build();

        Mockito.when(accountService.checkAccounts(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.CHECK_ACCOUNTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.accountsAreValid").value(true));
    }

    @Test
    void checkAccounts_AccountsAreInvalid() throws Exception {
        CheckAccountsRequest request = CheckAccountsRequest.builder()
                .cif("12345")
                .accountIds(List.of("1234567890"))
                .build();

        CheckAccountsResponse response = CheckAccountsResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accountsAreValid(false)
                .build();

        Mockito.when(accountService.checkAccounts(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.CHECK_ACCOUNTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.accountsAreValid").value(false));
    }

    @Test
    void checkAccounts_MissingMandatoryFields() throws Exception {
        CheckAccountsRequest request = CheckAccountsRequest.builder()
                .accountIds(List.of("1234567890"))
                .build();

        CheckAccountsResponse response = CheckAccountsResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accountsAreValid(false)
                .build();

        Mockito.when(accountService.checkAccounts(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.CHECK_ACCOUNTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkAccounts_DataNotFound() throws Exception {
        CheckAccountsRequest request = CheckAccountsRequest.builder()
                .cif("12355")
                .accountIds(List.of("1234567890"))
                .build();

        CheckAccountsResponse response = CheckAccountsResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accountsAreValid(false)
                .build();

        Mockito.when(accountService.checkAccounts(ArgumentMatchers.any())).thenThrow(new BusinessException(ResponseCode.DATA_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.CHECK_ACCOUNTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void overrideLockAmount_validInput_then200() throws Exception {
        LockAmountRequest request = LockAmountRequest.builder()
                .branchCode("0002")
                .accountNumber("0059010093")
                .description("SID01.06M")
                .fromDate("2023/12/13")
                .toDate("20240612")
                .lockedAmount("1000000")
                .supervisorId("admin")
                .createdBy("ODSP Staff")
                .msgId("MSG0323c1f1-66d5-4691-82c8-c4fd69518631") //unique
                .trxId("TRX0323c1f1-66d5-4691-82c8-c4fd69518631") //unique
                .terminalId("PC_TELLER_1")
                .build();
        LockAmountResponse response = LockAmountResponse.builder()
                .responseCode("00")
                .build();

        Mockito.when(accountService.overrideLockAmount(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.LOCK_AMOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()));
    }


    @Test
    void overrideLockAmount_accountNoIsNull_then400() throws Exception {
        LockAmountRequest request = LockAmountRequest.builder()
                .branchCode("0002")
                .description("SID01.06M")
                .fromDate("2023/12/13")
                .toDate("20240612")
                .lockedAmount("1000000")
                .supervisorId("admin")
                .createdBy("ODSP Staff")
                .msgId("MSG0323c1f1-66d5-4691-82c8-c4fd69518631") //unique
                .trxId("TRX0323c1f1-66d5-4691-82c8-c4fd69518631") //unique
                .terminalId("PC_TELLER_1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.LOCK_AMOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void activateDormantAccount_validInput_then200() throws Exception {
        ActivateDormantRequest request = ActivateDormantRequest.builder()
                .branchCode("0002")
                .accountNumber("0059010093")
                .resetDate("20240108")
                .trxId("TRX01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .msgId("MSG01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .createdBy("006288")
                .terminalId("")
                .supervisorId("admin")
                .build();
        ActivateDormantResponse response = ActivateDormantResponse.builder()
                .responseCode("00")
                .build();

        Mockito.when(accountService.activateDormantAccount(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.ACTIVATE_DORMANT_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()));
    }

    @Test
    void activateDormantAccount_accountNoIsNull_then400() throws Exception {
        ActivateDormantRequest request = ActivateDormantRequest.builder()
                .branchCode("0002")
                .resetDate("20240108")
                .trxId("TRX01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .msgId("MSG01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .createdBy("006288")
                .terminalId("")
                .supervisorId("admin")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0" + ApiPath.ACTIVATE_DORMANT_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
