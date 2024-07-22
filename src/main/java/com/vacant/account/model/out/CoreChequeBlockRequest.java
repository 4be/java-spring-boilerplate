package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreChequeBlockRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("!BRANCH")
    private String branchCode;
    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;
    @JsonProperty("PAYM.STOP.TYPE:1:1")
    private String paymentStartStopType;
    @JsonProperty("FIRST.CHEQUE.NO:1:1")
    private String firstCheque;
    @JsonProperty("LAST.CHEQUE.NO:1:1")
    private String lastCheque;
    @JsonProperty("CHEQUE.TYPE:1:1")
    private String chequeType;
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
