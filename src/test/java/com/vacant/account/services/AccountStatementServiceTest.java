package com.vacant.account.services;

import com.vacant.account.constants.CoreConstant;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import com.vacant.account.feignclients.CoreClient;
import com.vacant.account.model.in.BankStatementRequest;
import com.vacant.account.model.out.CoreAccountStatement;
import com.vacant.account.model.out.CoreGetAccountStatementRequest;
import com.vacant.account.model.out.CoreResponse;
import com.vacant.account.utils.DateUtil;
import com.vacant.account.utils.JsonMapper;
import com.vacant.account.utils.ResponseConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.*;


public class AccountStatementServiceTest {

    @InjectMocks
    private AccountStatementService accountStatementService;

    @Mock
    private CoreClient coreClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(coreClient);
    }

    @Test
    void getAccountStatement_Success() {
        var partnerId = UUID.randomUUID().toString();
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        var coreDateFormat = DateTimeFormatter.ofPattern(DateUtil.CORE_DATE_FORMAT);

        var coreGetAccountStatementRequest = CoreGetAccountStatementRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_STATEMENT)
                .accountNo(request.getAccountNo())
                .bookingDate(String.format("%s %s", LocalDate.of(2023, 6, 13).format(coreDateFormat), LocalDate.of(2023, 6, 13).format(coreDateFormat)))
                .build();

        var dataJson = "{\"Datetime::Datetime\":\"2023/06/13 09:05\",\"Description::Description\":\"Sales Transaction\",\"RefNo::RefNo\":\"FT231644QNKC\\\\EBK\",\"Currency::Currency\":\"IDR\",\"Amount::Amount\":\"9,508.00\",\"Balance::Balance\":\"1,217,739,013.00\",\"Narrative::Narrative\":\"QRfrom BROTOSKo\",\"STMT.ID::STMT.ID\":\"202536327632701.020001\",\"OriCurr::OriCurr\":\"IDR\",\"OriAmt::OriAmt\":\"9,508.00\"}";
        var coreData = new HashMap<String, Object>();
        coreData.put("1", JsonMapper.jsonStringToObject(dataJson, CoreAccountStatement.class));

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .size("1")
                .data(coreData)
                .build();


        when(coreClient.getAccountStatement(coreGetAccountStatementRequest)).thenReturn(coreResponse);

        var result = accountStatementService.getAccountStatement(partnerId, request);

        Assertions.assertEquals(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS), result.getResponseCode());
        Assertions.assertEquals("1", result.getTotalCreditEntries().getNumberOfEntries());
        Assertions.assertEquals("9508.00", result.getDetailData().get(0).getAmount().getValue());

        verify(coreClient).getAccountStatement(coreGetAccountStatementRequest);
    }

    @Test
    void getAccountStatement_ErrorInvalidDate_fromDateIsBiggerThanToDate() {
        var partnerId = UUID.randomUUID().toString();
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-14T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        try {
            accountStatementService.getAccountStatement(partnerId, request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.BAD_REQUEST.getCode(), be.getResponseCode().getCode());
        }
    }

    @Test
    void getAccountStatement_ErrorInvalidDate_moreThanOneMonthPeriod() {
        var partnerId = UUID.randomUUID().toString();
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-08-13T12:08:56+07:00")
                .build();

        try {
            accountStatementService.getAccountStatement(partnerId, request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.BAD_REQUEST.getCode(), be.getResponseCode().getCode());
        }
    }

    @Test
    void getAccountStatement_ErrorInvalidDate_olderThanOneYearBack() {
        var partnerId = UUID.randomUUID().toString();
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2022-06-13T12:08:56+07:00")
                .toDateTime("2022-08-13T12:08:56+07:00")
                .build();

        try {
            accountStatementService.getAccountStatement(partnerId, request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.BAD_REQUEST.getCode(), be.getResponseCode().getCode());
        }
    }

    @Test
    void getAccountStatement_ErrorFromCore() {
        var partnerId = UUID.randomUUID().toString();
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        var coreDateFormat = DateTimeFormatter.ofPattern(DateUtil.CORE_DATE_FORMAT);

        var coreGetAccountStatementRequest = CoreGetAccountStatementRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_STATEMENT)
                .accountNo(request.getAccountNo())
                .bookingDate(String.format("%s %s", LocalDate.of(2023, 6, 13).format(coreDateFormat), LocalDate.of(2023, 6, 13).format(coreDateFormat)))
                .build();

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_FAILED_RESPONSE)
                .build();


        when(coreClient.getAccountStatement(coreGetAccountStatementRequest)).thenReturn(coreResponse);

        try {
            accountStatementService.getAccountStatement(partnerId, request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), be.getResponseCode().getCode());
        }

        verify(coreClient).getAccountStatement(coreGetAccountStatementRequest);
    }

    @Test
    void getAccountStatement_ErrorInvalidAccount() {
        var partnerId = UUID.randomUUID().toString();
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        var coreDateFormat = DateTimeFormatter.ofPattern(DateUtil.CORE_DATE_FORMAT);

        var coreGetAccountStatementRequest = CoreGetAccountStatementRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_STATEMENT)
                .accountNo(request.getAccountNo())
                .bookingDate(String.format("%s %s", LocalDate.of(2023, 6, 13).format(coreDateFormat), LocalDate.of(2023, 6, 13).format(coreDateFormat)))
                .build();

        var dataJson = "{\"Datetime::Datetime\":\"ACCOUNT DOES NOT EXIST IN THIS COMPANY\"}";
        var coreData = new HashMap<String, Object>();
        coreData.put("1", JsonMapper.jsonStringToObject(dataJson, CoreAccountStatement.class));

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .data(coreData)
                .build();

        when(coreClient.getAccountStatement(coreGetAccountStatementRequest)).thenReturn(coreResponse);

        try {
            accountStatementService.getAccountStatement(partnerId, request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.INVALID_ACCOUNT.getCode(), be.getResponseCode().getCode());
        }

        verify(coreClient).getAccountStatement(coreGetAccountStatementRequest);
    }

    @Test
    void getAccountStatement_ErrorTransactionNotFound() {
        var partnerId = UUID.randomUUID().toString();
        var request = BankStatementRequest.builder()
                .accountNo("12345678")
                .fromDateTime("2023-06-13T12:08:56+07:00")
                .toDateTime("2023-06-13T12:08:56+07:00")
                .build();

        var coreDateFormat = DateTimeFormatter.ofPattern(DateUtil.CORE_DATE_FORMAT);

        var coreGetAccountStatementRequest = CoreGetAccountStatementRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_STATEMENT)
                .accountNo(request.getAccountNo())
                .bookingDate(String.format("%s %s", LocalDate.of(2023, 6, 13).format(coreDateFormat), LocalDate.of(2023, 6, 13).format(coreDateFormat)))
                .build();

        var dataJson = "{\"Datetime::Datetime\":\"\"}";
        var coreData = new HashMap<String, Object>();
        coreData.put("1", JsonMapper.jsonStringToObject(dataJson, CoreAccountStatement.class));

        var coreResponse = CoreResponse.builder()
                .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                .data(coreData)
                .build();

        when(coreClient.getAccountStatement(coreGetAccountStatementRequest)).thenReturn(coreResponse);

        try {
            accountStatementService.getAccountStatement(partnerId, request);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.TRANSACTION_NOT_FOUND.getCode(), be.getResponseCode().getCode());
        }

        verify(coreClient).getAccountStatement(coreGetAccountStatementRequest);
    }
}
