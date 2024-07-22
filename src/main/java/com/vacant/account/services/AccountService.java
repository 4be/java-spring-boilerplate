package com.vacant.account.services;

import com.vacant.account.constants.CoreConstant;
import com.vacant.account.enums.NonSnapResponseCode;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import com.vacant.account.exceptions.NonSnapException;
import com.vacant.account.feignclients.CoreClient;
import com.vacant.account.model.Account;
import com.vacant.account.model.AccountBalance;
import com.vacant.account.model.AccountDetailAndBalance;
import com.vacant.account.model.entity.CustomerPortfolioSummary;
import com.vacant.account.model.entity.EDWCustomerPortfolio;
import com.simas.account.model.in.*;
import com.simas.account.model.out.*;
import com.vacant.account.model.in.*;
import com.vacant.account.model.out.*;
import com.vacant.account.repositories.CustomerPortfolioRepository;
import com.vacant.account.repositories.CustomerPortfolioSummaryRepository;
import com.vacant.account.utils.CoreRawResponseParser;
import com.vacant.account.utils.JsonMapper;
import com.vacant.account.utils.NumberUtil;
import com.vacant.account.utils.ResponseConstructor;
import io.beanmapper.config.BeanMapperBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final CoreClient coreClient;
    private final CustomerPortfolioRepository customerPortfolioRepository;
    private final CustomerPortfolioSummaryRepository customerPortfolioSummaryRepository;

    public PartnerAccountInquiryResponse getPartnerAccounts(String cif) {
        List<Account> accounts = Optional.ofNullable(getSavingAccounts(cif))
                .orElse(new ArrayList<>())
                .stream()
                .map(account -> new BeanMapperBuilder().build().map(account, Account.class))
                .collect(Collectors.toList());

        return PartnerAccountInquiryResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accounts(accounts).build();
    }

    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        var coreCreateAccountRequest = CoreCreateAccountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CREATE_ACCOUNT)
                .options(CoreConstant.CORE_OPTIONS_CREATE_ACCOUNT)
                .productType(request.getAdditionalInfo().getCategory())
                .cif(request.getCif())
                .currencyCode(CoreConstant.CURRENCY_CODE)
                .branchCode(CoreConstant.DEFAULT_BRANCH)
                .channelReference(request.getPartnerReferenceNo())
                .umgReference(request.getPartnerReferenceNo())
                .build();

        var createAccountResponse = coreClient.createAccount(coreCreateAccountRequest);
        ResponseConstructor.validateCoreWriteResponse(createAccountResponse);

        var accountNumber = createAccountResponse.getData().stream()
                .filter(m -> m.containsKey("OPENING.DATE:1:1"))
                .map(map -> map.get("responseHeader").split("/")[0]).findFirst().orElse(null);

        if (ObjectUtils.isEmpty(accountNumber)) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        return CreateAccountResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .partnerReferenceNo(request.getPartnerReferenceNo())
                .referenceNo(createAccountResponse.getCorrelationId())
                .accountId(accountNumber)
                .additionalInfo(request.getAdditionalInfo())
                .build();
    }

    public BalanceInquiryResponse getAccountBalance(BalanceInquiryRequest request) {
        var coreGetAccountBalanceRequest = CoreGetAccountBalanceRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_BALANCE)
                .accountNo(request.getAccountNo())
                .build();

        var coreResponse = coreClient.getAccountBalance(coreGetAccountBalanceRequest);

        var coreGetAccountBalanceResponse = ResponseConstructor.validateAndMapCoreResponse(coreResponse, CoreAccountBalance.class);

        var accountBalance = Optional.ofNullable(coreGetAccountBalanceResponse.getData())
                .orElseThrow(() -> new BusinessException(ResponseCode.INVALID_ACCOUNT))
                .get(0);

        if (ObjectUtils.isEmpty(accountBalance.getAccountName())) {
            throw new BusinessException(ResponseCode.INVALID_ACCOUNT);
        }

        var accountInfo = AccountBalance.builder()
                .balanceType(accountBalance.getAccountType())
                .amount(NumberUtil.setBalanceAmount(NumberUtil.parseCoreAmount(accountBalance.getAmount()), accountBalance.getCurrency()))
                .floatAmount(NumberUtil.setBalanceAmount(BigDecimal.ZERO, accountBalance.getCurrency()))
                .holdAmount(NumberUtil.setBalanceAmount(NumberUtil.parseCoreAmount(accountBalance.getHoldAmount()), accountBalance.getCurrency()))
                .availableBalance(NumberUtil.setBalanceAmount(NumberUtil.parseCoreAmount(accountBalance.getAvailableBalance()), accountBalance.getCurrency()))
                .ledgerBalance(NumberUtil.setBalanceAmount(NumberUtil.parseCoreAmount(accountBalance.getOpenBalance()), accountBalance.getCurrency()))
                .currentMultilateralLimit(NumberUtil.setBalanceAmount(NumberUtil.parseCoreAmount(accountBalance.getLimitAmount()), accountBalance.getCurrency()))
                .status(StringUtils.leftPad(accountBalance.getStatus(), 4, "0"))
                .build();

        return BalanceInquiryResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accountNo(request.getAccountNo())
                .name(accountBalance.getAccountName())
                .partnerReferenceNo(request.getPartnerReferenceNo())
                .referenceNo(coreGetAccountBalanceResponse.getCorrelationId())
                .accountInfos(List.of(accountInfo))
                .additionalInfo(request.getAdditionalInfo())
                .build();
    }

    public List<CoreAccount> getSavingAccounts(String cif) {
        var savingAccountsRequest = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(cif).build();

        var savingAccountsResponse = ResponseConstructor.validateAndMapCoreResponse(coreClient.getSavingAccounts(savingAccountsRequest), CoreAccount.class);

        return savingAccountsResponse.getData() == null ? new ArrayList<>() : savingAccountsResponse.getData();
    }

    public CheckAccountsResponse checkAccounts(CheckAccountsRequest request) {
        List<CoreAccount> accounts = getSavingAccounts(request.getCif());

        if (accounts.isEmpty()) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        Map<String, String> accountsMap = accounts.stream().collect(Collectors.toMap(CoreAccount::getAccountNumber, CoreAccount::getAccountNumber));

        boolean accountsAreValid = request.getAccountIds().stream().allMatch(accountsMap::containsKey);

        return CheckAccountsResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accountsAreValid(accountsAreValid)
                .build();
    }

    public WaiveChargeResponse waiveCharge(WaiveChargeRequest request) {
        var coreWaiveChargeRequest = CoreWaiveChargeRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_WAIVE_CHARGE)
                .option(CoreConstant.CORE_OPTIONS_WAIVE_CHARGE)
                .channelId(request.getChannelId())
                .partnerId(request.getPartnerId())
                .branch(request.getAuditBranchCode())
                .transactionId("A-" + request.getAccountNumber())
                .waiveCharge(request.getCharges())
                .umgUniqueId(request.getMsgId())
                .umgAuthId(request.getAuditSupervisorId())
                .umgOperatorId(request.getAuditCreatedBy())
                .channelRefNum(request.getTrxId())
                .terminalId(request.getTerminalId())
                .build();

        CoreWriteOperationResponse coreResponse;
        try {
            coreResponse = coreClient.waiveCharge(coreWaiveChargeRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            String[] split = coreResponse.getRawResponse().split(",");
            throw new NonSnapException(coreResponse.getResponseCode(), split[1]);
        }

        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(
                        coreResponse.getRawResponse()),
                WaiveChargeResponse.class
        );

        return response;
    }

    public LockAmountResponse overrideLockAmount(LockAmountRequest request) {
        var coreLockAmountRequest = CoreLockAmountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_LOCK_AMOUNT)
                .options(CoreConstant.CORE_OPTIONS_LOCK_AMOUNT)
                .channelId(request.getChannelId())
                .partnerId(request.getPartnerId())
                .branchCode(request.getBranchCode())
                .accountNumber(request.getAccountNumber())
                .description(request.getDescription())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .lockedAmount(request.getLockedAmount())
                .supervisorId(request.getSupervisorId())
                .createdBy(request.getCreatedBy())
                .msgId(request.getMsgId())
                .trxId(request.getTrxId())
                .terminalId(request.getTerminalId())
                .correlationId(request.getTrxId())
                .build();

        LockAmountResponse response;
        try {
            var coreResponse = coreClient.overrideLockAmount(coreLockAmountRequest);
            response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), LockAmountResponse.class);
            response.setCorrelationId(coreResponse.getCorrelationId());
        } catch (BusinessException be) {
            throw new NonSnapException(be.getResponseCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        return response;
    }

    public ActivateDormantResponse activateDormantAccount(ActivateDormantRequest request) {
        var coreRequest = CoreActivateDormantRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ACTIVATE_DORMANT)
                .options(CoreConstant.CORE_OPTIONS_ACTIVATE_DORMANT)
                .channelId(request.getChannelId())
                .partnerId(request.getPartnerId())
                .accountNumber(request.getAccountNumber())
                .resetDate(request.getResetDate())
                .trxId(request.getTrxId())
                .msgId(request.getMsgId())
                .createdBy(request.getCreatedBy())
                .terminalId(request.getTerminalId())
                .supervisorId(request.getSupervisorId())
                .correlationId(request.getTrxId())
                .build();
        ActivateDormantResponse response;
        try {
            var coreResponse = coreClient.activateDormantAccount(coreRequest);
            response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), ActivateDormantResponse.class);
            response.setCorrelationId(coreResponse.getCorrelationId());
        } catch (BusinessException be) {
            throw new NonSnapException(be.getResponseCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        return response;
    }

    public DebitInterestResponse debitInterest(DebitInterestRequest request) {
        CoreDebitInterestRequest coreDebitInterest = CoreDebitInterestRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_DEBIT_INTEREST)
                .options(CoreConstant.CORE_OPTIONS_DEBIT_INTEREST)
                .channelId(request.getChannelId())
                .partnerId(request.getPartnerId())
                .branchCode(request.getBranchCode())
                .accountNumber(request.getAccountNumber())
                .chargesKey(request.getChargesKey())
                .interestDayBasis(request.getInterestDayBasis())
                .taxKey(request.getTaxKey())
                .drBalanceType(request.getDrBalanceType())
                .drCalculType(request.getDrCalculType())
                .drIntRate1(request.getDrIntRate1())
                .drIntRate2(request.getDrIntRate2())
                .drIntRate3(request.getDrIntRate3())
                .drIntRate4(request.getDrIntRate4())
                .drIntRate5(request.getDrIntRate5())
                .drIntRate6(request.getDrIntRate6())
                .drLimitAmount1(request.getDrLimitAmount1())
                .drLimitAmount2(request.getDrLimitAmount2())
                .drLimitAmount3(request.getDrLimitAmount3())
                .drLimitAmount4(request.getDrLimitAmount4())
                .drLimitAmount5(request.getDrLimitAmount5())
                .drLimitAmount6(request.getDrLimitAmount6())
                .build();

        log.info("core request: {}", coreDebitInterest);
        CoreRawResponse coreResponse;
        try {
            coreResponse = coreClient.coreDebitInterest(coreDebitInterest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), DebitInterestResponse.class);
        response.setCorrelationId(coreResponse.getCorrelationId());
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        return response;
    }

    public CloseAccountResponse closeAccount(CloseAccountRequest request) {
        var coreCloseAccountRequest = CoreCloseAccountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CLOSE_ACCOUNT)
                .branch(request.getAuditBranchCode())
                .options(CoreConstant.CORE_OPTIONS_CLOSE_ACCOUNT)
                .channelId(request.getChannelId())
                .partnerId(request.getPartnerId())
                .transactionId(request.getAccountNumber())
                .settlementAccount(request.getSettlementAccount())
                .closingChargeType(request.getClosingChargeType())
                .closingChargeAmount(request.getClosingChargeAmount())
                .postingRestrict(request.getClosingReason())
                .capitaliseInterest(request.getCapitaliseInterest())
                .onlineClosure(request.getOnlineClosure())
                .currency(request.getCurrency())
                .auditSupervisorId(request.getAuditSupervisorId())
                .auditCreatedBy(request.getAuditCreatedBy())
                .msgId(request.getMsgId())
                .trxId(request.getTrxId())
                .terminalId(request.getTerminalId())
                .build();

        String coreResponse;
        try {
            coreResponse = coreClient.closeAccount(coreCloseAccountRequest).getRawResponse();
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse), CloseAccountResponse.class);
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        return response;
    }

    public CreditInterestResponse creditInterest(CreditInterestRequest request) {
        var coreCreditInterestRequest = CoreCreditInterestRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CREDIT_INTEREST)
                .options(CoreConstant.CORE_OPTIONS_CREDIT_INTEREST)
                .channelId(request.getChannelId())
                .partnerId(request.getPartnerId())
                .branch(request.getBranch())
                .transactionId(request.getTransactionId())
                .interestDayBasis(request.getInterestDayBasis())
                .taxKey(request.getTaxKey())
                .crBalanceType(request.getCrBalanceType())
                .crCalculType(request.getCrCalculType())
                .crMinimumBalance(request.getCrMinimumBalance())
                .crIntRate(request.getCrIntRate())
                .build();

        CoreWriteOperationResponse coreResponse;
        try {
            coreResponse = coreClient.creditInterest(coreCreditInterestRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            String[] split = coreResponse.getRawResponse().split(",");
            throw new NonSnapException(coreResponse.getResponseCode(), split[1]);
        }

        return JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), CreditInterestResponse.class);

    }

    public AccountCreationSavingResponse accountCreationSaving(AccountCreationSavingRequest request) {
        var coreAccountCreationSavingRequest = CoreAccountCreationSavingRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ACCOUNT_CREATION_SAVING)
                .options(CoreConstant.CORE_OPTIONS_ACCOUNT_CREATION_SAVING)
                .umgUniqueId(request.getUmgUniqueId())
                .openingDate(request.getOpeningDate())
                .branch(request.getBranch())
                .acMeasureCat(request.getAcMeasureCat())
                .currency(request.getCurrency())
                .accountTitle(request.getAccountTitle())
                .accountOfficer(request.getAccountOfficer())
                .category(request.getCategory())
                .address(request.getAddress())
                .branchUpdate(request.getBranchUpdate())
                .channelRefNum(request.getChannelRefNum())
                .characteristic(request.getCharacteristic())
                .corespondName(request.getCorespondName())
                .customer(request.getCustomer())
                .emailAddress(request.getEmailAddress())
                .emailFlag(request.getEmailFlag())
                .interestType(request.getInterestType())
                .merchantCode(request.getMerchantCode())
                .passbookFlag(request.getPassbookFlag())
                .payrollAcct(request.getPayrollAcct())
                .poBoxNo(request.getPoBoxNo())
                .poCity(request.getPoCity())
                .poLocatCode(request.getPoLocalCode())
                .poPostCode(request.getPoPostCode())
                .poSuburbTown(request.getPoSuburbTown())
                .postingRestrict(request.getPostingRestrict())
                .proProvState(request.getProProvState())
                .saProdType(request.getSaProdType())
                .prodType(request.getProdType())
                .toRefSince(request.getToRefSince())
                .toReference(request.getToReference())
                .umgAuthId(request.getUmgAuthId())
                .umgAuthId2(request.getUmgAuthId2())
                .umgOperatorId(request.getUmgOperatorId())
                .umgTermId(request.getUmgTermId())
                .build();

        CoreWriteOperationResponse coreResponse;
        try {
            log.info(JsonMapper.toString(coreAccountCreationSavingRequest));
            coreResponse = coreClient.accountCreationSaving(coreAccountCreationSavingRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            String[] split = coreResponse.getRawResponse().split(",");
            throw new NonSnapException(coreResponse.getResponseCode(), split[1]);
        }

        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(
                        coreResponse.getRawResponse()),
                AccountCreationSavingResponse.class
        );

        return response;
    }

    public ChequeStockEntryResponse chequeStockEntry(ChequeStockEntryRequest request){
        log.info("Cheque Stock request: {}", request);
        int stockBookQuantity = Integer.parseInt(request.getStockBookQuantity());
        int stockSeriesNo = Integer.parseInt(request.getStockSeriesNo());
        Map<String, String> coreRequest = new HashMap<>();
        coreRequest.put("!OPERATION", CoreConstant.CORE_OPERATION_CHEQUE_STOCK_ENTRY);
        coreRequest.put("!OPTIONS", CoreConstant.CORE_OPTION_CHEQUE_STOCK_ENTRY);
        coreRequest.put("!BRANCH", request.getBranchCode());
        coreRequest.put("!TRANSACTION_ID", CoreConstant.CORE_TRANSACTION_ID_CHEQUE_STOCK_ENTRY);
        coreRequest.put("TO.REGISTER:1:1", "CHQ.ID001"+request.getToRegister());
        coreRequest.put("STOCK.START.NO:1:1", request.getStockStartNo());
//        coreRequest.put("STOCK.START.NO:1:1", String.valueOf(stockQuantity+stockSeriesNo+1));
        coreRequest.put("UMG.AUTH.ID:1:1", request.getSupervisorId());
        coreRequest.put("UMG.OPERATOR.ID:1:1", request.getCreatedBy());
        coreRequest.put("UMG.UNIQUE.ID:1:1", CoreConstant.MSG +request.getPartnerReferenceNo());
        coreRequest.put("CHANNEL.REF.NUM:1:1", CoreConstant.TRX +request.getPartnerReferenceNo());
        coreRequest.put("UMG.TERM.ID:1:1", request.getTerminalId());
        for (int i = 1; i <= stockBookQuantity; i++) {
            coreRequest.put("STOCK.SERIES:" + i + ":1", String.valueOf(stockSeriesNo+i));
            coreRequest.put("STOCK.QUANTITY:" + i + ":1", request.getStockQuantity());
            coreRequest.put("STOCK.START.NO:" + i + ":1", request.getStockStartNo());
            coreRequest.put("CHEQUE.TYPE:" + i + ":1", request.getChequeType());
            coreRequest.put("NOTES:" + i + ":1", request.getNotes());
        }


        CoreWriteOperationResponse coreResponse;
        try {
            log.info("core request: {}", coreRequest);
             coreResponse = coreClient.coreChequeStockEntry(coreRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), ChequeStockEntryResponse.class);
        response.setCorrelationId(coreResponse.getCorrelationId());
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        response.setResponseMessage(ResponseCode.SUCCESS.getMessage());
        log.info("Cheque Entry Stock response: {}", response);
        return response;
    }

    public ChequeIssuanceResponse chequeIssuance(ChequeIssuanceRequest request){
        CoreChequeIssuanceRequest coreChequeIssuanceRequest = CoreChequeIssuanceRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CHEQUE_ISSUANCE)
                .options(CoreConstant.CORE_OPTION_CHEQUE_ISSUANCE)
                .branchCode(request.getBranchCode())
                .transactionId(request.getTransactionId())
                .stockRegister(CoreConstant.CORE_STOCK_REQ_CHEQUE_ISSUANCE+request.getStockRegister())
                .currency(CoreConstant.CORE_CURRENCY)
                .chequeStatus(request.getChequeStatus())
                .stockStartNo(request.getStockStartNo())
                .quantityIssued(request.getQuantityIssued())
                .notes(request.getNotes())
                .weiveCharges(request.getWeiveCharges())
                .chequeCharges(request.getChequeCharges())
                .chequeBookCharges(request.getChequeBookCharges())
                .chequeBooksTamDuty(request.getChequeBooksTamDuty())
                .supervisorId(request.getSupervisorId())
                .createdBy(request.getCreatedBy())
                .msgId(CoreConstant.MSG +request.getPartnerReferenceNo())
                .trxId(CoreConstant.TRX +request.getPartnerReferenceNo())
                .terminalId(request.getTerminalId())
                .build();

        CoreRawResponse coreResponse;
        try {
            log.info("core request: {}", coreChequeIssuanceRequest);
            coreResponse = coreClient.coreChequeIssuance(coreChequeIssuanceRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), ChequeIssuanceResponse.class);
        response.setCorrelationId(coreResponse.getCorrelationId());
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        response.setResponseMessage(ResponseCode.SUCCESS.getMessage());
        log.info("Cheque Issuance response: {}", response);
        return response;
    }

    public ChequeBlockResponse chequeBlock(ChequeBlockRequest request){
        String chequeNo = NumberUtil.getNumericValueFromString(request.getChequeNo());
        CoreChequeBlockRequest coreChequeBlockRequest = CoreChequeBlockRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CHEQUE_BLOCK)
                .options(CoreConstant.CORE_OPTION_CHEQUE_BLOCK)
                .branchCode(request.getBranchCode())
                .transactionId(request.getTransactionId())
                .paymentStartStopType(request.getPaymentStartStopType())
                .firstCheque(chequeNo)
                .lastCheque(chequeNo)
                .chequeType(request.getChequeType())
                .supervisorId(request.getSupervisorId())
                .createdBy(request.getCreatedBy())
                .msgId(CoreConstant.MSG +request.getPartnerReferenceNo())
                .trxId(CoreConstant.TRX +request.getPartnerReferenceNo())
                .terminalId(request.getTerminalId())
                .build();

        CoreRawResponse coreResponse;
        try {
            log.info("core request: {}", coreChequeBlockRequest);
            coreResponse = coreClient.coreChequeBlock(coreChequeBlockRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), ChequeBlockResponse.class);
        response.setCorrelationId(coreResponse.getCorrelationId());
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        response.setResponseMessage(ResponseCode.SUCCESS.getMessage());
        log.info("Cheque Block Stock response: {}", response);
        return response;
    }

    public ChequeUnBlockResponse chequeUnBlock(ChequeUnBlockRequest request){
        CoreChequeUnBlockRequest coreChequeUnBlockRequest = CoreChequeUnBlockRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CHEQUE_BLOCK)
                .options(CoreConstant.CORE_OPTION_CHEQUE_BLOCK)
                .branchCode(request.getBranchCode())
                .transactionId(request.getTransactionId())
                .paymentStartStopType(request.getPaymentStartStopType())
                .chequeNo(request.getChequeNo())
                .chequeType(request.getChequeType())
                .supervisorId(request.getSupervisorId())
                .createdBy(request.getCreatedBy())
                .msgId(CoreConstant.MSG +request.getPartnerReferenceNo())
                .trxId(CoreConstant.TRX +request.getPartnerReferenceNo())
                .terminalId(request.getTerminalId())
                .build();

        CoreRawResponse coreResponse;
        try {
            log.info("core request: {}", coreChequeUnBlockRequest);
            coreResponse = coreClient.coreChequeUnBlock(coreChequeUnBlockRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), ChequeUnBlockResponse.class);
        response.setCorrelationId(coreResponse.getCorrelationId());
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        response.setResponseMessage(ResponseCode.SUCCESS.getMessage());
        log.info("Cheque Unblock Stock response: {}", response);
        return response;
    }

    public AccountCreationOverdraftResponse accountCreationOverdraft(AccountCreationOverdraftRequest request) {
        var coreAccountCreationOverdraftRequest = CoreAccountCreationOverdraftRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ACCOUNT_CREATION_OVERDRAFT)
                .options(CoreConstant.CORE_OPTIONS_ACCOUNT_CREATION_OVERDRAFT)
                .umgUniqueId(request.getUmgUniqueId())
                .branch(request.getBranch())
                .userInformation(request.getUserInformation())
                .accountOfficer(request.getAccountOfficer())
                .accountTitle(request.getAccountTitle())
                .addressFlag(request.getAddressFlag())
                .prodType(request.getProdType())
                .category(request.getCategory())
                .channelRefNum(request.getChannelRefNum())
                .corespondName(request.getCorespondName())
                .currency(request.getCurrency())
                .customer(request.getCustomer())
                .emailAddress(request.getEmailAddress())
                .emailFlag(request.getEmailFlag())
                .openingDate(request.getOpeningDate())
                .poBoxNo(request.getPoBoxNo())
                .poCity(request.getPoCity())
                .poLocatCode(request.getPoLocatCode())
                .poPostCode(request.getPoPostCode())
                .poSuburbTown(request.getPoSuburbTown())
                .proProvState(request.getProProvState())
                .startLDate(request.getStartLDate())
                .umgAuthId(request.getUmgAuthId())
                .umgAuthId2(request.getUmgAuthId2())
                .umgOperatorId(request.getUmgOperatorId())
                .umgTermId(request.getUmgTermId())
                .build();

        CoreWriteOperationResponse coreResponse;
        try {
            coreResponse = coreClient.accountCreationOverdraft(coreAccountCreationOverdraftRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            String[] split = coreResponse.getRawResponse().split(",");
            throw new NonSnapException(coreResponse.getResponseCode(), split[1]);
        }

        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(
                        coreResponse.getRawResponse()),
                AccountCreationOverdraftResponse.class
        );

        return response;
    }

    public AccountMaintenanceSavingResponse accountMaintenanceSaving(AccountMaintenanceSavingRequest request) {
        var coreAccountMaintenanceSavingRequest = new CoreAccountMaintenanceSavingRequest();
        try {
            coreAccountMaintenanceSavingRequest = CoreAccountMaintenanceSavingRequest.builder()
                    .operation(CoreConstant.CORE_OPERATION_ACCOUNT_MAINTENANCE_SAVING)
                    .options(CoreConstant.CORE_OPTIONS_ACCOUNT_MAINTENANCE_SAVING)
                    .accountNumber(request.getAccountNumber())
                    .auditBranchCode(request.getAuditBranchCode())
                    .cifNo(request.getCifNo())
                    .accountTitle(request.getAccountTitle())
                    .blockedReason(request.getBlockedReason())
                    .BSL2Passbook(request.getBSL2Passbook())
                    .BSL2ToReference(request.getBSL2ToReference())
                    .BSL2ToRefSince(request.getBSL2ToRefSince())
                    .flagSalaryAcc(request.getFlagSalaryAcc())
                    .category(request.getCategory())
                    .BSL2Characteristic(request.getBSL2Characteristic())
                    .BSL2SmIntType(request.getBSL2SmIntType())
                    .BSL2AcMeasureCat(request.getBSL2AcMeasureCat())
                    .addrCityTown(request.getAddrCityTown())
                    .addrSameCifEmail(request.getAddrSameCifEmail())
                    .addrEmail(request.getAddrEmail())
                    .addrSameCifAddress(request.getAddrSameCifAddress())
                    .addrCorrespondentName(request.getAddrCorrespondentName())
                    .addrStreetName1(request.getAddrStreetName1())
                    .addrVillage(request.getAddrVillage())
                    .addrSubDistrict(request.getAddrSubDistrict())
                    .addrProvince(request.getAddrProvince())
                    .addrPostcode(request.getAddrPostcode())
                    .acctInfo1(request.getAcctInfo1())
                    .auditSupervisorId(request.getAuditSupervisorId())
                    .auditCreatedBy(request.getAuditCreatedBy())
                    .msgId(request.getMsgId())
                    .trxId(request.getTrxId())
                    .terminalId(request.getTerminalId())
                    .branchUpdate("ID001" + request.getAuditBranchCode())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CoreWriteOperationResponse coreResponse;
        try {
            log.info("request: " + JsonMapper.toString(coreAccountMaintenanceSavingRequest));
            coreResponse = coreClient.accountMaintenanceSaving(coreAccountMaintenanceSavingRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            String[] split = coreResponse.getRawResponse().split(",");
            throw new NonSnapException(coreResponse.getResponseCode(), split[1]);
        }

        var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(
                        coreResponse.getRawResponse()),
                AccountMaintenanceSavingResponse.class
        );

        return response;
    }

    public CreateAccountGiroResponse createAccountGiro(CreateAccountGiroRequest request) {
        if (request.getAccountNumber() != null) {
            throw new NonSnapException(HttpStatus.BAD_REQUEST.value(), "01", "Account number must be empty!");
        }
        var coreRequest = CoreCreateAccountGiroRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CREATE_ACCOUNT_GIRO)
                .options(CoreConstant.CORE_OPTIONS_CREATE_ACCOUNT_GIRO)
                .correlationId(request.getTrxId())
                .accountNumber(request.getAccountNumber())
                .branchCode(request.getBranchCode())
                .cifNo(request.getCifNo())
                .productType(request.getProductType())
                .accountTitle(request.getAccountTitle())
                .currency(request.getCurrency())
                .officerCode(request.getOfficerCode())
                .blockedReason(request.getBlockedReason())
                .openDate(request.getOpenDate())
                .category(request.getCategory())
                .prodType(request.getProdType())
                .smintType(request.getSmintType())
                .acMeasureCat(request.getAcMeasureCat())
                .information4(request.getInformation4())
                .information5(request.getInformation5())
                .email2(request.getEmail2())
                .email3(request.getEmail3())
                .email4(request.getEmail4())
                .joinCifNo(request.getJoinCifNo())
                .joinRelationCode(request.getJoinRelationCode())
                .joinNotes(request.getJoinNotes())
                .card(request.getCard())
                .cardNumber(request.getCardNumber())
                .cardholder(request.getCardholder())
                .cardStatus(request.getCardStatus())
                .lastStatusUpdate(request.getLastStatusUpdate())
                .maxCardBalance(request.getMaxCardBalance())
                .flag(request.getFlag())
                .signCode(request.getSignCode())
                .signDate(request.getSignDate())
                .signInstruction(request.getSignInstruction())
                .sameCifEmail(request.getSameCifEmail())
                .email(request.getEmail())
                .sameCifAddress(request.getSameCifAddress())
                .corespondentName(request.getCorespondentName())
                .streetName1(request.getStreetName1())
                .village(request.getVillage())
                .subDistrict(request.getSubDistrict())
                .province(request.getProvince())
                .cityTown(request.getCityTown())
                .postcode(request.getPostcode())
                .supervisorId(request.getSupervisorId())
                .createdBy(request.getCreatedBy())
                .msgId(request.getMsgId())
                .trxId(request.getTrxId())
                .terminalId(request.getTerminalId())
                .supervisorId2(request.getSupervisorId2())
                .atmCard(request.getAtmCard())
                .digitalSignId(request.getDigitalSignId())
                .build();
        CreateAccountGiroResponse response;
        try {
            log.info("send to core : {}", JsonMapper.toString(coreRequest));
            var coreResponse = coreClient.createAccountGiro(coreRequest);
            response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), CreateAccountGiroResponse.class);
            response.setCorrelationId(coreResponse.getCorrelationId());
        } catch (BusinessException be) {
            throw new NonSnapException(be.getResponseCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        response.setAccountNumber(response.getResponseHeader().split("/")[0]);
        return response;
    }

    public UpdateAccountGiroResponse updateAccountGiro(UpdateAccountGiroRequest request) {
        if (request.getAccountNumber() == null) {
            throw new NonSnapException(HttpStatus.BAD_REQUEST.value(), "01", "Account number must not be empty!");
        }
        var coreRequest = CoreUpdateAccountGiroRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ACCOUNT_GIRO_MAINTENANCE)
                .options(CoreConstant.CORE_OPTIONS_ACCOUNT_GIRO_MAINTENANCE)
                .correlationId(request.getTrxId())
                .accountNumber(request.getAccountNumber())
                .branchCode(request.getBranchCode())
                .branchCodeUpdate("ID001%s".formatted(request.getBranchCode()))
                .cifNo(request.getCifNo())
                .productType(request.getProductType())
                .accountTitle(request.getAccountTitle())
                .currency(request.getCurrency())
                .officerCode(request.getOfficerCode())
                .blockedReason(request.getBlockedReason())
                .openDate(request.getOpenDate())
                .category(request.getCategory())
                .prodType(request.getProdType())
                .smintType(request.getSmintType())
                .acMeasureCat(request.getAcMeasureCat())
                .information4(request.getInformation4())
                .information5(request.getInformation5())
                .email2(request.getEmail2())
                .email3(request.getEmail3())
                .email4(request.getEmail4())
                .joinCifNo(request.getJoinCifNo())
                .joinRelationCode(request.getJoinRelationCode())
                .joinNotes(request.getJoinNotes())
                .card(request.getCard())
                .cardNumber(request.getCardNumber())
                .cardholder(request.getCardholder())
                .cardStatus(request.getCardStatus())
                .lastStatusUpdate(request.getLastStatusUpdate())
                .maxCardBalance(request.getMaxCardBalance())
                .flag(request.getFlag())
                .signCode(request.getSignCode())
                .signDate(request.getSignDate())
                .signInstruction(request.getSignInstruction())
                .sameCifEmail(request.getSameCifEmail())
                .email(request.getEmail())
                .sameCifAddress(request.getSameCifAddress())
                .corespondentName(request.getCorespondentName())
                .streetName1(request.getStreetName1())
                .village(request.getVillage())
                .subDistrict(request.getSubDistrict())
                .province(request.getProvince())
                .cityTown(request.getCityTown())
                .postcode(request.getPostcode())
                .supervisorId(request.getSupervisorId())
                .createdBy(request.getCreatedBy())
                .msgId(request.getMsgId())
                .trxId(request.getTrxId())
                .terminalId(request.getTerminalId())
                .supervisorId2(request.getSupervisorId2())
                .atmCard(request.getAtmCard())
                .digitalSignId(request.getDigitalSignId())
                .build();
        UpdateAccountGiroResponse response;
        try {
            log.info("send to core : {}", JsonMapper.toString(coreRequest));
            var coreResponse = coreClient.updateAccountGiro(coreRequest);
            response = JsonMapper.objectToOther(CoreRawResponseParser.parse(coreResponse.getRawResponse()), UpdateAccountGiroResponse.class);
            response.setCorrelationId(coreResponse.getCorrelationId());
        } catch (BusinessException be) {
            throw new NonSnapException(be.getResponseCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            throw new NonSnapException(response.getResponseCode(), response.getError());
        }
        return response;
    }

    public AccountMaintenanceOverdraftResponse accountMaintenanceOverdraft(AccountMaintenanceOverdraftRequest request) {
        {
            var coreAccountMaintenanceOverdraftRequest = CoreAccountMaintenanceOverdraftRequest.builder()
                    .operation(CoreConstant.CORE_OPERATION_ACCOUNT_CREATION_OVERDRAFT)
                    .options(CoreConstant.CORE_OPTIONS_ACCOUNT_CREATION_OVERDRAFT)
                    .umgUniqueId(request.getUmgUniqueId())
                    .branch(request.getBranch())
                    .userInformation(request.getUserInformation())
                    .transactionId(request.getTransactionId())
                    .accountOfficer(request.getAccountOfficer())
                    .accountTitle(request.getAccountTitle())
                    .addressFlag(request.getAddressFlag())
                    .prodType(request.getProdType())
                    .category(request.getCategory())
                    .channelRefNum(request.getChannelRefNum())
                    .corespondName(request.getCorespondName())
                    .currency(request.getCurrency())
                    .customer(request.getCustomer())
                    .emailAddress(request.getEmailAddress())
                    .emailFlag(request.getEmailFlag())
                    .openingDate(request.getOpeningDate())
                    .poBoxNo(request.getPoBoxNo())
                    .poCity(request.getPoCity())
                    .poLocatCode(request.getPoLocatCode())
                    .poPostCode(request.getPoPostCode())
                    .poSuburbTown(request.getPoSuburbTown())
                    .proProvState(request.getProProvState())
                    .startLDate(request.getStartLDate())
                    .umgAuthId(request.getUmgAuthId())
                    .umgAuthId2(request.getUmgAuthId2())
                    .umgOperatorId(request.getUmgOperatorId())
                    .umgTermId(request.getUmgTermId())
                    .build();

            CoreWriteOperationResponse coreResponse;
            try {
                log.info("request: " + JsonMapper.toString(coreAccountMaintenanceOverdraftRequest));
                coreResponse = coreClient.accountMaintenanceOverdraft(coreAccountMaintenanceOverdraftRequest);
                log.info("core response: {}", coreResponse);
            } catch (Exception e) {
                e.printStackTrace();
                throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
            }

            if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
                String[] split = coreResponse.getRawResponse().split(",");
                throw new NonSnapException(coreResponse.getResponseCode(), split[1]);
            }

            var response = JsonMapper.objectToOther(CoreRawResponseParser.parse(
                            coreResponse.getRawResponse()),
                    AccountMaintenanceOverdraftResponse.class
            );

            return response;
        }
    }

    public AccountDetailByCifResponse accountDetailByCif(String cifNumber, String partnerReferenceNo) {
        var coreAccountDetailByCifResponse = CoreGetSavingAccountsRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_ENQUIRY)
                .transactionId(CoreConstant.CORE_TRANSACTION_ID_ACCOUNT_DETAIL_BY_CIF)
                .customerEq(cifNumber)
                .build();

        CoreResponse coreResponse;
        try {
            log.info("request: " + JsonMapper.toString(coreAccountDetailByCifResponse));
            coreResponse = coreClient.getSavingAccounts(coreAccountDetailByCifResponse);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NonSnapException(NonSnapResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            throw new NonSnapException(NonSnapResponseCode.DATA_NOT_FOUND);
        }

        ResponseCode status = ResponseCode.SUCCESS;

        List<AccountDetailByCif> listOfAccountDetailByCif = coreResponse.getData().values().stream()
                .map(account -> JsonMapper.objectToOther(account, AccountDetailByCif.class))
                .toList();

        return AccountDetailByCifResponse.builder()
                .responseCode(status.getCode())
                .responseMessage(status.getMessage())
                .partnerReferenceNo(partnerReferenceNo)
                .data(listOfAccountDetailByCif)
                .build();
    }

    public InquiryLockAmountResponse inquiryLockAmount(String request, String partnerReferenceNo) {
        var coreInquiryLockAmountRequest = CoreInquiryLockAmountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_INQUIRY_LOCK_AMOUNT)
                .transactionId(CoreConstant.CORE_TRANSACTION_ID_INQUIRY_LOCK_AMOUNT)
                .accountNumber(request)
                .build();

        CoreResponse coreResponse;
        try {
            log.info("request: " + JsonMapper.toString(coreInquiryLockAmountRequest));
            coreResponse = coreClient.inquiryLockAmount(coreInquiryLockAmountRequest);
            log.info("core response: {}", coreResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NonSnapException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            String[] split = coreResponse.getRawResponse().split(",");
            String responseCode = (Objects.isNull(coreResponse.getResponseCode()) || coreResponse.getResponseCode().equals("null")) ? "01" : coreResponse.getResponseCode();
            throw new NonSnapException(responseCode, split[2]);
        }

        ResponseCode status = ResponseCode.SUCCESS;

        List<InquiryLockAmount> listOfInquiryLockAmount = coreResponse.getData().values().stream()
                .map(account -> JsonMapper.objectToOther(account, InquiryLockAmount.class))
                .toList();

        return InquiryLockAmountResponse.builder()
                .responseCode(status.getCode())
                .responseMessage(status.getMessage())
                .partnerReferenceNo(partnerReferenceNo)
                .data(listOfInquiryLockAmount)
                .build();
    }

    public CustomerPortfolioResponse customerPortfolio(String cifNumber, String partnerReferenceNo) {
        List<EDWCustomerPortfolio> EDWCustomerPortfolioByCifNumber = customerPortfolioRepository.findVCustomerPortfolioByCifNumberInBancassuranceAndOrReksadana(cifNumber);
        if (EDWCustomerPortfolioByCifNumber.isEmpty()) {
            throw new NonSnapException(NonSnapResponseCode.DATA_NOT_FOUND);
        }

        ResponseCode status = ResponseCode.SUCCESS;

        List<CustomerPortfolio> customerPortfolioList = EDWCustomerPortfolioByCifNumber
                .stream()
                .map(portfolio -> JsonMapper.objectToOther(portfolio, CustomerPortfolio.class))
                .toList();

        return CustomerPortfolioResponse.builder()
                .responseCode(status.getCode())
                .responseMessage(status.getMessage())
                .partnerReferenceNo(partnerReferenceNo)
                .data(customerPortfolioList)
                .build();
    }

    public CustomerPortfolioSummaryResponse customerPortfolioEODSummary(String cifNumber, String partnerReferenceNo) {
        log.info(" === Customer Portfolio EOD ===");
        List<CustomerPortfolioSummary> edwCustomerPortfolioSummaryByCifNumber = customerPortfolioSummaryRepository.findVCustomerPortfolioEODSummaryByCifNumber(cifNumber);

        if (edwCustomerPortfolioSummaryByCifNumber.isEmpty()) {
            throw new NonSnapException(NonSnapResponseCode.DATA_NOT_FOUND);
        }

        ResponseCode status = ResponseCode.SUCCESS;
        return CustomerPortfolioSummaryResponse.builder()
                .responseCode(status.getCode())
                .responseMessage(status.getMessage())
                .partnerReferenceNo(partnerReferenceNo)
                .data(edwCustomerPortfolioSummaryByCifNumber)
                .build();
    }

    public CustomerPortfolioSummaryResponse customerPortfolioEOMSummary(String cifNumber, String partnerReferenceNo) {
        log.info(" === Customer Portfolio EOM ===");
        List<CustomerPortfolioSummary> edwCustomerPortfolioSummaryByCifNumber = customerPortfolioSummaryRepository.findVCustomerPortfolioEOMSummaryByCifNumber(cifNumber);

        if (edwCustomerPortfolioSummaryByCifNumber.isEmpty()) {
            throw new NonSnapException(NonSnapResponseCode.DATA_NOT_FOUND);
        }

        ResponseCode status = ResponseCode.SUCCESS;
        return CustomerPortfolioSummaryResponse.builder()
                .responseCode(status.getCode())
                .responseMessage(status.getMessage())
                .partnerReferenceNo(partnerReferenceNo)
                .data(edwCustomerPortfolioSummaryByCifNumber)
                .build();
    }

    public CloseAccountContractResponse closeAccountAndContract(CloseAccountContractRequest request) {
        var coreRequest = CoreCloseAccountContractRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_CLOSE_ACCOUNT_AND_CONTRACT)
                .options(CoreConstant.CORE_OPTIONS_CLOSE_ACCOUNT_AND_CONTRACT)
                .closingFee(request.getClosingFee())
                .currency(request.getCurrency())
                .accNo(request.getAccNo())
                .accSettlement(request.getAccSettlement())
                .channelRefNum(request.getChannelRefNum())
                .umgAuthId(request.getAuthId())
                .umgTermId(request.getUmgTermId())
                .umgOperatorId(request.getUmgOperatorId())
                .umgUniqueId(request.getChannelRefNum())
                .build();
        CoreMultiDataResponse response;
        try {
            log.info("send to core : {}", JsonMapper.toString(coreRequest));
            response = coreClient.closeAccountAndContract(coreRequest);
        } catch (BusinessException be) {
            throw new NonSnapException(be.getResponseCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(NonSnapResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(response.getResponseCode())) {
            Map<String, String> parsed = CoreRawResponseParser.parse(response.getRawResponse());
            return CloseAccountContractResponse.builder()
                    .responseCode(parsed.get(CoreConstant.RESPONSE_CODE_KEY))
                    .responseMessage(parsed.get("error"))
                    .uniqueId(request.getUniqueId()).build();
        }
        return CloseAccountContractResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .uniqueId(request.getUniqueId()).build();
    }

    public GetDetailAndBalanceResponse getDetailAndBalanceMultiAccount(String accountNo, String partnerReferenceNo) {
        if (accountNo == null || partnerReferenceNo == null || accountNo.isBlank() || partnerReferenceNo.isBlank()) {
            throw new NonSnapException(NonSnapResponseCode.BAD_REQUEST);
        }
        var coreRequest = CoreGetDetailAndBalanceMultiAccountRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_GET_DETAIL_AND_BALANCE)
                .branchCode(CoreConstant.CORE_BRANCH_CODE_GET_DETAIL_AND_BALANCE)
                .transactionId(CoreConstant.CORE_TRANSACTION_ID_GET_DETAIL_AND_BALANCE_MULTI_ACCOUNT)
                .accountNumber(accountNo)
                .build();
        CoreResponse coreResponse;
        try {
            log.info("CORE REQUEST : {}", JsonMapper.toString(coreRequest));
            coreResponse = coreClient.getDetailAndBalanceMultiAccount(coreRequest);
            log.info("CORE RESPONSE : {}", JsonMapper.toString(coreResponse));
        } catch (BusinessException be) {
            throw new NonSnapException(be.getResponseCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(NonSnapResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            throw new NonSnapException(NonSnapResponseCode.DATA_NOT_FOUND);
        }
        List<AccountDetailAndBalance> accounts = coreResponse.getData().values().stream()
                .map(account -> JsonMapper.objectToOther(account, AccountDetailAndBalance.class)).toList();

        return GetDetailAndBalanceResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accounts(accounts)
                .partnerReferenceNo(partnerReferenceNo)
                .build();
    }

    public GetDetailAndBalanceResponse getDetailAndBalanceMultiCif(String cif, String partnerReferenceNo) {
        if (cif == null || partnerReferenceNo == null || cif.isBlank() || partnerReferenceNo.isBlank()) {
            throw new NonSnapException(NonSnapResponseCode.BAD_REQUEST);
        }
        var coreRequest = CoreGetDetailAndBalanceMultiCifRequest.builder()
                .operation(CoreConstant.CORE_OPERATION_GET_DETAIL_AND_BALANCE)
                .branchCode(CoreConstant.CORE_BRANCH_CODE_GET_DETAIL_AND_BALANCE)
                .transactionId(CoreConstant.CORE_TRANSACTION_ID_GET_DETAIL_AND_BALANCE_MULTI_CIF)
                .cif(cif)
                .build();
        CoreResponse coreResponse;
        try {
            log.info("CORE REQUEST : {}", JsonMapper.toString(coreRequest));
            coreResponse = coreClient.getDetailAndBalanceMultiCif(coreRequest);
            log.info("CORE RESPONSE : {}", JsonMapper.toString(coreResponse));
        } catch (BusinessException be) {
            throw new NonSnapException(be.getResponseCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NonSnapException(NonSnapResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (!ResponseCode.SUCCESS.getCode().equals(coreResponse.getResponseCode())) {
            throw new NonSnapException(NonSnapResponseCode.DATA_NOT_FOUND);
        }
        List<AccountDetailAndBalance> accounts = coreResponse.getData().values().stream()
                .map(account -> JsonMapper.objectToOther(account, AccountDetailAndBalance.class)).toList();

        return GetDetailAndBalanceResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accounts(accounts)
                .partnerReferenceNo(partnerReferenceNo)
                .build();
    }
}