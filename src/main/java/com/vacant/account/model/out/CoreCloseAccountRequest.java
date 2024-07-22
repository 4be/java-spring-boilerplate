package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoreCloseAccountRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!BRANCH")
    private String branch;
    @JsonProperty("CHANNEL.ID:1:1")
    private String channelId;
    @JsonProperty("PARTNER.ID:1:1")
    private String partnerId;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;
    @JsonProperty("SETTLEMENT.ACCT:1:1")
    private String settlementAccount;
    @JsonProperty("CLO.CHARGE.TYPE:1:1")
    private String closingChargeType;
    @JsonProperty("CLO.CHARGE.AMT:1:1")
    private String closingChargeAmount;
    @JsonProperty("POSTING.RESTRICT:1:1")
    private String postingRestrict;
    @JsonProperty("CAP.INTEREST:1:1")
    private String capitaliseInterest;
    @JsonProperty("CLOSE.ONLINE:1:1")
    private String onlineClosure;
    @JsonProperty("CURRENCY:1:1")
    private String currency;
    @JsonProperty("UMG.AUTH.ID:1:1")
    private String auditSupervisorId;
    @JsonProperty("UMG.OPERATOR.ID:1:1")
    private String auditCreatedBy;
    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String msgId;
    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String trxId;
    @JsonProperty("UMG.TERM.ID:1:1")
    private String terminalId;
}
