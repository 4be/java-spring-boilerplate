package com.vacant.account.services;


import com.vacant.account.constants.CoreConstant;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import com.vacant.account.exceptions.NonSnapException;
import com.vacant.account.feignclients.CoreClient;
import com.vacant.account.model.entity.CustomerPortfolioSummary;
import com.vacant.account.model.entity.EDWCustomerPortfolio;
import com.simas.account.model.in.*;
import com.simas.account.model.out.*;
import com.vacant.account.repositories.CustomerPortfolioRepository;
import com.vacant.account.repositories.CustomerPortfolioSummaryRepository;
import com.vacant.account.utils.JsonMapper;
import com.vacant.account.utils.ResponseConstructor;
import com.vacant.account.model.in.*;
import com.vacant.account.model.out.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {


    @InjectMocks
    private AccountService accountService;

    @Mock
    private CoreClient coreClient;

    @Mock
    private CustomerPortfolioRepository customerPortfolioRepository;

    @Mock
    private CustomerPortfolioSummaryRepository customerPortfolioSummaryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(coreClient);
    }

    @Test
    void getPartnerAccounts_Success() {
        var customerId = "12345";

        var coreRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(customerId).build();

        var coreAccount = CoreAccount.builder()
                .cifCode(customerId)
                .accountNumber("1234567890")
                .accountName("JHONY")
                .build();

        var coreDataMap = new HashMap<String, Object>();
        coreDataMap.put("1", JsonMapper.objectToMap(coreAccount));
        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .size("1")
                .data(coreDataMap)
                .build();

        when(coreClient.getSavingAccounts(coreRequest)).thenReturn(coreResponse);

        var response = accountService.getPartnerAccounts(customerId);

        Assertions.assertEquals(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS), response.getResponseCode());
        Assertions.assertEquals("1234567890", response.getAccounts().get(0).getAccountNo());

        verify(coreClient).getSavingAccounts(coreRequest);
    }

    @Test
    void getPartnerAccounts_NoDataFound() {
        var customerId = "12345";

        var coreRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(customerId).build();


        var coreErrorDataMap = new HashMap<String, String>();
        coreErrorDataMap.put("1", CoreConstant.NO_RECORD_FOUND_ERROR);
        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_FAILED_RESPONSE)
                .error(coreErrorDataMap)
                .build();

        when(coreClient.getSavingAccounts(coreRequest)).thenReturn(coreResponse);

        var response = accountService.getPartnerAccounts(customerId);

        Assertions.assertEquals(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS), response.getResponseCode());
        Assertions.assertTrue(response.getAccounts().isEmpty());

        verify(coreClient).getSavingAccounts(coreRequest);
    }

    @Test
    void getPartnerAccounts_ErrorFromCore() {
        var customerId = "12345";

        var coreRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(customerId).build();

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_FAILED_RESPONSE)
                .build();

        when(coreClient.getSavingAccounts(coreRequest)).thenReturn(coreResponse);

        try {
            accountService.getPartnerAccounts(customerId);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, be.getResponseCode());
        }

        verify(coreClient).getSavingAccounts(coreRequest);
    }

    @Test
    void getPartnerAccounts_TimeoutError() {
        var customerId = "12345";

        var coreRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(customerId).build();

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_TIMEOUT_RESPONSE)
                .build();

        when(coreClient.getSavingAccounts(coreRequest)).thenReturn(coreResponse);

        try {
            accountService.getPartnerAccounts(customerId);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.TIMEOUT, be.getResponseCode());
        }

        verify(coreClient).getSavingAccounts(coreRequest);
    }

    @Test
    void createAccount_Success() {
        var referenceNo = UUID.randomUUID().toString();
        var request = CreateAccountRequest.builder()
                .cif("12345")
                .partnerReferenceNo(UUID.randomUUID().toString())
                .additionalInfo(CreateAccountAdditionalInfo.builder()
                        .category("6002").build())
                .build();

        var dataMap = new HashMap<String, String>();
        dataMap.put("responseHeader", "0017077929/RFR232340314956753.00/1");
        dataMap.put("OPENING.DATE:1:1", "20230818");


        var coreRequest = CoreCreateAccountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CREATE_ACCOUNT)
                .options(CoreConstant.CORE_OPTIONS_CREATE_ACCOUNT)
                .productType(request.getAdditionalInfo().getCategory())
                .cif(request.getCif())
                .currencyCode(CoreConstant.CURRENCY_CODE)
                .branchCode(CoreConstant.DEFAULT_BRANCH)
                .channelReference(request.getPartnerReferenceNo())
                .umgReference(request.getPartnerReferenceNo())
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode(CoreConstant.CORE_SUCCESS_RESPONSE_CODE)
                .count("1")
                .data(List.of(dataMap))
                .correlationId(referenceNo)
                .build();

        when(coreClient.createAccount(coreRequest)).thenReturn(coreResponse);

        var result = accountService.createAccount(request);

        Assertions.assertEquals(referenceNo, result.getReferenceNo());
        Assertions.assertEquals("0017077929", result.getAccountId());

        verify(coreClient).createAccount(coreRequest);
    }

    @Test
    void createAccount_ErrorFromCore() {
        var request = CreateAccountRequest.builder()
                .cif("12345")
                .partnerReferenceNo(UUID.randomUUID().toString())
                .additionalInfo(CreateAccountAdditionalInfo.builder()
                        .category("6002").build())
                .build();

        var coreRequest = CoreCreateAccountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CREATE_ACCOUNT)
                .options(CoreConstant.CORE_OPTIONS_CREATE_ACCOUNT)
                .productType(request.getAdditionalInfo().getCategory())
                .cif(request.getCif())
                .currencyCode(CoreConstant.CURRENCY_CODE)
                .branchCode(CoreConstant.DEFAULT_BRANCH)
                .channelReference(request.getPartnerReferenceNo())
                .umgReference(request.getPartnerReferenceNo())
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode("99")
                .build();

        when(coreClient.createAccount(coreRequest)).thenReturn(coreResponse);

        try {
            accountService.createAccount(request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, be.getResponseCode());
        }

        verify(coreClient).createAccount(coreRequest);
    }

    @Test
    void createAccount_DuplicatePartnerReferenceNo() {
        var request = CreateAccountRequest.builder()
                .cif("12345")
                .partnerReferenceNo(UUID.randomUUID().toString())
                .additionalInfo(CreateAccountAdditionalInfo.builder()
                        .category("6002").build())
                .build();

        var coreRequest = CoreCreateAccountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CREATE_ACCOUNT)
                .options(CoreConstant.CORE_OPTIONS_CREATE_ACCOUNT)
                .productType(request.getAdditionalInfo().getCategory())
                .cif(request.getCif())
                .currencyCode(CoreConstant.CURRENCY_CODE)
                .branchCode(CoreConstant.DEFAULT_BRANCH)
                .channelReference(request.getPartnerReferenceNo())
                .umgReference(request.getPartnerReferenceNo())
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode(CoreConstant.CORE_DUPLICATE_REFERENCE_NO_RESPONSE_CODE)
                .build();

        when(coreClient.createAccount(coreRequest)).thenReturn(coreResponse);

        try {
            accountService.createAccount(request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.DUPLICATE_PARTNER_REFERENCE_NO, be.getResponseCode());
        }

        verify(coreClient).createAccount(coreRequest);
    }

    @Test
    void createAccount_NoAccountIdFromCore() {
        var referenceNo = UUID.randomUUID().toString();
        var request = CreateAccountRequest.builder()
                .cif("12345")
                .partnerReferenceNo(UUID.randomUUID().toString())
                .additionalInfo(CreateAccountAdditionalInfo.builder()
                        .category("6002").build())
                .build();

        var dataMap = new HashMap<String, String>();
        dataMap.put("responseHeader", "MP23230NN8QV/RFR232340314956752.00/1");

        var coreRequest = CoreCreateAccountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CREATE_ACCOUNT)
                .options(CoreConstant.CORE_OPTIONS_CREATE_ACCOUNT)
                .productType(request.getAdditionalInfo().getCategory())
                .cif(request.getCif())
                .currencyCode(CoreConstant.CURRENCY_CODE)
                .branchCode(CoreConstant.DEFAULT_BRANCH)
                .channelReference(request.getPartnerReferenceNo())
                .umgReference(request.getPartnerReferenceNo())
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode(CoreConstant.CORE_SUCCESS_RESPONSE_CODE)
                .count("1")
                .data(List.of(dataMap))
                .correlationId(referenceNo)
                .build();
        when(coreClient.createAccount(coreRequest)).thenReturn(coreResponse);

        try {
            accountService.createAccount(request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, be.getResponseCode());
        }
        verify(coreClient).createAccount(coreRequest);
    }

    @Test
    void getAccountBalance_Success() {
        var referenceNo = UUID.randomUUID().toString();
        var partnerReferenceNo = UUID.randomUUID().toString();

        var request = BalanceInquiryRequest.builder()
                .accountNo("12345678")
                .partnerReferenceNo(partnerReferenceNo)
                .build();

        var coreGetAccountBalanceRequest = CoreGetAccountBalanceRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_BALANCE)
                .accountNo(request.getAccountNo())
                .build();

        var coreAccountBalance = CoreAccountBalance
                .builder()
                .accountNo(request.getAccountNo())
                .accountName("STEVEN")
                .accountType("Tabungan Simas Gold")
                .amount("1000000")
                .currency(CoreConstant.CURRENCY_CODE)
                .status("1")
                .build();
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("1", JsonMapper.objectToMap(coreAccountBalance));

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .size("1")
                .data(mapData)
                .correlationId(referenceNo)
                .build();

        when(coreClient.getAccountBalance(coreGetAccountBalanceRequest)).thenReturn(coreResponse);

        var result = accountService.getAccountBalance(request);

        Assertions.assertEquals(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS), result.getResponseCode());
        Assertions.assertEquals("STEVEN", result.getName());

        verify(coreClient).getAccountBalance(coreGetAccountBalanceRequest);
    }

    @Test
    void getAccountBalance_ErrorFromCore() {
        var partnerReferenceNo = UUID.randomUUID().toString();

        var request = BalanceInquiryRequest.builder()
                .accountNo("12345678")
                .partnerReferenceNo(partnerReferenceNo)
                .build();

        var coreGetAccountBalanceRequest = CoreGetAccountBalanceRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_BALANCE)
                .accountNo(request.getAccountNo())
                .build();

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_FAILED_RESPONSE)
                .build();

        when(coreClient.getAccountBalance(coreGetAccountBalanceRequest)).thenReturn(coreResponse);

        try {
            accountService.getAccountBalance(request);
        } catch (BusinessException be) {
            Assertions.assertEquals(be.getResponseCode(), ResponseCode.INTERNAL_SERVER_ERROR);
        }

        verify(coreClient).getAccountBalance(coreGetAccountBalanceRequest);
    }

    @Test
    void getAccountBalance_ErrorTimeoutFromCore() {
        var partnerReferenceNo = UUID.randomUUID().toString();

        var request = BalanceInquiryRequest.builder()
                .accountNo("12345678")
                .partnerReferenceNo(partnerReferenceNo)
                .build();

        var coreGetAccountBalanceRequest = CoreGetAccountBalanceRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_BALANCE)
                .accountNo(request.getAccountNo())
                .build();

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_TIMEOUT_RESPONSE)
                .build();

        when(coreClient.getAccountBalance(coreGetAccountBalanceRequest)).thenReturn(coreResponse);

        try {
            accountService.getAccountBalance(request);
        } catch (BusinessException be) {
            Assertions.assertEquals(be.getResponseCode(), ResponseCode.TIMEOUT);
        }

        verify(coreClient).getAccountBalance(coreGetAccountBalanceRequest);
    }

    @Test
    void getAccountBalance_InvalidAccount() {
        var referenceNo = UUID.randomUUID().toString();
        var partnerReferenceNo = UUID.randomUUID().toString();

        var request = BalanceInquiryRequest.builder()
                .accountNo("12345678")
                .partnerReferenceNo(partnerReferenceNo)
                .build();

        var coreGetAccountBalanceRequest = CoreGetAccountBalanceRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_BALANCE)
                .accountNo(request.getAccountNo())
                .build();

        var coreAccountBalance = CoreAccountBalance
                .builder()
                .accountNo(request.getAccountNo())
                .status("1")
                .build();
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("1", JsonMapper.objectToMap(coreAccountBalance));

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .size("1")
                .data(mapData)
                .correlationId(referenceNo)
                .build();

        when(coreClient.getAccountBalance(coreGetAccountBalanceRequest)).thenReturn(coreResponse);

        try {
            accountService.getAccountBalance(request);
        } catch (BusinessException be) {
            Assertions.assertEquals(be.getResponseCode(), ResponseCode.INVALID_ACCOUNT);
        }

        verify(coreClient).getAccountBalance(coreGetAccountBalanceRequest);
    }

    @Test
    void checkAccounts_AccountsAreValid() {
        CheckAccountsRequest request = CheckAccountsRequest.builder()
                .cif("12345")
                .accountIds(List.of("1234567890"))
                .build();

        var coreRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(request.getCif()).build();

        var coreAccount = CoreAccount.builder()
                .cifCode(request.getCif())
                .accountNumber("1234567890")
                .accountName("JHONY")
                .build();

        var coreDataMap = new HashMap<String, Object>();
        coreDataMap.put("1", JsonMapper.objectToMap(coreAccount));
        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .size("1")
                .data(coreDataMap)
                .build();

        when(coreClient.getSavingAccounts(coreRequest)).thenReturn(coreResponse);

        CheckAccountsResponse result = accountService.checkAccounts(request);

        Assertions.assertEquals(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS), result.getResponseCode());
        Assertions.assertTrue(result.getAccountsAreValid());

        verify(coreClient).getSavingAccounts(coreRequest);
    }

    @Test
    void checkAccounts_AccountsAreInvalid() {
        CheckAccountsRequest request = CheckAccountsRequest.builder()
                .cif("12345")
                .accountIds(List.of("1234567890", "11"))
                .build();

        var coreRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(request.getCif()).build();

        var coreAccount = CoreAccount.builder()
                .cifCode(request.getCif())
                .accountNumber("1234567890")
                .accountName("JHONY")
                .build();

        var coreAccount2 = CoreAccount.builder()
                .cifCode(request.getCif())
                .accountNumber("098765454")
                .accountName("YES PAPA")
                .build();

        var coreDataMap = new HashMap<String, Object>();
        coreDataMap.put("1", JsonMapper.objectToMap(coreAccount));
        coreDataMap.put("2", JsonMapper.objectToMap(coreAccount2));
        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .size("2")
                .data(coreDataMap)
                .build();

        when(coreClient.getSavingAccounts(coreRequest)).thenReturn(coreResponse);

        CheckAccountsResponse result = accountService.checkAccounts(request);

        Assertions.assertEquals(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS), result.getResponseCode());
        Assertions.assertFalse(result.getAccountsAreValid());

        verify(coreClient).getSavingAccounts(coreRequest);
    }

    @Test
    void checkAccounts_DataNotFound() {
        CheckAccountsRequest request = CheckAccountsRequest.builder()
                .cif("12345")
                .accountIds(List.of("1234567890"))
                .build();

        var coreRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(request.getCif()).build();

        var coreErrorDataMap = new HashMap<String, String>();
        coreErrorDataMap.put("1", CoreConstant.NO_RECORD_FOUND_ERROR);
        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_FAILED_RESPONSE)
                .error(coreErrorDataMap)
                .build();

        when(coreClient.getSavingAccounts(coreRequest)).thenReturn(coreResponse);

        try {
            accountService.checkAccounts(request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.DATA_NOT_FOUND, be.getResponseCode());
        }

        verify(coreClient).getSavingAccounts(coreRequest);
    }

    @Test
    void waiveCharge_success() {
        WaiveChargeRequest waiveChargeRequest = WaiveChargeRequest.builder()
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode("00")
                .rawResponse("A-0059030973/KNV240115026838389.00/1,IC.CHG.PRODUCT:1:1=BALREQCHG,CAL.STEP.PERIOD:1:1=M,CHRG.FREQUENCY:1:1=20240205M0105,WAIVE.CHARGE:1:1=NO,CHRG.EFF.DATE:1:1=20090401,CHANNEL.REF.NUM:1:1=TRXd5d9ee7b-152e-4982-b4e1-b5d6c3da41,UMG.TERM.ID:1:1=PEGA-018939,UMG.AUTH.ID:1:1=admin,UMG.OPERATOR.ID:1:1=018939,UMG.UNIQUE.ID:1:1=MSGd5d9ee7b-152e-4982-b4e1-b5d6c3da5335,SM.WAIVE.CHARGE:1:1=Y,CURR.NO:1:1=38,INPUTTER:1:1=50268_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401111039,AUTHORISER:1:1=50268_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .build();

        when(coreClient.waiveCharge(any())).thenReturn(coreResponse);

        var result = accountService.waiveCharge(waiveChargeRequest);

        Assertions.assertEquals("00", result.getResponseCode());

        verify(coreClient).waiveCharge(any());
    }

    @Test
    void closeAccount_success() {
        CloseAccountRequest closeAccountRequest = CloseAccountRequest.builder()
                .build();

        var coreResponse = CoreRawResponse.builder()
                .rawResponse("0054898398/KNV240095039351383.00/1,CAPITAL.DATE:1:1=20240109,OPEN.ACTUAL.BAL:1:1=0,OPEN.CLEARED.BAL:1:1=0,ONLINE.ACTUAL.BAL:1:1=500000,ONLINE.CLEARED.BAL:1:1=500000,STANDING.ORDERS:1:1=NO,UNCLEARED.ENTRIES:1:1=NO,TOTAL.PENDING.DR:1:1=0,TOTAL.PENDING.CHG:1:1=0,TOTAL.PENDING.TAX:1:1=0,CURRENCY:1:1=IDR,SETTLEMENT.ACCT:1:1=0059026027,CHEQUES.OS:1:1=NO,BANK.CARDS:1:1=NO,CC.CHGS.OS:1:1=0,ACCT.LIQU.CURRENCY:1:1=IDR,POSTING.RESTRICT:1:1=95,TOTAL.ACC.AMT:1:1=499970,CHARGEABLE.AMT:1:1=500000,CLO.CHARGE.TYPE:1:1=ACCLSCURR,CLO.CHARGE.AMT:1:1=30.00,CLO.CHARGE.POSTED:1:1=YES,CAP.INTEREST:1:1=YES,CHANNEL.REF.NUM:1:1=TRX-20231221141023-100319,UMG.TERM.ID:1:1=PEGA-005418,UMG.AUTH.ID:1:1=admin,UMG.OPERATOR.ID:1:1=005418,UMG.UNIQUE.ID:1:1=MSG-20231221141023-100319,CLOSE.ONLINE:1:1=Y,RESERVED.1:1:1=204635039351383.02,RESERVED.1:2:1=1-2,RESERVED.1:3:1=ID0010168,RESERVED.1:4:1=204635039351383.03,RESERVED.1:5:1=1-2,OVERRIDE:1:1=ACL.ENTRIES.POSTED.TODAY}CALCULATED INTEREST MAY BE INACCURATE - ENTRIES POSTED TODAY,OVERRIDE:2:1=WITHDRAWL.LT.MIN.BAL,OVERRIDE:3:1=POSTING.RESTRICT}Account & - &{0054898398}Closed Member/Client Request{IDR{499970{0054898398{2299924{178{{95,CURR.NO:1:1=1,INPUTTER:1:1=50393_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401091416,AUTHORISER:1:1=50393_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010001,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .build();

        when(coreClient.closeAccount(any())).thenReturn(coreResponse);

        var result = accountService.closeAccount(closeAccountRequest);

        Assertions.assertEquals("00", result.getResponseCode());

        verify(coreClient).closeAccount(any());
    }

    @Test
    void creditInterest_success() {
        CreditInterestRequest creditInterestRequest = CreditInterestRequest.builder()
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode("00")
                .rawResponse("0000000345-20240109/KNV240098155759444.00/1,INTEREST.DAY.BASIS:1:1=E,TAX.KEY:1:1=11,CR.BALANCE.TYPE:1:1=DAILY,CR.CALCUL.TYPE:1:1=LEVEL,CR.INT.RATE:1:1=3,CURR.NO:1:1=4,INPUTTER:1:1=81557_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401091630,AUTHORISER:1:1=81557_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010001,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .build();

        when(coreClient.creditInterest(any())).thenReturn(coreResponse);

        var result = accountService.creditInterest(creditInterestRequest);

        Assertions.assertEquals("00", result.getResponseCode());

        verify(coreClient).creditInterest(any());
    }

    @Test
    void overrideLockAmount_Success() {
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
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("ACLK24008NWM6V/KNV240082303038493.00/1,ACCOUNT.NUMBER:1:1=0059010093,DESCRIPTION:1:1=SID01.06M,FROM.DATE:1:1=20240108,TO.DATE:1:1=20240612,LOCKED.AMOUNT:1:1=1000000.00,CHANNEL.REF.NUM:1:1=TRX0323c1f1-66d5-4691-82c8-c4fd69518632,UMG.AUTH.ID:1:1=admin,UMG.OPERATOR.ID:1:1=ODSP Staff,UMG.UNIQUE.ID:1:1=MSG0323c1f1-66d5-4691-82c8-c4fd69518632,TXN.SIGN:1:1=DR,CURR.NO:1:1=1,INPUTTER:1:1=23030_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401081041,AUTHORISER:1:1=23030_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.overrideLockAmount(ArgumentMatchers.any())).thenReturn(coreResponse);

        var result = accountService.overrideLockAmount(request);

        Assertions.assertEquals("00", result.getResponseCode());
        Mockito.verify(coreClient).overrideLockAmount(any(CoreLockAmountRequest.class));
    }

    @Test
    void overrideLockAmount_Error() {
        LockAmountRequest request = LockAmountRequest.builder()
                .branchCode("0002")
                .accountNumber("0059010093")
                .description("SID01.06M")
                .fromDate("2023/12/131")
                .toDate("20240612")
                .lockedAmount("1000000")
                .supervisorId("admin")
                .createdBy("ODSP Staff")
                .msgId("MSG0323c1f1-66d5-4691-82c8-c4fd69518631")
                .trxId("TRX0323c1f1-66d5-4691-82c8-c4fd69518631")
                .terminalId("PC_TELLER_1")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("ACLK24005071LD/KNV240053731159773.00/-1/NO,UMG.UNIQUE.ID:1:1=ALT KEY ALREADY ASSIGNED TO ACLK24005G6QMK REC IN THIS COMPANY,Y.ISO.RESPONSE:1:1=01")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.overrideLockAmount(ArgumentMatchers.any())).thenReturn(coreResponse);

        try {
            accountService.overrideLockAmount(request);
        } catch (NonSnapException e) {
            Assertions.assertEquals("01", e.getResponseCode());
            Assertions.assertEquals("ALT KEY ALREADY ASSIGNED TO ACLK24005G6QMK REC IN THIS COMPANY", e.getResponseMessage());
        }
        Mockito.verify(coreClient).overrideLockAmount(any(CoreLockAmountRequest.class));
    }

    @Test
    void activateDormantAccount_Success() {
        ActivateDormantRequest request = ActivateDormantRequest.builder()
                .branchCode("0002")
                .resetDate("20240108")
                .trxId("TRX01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .msgId("MSG01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .createdBy("006288")
                .terminalId("")
                .supervisorId("admin")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("0011652336/KNV240110274635030.00/1,RESET.DATE:1:1=20240108,CHANNEL.REF.NUM:1:1=TRX01a3fa17-9ff9-40a0-955d-828f22f2fa98,UMG.OPERATOR.ID:1:1=006288,UMG.UNIQUE.ID:1:1=MSG01a3fa17-9ff9-40a0-955d-828f22f2fa98,UMG.AUTH.ID:1:1=admin,CURR.NO:1:1=2,INPUTTER:1:1=2746_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401110943,AUTHORISER:1:1=2746_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010121,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.activateDormantAccount(ArgumentMatchers.any())).thenReturn(coreResponse);

        var result = accountService.activateDormantAccount(request);

        Assertions.assertEquals("00", result.getResponseCode());
        Mockito.verify(coreClient).activateDormantAccount(any(CoreActivateDormantRequest.class));
    }

    @Test
    void activateDormantAccount_FailAccountNotInactive() {
        ActivateDormantRequest request = ActivateDormantRequest.builder()
                .branchCode("0002")
                .resetDate("20240108")
                .trxId("TRX01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .msgId("MSG01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .createdBy("006288")
                .terminalId("")
                .supervisorId("admin")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("0011652336/KNV240117565435460.00/-1/NO,@ID:1:1=ACCOUNT NOT INACTIVE,Y.ISO.RESPONSE:1:1=01")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.activateDormantAccount(ArgumentMatchers.any())).thenReturn(coreResponse);

        try {
            accountService.activateDormantAccount(request);
        } catch (NonSnapException e) {
            Assertions.assertEquals("01", e.getResponseCode());
            Assertions.assertEquals("ACCOUNT NOT INACTIVE", e.getResponseMessage());
        }
        Mockito.verify(coreClient).activateDormantAccount(any(CoreActivateDormantRequest.class));
    }

    @Test
    void activateDormantAccount_FailDataNotChanged() {
        ActivateDormantRequest request = ActivateDormantRequest.builder()
                .branchCode("0002")
                .resetDate("20240108")
                .trxId("TRX01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .msgId("MSG01a3fa17-9ff9-40a0-955d-828f22f2fa97")
                .createdBy("006288")
                .terminalId("")
                .supervisorId("admin")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("0000160997-20240111/KNV240111678534917.00/-1/NO,LIVE RECORD NOT CHANGED,Y.ISO.RESPONSE:1:1=01")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.activateDormantAccount(ArgumentMatchers.any())).thenReturn(coreResponse);

        try {
            accountService.activateDormantAccount(request);
        } catch (NonSnapException e) {
            Assertions.assertEquals("01", e.getResponseCode());
            Assertions.assertEquals("LIVE RECORD NOT CHANGED", e.getResponseMessage());
        }
        Mockito.verify(coreClient).activateDormantAccount(any(CoreActivateDormantRequest.class));
    }

    @Test
    void debitInterest_Success() {
        DebitInterestRequest request = DebitInterestRequest.builder()
                .branchCode("0018")
                .accountNumber("0000160997")
                .chargesKey("99")
                .interestDayBasis("B")
                .drBalanceType("DAILY")
                .drIntRate1("8.5")
                .drIntRate2("48.00")
                .drLimitAmount1("600000000")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("0000160997-20240111/KNV240110274634349.00/1,CHARGE.KEY:1:1=99,INTEREST.DAY.BASIS:1:1=B,DR.BALANCE.TYPE:1:1=DAILY,DR.CALCUL.TYPE:1:1=BAND,DR.INT.RATE:1:1=9.5,DR.INT.RATE:2:1=48,DR.LIMIT.AMT:1:1=700000000.00,APR.REQUIRED:1:1=NO,CURR.NO:1:1=3,INPUTTER:1:1=2746_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401110932,AUTHORISER:1:1=2746_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010018,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .correlationId("UMG-DYG9S8-db7484d50938491892117b3073469537-253")
                .build();
        Mockito.when(coreClient.coreDebitInterest(ArgumentMatchers.any())).thenReturn(coreResponse);

        var result = accountService.debitInterest(request);

        Assertions.assertEquals("00", result.getResponseCode());
        Mockito.verify(coreClient).coreDebitInterest(any(CoreDebitInterestRequest.class));
    }

    @Test
    void accountCreationSaving_success() {
        AccountCreationSavingRequest accountCreationSavingRequest = AccountCreationSavingRequest.builder()
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode("00")
                .rawResponse("0054898894/KNV240119214349260.00/1,CUSTOMER:1:1=4927105,CATEGORY:1:1=6005,ACCOUNT.TITLE.1:1:1=NITA HILDAYANTI,SHORT.TITLE:1:1=NITA HILDAYANTI,POSITION.TYPE:1:1=TR,CURRENCY:1:1=IDR,CURRENCY.MARKET:1:1=1,ACCOUNT.OFFICER:1:1=2,POSTING.RESTRICT:1:1=4,CLASSIFICATION:1:1=1,SM.GENDER:1:1=F,EMPLYRS.CODE:1:1=37,INS.Y.N:1:1=Y,SA.PROD.TYPE:1:1=21,PAYROLL.ACCT:1:1=N,E.MAIL.FLAG:1:1=Y,ADDRESS.FLAG:1:1=Y,PROD.TYPE:1:1=021-SA,CHARACTERISTIC:1:1=9,INTEREST.TYPE:1:1=2,CHANNEL.REF.NUM:1:1=TRXc4ba143f-73fb-407c-83e2-12cdfd2ab575,UMG.TERM.ID:1:1=PEGA-QA001,UMG.AUTH.ID:1:1=admin,UMG.AUTH.ID:2:1=admin,UMG.OPERATOR.ID:1:1=QA001,UMG.UNIQUE.ID:1:1=MSGc4ba143f-73fb-407c-83e2-12cdfd2ab579,AC.MEASURE.CAT:1:1=5,PASSBOOK.FLAG:1:1=Y,BRANCH.UPDATE:1:1=ID0010002,CONDITION.GROUP:1:1=25,PASSBOOK:1:1=NO,OPENING.DATE:1:1=20240111,OPEN.CATEGORY:1:1=6005,CHARGE.CCY:1:1=IDR,CHARGE.MKT:1:1=1,INTEREST.CCY:1:1=IDR,INTEREST.MKT:1:1=1,ALT.ACCT.TYPE:1:1=SHINTA,ALLOW.NETTING:1:1=NO,CURR.NO:1:1=1,INPUTTER:1:1=21743_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401111341,AUTHORISER:1:1=21743_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .build();

        when(coreClient.accountCreationSaving(any())).thenReturn(coreResponse);

        var result = accountService.accountCreationSaving(accountCreationSavingRequest);

        Assertions.assertEquals("00", result.getResponseCode());

        verify(coreClient).accountCreationSaving(any());
    }

    @Test
    void accountMaintenanceSaving_success() {
        AccountMaintenanceSavingRequest accountMaintenanceSavingRequest = AccountMaintenanceSavingRequest.builder()
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode("00")
                .rawResponse("0059031678/KNV240128430034318.00/1,CUSTOMER:1:1=4980578,CATEGORY:1:1=6002,ACCOUNT.TITLE.1:1:1=HANDI PUTRA TJIOE,SHORT.TITLE:1:1=HANDI PUTRA TJIOE,POSITION.TYPE:1:1=TR,CURRENCY:1:1=IDR,CURRENCY.MARKET:1:1=1,ACCOUNT.OFFICER:1:1=2,POSTING.RESTRICT:1:1=13,CLASSIFICATION:1:1=1,SM.GENDER:1:1=M,EMPLOYEE.NO:1:1=020924,EMPLYRS.CODE:1:1=99,INS.Y.N:1:1=Y,SA.PROD.TYPE:1:1=21,PAYROLL.ACCT:1:1=N,E.MAIL.FLAG:1:1=Y,ADDRESS.FLAG:1:1=Y,PROD.TYPE:1:1=021-SA,CHARACTERISTIC:1:1=9,INTEREST.TYPE:1:1=2,CHANNEL.REF.NUM:1:1=TRXcf200bac-6839-409a-8be5-d2de5fd090e4,UMG.TERM.ID:1:1=PEGA-020922,UMG.AUTH.ID:1:1=admin,UMG.AUTH.ID:2:1=admin,UMG.OPERATOR.ID:1:1=020922,UMG.UNIQUE.ID:1:1=MSGcf200bac-6839-409a-8be5-d2de5fd090e5,AC.MEASURE.CAT:1:1=5,PASSBOOK.FLAG:1:1=Y,BRANCH.UPDATE:1:1=ID0010002,CONDITION.GROUP:1:1=23,PASSBOOK:1:1=NO,OPENING.DATE:1:1=20240109,OPEN.CATEGORY:1:1=6002,CHARGE.CCY:1:1=IDR,CHARGE.MKT:1:1=1,INTEREST.CCY:1:1=IDR,INTEREST.MKT:1:1=1,ALT.ACCT.TYPE:1:1=SHINTA,ALLOW.NETTING:1:1=NO,CURR.NO:1:1=3,INPUTTER:1:1=84300_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401120931,AUTHORISER:1:1=84300_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .build();

        when(coreClient.accountMaintenanceSaving(any())).thenReturn(coreResponse);

        var result = accountService.accountMaintenanceSaving(accountMaintenanceSavingRequest);

        Assertions.assertEquals("00", result.getResponseCode());

        verify(coreClient).accountMaintenanceSaving(any());
    }

    @Test
    void accountDetailByCif_success() {
        var customerId = "12345";
        var partnerReferenceNo = "127512";

        var coreRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(customerId)
                .build();

        LinkedList listOfCoreAccountDetail = new LinkedList();
        AccountDetailByCif accountDetail = AccountDetailByCif.builder()
                .custNo("4918907")
                .accountNumber("0054843615")
                .name("SUWARNA")
                .accountType("6002")
                .productType("Tabungan Simas Gold")
                .bank("PT BANK SINARMAS")
                .bankBranch("ID0010121")
                .currency("IDR")
                .bankCode("ID0010121")
                .jointNotes("")
                .merchantCode("")
                .workingBalance("16681934")
                .availableBalance("16581934")
                .descriptionInstId("")
                .dormant("")
                .locked("0")
                .minimumBalance("20000.00")
                .build();

        var coreDataMap = new HashMap<String, Object>();
        coreDataMap.put("1", JsonMapper.objectToMap(accountDetail));
        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .responseCode("00")
                .size("1")
                .data(coreDataMap)
                .build();

        when(coreClient.getSavingAccounts(coreRequest)).thenReturn(coreResponse);
        AccountDetailByCifResponse result = accountService.accountDetailByCif(customerId, partnerReferenceNo);

        Assertions.assertEquals(1, result.getData().size());

        verify(coreClient).getSavingAccounts(any());
    }

    @Test
    void inquiryLockAmount_success() {
        var accountNumber = "0059010093";
        var partnerReferenceNo = "41414";

        var coreRequest = CoreInquiryLockAmountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_INQUIRY_LOCK_AMOUNT)
                .transactionId(CoreConstant.CORE_TRANSACTION_ID_INQUIRY_LOCK_AMOUNT)
                .accountNumber(accountNumber)
                .build();

        var inquiryLockAmountResponse = InquiryLockAmount.builder()
                .lockedId("ACLK23347DN66P")
                .accountNo("0059010093")
                .lockedCode("SID01.06M")
                .lockedDescription("PROGRAM SIDU 06 BULAN")
                .lockedAmount("1000000.00")
                .fromDate("13 DEC 2023")
                .toDate("12 JUN 2024")
                .company("ID0010002")
                .build();

        var coreDataMap = new HashMap<String, Object>();
        coreDataMap.put("1", JsonMapper.objectToMap(inquiryLockAmountResponse));
        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .responseCode("00")
                .size("1")
                .data(coreDataMap)
                .build();

        when(coreClient.inquiryLockAmount(coreRequest)).thenReturn(coreResponse);
        InquiryLockAmountResponse result = accountService.inquiryLockAmount(accountNumber, partnerReferenceNo);

        Assertions.assertEquals(1, result.getData().size());

        verify(coreClient).inquiryLockAmount(any());
    }

    @Test
    void customerPortfolio_success() {
        var cifNumber = "0000";
        var partnerReferenceNo = "7793";
        var customer = EDWCustomerPortfolio.builder()
                .cifNumber("000")
                .build();

        when(customerPortfolioRepository.findVCustomerPortfolioByCifNumberInBancassuranceAndOrReksadana(cifNumber)).thenReturn(List.of(customer));
        CustomerPortfolioResponse listOfCustomerPortfolio = accountService.customerPortfolio(cifNumber, partnerReferenceNo);

        Assertions.assertEquals(1, listOfCustomerPortfolio.getData().size());

        verify(customerPortfolioRepository).findVCustomerPortfolioByCifNumberInBancassuranceAndOrReksadana(cifNumber);
    }

    @Test
    void accountCreationOverdraft_success() {
        AccountCreationOverdraftRequest accountCreationOverdraftRequest = AccountCreationOverdraftRequest.builder()
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode("00")
                .rawResponse("0054900066/KNV240156852470006.00/1,CUSTOMER:1:1=4926673,CATEGORY:1:1=1050,ACCOUNT.TITLE.1:1:1=ASDF,SHORT.TITLE:1:1=ASDF,POSITION.TYPE:1:1=TR,CURRENCY:1:1=IDR,CURRENCY.MARKET:1:1=1,ACCOUNT.OFFICER:1:1=2,SM.GENDER:1:1=M,EMPLOYEE.NO:1:1=1,EMPLYRS.CODE:1:1=99,CA.PROD.TYPE:1:1=1,PAYROLL.ACCT:1:1=N,UNUSED.LOAN:1:1=Committed,E.MAIL.FLAG:1:1=Y,ADDRESS.FLAG:1:1=Y,PROD.TYPE:1:1=001-CA,START.L.DATE:1:1=20240112,CHANNEL.REF.NUM:1:1=TRX05398513-bf5f-486d-90f3-53b723afaa7b,UMG.TERM.ID:1:1=PEGA-022044,UMG.AUTH.ID:1:1=admin,UMG.AUTH.ID:2:1=admin,UMG.OPERATOR.ID:1:1=022044,UMG.UNIQUE.ID:1:1=MSG05398513-bf5f-486d-90f3-53b723afaa9b,CONDITION.GROUP:1:1=15,PASSBOOK:1:1=NO,OPENING.DATE:1:1=20240115,OPEN.CATEGORY:1:1=1050,CHARGE.CCY:1:1=IDR,CHARGE.MKT:1:1=1,INTEREST.CCY:1:1=IDR,INTEREST.MKT:1:1=1,ALT.ACCT.TYPE:1:1=SHINTA,ALLOW.NETTING:1:1=NO,LIQUIDATION.MODE:1:1=SEMI-AUTOMATIC,SINGLE.LIMIT:1:1=Y,CURR.NO:1:1=1,INPUTTER:1:1=68524_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401151926,AUTHORISER:1:1=68524_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .build();

        when(coreClient.accountCreationOverdraft(any())).thenReturn(coreResponse);

        var result = accountService.accountCreationOverdraft(accountCreationOverdraftRequest);

        Assertions.assertEquals("00", result.getResponseCode());

        verify(coreClient).accountCreationOverdraft(any());
    }

    @Test
    void createAccountGiro_Success() {
        CreateAccountGiroRequest request = CreateAccountGiroRequest.builder()
                .acMeasureCat("5")
                .prodType("001-CA")
                .smintType("2")
                .accountTitle("GLOW INC2")
                .cityTown("0392")
                .corespondentName("GLOW INC2")
                .postcode("14340")
                .province("DKI Jakarta")
                .sameCifAddress("Y")
                .sameCifEmail("Y")
                .streetName1("ASD")
                .subDistrict("TANJUNG PRIOK")
                .village("PAPANGGO")
                .branchCode("0002")
                .createdBy("006288")
                .supervisorId("admin")
                .category("1")
                .cifNo("4925302")
                .currency("IDR")
                .msgId("MSG26f92002-fbf4-4fa0-ba4a-865abeba7c47")
                .officerCode("2")
                .openDate("2024/01/09")
                .productType("1000")
                .supervisorId2("admin")
                .terminalId("PEGA-006288")
                .trxId("TRX26f92002-fbf4-4fa0-ba4a-865abeba7c47")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("0054900333/KNV240163656852804.00/1,CUSTOMER:1:1=4925302,CATEGORY:1:1=1000,ACCOUNT.TITLE.1:1:1=GLOW INC2,SHORT.TITLE:1:1=GLOW INC2,POSITION.TYPE:1:1=TR,CURRENCY:1:1=IDR,CURRENCY.MARKET:1:1=1,ACCOUNT.OFFICER:1:1=2,CA.PROD.TYPE:1:1=1,PAYROLL.ACCT:1:1=N,E.MAIL.FLAG:1:1=Y,ADDRESS.FLAG:1:1=Y,PROD.TYPE:1:1=001-CA,INTEREST.TYPE:1:1=2,CHANNEL.REF.NUM:1:1=TRX26f92002-fbf4-4fa0-ba4a-865abeba7c48,UMG.TERM.ID:1:1=PEGA-006288,UMG.AUTH.ID:1:1=admin,UMG.AUTH.ID:2:1=admin,UMG.OPERATOR.ID:1:1=006288,UMG.UNIQUE.ID:1:1=MSG26f92002-fbf4-4fa0-ba4a-865abeba7c48,AC.MEASURE.CAT:1:1=5,CONDITION.GROUP:1:1=11,PASSBOOK:1:1=NO,OPENING.DATE:1:1=20240116,OPEN.CATEGORY:1:1=1000,CHARGE.CCY:1:1=IDR,CHARGE.MKT:1:1=1,INTEREST.CCY:1:1=IDR,INTEREST.MKT:1:1=1,ALT.ACCT.TYPE:1:1=SHINTA,ALLOW.NETTING:1:1=NO,CURR.NO:1:1=1,INPUTTER:1:1=36568_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401161440,AUTHORISER:1:1=36568_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.createAccountGiro(ArgumentMatchers.any())).thenReturn(coreResponse);

        var result = accountService.createAccountGiro(request);

        Assertions.assertEquals("00", result.getResponseCode());
        Mockito.verify(coreClient).createAccountGiro(any(CoreCreateAccountGiroRequest.class));
    }

    @Test
    void createAccountGiro_Error_AccountNumberIsFilled() {
        CreateAccountGiroRequest request = CreateAccountGiroRequest.builder()
                .acMeasureCat("5")
                .prodType("001-CA")
                .smintType("2")
                .accountNumber("000023")
                .accountTitle("GLOW INC2")
                .cityTown("0392")
                .corespondentName("GLOW INC2")
                .postcode("14340")
                .province("DKI Jakarta")
                .sameCifAddress("Y")
                .sameCifEmail("Y")
                .streetName1("ASD")
                .subDistrict("TANJUNG PRIOK")
                .village("PAPANGGO")
                .branchCode("0002")
                .createdBy("006288")
                .supervisorId("admin")
                .category("1")
                .cifNo("4925302")
                .currency("IDR")
                .msgId("MSG26f92002-fbf4-4fa0-ba4a-865abeba7c47")
                .officerCode("2")
                .openDate("2024/01/09")
                .productType("1000")
                .supervisorId2("admin")
                .terminalId("PEGA-006288")
                .trxId("TRX26f92002-fbf4-4fa0-ba4a-865abeba7c47")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("0054900333/KNV240163656852804.00/1,CUSTOMER:1:1=4925302,CATEGORY:1:1=1000,ACCOUNT.TITLE.1:1:1=GLOW INC2,SHORT.TITLE:1:1=GLOW INC2,POSITION.TYPE:1:1=TR,CURRENCY:1:1=IDR,CURRENCY.MARKET:1:1=1,ACCOUNT.OFFICER:1:1=2,CA.PROD.TYPE:1:1=1,PAYROLL.ACCT:1:1=N,E.MAIL.FLAG:1:1=Y,ADDRESS.FLAG:1:1=Y,PROD.TYPE:1:1=001-CA,INTEREST.TYPE:1:1=2,CHANNEL.REF.NUM:1:1=TRX26f92002-fbf4-4fa0-ba4a-865abeba7c48,UMG.TERM.ID:1:1=PEGA-006288,UMG.AUTH.ID:1:1=admin,UMG.AUTH.ID:2:1=admin,UMG.OPERATOR.ID:1:1=006288,UMG.UNIQUE.ID:1:1=MSG26f92002-fbf4-4fa0-ba4a-865abeba7c48,AC.MEASURE.CAT:1:1=5,CONDITION.GROUP:1:1=11,PASSBOOK:1:1=NO,OPENING.DATE:1:1=20240116,OPEN.CATEGORY:1:1=1000,CHARGE.CCY:1:1=IDR,CHARGE.MKT:1:1=1,INTEREST.CCY:1:1=IDR,INTEREST.MKT:1:1=1,ALT.ACCT.TYPE:1:1=SHINTA,ALLOW.NETTING:1:1=NO,CURR.NO:1:1=1,INPUTTER:1:1=36568_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401161440,AUTHORISER:1:1=36568_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.createAccountGiro(ArgumentMatchers.any())).thenReturn(coreResponse);

        try {
            accountService.createAccountGiro(request);
        } catch (NonSnapException e) {
            Assertions.assertEquals("01", e.getResponseCode());
            Assertions.assertEquals("Account number must be empty!", e.getResponseMessage());
        }
        Mockito.verify(coreClient, times(0)).createAccountGiro(any(CoreCreateAccountGiroRequest.class));
    }

    @Test
    void updateAccountGiro_Success() {
        UpdateAccountGiroRequest request = UpdateAccountGiroRequest.builder()
                .acMeasureCat("5")
                .prodType("001-CA")
                .smintType("2")
                .accountTitle("GLOW INC2")
                .accountNumber("000023")
                .cityTown("0392")
                .corespondentName("GLOW INC2")
                .postcode("14340")
                .province("DKI Jakarta")
                .sameCifAddress("Y")
                .sameCifEmail("Y")
                .streetName1("ASD")
                .subDistrict("TANJUNG PRIOK")
                .village("PAPANGGO")
                .branchCode("0002")
                .createdBy("006288")
                .supervisorId("admin")
                .category("1")
                .cifNo("4925302")
                .currency("IDR")
                .msgId("MSG26f92002-fbf4-4fa0-ba4a-865abeba7c47")
                .officerCode("2")
                .openDate("2024/01/09")
                .productType("1000")
                .supervisorId2("admin")
                .terminalId("PEGA-006288")
                .trxId("TRX26f92002-fbf4-4fa0-ba4a-865abeba7c47")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("0054900732/KNV240174731455525.00/1,CUSTOMER:1:1=4925302,CATEGORY:1:1=1000,ACCOUNT.TITLE.1:1:1=GLOW INC2,SHORT.TITLE:1:1=GLOW INC2,POSITION.TYPE:1:1=TR,CURRENCY:1:1=IDR,CURRENCY.MARKET:1:1=1,ACCOUNT.OFFICER:1:1=2,CA.PROD.TYPE:1:1=1,PAYROLL.ACCT:1:1=N,E.MAIL.FLAG:1:1=Y,ADDRESS.FLAG:1:1=Y,CHANNEL.REF.NUM:1:1=TRX26f92002-fbf4-4fa0-ba4a-865abeba7c58,UMG.TERM.ID:1:1=PEGA-006288,UMG.AUTH.ID:1:1=admin,UMG.AUTH.ID:2:1=admin,UMG.OPERATOR.ID:1:1=006288,UMG.UNIQUE.ID:1:1=MSG26f92002-fbf4-4fa0-ba4a-865abeba7c58,BRANCH.UPDATE:1:1=ID0010002,CONDITION.GROUP:1:1=11,PASSBOOK:1:1=NO,OPENING.DATE:1:1=20240117,OPEN.CATEGORY:1:1=1000,CHARGE.CCY:1:1=IDR,CHARGE.MKT:1:1=1,INTEREST.CCY:1:1=IDR,INTEREST.MKT:1:1=1,ALT.ACCT.TYPE:1:1=SHINTA,ALLOW.NETTING:1:1=NO,CURR.NO:1:1=1,INPUTTER:1:1=47314_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401171525,AUTHORISER:1:1=47314_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.updateAccountGiro(ArgumentMatchers.any())).thenReturn(coreResponse);

        var result = accountService.updateAccountGiro(request);

        Assertions.assertEquals("00", result.getResponseCode());
        Mockito.verify(coreClient).updateAccountGiro(any(CoreUpdateAccountGiroRequest.class));
    }

    @Test
    void updateAccountGiro_Error_AccountNumberIsNotFilled() {
        UpdateAccountGiroRequest request = UpdateAccountGiroRequest.builder()
                .acMeasureCat("5")
                .prodType("001-CA")
                .smintType("2")
                .accountTitle("GLOW INC2")
                .cityTown("0392")
                .corespondentName("GLOW INC2")
                .postcode("14340")
                .province("DKI Jakarta")
                .sameCifAddress("Y")
                .sameCifEmail("Y")
                .streetName1("ASD")
                .subDistrict("TANJUNG PRIOK")
                .village("PAPANGGO")
                .branchCode("0002")
                .createdBy("006288")
                .supervisorId("admin")
                .category("1")
                .cifNo("4925302")
                .currency("IDR")
                .msgId("MSG26f92002-fbf4-4fa0-ba4a-865abeba7c47")
                .officerCode("2")
                .openDate("2024/01/09")
                .productType("1000")
                .supervisorId2("admin")
                .terminalId("PEGA-006288")
                .trxId("TRX26f92002-fbf4-4fa0-ba4a-865abeba7c47")
                .build();
        CoreRawResponse coreResponse = CoreRawResponse.builder()
                .rawResponse("0054900333/KNV240163656852804.00/1,CUSTOMER:1:1=4925302,CATEGORY:1:1=1000,ACCOUNT.TITLE.1:1:1=GLOW INC2,SHORT.TITLE:1:1=GLOW INC2,POSITION.TYPE:1:1=TR,CURRENCY:1:1=IDR,CURRENCY.MARKET:1:1=1,ACCOUNT.OFFICER:1:1=2,CA.PROD.TYPE:1:1=1,PAYROLL.ACCT:1:1=N,E.MAIL.FLAG:1:1=Y,ADDRESS.FLAG:1:1=Y,PROD.TYPE:1:1=001-CA,INTEREST.TYPE:1:1=2,CHANNEL.REF.NUM:1:1=TRX26f92002-fbf4-4fa0-ba4a-865abeba7c48,UMG.TERM.ID:1:1=PEGA-006288,UMG.AUTH.ID:1:1=admin,UMG.AUTH.ID:2:1=admin,UMG.OPERATOR.ID:1:1=006288,UMG.UNIQUE.ID:1:1=MSG26f92002-fbf4-4fa0-ba4a-865abeba7c48,AC.MEASURE.CAT:1:1=5,CONDITION.GROUP:1:1=11,PASSBOOK:1:1=NO,OPENING.DATE:1:1=20240116,OPEN.CATEGORY:1:1=1000,CHARGE.CCY:1:1=IDR,CHARGE.MKT:1:1=1,INTEREST.CCY:1:1=IDR,INTEREST.MKT:1:1=1,ALT.ACCT.TYPE:1:1=SHINTA,ALLOW.NETTING:1:1=NO,CURR.NO:1:1=1,INPUTTER:1:1=36568_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401161440,AUTHORISER:1:1=36568_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .correlationId(request.getTrxId())
                .build();
        Mockito.when(coreClient.updateAccountGiro(ArgumentMatchers.any())).thenReturn(coreResponse);

        try {
            accountService.updateAccountGiro(request);
        } catch (NonSnapException e) {
            Assertions.assertEquals("01", e.getResponseCode());
            Assertions.assertEquals("Account number must not be empty!", e.getResponseMessage());
        }
        Mockito.verify(coreClient, times(0)).updateAccountGiro(any(CoreUpdateAccountGiroRequest.class));
    }

    @Test
    void accountMaintenanceOverdraft_success() {
        AccountMaintenanceOverdraftRequest accountMaintenanceOverdraftRequest = AccountMaintenanceOverdraftRequest.builder()
                .build();

        var coreResponse = CoreWriteOperationResponse.builder()
                .responseCode("00")
                .rawResponse("0054900066/KNV240156852470006.00/1,CUSTOMER:1:1=4926673,CATEGORY:1:1=1050,ACCOUNT.TITLE.1:1:1=ASDF,SHORT.TITLE:1:1=ASDF,POSITION.TYPE:1:1=TR,CURRENCY:1:1=IDR,CURRENCY.MARKET:1:1=1,ACCOUNT.OFFICER:1:1=2,SM.GENDER:1:1=M,EMPLOYEE.NO:1:1=1,EMPLYRS.CODE:1:1=99,CA.PROD.TYPE:1:1=1,PAYROLL.ACCT:1:1=N,UNUSED.LOAN:1:1=Committed,E.MAIL.FLAG:1:1=Y,ADDRESS.FLAG:1:1=Y,PROD.TYPE:1:1=001-CA,START.L.DATE:1:1=20240112,CHANNEL.REF.NUM:1:1=TRX05398513-bf5f-486d-90f3-53b723afaa7b,UMG.TERM.ID:1:1=PEGA-022044,UMG.AUTH.ID:1:1=admin,UMG.AUTH.ID:2:1=admin,UMG.OPERATOR.ID:1:1=022044,UMG.UNIQUE.ID:1:1=MSG05398513-bf5f-486d-90f3-53b723afaa9b,CONDITION.GROUP:1:1=15,PASSBOOK:1:1=NO,OPENING.DATE:1:1=20240115,OPEN.CATEGORY:1:1=1050,CHARGE.CCY:1:1=IDR,CHARGE.MKT:1:1=1,INTEREST.CCY:1:1=IDR,INTEREST.MKT:1:1=1,ALT.ACCT.TYPE:1:1=SHINTA,ALLOW.NETTING:1:1=NO,LIQUIDATION.MODE:1:1=SEMI-AUTOMATIC,SINGLE.LIMIT:1:1=Y,CURR.NO:1:1=1,INPUTTER:1:1=68524_UMG.USER1__OFS_UMG.OFS,DATE.TIME:1:1=2401151926,AUTHORISER:1:1=68524_UMG.USER1_OFS_UMG.OFS,CO.CODE:1:1=ID0010002,DEPT.CODE:1:1=1,Y.ISO.RESPONSE:1:1=00")
                .build();

        when(coreClient.accountMaintenanceOverdraft(any())).thenReturn(coreResponse);

        var result = accountService.accountMaintenanceOverdraft(accountMaintenanceOverdraftRequest);

        Assertions.assertEquals("00", result.getResponseCode());

        verify(coreClient).accountMaintenanceOverdraft(any());
    }

    @Test
    void customerPortfolioSummaryEOD_success() {
        var cifNumber = "0000";
        var partnerReferenceNo = "7793";
        var customer = CustomerPortfolioSummary.builder()
                .cifNumber("0000")
                .build();

        when(customerPortfolioSummaryRepository.findVCustomerPortfolioEODSummaryByCifNumber(cifNumber)).thenReturn(List.of(customer));
        CustomerPortfolioSummaryResponse listOfCustomerPortfolio = accountService.customerPortfolioEODSummary(cifNumber, partnerReferenceNo);

        Assertions.assertEquals(1, listOfCustomerPortfolio.getData().size());

        verify(customerPortfolioSummaryRepository).findVCustomerPortfolioEODSummaryByCifNumber(cifNumber);
    }

    @Test
    void customerPortfolioSummaryEOM_success() {
        var cifNumber = "0000";
        var partnerReferenceNo = "7793";
        var customer = CustomerPortfolioSummary.builder()
                .cifNumber("0000")
                .build();

        when(customerPortfolioSummaryRepository.findVCustomerPortfolioEOMSummaryByCifNumber(cifNumber)).thenReturn(List.of(customer));
        CustomerPortfolioSummaryResponse listOfCustomerPortfolio = accountService.customerPortfolioEOMSummary(cifNumber, partnerReferenceNo);

        Assertions.assertEquals(1, listOfCustomerPortfolio.getData().size());

        verify(customerPortfolioSummaryRepository).findVCustomerPortfolioEOMSummaryByCifNumber(cifNumber);
    }

    @Test
    void closeAccountAndContract_Success() {
        var request = CloseAccountContractRequest.builder()
                .closingFee("0")
                .currency("IDR")
                .accNo("0054906676")
                .accSettlement("0058985864")
                .uniqueId("CHH-00000001")
                .channelRefNum("MB-CA-49261735240000490")
                .authId("911669")
                .umgTermId("PEGA-006288")
                .umgOperatorId("006288")
                .merchantCode("")
                .build();
        var coreResponse = CoreMultiDataResponse.builder()
                .responseCode("00")
                .count("3")
                .data(List.of(new LinkedHashMap<>()))
                .rawResponse("")
                .build();
        Mockito.when(coreClient.closeAccountAndContract(ArgumentMatchers.any())).thenReturn(coreResponse);

        var result = accountService.closeAccountAndContract(request);

        Assertions.assertEquals("00", result.getResponseCode());
        Mockito.verify(coreClient).closeAccountAndContract(any(CoreCloseAccountContractRequest.class));
    }

    @Test
    void getDetailAndBalanceMultiCif_success() {
        var cif = "106245";
        var partnerReferenceNo = "0000001";
        var coreResponse = JsonMapper.jsonStringToObject("{\"size\":\"2\",\"responseMessage\":\"Success\",\"responseCode\":\"00\",\"rawResponse\":\",CUST.NO::CifCode/ACC.NO::AccountNumber/NAME::Name/ACCOUNT.TYPE::AccountType/PRODUCT.TYPE::ProductType/BANK::Bank/BANK.BRANCH::BankBranch/CURRENCY::Currency/BANK.CODE::BankCode/JOINT.NOTES::Joint Notes/MERCHANT.CODE::Merchant Code/WORKING.BAL::WorkingBalance/CURR.BAL::AvailableBalance/DESCRIPTION.INST::DescriptionInstId/INACTIV.MARKER::Dormant/LOCKED.AMOUNT::LockedAmount,\\\"106245\\\"\\t\\\"0054011768\\\"\\t\\\"WOVWUQJ SI\\\"\\t\\\"1010\\\"\\t\\\"Giro Sinarmas USD - Personal\\\"\\t\\\"PT BANK SINARMAS\\\"\\t\\\"ID0010001\\\"\\t\\\"USD\\\"\\t\\\"ID0010001\\\"\\t\\\"\\\"\\t\\\"\\\"\\t\\\"0\\\"\\t\\\"-10\\\"\\t\\\"\\\"\\t\\\"Y\\\"\\t\\\"\\\",\\\"106245\\\"\\t\\\"0000137577\\\"\\t\\\"WOVWUQJ SI\\\"\\t\\\"6002\\\"\\t\\\"Tabungan Simas Gold\\\"\\t\\\"PT BANK SINARMAS\\\"\\t\\\"ID0010012\\\"\\t\\\"IDR\\\"\\t\\\"ID0010012\\\"\\t\\\"\\\"\\t\\\"\\\"\\t\\\"2796365294781.61\\\"\\t\\\"2796365194781.61\\\"\\t\\\"\\\"\\t\\\"\\\"\\t\\\"\\\"\",\"data\":{\"1\":{\"CUST.NO::CifCode\":\"106245\",\"ACC.NO::AccountNumber\":\"0054011768\",\"NAME::Name\":\"WOVWUQJ SI\",\"ACCOUNT.TYPE::AccountType\":\"1010\",\"PRODUCT.TYPE::ProductType\":\"Giro Sinarmas USD - Personal\",\"BANK::Bank\":\"PT BANK SINARMAS\",\"BANK.BRANCH::BankBranch\":\"ID0010001\",\"CURRENCY::Currency\":\"USD\",\"BANK.CODE::BankCode\":\"ID0010001\",\"JOINT.NOTES::Joint Notes\":\"\",\"MERCHANT.CODE::Merchant Code\":\"\",\"WORKING.BAL::WorkingBalance\":\"0\",\"CURR.BAL::AvailableBalance\":\"-10\",\"DESCRIPTION.INST::DescriptionInstId\":\"\",\"INACTIV.MARKER::Dormant\":\"Y\",\"LOCKED.AMOUNT::LockedAmount\":\"\"},\"2\":{\"CUST.NO::CifCode\":\"106245\",\"ACC.NO::AccountNumber\":\"0000137577\",\"NAME::Name\":\"WOVWUQJ SI\",\"ACCOUNT.TYPE::AccountType\":\"6002\",\"PRODUCT.TYPE::ProductType\":\"Tabungan Simas Gold\",\"BANK::Bank\":\"PT BANK SINARMAS\",\"BANK.BRANCH::BankBranch\":\"ID0010012\",\"CURRENCY::Currency\":\"IDR\",\"BANK.CODE::BankCode\":\"ID0010012\",\"JOINT.NOTES::Joint Notes\":\"\",\"MERCHANT.CODE::Merchant Code\":\"\",\"WORKING.BAL::WorkingBalance\":\"2796365294781.61\",\"CURR.BAL::AvailableBalance\":\"2796365194781.61\",\"DESCRIPTION.INST::DescriptionInstId\":\"\",\"INACTIV.MARKER::Dormant\":\"\",\"LOCKED.AMOUNT::LockedAmount\":\"\"}},\"error\":null,\"correlationId\":\"UMG-K5XDBZ-f039278f31c14817972e6657e52e4b62-784\"}",CoreResponse.class);
        when(coreClient.getDetailAndBalanceMultiCif(any(CoreGetDetailAndBalanceMultiCifRequest.class))).thenReturn(coreResponse);
        var response = accountService.getDetailAndBalanceMultiCif(cif,partnerReferenceNo);

        Assertions.assertEquals("00", response.getResponseCode());

        verify(coreClient).getDetailAndBalanceMultiCif(any(CoreGetDetailAndBalanceMultiCifRequest.class));
    }

    @Test
    void getDetailAndBalanceMultiAccount_success() {
        var accountNo = "0000137577";
        var partnerReferenceNo = "0000001";
        var coreResponse = JsonMapper.jsonStringToObject("{\"size\":\"2\",\"responseMessage\":\"Success\",\"responseCode\":\"00\",\"rawResponse\":\",CUST.NO::CifCode/ACC.NO::AccountNumber/NAME::Name/ACCOUNT.TYPE::AccountType/PRODUCT.TYPE::ProductType/BANK::Bank/BANK.BRANCH::BankBranch/CURRENCY::Currency/BANK.CODE::BankCode/JOINT.NOTES::Joint Notes/MERCHANT.CODE::Merchant Code/WORKING.BAL::WorkingBalance/CURR.BAL::AvailableBalance/DESCRIPTION.INST::DescriptionInstId/INACTIV.MARKER::Dormant/LOCKED.AMOUNT::LockedAmount,\\\"106245\\\"\\t\\\"0054011768\\\"\\t\\\"WOVWUQJ SI\\\"\\t\\\"1010\\\"\\t\\\"Giro Sinarmas USD - Personal\\\"\\t\\\"PT BANK SINARMAS\\\"\\t\\\"ID0010001\\\"\\t\\\"USD\\\"\\t\\\"ID0010001\\\"\\t\\\"\\\"\\t\\\"\\\"\\t\\\"0\\\"\\t\\\"-10\\\"\\t\\\"\\\"\\t\\\"Y\\\"\\t\\\"\\\",\\\"106245\\\"\\t\\\"0000137577\\\"\\t\\\"WOVWUQJ SI\\\"\\t\\\"6002\\\"\\t\\\"Tabungan Simas Gold\\\"\\t\\\"PT BANK SINARMAS\\\"\\t\\\"ID0010012\\\"\\t\\\"IDR\\\"\\t\\\"ID0010012\\\"\\t\\\"\\\"\\t\\\"\\\"\\t\\\"2796365294781.61\\\"\\t\\\"2796365194781.61\\\"\\t\\\"\\\"\\t\\\"\\\"\\t\\\"\\\"\",\"data\":{\"1\":{\"CUST.NO::CifCode\":\"106245\",\"ACC.NO::AccountNumber\":\"0054011768\",\"NAME::Name\":\"WOVWUQJ SI\",\"ACCOUNT.TYPE::AccountType\":\"1010\",\"PRODUCT.TYPE::ProductType\":\"Giro Sinarmas USD - Personal\",\"BANK::Bank\":\"PT BANK SINARMAS\",\"BANK.BRANCH::BankBranch\":\"ID0010001\",\"CURRENCY::Currency\":\"USD\",\"BANK.CODE::BankCode\":\"ID0010001\",\"JOINT.NOTES::Joint Notes\":\"\",\"MERCHANT.CODE::Merchant Code\":\"\",\"WORKING.BAL::WorkingBalance\":\"0\",\"CURR.BAL::AvailableBalance\":\"-10\",\"DESCRIPTION.INST::DescriptionInstId\":\"\",\"INACTIV.MARKER::Dormant\":\"Y\",\"LOCKED.AMOUNT::LockedAmount\":\"\"},\"2\":{\"CUST.NO::CifCode\":\"106245\",\"ACC.NO::AccountNumber\":\"0000137577\",\"NAME::Name\":\"WOVWUQJ SI\",\"ACCOUNT.TYPE::AccountType\":\"6002\",\"PRODUCT.TYPE::ProductType\":\"Tabungan Simas Gold\",\"BANK::Bank\":\"PT BANK SINARMAS\",\"BANK.BRANCH::BankBranch\":\"ID0010012\",\"CURRENCY::Currency\":\"IDR\",\"BANK.CODE::BankCode\":\"ID0010012\",\"JOINT.NOTES::Joint Notes\":\"\",\"MERCHANT.CODE::Merchant Code\":\"\",\"WORKING.BAL::WorkingBalance\":\"2796365294781.61\",\"CURR.BAL::AvailableBalance\":\"2796365194781.61\",\"DESCRIPTION.INST::DescriptionInstId\":\"\",\"INACTIV.MARKER::Dormant\":\"\",\"LOCKED.AMOUNT::LockedAmount\":\"\"}},\"error\":null,\"correlationId\":\"UMG-K5XDBZ-f039278f31c14817972e6657e52e4b62-784\"}",CoreResponse.class);
        when(coreClient.getDetailAndBalanceMultiAccount(any(CoreGetDetailAndBalanceMultiAccountRequest.class))).thenReturn(coreResponse);
        var response = accountService.getDetailAndBalanceMultiAccount(accountNo,partnerReferenceNo);

        Assertions.assertEquals("00", response.getResponseCode());

        verify(coreClient).getDetailAndBalanceMultiAccount(any(CoreGetDetailAndBalanceMultiAccountRequest.class));
    }
}
