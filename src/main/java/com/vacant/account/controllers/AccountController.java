package com.vacant.account.controllers;

import com.simas.account.model.in.*;
import com.vacant.account.model.in.*;
import com.vacant.account.services.AccountService;
import com.vacant.account.constants.ApiPath;
import com.vacant.account.utils.ResponseConstructor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AccountController {


    private final AccountService accountService;

    @GetMapping(value = "/v1.0" + ApiPath.PARTNER_ACCOUNT_INQUIRY)
    public ResponseEntity<PartnerAccountInquiryResponse> getPartnerAccounts(@RequestParam(value = "customerId") String customerId) {
        return ResponseEntity.ok(accountService.getPartnerAccounts(customerId));
    }

    @PostMapping(value = "/v1.0" + ApiPath.REGISTRATION_ACCOUNT_CREATION)
    public ResponseEntity<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.createAccount(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.BALANCE_INQUIRY)
    public ResponseEntity<BalanceInquiryResponse> getAccountBalance(@Valid @RequestBody BalanceInquiryRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.getAccountBalance(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.CHECK_ACCOUNTS)
    public ResponseEntity<CheckAccountsResponse> checkAccounts(@RequestBody @Valid CheckAccountsRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.checkAccounts(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.WAIVE_CHARGE)
    public ResponseEntity<WaiveChargeResponse> waiveCharge(@RequestBody @Valid WaiveChargeRequest request) {
        return ResponseEntity.ok(accountService.waiveCharge(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.CLOSE_ACCOUNT)
    public ResponseEntity<CloseAccountResponse> closeAccount(@RequestBody @Valid CloseAccountRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.closeAccount(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.CREDIT_INTEREST)
    public ResponseEntity<CreditInterestResponse> creditInterest(@RequestBody @Valid CreditInterestRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.creditInterest(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.LOCK_AMOUNT)
    public ResponseEntity<LockAmountResponse> overrideLockAmount(@RequestBody @Valid LockAmountRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.overrideLockAmount(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.DEBIT_INTEREST)
    public ResponseEntity<DebitInterestResponse> debitInterest(@RequestBody @Valid DebitInterestRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.debitInterest(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.ACTIVATE_DORMANT_ACCOUNT)
    public ResponseEntity<ActivateDormantResponse> activateDormantAccount(@RequestBody @Valid ActivateDormantRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.activateDormantAccount(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.ACCOUNT_CREATION_SAVING)
    public ResponseEntity<AccountCreationSavingResponse> accountCreationSaving(@RequestBody @Valid AccountCreationSavingRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.accountCreationSaving(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.ACCOUNT_MAINTENANCE_SAVING)
    public ResponseEntity<AccountMaintenanceSavingResponse> accountMaintenanceSaving(@RequestBody @Valid AccountMaintenanceSavingRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.accountMaintenanceSaving(request));
    }

    @GetMapping(value = "v1.0" + ApiPath.ACCOUNT_DETAIL_BY_CIF)
    public ResponseEntity<AccountDetailByCifResponse> accountDetailByCif(@RequestParam(name = "cifNumber") String cifNumber, @RequestParam(name = "partnerReferenceNo") String partnerReferenceNo) {
        return ResponseConstructor.constructSnapApiResponse(accountService.accountDetailByCif(cifNumber, partnerReferenceNo));
    }

    @GetMapping(value = "v1.0" + ApiPath.INQUIRY_LOCK_AMOUNT)
    public ResponseEntity<InquiryLockAmountResponse> inquiryLockAmount(@RequestParam(name = "accountNumber") String accountNumber, @RequestParam(name = "partnerReferenceNo") String partnerReferenceNo) {
        return ResponseConstructor.constructSnapApiResponse(accountService.inquiryLockAmount(accountNumber, partnerReferenceNo));
    }

    @GetMapping(value = "v1.0" + ApiPath.CUSTOMER_PORTFOLIO)
    public ResponseEntity<CustomerPortfolioResponse> customerPortfolio(@RequestParam(name = "cifNumber") String cifNumber, @RequestParam(name = "partnerReferenceNo") String partnerReferenceNo) {
        return ResponseConstructor.constructSnapApiResponse(accountService.customerPortfolio(cifNumber, partnerReferenceNo));
    }

    @PostMapping(value = "v1.0" + ApiPath.ACCOUNT_CREATION_OVERDRAFT)
    public ResponseEntity<AccountCreationOverdraftResponse> accountCreationOverdraft(@RequestBody @Valid AccountCreationOverdraftRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.accountCreationOverdraft(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.CREATE_ACCOUNT_GIRO)
    public ResponseEntity<CreateAccountGiroResponse> createAccountGiro(@RequestBody @Valid CreateAccountGiroRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.createAccountGiro(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.UPDATE_ACCOUNT_GIRO)
    public ResponseEntity<UpdateAccountGiroResponse> updateAccountGiro(@RequestBody @Valid UpdateAccountGiroRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.updateAccountGiro(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.ACCOUNT_MAINTENANCE_OVERDRAFT)
    public ResponseEntity<AccountMaintenanceOverdraftResponse> accountMaintenanceOverdraft(@RequestBody @Valid AccountMaintenanceOverdraftRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.accountMaintenanceOverdraft(request));
    }

    @GetMapping(value = "v1.0" + ApiPath.CUSTOMER_PORTFOLIO_EOD_SUMMARY)
    public ResponseEntity<CustomerPortfolioSummaryResponse> getCustomerPortfolioEODSummary(@RequestParam(name = "cifNumber") String cifNumber, @RequestParam(name = "partnerReferenceNo") String partnerReferenceNo) {
        return ResponseConstructor.constructSnapApiResponse(accountService.customerPortfolioEODSummary(cifNumber, partnerReferenceNo));
    }

    @GetMapping(value = "v1.0" + ApiPath.CUSTOMER_PORTFOLIO_EOM_SUMMARY)
    public ResponseEntity<CustomerPortfolioSummaryResponse> getCustomerPortfolioEOMSummary(@RequestParam(name = "cifNumber") String cifNumber, @RequestParam(name = "partnerReferenceNo") String partnerReferenceNo) {
        return ResponseConstructor.constructSnapApiResponse(accountService.customerPortfolioEOMSummary(cifNumber, partnerReferenceNo));
    }

    @PostMapping(value = "v1.0" + ApiPath.CLOSE_ACCOUNT_AND_CONTRACT)
    public ResponseEntity<Object> closeAccountAndContract(@RequestBody CloseAccountContractRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.closeAccountAndContract(request));
    }

    @GetMapping(value = "v1.0" + ApiPath.GET_DETAIL_AND_BALANCE_MULTI_ACCOUNT)
    public ResponseEntity<GetDetailAndBalanceResponse> getDetailAndBalanceMultiAcc(@RequestParam("accountNo") String accountNo,
                                                                                   @RequestParam("partnerReferenceNo") String partnerReferenceNo) {
        MDC.put("partnerReferenceNo", partnerReferenceNo);
        return ResponseConstructor.constructSnapApiResponse(accountService.getDetailAndBalanceMultiAccount(accountNo, partnerReferenceNo));
    }

    @GetMapping(value = "v1.0" + ApiPath.GET_DETAIL_AND_BALANCE_MULTI_CIF)
    public ResponseEntity<GetDetailAndBalanceResponse> getDetailAndBalanceMultiCif(@RequestParam("cif") String cif,
                                                                                   @RequestParam("partnerReferenceNo") String partnerReferenceNo) {
        MDC.put("partnerReferenceNo", partnerReferenceNo);
        return ResponseConstructor.constructSnapApiResponse(accountService.getDetailAndBalanceMultiCif(cif, partnerReferenceNo));
    }

    @PostMapping(value = "v1.0" + ApiPath.CHEQUE_STOCK_ENTRY)
    public ResponseEntity<ChequeStockEntryResponse> chequeStockEntry(@RequestBody @Valid ChequeStockEntryRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.chequeStockEntry(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.CHEQUE_ISSUANCE)
    public ResponseEntity<ChequeIssuanceResponse> chequeIssuance(@RequestBody @Valid ChequeIssuanceRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.chequeIssuance(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.CHEQUE_BLOCK)
    public ResponseEntity<ChequeBlockResponse> chequeBlock(@RequestBody @Valid ChequeBlockRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.chequeBlock(request));
    }

    @PostMapping(value = "v1.0" + ApiPath.CHEQUE_UNBLOCK)
    public ResponseEntity<ChequeUnBlockResponse> chequeUnBlock(@RequestBody @Valid ChequeUnBlockRequest request) {
        return ResponseConstructor.constructSnapApiResponse(accountService.chequeUnBlock(request));
    }
}
