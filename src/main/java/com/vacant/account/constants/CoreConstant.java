package com.vacant.account.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CoreConstant {

    public final String CORE_SUCCESS_RESPONSE = "Success";
    public final String CORE_FAILED_RESPONSE = "Failed";
    public final String CORE_TIMEOUT_RESPONSE = "Timeout";

    public final String CORE_OPERATION_ENQUIRY = "ENQUIRY.SELECT";
    public final String CORE_TRANSACTION_ACCOUNT_DETAIL_BY_CIF = "IB.ACCTDETANDBAL.BYCIF";
    public final String CORE_OPERATION_CREATE_ACCOUNT = "IT.MULTI.OFS.TEMPLATE";
    public final String CORE_OPTIONS_CREATE_ACCOUNT = "IB.SAVING/I/PROCESS//0";
    public final String CORE_TRANSACTION_ACCOUNT_BALANCE  = "E.IT.SN.AC.ENQ";
    public final String CORE_TRANSACTION_ACCOUNT_STATEMENT  = "E.IT.SN.STMT.ONDEMAND";
    public final String CORE_OPERATION_WAIVE_CHARGE = "IC.CHARGE";
    public final String CORE_OPTIONS_WAIVE_CHARGE = "S1.ICCHARGE";
    public final String CORE_OPERATION_LOCK_AMOUNT = "AC.LOCKED.EVENTS";
    public final String CORE_OPTIONS_LOCK_AMOUNT = "S1.LOCK/I/PROCESS";
    public final String CORE_OPERATION_DEBIT_INTEREST = "ACCOUNT.DEBIT.INT";
    public final String CORE_OPTIONS_DEBIT_INTEREST = "S1.DR";
    public final String CORE_OPERATION_ACTIVATE_DORMANT = "ACCT.INACTIVE.RESET";
    public final String CORE_OPTIONS_ACTIVATE_DORMANT = "S1.RESET/I/PROCESS";
    public final String CORE_OPERATION_CLOSE_ACCOUNT = "ACCOUNT.CLOSURE";
    public final String CORE_OPTIONS_CLOSE_ACCOUNT = "S1.SHINACCTS/I/PROCESS";
    public final String CORE_OPERATION_CREDIT_INTEREST = "ACCOUNT.CREDIT.INT";
    public final String CORE_OPTIONS_CREDIT_INTEREST = "S1.CR";
    public final String CORE_OPERATION_ACCOUNT_CREATION_SAVING = "ACCOUNT";
    public final String CORE_OPTIONS_ACCOUNT_CREATION_SAVING = "S1.SAVING/I/PROCESS";
    public final String CORE_OPERATION_ACCOUNT_CREATION_OVERDRAFT = "ACCOUNT";
    public final String CORE_OPTIONS_ACCOUNT_CREATION_OVERDRAFT = "S1.OVERDRAFT/I/PROCESS";
    public final String CORE_OPERATION_ACCOUNT_MAINTENANCE_SAVING = "ACCOUNT";
    public final String CORE_OPTIONS_ACCOUNT_MAINTENANCE_SAVING = "S1.SAVING.MAIN/I/PROCESS";
    public final String CORE_OPERATION_CREATE_ACCOUNT_GIRO = "ACCOUNT";
    public final String CORE_OPTIONS_CREATE_ACCOUNT_GIRO = "UR.CURRENT/I/PROCESS";
    public final String CORE_OPERATION_ACCOUNT_GIRO_MAINTENANCE = "ACCOUNT";
    public final String CORE_OPTIONS_ACCOUNT_GIRO_MAINTENANCE = "UR.CURRENT/I/PROCESS";
    public final String CORE_TRANSACTION_ID_ACCOUNT_DETAIL_BY_CIF = "IB.ACCTDETANDBAL.BYCIF";
    public final String CORE_OPERATION_INQUIRY_LOCK_AMOUNT = "ENQUIRY.SELECT";
    public final String CORE_TRANSACTION_ID_INQUIRY_LOCK_AMOUNT = "IB.LOCKEDAMT.BYACCNO";
    public final String CORE_OPERATION_CLOSE_ACCOUNT_AND_CONTRACT = "IT.MULTI.OFS.TEMPLATE";
    public final String CORE_OPTIONS_CLOSE_ACCOUNT_AND_CONTRACT = "IB.CLOSE.ACC/I/PROCESS//0";
    public final String CORE_OPERATION_GET_DETAIL_AND_BALANCE = "ENQUIRY.SELECT";
    public final String CORE_BRANCH_CODE_GET_DETAIL_AND_BALANCE = "9999";
    public final String CORE_TRANSACTION_ID_GET_DETAIL_AND_BALANCE_MULTI_ACCOUNT = "IBCORP.ACCTDETANDBAL.MLTACC";
    public final String CORE_TRANSACTION_ID_GET_DETAIL_AND_BALANCE_MULTI_CIF = "IBCORP.ACCTDETANDBAL.MLTCIF";
    public final String CORE_OPERATION_CHEQUE_STOCK_ENTRY = "STOCK.ENTRY";
    public final String CORE_OPTION_CHEQUE_STOCK_ENTRY = "S1.SHINSTOCK/I/PROCESS";
    public final String CORE_TRANSACTION_ID_CHEQUE_STOCK_ENTRY = "1";
    public final String CORE_OPERATION_CHEQUE_ISSUANCE = "CHEQUE.ISSUE";
    public final String CORE_OPTION_CHEQUE_ISSUANCE = "S1.SHINMAIN/I/PROCESS";
    public final String CORE_STOCK_REQ_CHEQUE_ISSUANCE = "CHQ.ID001";
    public final String CORE_CURRENCY = "IDR";
    public final String CORE_OPERATION_CHEQUE_BLOCK = "PAYMENT.STOP";
    public final String CORE_OPTION_CHEQUE_BLOCK = "S1.STOP/I/PROCESS";



    public final String NO_RECORD_FOUND_ERROR = "No records were found that matched the selection criteria";
    public final String INVALID_ACCOUNT_ERROR = "ACCOUNT DOES NOT EXIST IN THIS COMPANY";


    public final String CORE_SUCCESS_RESPONSE_CODE = "00";
    public final String CORE_TIMEOUT_RESPONSE_CODE = "68";
    public final String CORE_DUPLICATE_REFERENCE_NO_RESPONSE_CODE = "12";
    public final String CORE_TRANSACTION_DUPLICATE_RESPONSE_CODE = "94";


    public final String TRX = "TRX";
    public final String MSG = "MSG";
    public final String CURRENCY_CODE = "IDR";
    public final String DEFAULT_BRANCH = "ID0010121";

    public final String RESPONSE_CODE_KEY = "Y.ISO.RESPONSE:1:1";

    public final String CORE_ERROR_RESPONSE = "rcerror68";

}