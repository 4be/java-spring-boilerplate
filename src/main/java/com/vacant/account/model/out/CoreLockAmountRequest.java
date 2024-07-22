package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoreLockAmountRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("CHANNEL.ID:1:1")
    private String channelId;
    @JsonProperty("PARTNER.ID:1:1")
    private String partnerId;
    @JsonProperty("!BRANCH")
    private String branchCode;
    @JsonProperty("ACCOUNT.NUMBER:1:1")
    private String accountNumber;
    @JsonProperty("DESCRIPTION:1:1")
    private String description;
    @JsonProperty("FROM.DATE:1:1")
    private String fromDate;
    @JsonProperty("TO.DATE:1:1")
    private String toDate;
    @JsonProperty("LOCKED.AMOUNT:1:1")
    private String lockedAmount;
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
    private String correlationId;
}
