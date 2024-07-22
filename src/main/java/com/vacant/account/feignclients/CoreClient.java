package com.vacant.account.feignclients;

import com.simas.account.model.out.*;
import com.vacant.account.model.out.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(value = "ms-core", url = "${ms-core.url}")
public interface CoreClient {

    @PostMapping(value = "/generic")
    CoreResponse getSavingAccounts(CoreGetSavingAccountsRequest request);

    @PostMapping(value = "/generic")
    CoreWriteOperationResponse createAccount(CoreCreateAccountRequest request);

    @PostMapping(value = "/generic")
    CoreResponse getAccountBalance(CoreGetAccountBalanceRequest request);

    @PostMapping(value = "/generic")
    CoreResponse getAccountStatement(CoreGetAccountStatementRequest request);

    @PostMapping(value = "/generic")
    CoreWriteOperationResponse waiveCharge(CoreWaiveChargeRequest request);

    @PostMapping(value = "/generic")
    CoreRawResponse overrideLockAmount(CoreLockAmountRequest request);

    @PostMapping(value = "/generic")
    CoreRawResponse coreDebitInterest(CoreDebitInterestRequest request);

    @PostMapping(value = "/generic")
    CoreRawResponse activateDormantAccount(CoreActivateDormantRequest request);

    @PostMapping(value = "/generic")
    CoreRawResponse closeAccount(CoreCloseAccountRequest request);

    @PostMapping(value = "/generic")
    CoreWriteOperationResponse creditInterest(CoreCreditInterestRequest request);

    @PostMapping(value = "/generic")
    CoreWriteOperationResponse accountCreationSaving(CoreAccountCreationSavingRequest request);

    @PostMapping(value = "/generic")
    CoreWriteOperationResponse accountMaintenanceSaving(CoreAccountMaintenanceSavingRequest request);

    @PostMapping(value = "/generic")
    CoreWriteOperationResponse accountCreationOverdraft(CoreAccountCreationOverdraftRequest request);

    @PostMapping(value = "/generic")
    CoreRawResponse createAccountGiro(CoreCreateAccountGiroRequest request);

    @PostMapping(value = "/generic")
    CoreRawResponse updateAccountGiro(CoreUpdateAccountGiroRequest request);

    @PostMapping(value = "/generic")
    CoreWriteOperationResponse accountMaintenanceOverdraft(CoreAccountMaintenanceOverdraftRequest request);

    @PostMapping(value = "/generic")
    CoreResponse inquiryLockAmount(CoreInquiryLockAmountRequest request);

    @PostMapping(value = "/generic")
    CoreMultiDataResponse closeAccountAndContract(CoreCloseAccountContractRequest request);

    @PostMapping(value = "/generic")
    CoreResponse getDetailAndBalanceMultiAccount(CoreGetDetailAndBalanceMultiAccountRequest request);

    @PostMapping(value = "/generic")
    CoreResponse getDetailAndBalanceMultiCif(CoreGetDetailAndBalanceMultiCifRequest request);

    @PostMapping(value = "/generic")
    CoreWriteOperationResponse coreChequeStockEntry(Map<String,String> request);

    @PostMapping(value = "/generic")
    CoreRawResponse coreChequeIssuance(CoreChequeIssuanceRequest request);

    @PostMapping(value = "/generic")
    CoreRawResponse coreChequeBlock(CoreChequeBlockRequest request);
    @PostMapping(value = "/generic")
    CoreRawResponse coreChequeUnBlock(CoreChequeUnBlockRequest request);
}
