package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreChequeIssuanceRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("!BRANCH")
    private String branchCode;
    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;
    @JsonProperty("STOCK.REG:1:1")
    private String stockRegister;
    @JsonProperty("CURRENCY:1:1")
    private String currency;
    @JsonProperty("CHEQUE.STATUS:1:1")
    private String chequeStatus;
    @JsonProperty("CHQ.NO.START:1:1")
    private String stockStartNo;
    @JsonProperty("NUMBER.ISSUED:1:1")
    private String quantityIssued;
    @JsonProperty("NOTES:1:1")
    private String notes;
    @JsonProperty("WAIVE.CHARGES:1:1")
    private String weiveCharges;
    @JsonProperty("CHARGES:1:1")
    private String chequeCharges;
    @JsonProperty("CHG.CODE:1:1")
    private String chequeBookCharges;
    @JsonProperty("CHG.CODE:2:1")
    private String chequeBooksTamDuty;
    @JsonProperty("UMG.AUTH.ID:1:1")
    private String supervisorId;
    @JsonProperty("UMG.OPERATOR.ID:1:1")
    private String createdBy;
    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String msgId;
    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String trxId;
    @JsonProperty("UMG.TERM.ID:1:1")
    private String terminalId;
}
